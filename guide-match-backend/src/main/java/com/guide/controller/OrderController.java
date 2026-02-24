package com.guide.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guide.entity.Guide;
import com.guide.entity.GuideCalendar;
import com.guide.entity.Order;
import com.guide.entity.Review;
import com.guide.entity.User;
import com.guide.mapper.GuideCalendarMapper;
import com.guide.mapper.GuideMapper;
import com.guide.mapper.OrderMapper;
import com.guide.mapper.ReviewMapper;
import com.guide.mapper.UserMapper;
import com.guide.service.UserLevelService;
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
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private GuideMapper guideMapper;

    @Autowired
    private GuideCalendarMapper guideCalendarMapper;

    @Autowired
    private UserLevelService userLevelService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ReviewMapper reviewMapper;

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(
            @RequestAttribute Integer userId,
            @RequestBody Map<String, Object> request) {

        Integer guideId = (Integer) request.get("guideId");
        if (guideId == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("向导ID不能为空"));
        }

        LocalDate startDate = LocalDate.parse((String) request.get("startDate"));
        LocalDate endDate = LocalDate.parse((String) request.get("endDate"));
        
        if (startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest().body(createErrorResponse("开始日期不能晚于结束日期"));
        }
        
        if (startDate.isBefore(LocalDate.now())) {
            return ResponseEntity.badRequest().body(createErrorResponse("开始日期不能早于今天"));
        }

        // 检查日期冲突
        QueryWrapper<Order> conflictQuery = new QueryWrapper<>();
        conflictQuery.eq("guide_id", guideId);
        conflictQuery.in("status", java.util.Arrays.asList("confirmed", "in_progress"));
        conflictQuery.and(wrapper -> wrapper
            .and(w -> w.le("start_date", endDate).ge("end_date", startDate))
        );
        List<Order> conflictingOrders = orderMapper.selectList(conflictQuery);
        
        if (!conflictingOrders.isEmpty()) {
            return ResponseEntity.badRequest().body(createErrorResponse("该时间段内向导已有其他订单，请选择其他日期"));
        }

        // 检查接单权限
        User guideUser = userMapper.selectById(guideId);
        if (guideUser == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("向导不存在"));
        }
        int maxConcurrentOrders = userLevelService.getMaxConcurrentOrders(guideId);
        QueryWrapper<Order> activeOrderQuery = new QueryWrapper<>();
        activeOrderQuery.eq("guide_id", guideId);
        activeOrderQuery.in("status", java.util.Arrays.asList("pending", "confirmed", "in_progress"));
        long activeOrderCount = orderMapper.selectCount(activeOrderQuery);
        if (activeOrderCount >= maxConcurrentOrders) {
            int guideLevel = guideUser.getLv() != null ? guideUser.getLv() : 1;
            return ResponseEntity.badRequest().body(createErrorResponse(
                String.format("向导当前已有 %d 个进行中的订单，已达到最大接单数限制（等级 %d 级最多 %d 单），请等待订单完成后再下单", 
                    activeOrderCount, guideLevel, maxConcurrentOrders)));
        }

        // 检查日历可用性
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            QueryWrapper<GuideCalendar> calendarQuery = new QueryWrapper<>();
            calendarQuery.eq("guide_id", guideId);
            calendarQuery.eq("date", currentDate);
            GuideCalendar calendar = guideCalendarMapper.selectOne(calendarQuery);
            if (calendar != null) {
                String status = calendar.getStatus();
                if ("unavailable".equals(status)) {
                    return ResponseEntity.badRequest().body(createErrorResponse("日期 " + currentDate + " 向导请假不可用，请选择其他日期"));
                }
                if ("booked".equals(status)) {
                    return ResponseEntity.badRequest().body(createErrorResponse("日期 " + currentDate + " 向导已约满，请选择其他日期"));
                }
            }
            currentDate = currentDate.plusDays(1);
        }

        Order order = new Order();
        order.setTouristId(userId);
        order.setGuideId(guideId);
        order.setOrderType((String) request.get("orderType"));
        order.setStartDate(startDate);
        order.setEndDate(endDate);
        order.setParticipants((Integer) request.get("participants"));
        order.setSpecialRequirements((String) request.get("specialRequirements"));
        order.setTotalPrice(new BigDecimal(request.get("totalPrice").toString()));
        order.setCommissionRate(new BigDecimal("0.10"));
        
        BigDecimal commissionAmount = order.getTotalPrice().multiply(order.getCommissionRate());
        order.setCommissionAmount(commissionAmount);
        order.setGuideIncome(order.getTotalPrice().subtract(commissionAmount));
        
        order.setStatus("pending");
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        orderMapper.insert(order);

        return ResponseEntity.ok(order);
    }
    
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(
            @RequestAttribute Integer userId,
            @PathVariable Integer id,
            @RequestBody Map<String, String> request) {

        Order order = orderMapper.selectById(id);
        if (order == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("订单不存在"));
        }

        if (!order.getTouristId().equals(userId)) {
            return ResponseEntity.badRequest().body(createErrorResponse("无权操作此订单"));
        }

        if (!"pending".equals(order.getStatus()) && !"confirmed".equals(order.getStatus())) {
            return ResponseEntity.badRequest().body(createErrorResponse("订单状态不允许取消"));
        }

        order.setStatus("cancelled");
        order.setCancelReason(request.get("reason"));
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);

        return ResponseEntity.ok(order);
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<?> acceptOrder(
            @RequestAttribute Integer userId,
            @PathVariable Integer id) {

        Order order = orderMapper.selectById(id);
        if (order == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("订单不存在"));
        }

        if (!order.getGuideId().equals(userId)) {
            return ResponseEntity.badRequest().body(createErrorResponse("无权操作此订单"));
        }

        if (!"pending".equals(order.getStatus())) {
            return ResponseEntity.badRequest().body(createErrorResponse("订单状态不允许接单"));
        }

        order.setStatus("confirmed");
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);

        return ResponseEntity.ok(order);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectOrder(
            @RequestAttribute Integer userId,
            @PathVariable Integer id,
            @RequestBody Map<String, String> request) {

        Order order = orderMapper.selectById(id);
        if (order == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("订单不存在"));
        }

        if (!order.getGuideId().equals(userId)) {
            return ResponseEntity.badRequest().body(createErrorResponse("无权操作此订单"));
        }

        if (!"pending".equals(order.getStatus())) {
            return ResponseEntity.badRequest().body(createErrorResponse("订单状态不允许拒单"));
        }

        order.setStatus("cancelled");
        order.setRejectReason(request.get("reason"));
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);

        return ResponseEntity.ok(order);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getOrderList(
            @RequestAttribute Integer userId,
            @RequestAttribute String role,
            @RequestParam(required = false) String status) {

        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        
        if ("tourist".equals(role)) {
            queryWrapper.eq("tourist_id", userId);
        } else if ("guide".equals(role)) {
            queryWrapper.eq("guide_id", userId);
        }

        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }

        queryWrapper.orderByDesc("create_time");
        List<Order> orders = orderMapper.selectList(queryWrapper);

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

            User tourist = userMapper.selectById(order.getTouristId());
            if (tourist != null) {
                orderMap.put("touristName", tourist.getNickname());
                orderMap.put("touristAvatar", tourist.getAvatarPath());
            } else {
                orderMap.put("touristName", "未知用户");
                orderMap.put("touristAvatar", null);
            }

            User guide = userMapper.selectById(order.getGuideId());
            if (guide != null) {
                orderMap.put("guideName", guide.getNickname());
                orderMap.put("guideAvatar", guide.getAvatarPath());
            } else {
                orderMap.put("guideName", "未知向导");
                orderMap.put("guideAvatar", null);
            }

            QueryWrapper<Review> reviewQuery = new QueryWrapper<>();
            reviewQuery.eq("order_id", order.getId());
            Review review = reviewMapper.selectOne(reviewQuery);
            if (review != null) {
                Map<String, Object> reviewMap = new HashMap<>();
                reviewMap.put("id", review.getId());
                reviewMap.put("rating", review.getRating());
                reviewMap.put("comment", review.getComment());
                reviewMap.put("createTime", review.getCreateTime());
                
                User reviewer = userMapper.selectById(review.getTouristId());
                if (reviewer != null) {
                    reviewMap.put("nickname", reviewer.getNickname());
                    reviewMap.put("avatarPath", reviewer.getAvatarPath());
                    reviewMap.put("avatarStatus", reviewer.getAvatarStatus());
                }
                
                orderMap.put("review", reviewMap);
                orderMap.put("reviewed", true);
            } else {
                orderMap.put("review", null);
                orderMap.put("reviewed", false);
            }

            return orderMap;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("list", orderList);
        result.put("total", orderList.size());

        return ResponseEntity.ok(result);
    }

    /**
     * 开始订单（将订单状态从"已确认"改为"进行中"）
     */
    @PostMapping("/{id}/start")
    public ResponseEntity<?> startOrder(
            @RequestAttribute Integer userId,
            @PathVariable Integer id) {

        Order order = orderMapper.selectById(id);
        if (order == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("订单不存在"));
        }

        if (!order.getGuideId().equals(userId)) {
            return ResponseEntity.badRequest().body(createErrorResponse("无权操作此订单"));
        }

        if (!"confirmed".equals(order.getStatus())) {
            return ResponseEntity.badRequest().body(createErrorResponse("订单状态不允许开始"));
        }

        order.setStatus("in_progress");
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);

        return ResponseEntity.ok(order);
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<?> completeOrder(
            @RequestAttribute Integer userId,
            @PathVariable Integer id) {

        Order order = orderMapper.selectById(id);
        if (order == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("订单不存在"));
        }

        if (!order.getGuideId().equals(userId)) {
            return ResponseEntity.badRequest().body(createErrorResponse("无权操作此订单"));
        }

        if (!"in_progress".equals(order.getStatus())) {
            return ResponseEntity.badRequest().body(createErrorResponse("订单状态不允许完成"));
        }

        order.setStatus("completed");
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);

        Guide guide = guideMapper.selectOne(
            new QueryWrapper<Guide>().eq("user_id", order.getGuideId()));
        if (guide != null) {
            guide.setTotalIncome(guide.getTotalIncome().add(order.getGuideIncome()));
            guide.setTotalOrders(guide.getTotalOrders() + 1);
            guideMapper.updateById(guide);
        }

        userLevelService.addExperience(order.getTouristId(), 30); // 游客完成订单 +30
        userLevelService.addExperience(order.getGuideId(), 30); // 向导完成订单 +30

        return ResponseEntity.ok(order);
    }

    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}
