package com.guide.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guide.entity.Guide;
import com.guide.entity.Order;
import com.guide.entity.Post;
import com.guide.entity.User;
import com.guide.mapper.GuideMapper;
import com.guide.mapper.OrderMapper;
import com.guide.mapper.PostMapper;
import com.guide.mapper.UserMapper;
import com.guide.service.NotificationService;
import com.guide.service.UserLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GuideMapper guideMapper;

    @Autowired
    private UserLevelService userLevelService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private NotificationService notificationService;

    /**
     * 获取平台统计数据
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        Map<String, Object> stats = new HashMap<>();

        Long totalUsersCount = userMapper.selectCount(null);
        stats.put("totalUsers", totalUsersCount != null ? totalUsersCount.intValue() : 0);
        
        Long totalGuidesCount = guideMapper.selectCount(null);
        stats.put("totalGuides", totalGuidesCount != null ? totalGuidesCount.intValue() : 0);

        Long totalOrdersCount = orderMapper.selectCount(null);
        stats.put("totalOrders", totalOrdersCount != null ? totalOrdersCount.intValue() : 0);
        
        QueryWrapper<Order> completedQuery = new QueryWrapper<>();
        completedQuery.eq("status", "completed");
        Long completedOrdersCount = orderMapper.selectCount(completedQuery);
        stats.put("completedOrders", completedOrdersCount != null ? completedOrdersCount.intValue() : 0);

        QueryWrapper<Order> commissionQuery = new QueryWrapper<>();
        commissionQuery.eq("status", "completed");
        List<Order> completedOrders = orderMapper.selectList(commissionQuery);
        BigDecimal totalCommission = BigDecimal.ZERO;
        if (completedOrders != null && !completedOrders.isEmpty()) {
            totalCommission = completedOrders.stream()
                .map(order -> order.getCommissionAmount() != null ? order.getCommissionAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        stats.put("totalCommission", totalCommission);

        QueryWrapper<Post> pendingPostsQuery = new QueryWrapper<>();
        pendingPostsQuery.eq("status", "pending");
        Long pendingPostsCount = postMapper.selectCount(pendingPostsQuery);
        stats.put("pendingPosts", pendingPostsCount != null ? pendingPostsCount.intValue() : 0);

        return ResponseEntity.ok(stats);
    }

    /**
     * 获取用户列表
     */
    @GetMapping("/users")
    public ResponseEntity<?> getUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status) {

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (role != null && !role.isEmpty()) {
            queryWrapper.eq("role", role);
        }
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderByDesc("create_time");

        List<User> users = userMapper.selectList(queryWrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("list", users);
        result.put("total", users.size());

        return ResponseEntity.ok(result);
    }

    /**
     * 获取带头像的用户列表（用于管理员头像审核）
     */
    @GetMapping("/avatars")
    public ResponseEntity<?> getUsersWithAvatar(
            @RequestParam(required = false) String status) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("avatar_path");
        queryWrapper.ne("avatar_path", "");
        
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("avatar_status", status);
        }
        
        queryWrapper.orderByDesc("create_time");

        List<User> users = userMapper.selectList(queryWrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("list", users);
        result.put("total", users.size());

        return ResponseEntity.ok(result);
    }

    /**
     * 获取待审核头像列表
     */
    @GetMapping("/avatars/pending")
    public ResponseEntity<?> getPendingAvatars() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("avatar_path");
        queryWrapper.ne("avatar_path", "");
        queryWrapper.and(wrapper -> wrapper
            .eq("avatar_status", "pending")
            .or()
            .isNull("avatar_status")
        );
        queryWrapper.orderByDesc("create_time");

        List<User> users = userMapper.selectList(queryWrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("list", users);
        result.put("total", users.size());

        return ResponseEntity.ok(result);
    }

    /**
     * 审核通过头像
     */
    @PostMapping("/avatars/{userId}/approve")
    public ResponseEntity<?> approveAvatar(@PathVariable Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("用户不存在"));
        }

        if (user.getAvatarPath() == null || user.getAvatarPath().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(createErrorResponse("用户未上传头像"));
        }

        user.setAvatarStatus("approved");
        userMapper.updateById(user);

        try {
            notificationService.createSystemNotification(
                user.getId(),
                "头像审核通过",
                "恭喜！您的头像已通过审核，现已正常显示。"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(createSuccessResponse("头像审核通过"));
    }

    /**
     * 审核拒绝头像
     */
    @PostMapping("/avatars/{userId}/reject")
    public ResponseEntity<?> rejectAvatar(
            @PathVariable Integer userId,
            @RequestBody Map<String, String> request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("用户不存在"));
        }

        user.setAvatarStatus("rejected");
        // 下架头像（清空路径）
        user.setAvatarPath(null);
        userMapper.updateById(user);

        String reason = request.get("reason");
        try {
            notificationService.createSystemNotification(
                user.getId(),
                "头像审核未通过",
                String.format("很抱歉，您的头像未通过审核。%s", reason != null ? "原因：" + reason : "")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(createSuccessResponse("头像已拒绝并下架"));
    }

    /**
     * 冻结用户
     */
    @PostMapping("/users/{id}/freeze")
    public ResponseEntity<?> freezeUser(@PathVariable Integer id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("用户不存在"));
        }

        user.setStatus("frozen");
        userMapper.updateById(user);

        return ResponseEntity.ok(createSuccessResponse("用户已冻结"));
    }

    /**
     * 解冻用户
     */
    @PostMapping("/users/{id}/unfreeze")
    public ResponseEntity<?> unfreezeUser(@PathVariable Integer id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("用户不存在"));
        }

        user.setStatus("active");
        userMapper.updateById(user);

        return ResponseEntity.ok(createSuccessResponse("用户已解冻"));
    }

    /**
     * 管理员下架用户头像（违规处理）
     */
    @PostMapping("/users/{id}/avatar/remove")
    public ResponseEntity<?> removeUserAvatar(@PathVariable Integer id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("用户不存在"));
        }

        user.setAvatarPath(null);
        user.setAvatarStatus("rejected");
        userMapper.updateById(user);

        try {
            notificationService.createSystemNotification(
                    user.getId(),
                    "头像已被管理员下架",
                    "由于检测到您的头像可能存在违规内容，已被管理员移除，请重新上传合规头像。"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(createSuccessResponse("头像已下架"));
    }

    /**
     * 获取所有向导列表（管理员）
     */
    @GetMapping("/guides")
    public ResponseEntity<?> getAllGuides(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        QueryWrapper<Guide> queryWrapper = new QueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("verification_status", status);
        }
        queryWrapper.orderByDesc("create_time");

        List<Guide> guides = guideMapper.selectList(queryWrapper);

        // 关联用户信息
        List<Map<String, Object>> guideList = new java.util.ArrayList<>();
        for (Guide guide : guides) {
            User user = userMapper.selectById(guide.getUserId());
            Map<String, Object> guideData = new HashMap<>();
            guideData.put("id", guide.getId());
            guideData.put("userId", guide.getUserId());
            guideData.put("nickname", user != null ? user.getNickname() : "");
            guideData.put("email", user != null ? user.getEmail() : "");
            guideData.put("avatarPath", user != null ? user.getAvatarPath() : null);
            guideData.put("avatarStatus", user != null ? user.getAvatarStatus() : null);
            guideData.put("languages", guide.getLanguages());
            guideData.put("cities", guide.getCities());
            guideData.put("tags", guide.getTags());
            guideData.put("bio", guide.getBio());
            guideData.put("hourlyRate", guide.getHourlyRate());
            guideData.put("dailyRate", guide.getDailyRate());
            guideData.put("verificationStatus", guide.getVerificationStatus());
            guideData.put("rejectionReason", guide.getRejectionReason());
            guideData.put("rating", guide.getRating());
            guideData.put("totalReviews", guide.getTotalReviews());
            guideData.put("totalOrders", guide.getTotalOrders());
            guideData.put("totalIncome", guide.getTotalIncome());
            guideData.put("createTime", guide.getCreateTime());
            guideData.put("updateTime", guide.getUpdateTime());
            guideList.add(guideData);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", guideList);
        result.put("total", guideList.size());

        return ResponseEntity.ok(result);
    }

    /**
     * 获取待审核向导列表
     */
    @GetMapping("/guides/pending")
    public ResponseEntity<?> getPendingGuides() {
        QueryWrapper<Guide> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("verification_status", "pending");
        queryWrapper.orderByDesc("create_time");

        List<Guide> guides = guideMapper.selectList(queryWrapper);

        // 关联用户信息
        List<Map<String, Object>> guideList = new java.util.ArrayList<>();
        for (Guide guide : guides) {
            User user = userMapper.selectById(guide.getUserId());
            Map<String, Object> guideData = new HashMap<>();
            guideData.put("id", guide.getId());
            guideData.put("userId", guide.getUserId());
            guideData.put("nickname", user != null ? user.getNickname() : "");
            guideData.put("email", user != null ? user.getEmail() : "");
            guideData.put("avatarPath", user != null ? user.getAvatarPath() : null);
            guideData.put("avatarStatus", user != null ? user.getAvatarStatus() : null);
            guideData.put("languages", guide.getLanguages());
            guideData.put("cities", guide.getCities());
            guideData.put("tags", guide.getTags());
            guideData.put("bio", guide.getBio());
            guideData.put("hourlyRate", guide.getHourlyRate());
            guideData.put("dailyRate", guide.getDailyRate());
            guideData.put("certificates", guide.getCertificates());
            guideData.put("verificationStatus", guide.getVerificationStatus());
            guideData.put("createTime", guide.getCreateTime());
            guideData.put("updateTime", guide.getUpdateTime());
            guideList.add(guideData);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", guideList);
        result.put("total", guideList.size());

        return ResponseEntity.ok(result);
    }

    /**
     * 获取向导详情（管理员）
     */
    @GetMapping("/guides/{id}")
    public ResponseEntity<?> getGuideDetail(@PathVariable Integer id) {
        Guide guide = guideMapper.selectById(id);
        if (guide == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("向导不存在"));
        }

        User user = userMapper.selectById(guide.getUserId());
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", guide.getId());
        result.put("userId", guide.getUserId());
        result.put("nickname", user != null ? user.getNickname() : "");
        result.put("email", user != null ? user.getEmail() : "");
        result.put("languages", guide.getLanguages());
        result.put("cities", guide.getCities());
        result.put("tags", guide.getTags());
        result.put("bio", guide.getBio());
        result.put("hourlyRate", guide.getHourlyRate());
        result.put("dailyRate", guide.getDailyRate());
        result.put("certificates", guide.getCertificates());
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
     * 更新向导信息（管理员）
     */
    @PutMapping("/guides/{id}")
    public ResponseEntity<?> updateGuide(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> updates) {
        
        Guide guide = guideMapper.selectById(id);
        if (guide == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("向导不存在"));
        }

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
        if (updates.containsKey("verificationStatus")) {
            guide.setVerificationStatus((String) updates.get("verificationStatus"));
        }
        if (updates.containsKey("rejectionReason")) {
            guide.setRejectionReason((String) updates.get("rejectionReason"));
        }

        guide.setUpdateTime(LocalDateTime.now());
        guideMapper.updateById(guide);

        return ResponseEntity.ok(guide);
    }

    /**
     * 删除向导（管理员）
     */
    @DeleteMapping("/guides/{id}")
    public ResponseEntity<?> deleteGuide(@PathVariable Integer id) {
        Guide guide = guideMapper.selectById(id);
        if (guide == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("向导不存在"));
        }

        // 检查是否有进行中的订单
        QueryWrapper<Order> orderQuery = new QueryWrapper<>();
        orderQuery.eq("guide_id", guide.getUserId());
        orderQuery.in("status", java.util.Arrays.asList("pending", "confirmed", "in_progress"));
        long activeOrders = orderMapper.selectCount(orderQuery);
        
        if (activeOrders > 0) {
            return ResponseEntity.badRequest().body(createErrorResponse("存在进行中的订单，无法删除向导"));
        }

        guideMapper.deleteById(id);
        return ResponseEntity.ok(createSuccessResponse("向导已删除"));
    }

    /**
     * 审核通过向导
     */
    @PostMapping("/guides/{id}/approve")
    public ResponseEntity<?> approveGuide(@PathVariable Integer id) {
        Guide guide = guideMapper.selectById(id);
        if (guide == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("向导不存在"));
        }

        guide.setVerificationStatus("approved");
        guide.setUpdateTime(LocalDateTime.now());
        guideMapper.updateById(guide);

        try {
            notificationService.notifyGuideForVerificationResult(guide.getUserId(), true, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(createSuccessResponse("审核通过"));
    }

    /**
     * 审核拒绝向导
     */
    @PostMapping("/guides/{id}/reject")
    public ResponseEntity<?> rejectGuide(
            @PathVariable Integer id,
            @RequestBody Map<String, String> request) {

        Guide guide = guideMapper.selectById(id);
        if (guide == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("向导不存在"));
        }

        guide.setVerificationStatus("rejected");
        String reason = request.get("reason");
        guide.setRejectionReason(reason);
        guide.setUpdateTime(LocalDateTime.now());
        guideMapper.updateById(guide);

        try {
            notificationService.notifyGuideForVerificationResult(guide.getUserId(), false, reason);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(createSuccessResponse("已拒绝"));
    }

    /**
     * 获取待审核帖子列表
     */
    @GetMapping("/posts/pending")
    public ResponseEntity<?> getPendingPosts() {
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "pending");
        queryWrapper.orderByDesc("create_time");

        List<Post> posts = postMapper.selectList(queryWrapper);

        // 关联用户信息
        for (Post post : posts) {
            User user = userMapper.selectById(post.getUserId());
            if (user != null) {
                // 可以创建DTO返回完整信息
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", posts);
        result.put("total", posts.size());

        return ResponseEntity.ok(result);
    }

    /**
     * 审核通过帖子
     */
    @PostMapping("/posts/{id}/approve")
    public ResponseEntity<?> approvePost(@PathVariable Integer id) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("帖子不存在"));
        }

        post.setStatus("approved");
        post.setUpdateTime(LocalDateTime.now());

        // 奖励经验值（如果还未奖励）
        if (post.getExpAwarded() == 0) {
            post.setExpAwarded(20);
            // 使用 UserLevelService 增加经验值并自动升级
            userLevelService.addExperience(post.getUserId(), 20);
        }

        postMapper.updateById(post);

        try {
            notificationService.notifyUserForPostVerificationResult(post.getUserId(), true, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(createSuccessResponse("审核通过"));
    }

    /**
     * 审核拒绝帖子
     */
    @PostMapping("/posts/{id}/reject")
    public ResponseEntity<?> rejectPost(
            @PathVariable Integer id,
            @RequestBody Map<String, String> request) {

        Post post = postMapper.selectById(id);
        if (post == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("帖子不存在"));
        }

        post.setStatus("rejected");
        String reason = request.get("reason");
        post.setRejectionReason(reason);
        post.setUpdateTime(LocalDateTime.now());
        postMapper.updateById(post);

        try {
            notificationService.notifyUserForPostVerificationResult(post.getUserId(), false, reason);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(createSuccessResponse("已拒绝"));
    }

    /**
     * 获取所有订单
     */
    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders() {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        List<Order> orders = orderMapper.selectList(queryWrapper);

        // 关联查询用户信息，添加游客和向导的用户名
        List<Map<String, Object>> orderList = orders.stream().map(order -> {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("id", order.getId());
            orderMap.put("touristId", order.getTouristId());
            orderMap.put("guideId", order.getGuideId());
            orderMap.put("orderType", order.getOrderType());
            orderMap.put("routeName", order.getRouteName());
            orderMap.put("routeId", order.getRouteId());
            orderMap.put("startDate", order.getStartDate());
            orderMap.put("endDate", order.getEndDate());
            orderMap.put("participants", order.getParticipants());
            orderMap.put("specialRequirements", order.getSpecialRequirements());
            orderMap.put("totalPrice", order.getTotalPrice());
            orderMap.put("commissionRate", order.getCommissionRate());
            orderMap.put("commissionAmount", order.getCommissionAmount());
            orderMap.put("guideIncome", order.getGuideIncome());
            orderMap.put("status", order.getStatus());
            orderMap.put("cancelReason", order.getCancelReason());
            orderMap.put("rejectReason", order.getRejectReason());
            orderMap.put("createTime", order.getCreateTime());
            orderMap.put("updateTime", order.getUpdateTime());

            // 查询游客信息
            User tourist = userMapper.selectById(order.getTouristId());
            if (tourist != null) {
                orderMap.put("touristName", tourist.getNickname());
                orderMap.put("touristAvatar", tourist.getAvatarPath());
            } else {
                orderMap.put("touristName", "未知用户");
                orderMap.put("touristAvatar", null);
            }

            // 查询向导信息
            User guide = userMapper.selectById(order.getGuideId());
            if (guide != null) {
                orderMap.put("guideName", guide.getNickname());
                orderMap.put("guideAvatar", guide.getAvatarPath());
            } else {
                orderMap.put("guideName", "未知向导");
                orderMap.put("guideAvatar", null);
            }

            return orderMap;
        }).collect(java.util.stream.Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("list", orderList);
        result.put("total", orderList.size());

        return ResponseEntity.ok(result);
    }

    /**
     * 导出订单数据为 Excel (XLS)
     */
    @GetMapping("/orders/export")
    public ResponseEntity<?> exportOrders() {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        List<Order> orders = orderMapper.selectList(queryWrapper);

        try {
            // 创建 Excel 工作簿
            org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("订单数据");

            // 创建表头
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            String[] headers = {"订单ID", "游客ID", "向导ID", "开始日期", "结束日期", "参与人数", "订单类型", "总价", "佣金", "状态", "创建时间"};
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
                org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);
                cell.setCellStyle(headerStyle);
            }

            // 填充数据
            int rowNum = 1;
            for (Order order : orders) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(order.getId() != null ? order.getId() : 0);
                row.createCell(1).setCellValue(order.getTouristId() != null ? order.getTouristId() : 0);
                row.createCell(2).setCellValue(order.getGuideId() != null ? order.getGuideId() : 0);
                row.createCell(3).setCellValue(order.getStartDate() != null ? order.getStartDate().toString() : "");
                row.createCell(4).setCellValue(order.getEndDate() != null ? order.getEndDate().toString() : "");
                row.createCell(5).setCellValue(order.getParticipants() != null ? order.getParticipants() : 0);
                row.createCell(6).setCellValue(order.getOrderType() != null ? order.getOrderType() : "");
                row.createCell(7).setCellValue(order.getTotalPrice() != null ? order.getTotalPrice().doubleValue() : 0);
                row.createCell(8).setCellValue(order.getCommissionAmount() != null ? order.getCommissionAmount().doubleValue() : 0);
                row.createCell(9).setCellValue(order.getStatus() != null ? order.getStatus() : "");
                row.createCell(10).setCellValue(order.getCreateTime() != null ? order.getCreateTime().toString() : "");
            }

            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 写入到字节数组
            java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            // 设置响应头
            org.springframework.http.HttpHeaders responseHeaders = new org.springframework.http.HttpHeaders();
            responseHeaders.setContentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM);
            responseHeaders.setContentDispositionFormData("attachment", 
                "orders_" + java.time.LocalDate.now().toString() + ".xlsx");

            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(createErrorResponse("导出失败: " + e.getMessage()));
        }
    }

    /**
     * 获取图表数据
     */
    @GetMapping("/stats/charts")
    public ResponseEntity<?> getChartData() {
        Map<String, Object> chartData = new HashMap<>();

        // 用户增长趋势（最近7天）
        List<Map<String, Object>> userGrowth = new java.util.ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDateTime startDate = LocalDateTime.now().minusDays(i).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endDate = startDate.plusDays(1);
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.ge("create_time", startDate);
            queryWrapper.lt("create_time", endDate);
            int count = userMapper.selectCount(queryWrapper).intValue();
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", startDate.toLocalDate().toString());
            dayData.put("count", count);
            userGrowth.add(dayData);
        }
        chartData.put("userGrowth", userGrowth);

        // 订单状态分布
        Map<String, Integer> orderStatus = new HashMap<>();
        orderStatus.put("pending", orderMapper.selectCount(
            new QueryWrapper<Order>().eq("status", "pending")).intValue());
        orderStatus.put("confirmed", orderMapper.selectCount(
            new QueryWrapper<Order>().eq("status", "confirmed")).intValue());
        orderStatus.put("in_progress", orderMapper.selectCount(
            new QueryWrapper<Order>().eq("status", "in_progress")).intValue());
        orderStatus.put("completed", orderMapper.selectCount(
            new QueryWrapper<Order>().eq("status", "completed")).intValue());
        orderStatus.put("cancelled", orderMapper.selectCount(
            new QueryWrapper<Order>().eq("status", "cancelled")).intValue());
        chartData.put("orderStatus", orderStatus);

        // 用户角色分布
        Map<String, Integer> userRoles = new HashMap<>();
        userRoles.put("tourist", userMapper.selectCount(
            new QueryWrapper<User>().eq("role", "tourist")).intValue());
        userRoles.put("guide", userMapper.selectCount(
            new QueryWrapper<User>().eq("role", "guide")).intValue());
        userRoles.put("admin", userMapper.selectCount(
            new QueryWrapper<User>().eq("role", "admin")).intValue());
        chartData.put("userRoles", userRoles);

        // 最近7天订单趋势
        List<Map<String, Object>> orderTrend = new java.util.ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDateTime startDate = LocalDateTime.now().minusDays(i).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endDate = startDate.plusDays(1);
            QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
            queryWrapper.ge("create_time", startDate);
            queryWrapper.lt("create_time", endDate);
            int count = orderMapper.selectCount(queryWrapper).intValue();
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", startDate.toLocalDate().toString());
            dayData.put("count", count);
            orderTrend.add(dayData);
        }
        chartData.put("orderTrend", orderTrend);

        return ResponseEntity.ok(chartData);
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
