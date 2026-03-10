package com.guide.service.impl;

import com.guide.entity.MemberPoints;
import com.guide.entity.PointsTransaction;
import com.guide.service.MemberService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class MemberServiceImpl implements MemberService {

    // 模拟数据库存储
    private static final Map<Long, MemberPoints> memberDatabase = new HashMap<>();
    private static final List<PointsTransaction> transactionDatabase = new ArrayList<>();

    @Override
    public MemberPoints getMemberInfo(Long userId) {
        return memberDatabase.computeIfAbsent(userId, id -> {
            MemberPoints member = new MemberPoints();
            member.setUserId(id);
            member.setPoints(0);
            member.setTotalPoints(0);
            member.setMemberLevel("NORMAL");
            member.setLevelUpdatedAt(LocalDateTime.now());
            member.setLastActiveTime(LocalDateTime.now());
            member.setHasGoodReputation(true);
            member.setCreatedAt(LocalDateTime.now());
            member.setUpdatedAt(LocalDateTime.now());
            return member;
        });
    }

    @Override
    public List<PointsTransaction> getPointsTransactions(Long userId, int page, int size) {
        return transactionDatabase.stream()
                .filter(t -> t.getUserId().equals(userId))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .skip((page - 1) * size)
                .limit(size)
                .toList();
    }

    @Override
    public void earnPointsFromConsumption(Long userId, String orderId, Double amount) {
        Integer pointsEarned = (int) (amount * 0.5); // 消费用的50%作为积分
        
        MemberPoints member = getMemberInfo(userId);
        member.setPoints(member.getPoints() + pointsEarned);
        member.setTotalPoints(member.getTotalPoints() + pointsEarned);
        member.setUpdatedAt(LocalDateTime.now());
        
        // 记录交易
        PointsTransaction transaction = new PointsTransaction();
        transaction.setUserId(userId);
        transaction.setPoints(pointsEarned);
        transaction.setTransactionType("CONSUMPTION");
        transaction.setDescription("消费获得积分 - 订单号: " + orderId);
        transaction.setRelatedOrderId(orderId);
        transaction.setCreatedAt(LocalDateTime.now());
        transactionDatabase.add(transaction);
        
        // 检查升级
        checkAndUpgradeMemberLevel(userId, member);
    }

    @Override
    public void earnPointsFromInteraction(Long userId, String interactionType, String relatedId) {
        Integer pointsEarned = 50; // 每次互动获得50积分
        
        MemberPoints member = getMemberInfo(userId);
        member.setPoints(member.getPoints() + pointsEarned);
        member.setTotalPoints(member.getTotalPoints() + pointsEarned);
        member.setUpdatedAt(LocalDateTime.now());
        
        // 记录交易
        PointsTransaction transaction = new PointsTransaction();
        transaction.setUserId(userId);
        transaction.setPoints(pointsEarned);
        transaction.setTransactionType(interactionType);
        transaction.setDescription(getInteractionDescription(interactionType, relatedId));
        if ("REVIEW".equals(interactionType)) {
            transaction.setRelatedGuideId(relatedId);
        }
        transaction.setCreatedAt(LocalDateTime.now());
        transactionDatabase.add(transaction);
        
        // 检查升级
        checkAndUpgradeMemberLevel(userId, member);
    }

    @Override
    public Map<String, Object> checkLevelUpgrade(Long userId) {
        MemberPoints member = getMemberInfo(userId);
        Map<String, Object> result = new HashMap<>();
        
        String currentLevel = member.getMemberLevel();
        String newLevel = calculateMemberLevel(member.getTotalPoints());
        
        result.put("currentLevel", currentLevel);
        result.put("newLevel", newLevel);
        result.put("currentPoints", member.getTotalPoints());
        result.put("canUpgrade", !currentLevel.equals(newLevel));
        result.put("nextLevelPoints", getPointsForNextLevel(currentLevel));
        
        if (!currentLevel.equals(newLevel)) {
            member.setMemberLevel(newLevel);
            member.setLevelUpdatedAt(LocalDateTime.now());
            member.setUpdatedAt(LocalDateTime.now());
        }
        
        return result;
    }

    @Override
    public Map<String, Object> getMemberBenefits() {
        Map<String, Object> benefits = new HashMap<>();
        
        Map<String, Object> basic = new HashMap<>();
        basic.put("专属客服", true);
        basic.put("紧急求助优先", true);
        
        Map<String, Object> level = new HashMap<>();
        level.put("V1_GOLD", Map.of(
            "匹配速度", "提升20%",
            "匹配精度", "提升15%"
        ));
        level.put("V2_DIAMOND", Map.of(
            "匹配速度", "提升50%",
            "匹配精度", "提升40%"
        ));
        level.put("V3_BLACK_DIAMOND", Map.of(
            "匹配速度", "提升100%",
            "匹配精度", "提升80%"
        ));
        
        benefits.put("basic", basic);
        benefits.put("level", level);
        
        return benefits;
    }

    private String calculateMemberLevel(Integer totalPoints) {
        if (totalPoints >= 30000) {
            return "V3_BLACK_DIAMOND";
        } else if (totalPoints >= 10000) {
            return "V2_DIAMOND";
        } else if (totalPoints >= 1000) {
            return "V1_GOLD";
        } else {
            return "NORMAL";
        }
    }

    private Integer getPointsForNextLevel(String currentLevel) {
        switch (currentLevel) {
            case "NORMAL":
                return 1000;
            case "V1_GOLD":
                return 10000;
            case "V2_DIAMOND":
                return 30000;
            default:
                return null;
        }
    }

    private void checkAndUpgradeMemberLevel(Long userId, MemberPoints member) {
        String newLevel = calculateMemberLevel(member.getTotalPoints());
        if (!member.getMemberLevel().equals(newLevel)) {
            member.setMemberLevel(newLevel);
            member.setLevelUpdatedAt(LocalDateTime.now());
            member.setUpdatedAt(LocalDateTime.now());
        }
    }

    private String getInteractionDescription(String interactionType, String relatedId) {
        switch (interactionType) {
            case "REVIEW":
                return "导游评价获得积分 - 导游ID: " + relatedId;
            case "COMMUNITY_POST":
                return "社区分享获得积分 - 帖子ID: " + relatedId;
            case "SOCIAL_SHARE":
                return "社交媒体分享获得积分 - 内容ID: " + relatedId;
            default:
                return "互动获得积分";
        }
    }
}
