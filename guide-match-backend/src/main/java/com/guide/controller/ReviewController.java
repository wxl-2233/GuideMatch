package com.guide.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guide.entity.Guide;
import com.guide.entity.Order;
import com.guide.entity.Review;
import com.guide.entity.User;
import com.guide.mapper.GuideMapper;
import com.guide.mapper.OrderMapper;
import com.guide.mapper.ReviewMapper;
import com.guide.mapper.UserMapper;
import com.guide.service.UserLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private GuideMapper guideMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserLevelService userLevelService;

    @PostMapping("/create")
    public ResponseEntity<?> createReview(
            @RequestAttribute Integer userId,
            @RequestBody Review review) {

        Order order = orderMapper.selectById(review.getOrderId());
        if (order == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("订单不存在"));
        }

        if (!order.getTouristId().equals(userId)) {
            return ResponseEntity.badRequest().body(createErrorResponse("无权评价此订单"));
        }

        if (!"completed".equals(order.getStatus())) {
            return ResponseEntity.badRequest().body(createErrorResponse("只能评价已完成的订单"));
        }

        // 防止重复评价
        QueryWrapper<Review> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", review.getOrderId());
        if (reviewMapper.selectOne(queryWrapper) != null) {
            return ResponseEntity.badRequest().body(createErrorResponse("该订单已评价"));
        }

        review.setTouristId(userId);
        review.setGuideId(order.getGuideId());
        review.setCreateTime(LocalDateTime.now());
        reviewMapper.insert(review);

        updateGuideRating(order.getGuideId());
        userLevelService.addExperience(userId, 10);

        return ResponseEntity.ok(review);
    }

    @GetMapping("/guide/{guideId}")
    public ResponseEntity<?> getGuideReviews(@PathVariable Integer guideId) {
        QueryWrapper<Review> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("guide_id", guideId);
        queryWrapper.orderByDesc("create_time");
        List<Review> reviews = reviewMapper.selectList(queryWrapper);

        List<Map<String, Object>> reviewsWithUserInfo = new java.util.ArrayList<>();
        for (Review review : reviews) {
            Map<String, Object> reviewMap = new HashMap<>();
            reviewMap.put("id", review.getId());
            reviewMap.put("orderId", review.getOrderId());
            reviewMap.put("rating", review.getRating());
            reviewMap.put("comment", review.getComment());
            reviewMap.put("createTime", review.getCreateTime());
            
            User tourist = userMapper.findById(review.getTouristId());
            if (tourist != null) {
                reviewMap.put("nickname", tourist.getNickname());
                reviewMap.put("avatarPath", tourist.getAvatarPath());
                reviewMap.put("avatarStatus", tourist.getAvatarStatus());
            } else {
                reviewMap.put("nickname", "匿名用户");
                reviewMap.put("avatarPath", null);
                reviewMap.put("avatarStatus", null);
            }
            
            reviewsWithUserInfo.add(reviewMap);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", reviewsWithUserInfo);
        result.put("total", reviewsWithUserInfo.size());

        return ResponseEntity.ok(result);
    }

    private void updateGuideRating(Integer guideUserId) {
        QueryWrapper<Review> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("guide_id", guideUserId);
        List<Review> reviews = reviewMapper.selectList(queryWrapper);

        if (reviews.isEmpty()) {
            return;
        }

        double avgRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        // reviews表的guide_id是user_id，不是guide表的id
        QueryWrapper<Guide> guideQuery = new QueryWrapper<>();
        guideQuery.eq("user_id", guideUserId);
        Guide guide = guideMapper.selectOne(guideQuery);
        if (guide != null) {
            guide.setRating(BigDecimal.valueOf(avgRating));
            guide.setTotalReviews(reviews.size());
            guideMapper.updateById(guide);
        }
    }

    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}
