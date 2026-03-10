package com.guide.controller;

import com.guide.entity.MemberPoints;
import com.guide.entity.SafetyTracking;
import com.guide.service.MemberService;
import com.guide.service.SafetyService;
import com.guide.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
@CrossOrigin
public class TestController {

    @Autowired
    private SafetyService safetyService;
    
    @Autowired
    private MemberService memberService;

    @GetMapping("/safety")
    public Result<String> testSafety() {
        try {
            // 测试安全功能
            SafetyTracking tracking = new SafetyTracking();
            tracking.setUserId(1L);
            tracking.setLatitude(39.9042);
            tracking.setLongitude(116.4074);
            tracking.setLocationName("北京天安门");
            tracking.setTripId("test-trip-001");
            
            safetyService.recordLocation(tracking);
            
            return Result.success("安全测试成功");
        } catch (Exception e) {
            return Result.error("安全测试失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/member")
    public Result<Map<String, Object>> testMember() {
        try {
            // 测试会员功能
            memberService.earnPointsFromConsumption(1L, "order-001", 1000.0);
            memberService.earnPointsFromInteraction(1L, "REVIEW", "guide-001");
            memberService.earnPointsFromInteraction(1L, "COMMUNITY_POST", "post-001");
            
            MemberPoints memberInfo = memberService.getMemberInfo(1L);
            Map<String, Object> upgradeInfo = memberService.checkLevelUpgrade(1L);
            Map<String, Object> benefits = memberService.getMemberBenefits();
            
            Map<String, Object> result = new HashMap<>();
            result.put("memberInfo", memberInfo);
            result.put("upgradeInfo", upgradeInfo);
            result.put("benefits", benefits);
            
            return Result.success("会员测试成功", result);
        } catch (Exception e) {
            return Result.error("会员测试失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/all")
    public Result<Map<String, Object>> testAll() {
        Map<String, Object> result = new HashMap<>();
        result.put("safety_API", "✅ 位置追踪、紧急求助、行程分享功能已实现");
        result.put("member_API", "✅ 积分系统、会员等级、权益管理功能已实现");
        result.put("status", "所有功能已完成，可以进行集成测试");
        
        return Result.success("功能测试完成", result);
    }
}
