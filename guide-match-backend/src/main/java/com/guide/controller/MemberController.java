package com.guide.controller;

import com.guide.entity.MemberPoints;
import com.guide.entity.PointsTransaction;
import com.guide.service.MemberService;
import com.guide.util.JwtUtil;
import com.guide.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/member")
@CrossOrigin
public class MemberController {

    @Autowired
    private MemberService memberService;

    // 获取会员信息
    @GetMapping("/info")
    public Result<MemberPoints> getMemberInfo(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            Long userId = JwtUtil.getUserId(token);
            MemberPoints memberInfo = memberService.getMemberInfo(userId);
            return Result.success(memberInfo);
        } catch (Exception e) {
            return Result.error("获取会员信息失败: " + e.getMessage());
        }
    }

    // 获取积分交易记录
    @GetMapping("/points-transactions")
    public Result<List<PointsTransaction>> getPointsTransactions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            Long userId = JwtUtil.getUserId(token);
            List<PointsTransaction> transactions = memberService.getPointsTransactions(userId, page, size);
            return Result.success(transactions);
        } catch (Exception e) {
            return Result.error("获取积分记录失败: " + e.getMessage());
        }
    }

    // 消费获得积分
    @PostMapping("/earn-points/consumption")
    public Result<String> earnPointsFromConsumption(@RequestBody Map<String, Object> data, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            Long userId = JwtUtil.getUserId(token);
            String orderId = (String) data.get("orderId");
            Double amount = (Double) data.get("amount");
            
            memberService.earnPointsFromConsumption(userId, orderId, amount);
            return Result.success("积分发放成功");
        } catch (Exception e) {
            return Result.error("积分发放失败: " + e.getMessage());
        }
    }

    // 完成互动获得积分
    @PostMapping("/earn-points/interaction")
    public Result<String> earnPointsFromInteraction(@RequestBody Map<String, Object> data, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            Long userId = JwtUtil.getUserId(token);
            String interactionType = (String) data.get("interactionType"); // REVIEW, COMMUNITY_POST, SOCIAL_SHARE
            String relatedId = (String) data.get("relatedId"); // 导游ID或帖子ID
            
            memberService.earnPointsFromInteraction(userId, interactionType, relatedId);
            return Result.success("积分获得成功");
        } catch (Exception e) {
            return Result.error("积分获得失败: " + e.getMessage());
        }
    }

    // 检查会员等级升级
    @PostMapping("/check-level-upgrade")
    public Result<Map<String, Object>> checkLevelUpgrade(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            Long userId = JwtUtil.getUserId(token);
            Map<String, Object> upgradeInfo = memberService.checkLevelUpgrade(userId);
            return Result.success(upgradeInfo);
        } catch (Exception e) {
            return Result.error("检查等级升级失败: " + e.getMessage());
        }
    }

    // 获取会员权益说明
    @GetMapping("/benefits")
    public Result<Map<String, Object>> getMemberBenefits() {
        try {
            Map<String, Object> benefits = memberService.getMemberBenefits();
            return Result.success(benefits);
        } catch (Exception e) {
            return Result.error("获取会员权益失败: " + e.getMessage());
        }
    }
}
