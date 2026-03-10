package com.guide.service;

import com.guide.entity.MemberPoints;
import com.guide.entity.PointsTransaction;
import java.util.List;
import java.util.Map;

public interface MemberService {
    MemberPoints getMemberInfo(Long userId);
    List<PointsTransaction> getPointsTransactions(Long userId, int page, int size);
    void earnPointsFromConsumption(Long userId, String orderId, Double amount);
    void earnPointsFromInteraction(Long userId, String interactionType, String relatedId);
    Map<String, Object> checkLevelUpgrade(Long userId);
    Map<String, Object> getMemberBenefits();
}
