package com.guide.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guide.entity.*;
import com.guide.mapper.*;
import com.guide.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/guides")
public class GuideController {

    @Autowired
    private GuideMapper guideMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GuideCalendarMapper guideCalendarMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private WithdrawalMapper withdrawalMapper;

    @Autowired
    private com.guide.service.UserLevelService userLevelService;

    @GetMapping("/list")
    public ResponseEntity<?> getGuideList(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String sortBy, // rating, price, orders, available_date
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        QueryWrapper<Guide> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("verification_status", "approved");

        // 获取所有已审核的向导
        List<Guide> allGuides = guideMapper.selectList(queryWrapper);
        
        // SQL Server对JSON字段支持有限，在内存中筛选
        List<Map<String, Object>> filteredGuides = new java.util.ArrayList<>();
        
        for (Guide guide : allGuides) {
            if (city != null && !city.isEmpty()) {
                String citiesJson = guide.getCities();
                if (citiesJson == null || !citiesJson.contains(city)) {
                    continue;
                }
            }
            
            if (language != null && !language.isEmpty()) {
                String languagesJson = guide.getLanguages();
                if (languagesJson == null || !languagesJson.contains(language)) {
                    continue;
                }
            }
            
            if (minPrice != null && guide.getDailyRate() != null) {
                if (guide.getDailyRate().compareTo(minPrice) < 0) {
                    continue;
                }
            }
            if (maxPrice != null && guide.getDailyRate() != null) {
                if (guide.getDailyRate().compareTo(maxPrice) > 0) {
                    continue;
                }
            }
            
            if (tag != null && !tag.isEmpty()) {
                String tagsJson = guide.getTags();
                if (tagsJson == null || !tagsJson.contains(tag)) {
                    continue;
                }
            }
            
            User user = userMapper.selectById(guide.getUserId());
            if (user == null) {
                continue;
            }
            
            if (!"guide".equals(user.getRole())) {
                continue;
            }
            
            // guide_calendar表的guide_id是user_id，不是guide表的id
            QueryWrapper<GuideCalendar> calendarQuery = new QueryWrapper<>();
            calendarQuery.eq("guide_id", guide.getUserId());
            calendarQuery.eq("status", "available");
            calendarQuery.ge("date", LocalDate.now());
            calendarQuery.orderByAsc("date");
            calendarQuery.last("OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY");
            GuideCalendar nextAvailable = guideCalendarMapper.selectOne(calendarQuery);
            LocalDate nextAvailableDate = nextAvailable != null ? nextAvailable.getDate() : null;
            
            Map<String, Object> guideData = new HashMap<>();
            guideData.put("id", guide.getId());
            guideData.put("userId", guide.getUserId());
            guideData.put("nickname", user.getNickname());
            guideData.put("avatarPath", user.getAvatarPath());
            guideData.put("avatarStatus", user.getAvatarStatus());
            guideData.put("languages", guide.getLanguages());
            guideData.put("cities", guide.getCities());
            guideData.put("tags", guide.getTags());
            guideData.put("bio", guide.getBio());
            guideData.put("hourlyRate", guide.getHourlyRate());
            guideData.put("dailyRate", guide.getDailyRate());
            guideData.put("rating", guide.getRating() != null ? guide.getRating() : BigDecimal.ZERO);
            guideData.put("totalReviews", guide.getTotalReviews() != null ? guide.getTotalReviews() : 0);
            guideData.put("totalOrders", guide.getTotalOrders() != null ? guide.getTotalOrders() : 0);
            guideData.put("nextAvailableDate", nextAvailableDate);
            
            int userLevel = user.getLv() != null ? user.getLv() : 1;
            guideData.put("level", userLevel);
            double exposureBoost = userLevelService.getExposureBoost(guide.getUserId());
            guideData.put("exposureBoost", exposureBoost);
            
            filteredGuides.add(guideData);
        }
        
        if (sortBy != null) {
            switch (sortBy) {
                case "rating":
                    filteredGuides.sort((a, b) -> {
                        BigDecimal ratingA = (BigDecimal) a.get("rating");
                        BigDecimal ratingB = (BigDecimal) b.get("rating");
                        if (ratingA == null) ratingA = BigDecimal.ZERO;
                        if (ratingB == null) ratingB = BigDecimal.ZERO;
                        // 应用曝光率加成：高等级向导的评分会被加权
                        Double boostA = (Double) a.get("exposureBoost");
                        Double boostB = (Double) b.get("exposureBoost");
                        if (boostA == null) boostA = 1.0;
                        if (boostB == null) boostB = 1.0;
                        double scoreA = ratingA.doubleValue() * boostA;
                        double scoreB = ratingB.doubleValue() * boostB;
                        return Double.compare(scoreB, scoreA); // 降序
                    });
                    break;
                case "price":
                    filteredGuides.sort((a, b) -> {
                        BigDecimal priceA = (BigDecimal) a.get("dailyRate");
                        BigDecimal priceB = (BigDecimal) b.get("dailyRate");
                        if (priceA == null) priceA = BigDecimal.ZERO;
                        if (priceB == null) priceB = BigDecimal.ZERO;
                        return priceA.compareTo(priceB);
                    });
                    break;
                case "orders":
                    filteredGuides.sort((a, b) -> {
                        Integer ordersA = (Integer) a.get("totalOrders");
                        Integer ordersB = (Integer) b.get("totalOrders");
                        if (ordersA == null) ordersA = 0;
                        if (ordersB == null) ordersB = 0;
                        return ordersB.compareTo(ordersA);
                    });
                    break;
                case "available_date":
                    filteredGuides.sort((a, b) -> {
                        LocalDate dateA = (LocalDate) a.get("nextAvailableDate");
                        LocalDate dateB = (LocalDate) b.get("nextAvailableDate");
                        if (dateA == null && dateB == null) return 0;
                        if (dateA == null) return 1;
                        if (dateB == null) return -1;
                        return dateA.compareTo(dateB);
                    });
                    break;
            }
        }
        
        int start = (page - 1) * size;
        int end = Math.min(start + size, filteredGuides.size());
        List<Map<String, Object>> pagedGuides = filteredGuides.subList(start, end);

        Map<String, Object> result = new HashMap<>();
        result.put("list", pagedGuides);
        result.put("total", filteredGuides.size());

        return ResponseEntity.ok(result);
    }

    /**
     * 获取向导详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getGuideDetail(@PathVariable Integer id) {
        Guide guide = guideMapper.selectById(id);
        if (guide == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("向导不存在"));
        }

        User user = userMapper.selectById(guide.getUserId());
        if (user == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("用户不存在"));
        }

        Map<String, Object> guideData = new HashMap<>();
        guideData.put("id", guide.getId());
        guideData.put("userId", guide.getUserId());
        guideData.put("nickname", user.getNickname());
        guideData.put("avatarPath", user.getAvatarPath());
        guideData.put("avatarStatus", user.getAvatarStatus());
        guideData.put("languages", guide.getLanguages() != null ? guide.getLanguages() : "[]");
        guideData.put("cities", guide.getCities() != null ? guide.getCities() : "[]");
        guideData.put("tags", guide.getTags() != null ? guide.getTags() : "[]");
        guideData.put("certificates", guide.getCertificates() != null ? guide.getCertificates() : "[]");
        guideData.put("bio", guide.getBio() != null ? guide.getBio() : "");
        guideData.put("hourlyRate", guide.getHourlyRate());
        guideData.put("dailyRate", guide.getDailyRate());
        guideData.put("rating", guide.getRating() != null ? guide.getRating() : BigDecimal.ZERO);
        guideData.put("totalReviews", guide.getTotalReviews() != null ? guide.getTotalReviews() : 0);
        guideData.put("totalOrders", guide.getTotalOrders() != null ? guide.getTotalOrders() : 0);
        guideData.put("verificationStatus", guide.getVerificationStatus());

        Map<String, Object> result = new HashMap<>();
        result.put("guide", guideData);
        result.put("user", user);

        // guide_calendar表的guide_id是user_id，不是guide表的id
        QueryWrapper<GuideCalendar> calendarQuery = new QueryWrapper<>();
        calendarQuery.eq("guide_id", guide.getUserId());
        calendarQuery.ge("date", LocalDate.now());
        calendarQuery.orderByAsc("date");
        List<GuideCalendar> calendar = guideCalendarMapper.selectList(calendarQuery);
        result.put("calendar", calendar);

        return ResponseEntity.ok(result);
    }

    /**
     * 获取向导统计数据
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getGuideStats(@RequestAttribute(required = false) Integer userId) {
        if (userId == null) {
            return ResponseEntity.status(401).body(createErrorResponse("请先登录"));
        }
        QueryWrapper<Guide> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        Guide guide = guideMapper.selectOne(queryWrapper);
        
        if (guide == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("请先创建向导资料"));
        }

        // 统计订单数
        QueryWrapper<Order> pendingQuery = new QueryWrapper<>();
        pendingQuery.eq("guide_id", userId);
        pendingQuery.eq("status", "pending");
        int pendingOrders = orderMapper.selectCount(pendingQuery).intValue();

        QueryWrapper<Order> progressQuery = new QueryWrapper<>();
        progressQuery.eq("guide_id", userId);
        progressQuery.eq("status", "in_progress");
        int inProgressOrders = orderMapper.selectCount(progressQuery).intValue();

        Map<String, Object> stats = new HashMap<>();
        stats.put("pendingOrders", pendingOrders);
        stats.put("inProgressOrders", inProgressOrders);
        stats.put("totalIncome", guide.getTotalIncome());
        stats.put("rating", guide.getRating());

        return ResponseEntity.ok(stats);
    }

    /**
     * 获取当前用户的向导信息
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getGuideProfile(@RequestAttribute Integer userId) {
        QueryWrapper<Guide> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        Guide guide = guideMapper.selectOne(queryWrapper);
        
        if (guide == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("请先创建向导资料"));
        }

        // 关联用户信息
        User user = userMapper.selectById(userId);
        
        // 构建返回数据，包含用户信息
        Map<String, Object> result = new HashMap<>();
        result.put("id", guide.getId());
        result.put("userId", guide.getUserId());
        result.put("nickname", user != null ? user.getNickname() : "");
        result.put("email", user != null ? user.getEmail() : "");
        result.put("avatarPath", user != null ? user.getAvatarPath() : "");
        result.put("avatarStatus", user != null ? user.getAvatarStatus() : null);
        result.put("languages", guide.getLanguages() != null ? guide.getLanguages() : "[]");
        result.put("cities", guide.getCities() != null ? guide.getCities() : "[]");
        result.put("tags", guide.getTags() != null ? guide.getTags() : "[]");
        result.put("certificates", guide.getCertificates() != null ? guide.getCertificates() : "[]");
        result.put("bio", guide.getBio() != null ? guide.getBio() : "");
        result.put("hourlyRate", guide.getHourlyRate());
        result.put("dailyRate", guide.getDailyRate());
        result.put("verificationStatus", guide.getVerificationStatus());
        result.put("rejectionReason", guide.getRejectionReason());
        result.put("rating", guide.getRating());
        result.put("totalReviews", guide.getTotalReviews());
        result.put("totalOrders", guide.getTotalOrders());
        result.put("totalIncome", guide.getTotalIncome());
        result.put("createTime", guide.getCreateTime());
        result.put("updateTime", guide.getUpdateTime());

        return ResponseEntity.ok(result);
    }

    /**
     * 创建或更新向导信息
     */
    @PostMapping("/profile")
    public ResponseEntity<?> saveGuideProfile(
            @RequestAttribute Integer userId,
            @RequestBody Guide guide) {

        // 检查用户角色，必须是 'guide' 角色
        User user = userMapper.selectById(userId);
        if (user == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("用户不存在"));
        }
        if (!"guide".equals(user.getRole())) {
            return ResponseEntity.badRequest().body(createErrorResponse("只有向导角色可以创建向导资料。您的当前角色是：" + user.getRole()));
        }

        // 检查是否已有向导资料
        QueryWrapper<Guide> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        Guide existingGuide = guideMapper.selectOne(queryWrapper);

        if (existingGuide != null) {
            guide.setId(existingGuide.getId());
            guide.setUserId(userId);
            guide.setRating(existingGuide.getRating()); // 保留评分
            guide.setTotalReviews(existingGuide.getTotalReviews()); // 保留评价数
            guide.setTotalOrders(existingGuide.getTotalOrders()); // 保留订单数
            guide.setTotalIncome(existingGuide.getTotalIncome()); // 保留收入
            if ("approved".equals(existingGuide.getVerificationStatus())) {
                guide.setVerificationStatus("pending");
            } else {
                guide.setVerificationStatus(existingGuide.getVerificationStatus());
            }
            guide.setUpdateTime(LocalDateTime.now());
            guideMapper.updateById(guide);
        } else {
            // 新建
            guide.setUserId(userId);
            guide.setVerificationStatus("pending");
            guide.setRating(BigDecimal.ZERO);
            guide.setTotalReviews(0);
            guide.setTotalOrders(0);
            guide.setTotalIncome(BigDecimal.ZERO);
            guide.setCreateTime(LocalDateTime.now());
            guide.setUpdateTime(LocalDateTime.now());
            guideMapper.insert(guide);
        }

        return ResponseEntity.ok(guide);
    }

    /**
     * 更新向导信息（PUT方法，部分更新）
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateGuideProfile(
            @RequestAttribute Integer userId,
            @RequestBody Map<String, Object> updates) {

        // 检查用户角色，必须是 'guide' 角色
        User user = userMapper.selectById(userId);
        if (user == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("用户不存在"));
        }
        if (!"guide".equals(user.getRole())) {
            return ResponseEntity.badRequest().body(createErrorResponse("只有向导角色可以更新向导资料。您的当前角色是：" + user.getRole()));
        }

        QueryWrapper<Guide> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        Guide guide = guideMapper.selectOne(queryWrapper);
        
        if (guide == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("请先创建向导资料"));
        }

        // 部分更新字段
        if (updates.containsKey("languages")) {
            guide.setLanguages((String) updates.get("languages"));
        }
        if (updates.containsKey("cities")) {
            guide.setCities((String) updates.get("cities"));
        }
        if (updates.containsKey("tags")) {
            guide.setTags((String) updates.get("tags"));
        }
        if (updates.containsKey("bio")) {
            guide.setBio((String) updates.get("bio"));
        }
        if (updates.containsKey("hourlyRate")) {
            guide.setHourlyRate(new BigDecimal(updates.get("hourlyRate").toString()));
        }
        if (updates.containsKey("dailyRate")) {
            guide.setDailyRate(new BigDecimal(updates.get("dailyRate").toString()));
        }
        if (updates.containsKey("certificates")) {
            guide.setCertificates((String) updates.get("certificates"));
        }

        boolean needNotifyAdmin = false;
        if ("approved".equals(guide.getVerificationStatus())) {
            guide.setVerificationStatus("pending");
            needNotifyAdmin = true;
        }
        
        guide.setUpdateTime(LocalDateTime.now());
        guideMapper.updateById(guide);

        if (needNotifyAdmin) {
            try {
                notificationService.notifyAdminForGuideVerification(guide.getId(), user.getNickname());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ResponseEntity.ok(guide);
    }

    /**
     * 删除向导资料（向导端）
     * 只有角色为 'guide' 的用户才能删除向导资料
     */
    @DeleteMapping("/profile")
    public ResponseEntity<?> deleteGuideProfile(@RequestAttribute Integer userId) {
        // 检查用户角色，必须是 'guide' 角色
        User user = userMapper.selectById(userId);
        if (user == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("用户不存在"));
        }
        if (!"guide".equals(user.getRole())) {
            return ResponseEntity.badRequest().body(createErrorResponse("只有向导角色可以删除向导资料。您的当前角色是：" + user.getRole()));
        }

        QueryWrapper<Guide> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        Guide guide = guideMapper.selectOne(queryWrapper);
        
        if (guide == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("向导资料不存在"));
        }

        // 检查是否有进行中的订单
        QueryWrapper<Order> orderQuery = new QueryWrapper<>();
        orderQuery.eq("guide_id", userId);
        orderQuery.in("status", java.util.Arrays.asList("pending", "confirmed", "in_progress"));
        long activeOrders = orderMapper.selectCount(orderQuery);
        
        if (activeOrders > 0) {
            return ResponseEntity.badRequest().body(createErrorResponse("存在进行中的订单，无法删除资料"));
        }

        guideMapper.deleteById(guide.getId());
        return ResponseEntity.ok(createSuccessResponse("向导资料已删除"));
    }

    /**
     * 获取日历
     */
    @GetMapping("/calendar")
    public ResponseEntity<?> getCalendar(
            @RequestAttribute Integer userId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {

        QueryWrapper<Guide> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        Guide guide = guideMapper.selectOne(queryWrapper);
        if (guide == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("请先创建向导资料"));
        }

        QueryWrapper<GuideCalendar> calendarQuery = new QueryWrapper<>();
        calendarQuery.eq("guide_id", guide.getUserId()); // guide_id是user_id
        if (year != null && month != null) {
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
            calendarQuery.between("date", startDate, endDate);
        }
        List<GuideCalendar> calendar = guideCalendarMapper.selectList(calendarQuery);

        Map<String, Object> result = new HashMap<>();
        result.put("list", calendar);
        result.put("total", calendar.size());

        return ResponseEntity.ok(result);
    }

    /**
     * 设置日历状态
     */
    @PostMapping("/calendar")
    public ResponseEntity<?> setCalendarStatus(
            @RequestAttribute Integer userId,
            @RequestBody Map<String, Object> request) {

        LocalDate date = LocalDate.parse((String) request.get("date"));
        String status = (String) request.get("status");

        QueryWrapper<Guide> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        Guide guide = guideMapper.selectOne(queryWrapper);
        if (guide == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("请先创建向导资料"));
        }

        QueryWrapper<GuideCalendar> calendarQuery = new QueryWrapper<>();
        calendarQuery.eq("guide_id", guide.getUserId()); // guide_id是user_id
        calendarQuery.eq("date", date);
        GuideCalendar calendar = guideCalendarMapper.selectOne(calendarQuery);

        if (calendar != null) {
            calendar.setStatus(status);
            guideCalendarMapper.updateById(calendar);
        } else {
            calendar = new GuideCalendar();
            calendar.setGuideId(guide.getUserId()); // guide_id是user_id
            calendar.setDate(date);
            calendar.setStatus(status);
            calendar.setCreateTime(LocalDateTime.now());
            guideCalendarMapper.insert(calendar);
        }

        return ResponseEntity.ok(calendar);
    }

    /**
     * 获取收入统计
     */
    @GetMapping("/income")
    public ResponseEntity<?> getIncome(
            @RequestAttribute Integer userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        QueryWrapper<Guide> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        Guide guide = guideMapper.selectOne(queryWrapper);
        if (guide == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("请先创建向导资料"));
        }

        QueryWrapper<Order> orderQuery = new QueryWrapper<>();
        orderQuery.eq("guide_id", userId);
        orderQuery.eq("status", "completed");
        if (startDate != null) {
            orderQuery.ge("create_time", LocalDate.parse(startDate));
        }
        if (endDate != null) {
            orderQuery.le("create_time", LocalDate.parse(endDate));
        }
        List<Order> orders = orderMapper.selectList(orderQuery);

        // 计算统计数据
        BigDecimal totalIncome = guide.getTotalIncome() != null ? guide.getTotalIncome() : BigDecimal.ZERO;
        BigDecimal monthlyIncome = orders.stream()
            .filter(order -> {
                LocalDateTime orderTime = order.getCreateTime();
                LocalDateTime now = LocalDateTime.now();
                return orderTime.getYear() == now.getYear() &&
                       orderTime.getMonth() == now.getMonth();
            })
            .map(Order::getGuideIncome)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 计算已提现总金额
        QueryWrapper<Withdrawal> withdrawalQuery = new QueryWrapper<>();
        withdrawalQuery.eq("guide_id", userId);
        withdrawalQuery.eq("status", "completed");
        List<Withdrawal> completedWithdrawals = withdrawalMapper.selectList(withdrawalQuery);
        BigDecimal totalWithdrawn = completedWithdrawals.stream()
            .map(Withdrawal::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalIncome", totalIncome);
        stats.put("monthlyIncome", monthlyIncome);
        // 可提现金额 = 累计收入 - 已提现总金额
        BigDecimal availableBalance = totalIncome.subtract(totalWithdrawn);
        if (availableBalance.compareTo(BigDecimal.ZERO) < 0) {
            availableBalance = BigDecimal.ZERO;
        }
        stats.put("availableBalance", availableBalance);
        stats.put("completedOrders", orders.size());

        // 转换订单数据为前端需要的格式
        List<Map<String, Object>> incomeList = orders.stream().map(order -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", order.getId());
            item.put("orderId", order.getId() != null ? order.getId() : "-");
            String dateStr = "";
            if (order.getStartDate() != null) {
                dateStr = order.getStartDate().toString();
            } else if (order.getCreateTime() != null) {
                dateStr = order.getCreateTime().toLocalDate().toString();
            }
            item.put("date", dateStr.isEmpty() ? "-" : dateStr);
            item.put("totalPrice", order.getTotalPrice());
            item.put("commissionAmount", order.getCommissionAmount());
            item.put("guideIncome", order.getGuideIncome());
            item.put("status", order.getStatus());
            return item;
        }).collect(java.util.stream.Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("stats", stats);
        result.put("list", incomeList);

        return ResponseEntity.ok(result);
    }

    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }

    private Map<String, String> createSuccessResponse(String message) {
        Map<String, String> success = new HashMap<>();
        success.put("message", message);
        return success;
    }
}