package com.guide.service;

import com.guide.entity.User;
import com.guide.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserLevelService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 计算升级所需经验值
     * 公式：下一级所需经验 = 当前等级 * 100
     */
    private int getExpRequiredForNextLevel(int currentLevel) {
        return currentLevel * 100;
    }

    /**
     * 增加经验值并检查是否升级
     */
    public void addExperience(Integer userId, int expToAdd) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return;
        }

        int currentExp = user.getExp() != null ? user.getExp() : 0;
        int currentLevel = user.getLv() != null ? user.getLv() : 1;
        
        int newExp = currentExp + expToAdd;
        int newLevel = currentLevel;
        boolean leveledUp = false;

        // 检查是否可以升级
        while (newExp >= getExpRequiredForNextLevel(newLevel)) {
            newExp -= getExpRequiredForNextLevel(newLevel);
            newLevel++;
            leveledUp = true;
        }

        // 更新用户信息
        user.setExp(newExp);
        user.setLv(newLevel);
        userMapper.updateById(user);

        // 如果升级了，可以在这里触发升级通知或权益更新
        if (leveledUp) {
            // TODO: 发送升级通知
            // TODO: 应用等级权益
        }
    }

    /**
     * 获取当前等级进度百分比
     */
    public double getLevelProgress(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return 0.0;
        }

        int currentExp = user.getExp() != null ? user.getExp() : 0;
        int currentLevel = user.getLv() != null ? user.getLv() : 1;
        int expRequired = getExpRequiredForNextLevel(currentLevel);

        if (expRequired == 0) {
            return 100.0;
        }

        return (double) currentExp / expRequired * 100.0;
    }

    /**
     * 获取等级权益配置
     * @param level 用户等级
     * @return 权益配置Map
     */
    public Map<String, Object> getLevelBenefits(int level) {
        Map<String, Object> benefits = new HashMap<>();
        
        // 曝光率加成：等级越高，曝光率越高（1级=100%, 2级=110%, 3级=120%...）
        double exposureBoost = 1.0 + (level - 1) * 0.1;
        benefits.put("exposureBoost", Math.min(exposureBoost, 2.0)); // 最高200%
        
        // 接单权限：等级越高，可同时接单数量越多
        int maxConcurrentOrders = Math.min(3 + (level - 1) / 2, 10); // 1级=3单, 3级=4单, 5级=5单...最高10单
        benefits.put("maxConcurrentOrders", maxConcurrentOrders);
        
        // 接单优先级：等级越高，优先级越高（用于订单分配）
        int priority = level * 10; // 1级=10, 2级=20, 3级=30...
        benefits.put("priority", priority);
        
        // 优惠券奖励：达到特定等级时给予优惠券（简化处理，返回等级对应的优惠券数量）
        int couponCount = 0;
        if (level >= 5) couponCount = 1; // 5级获得1张优惠券
        if (level >= 10) couponCount = 2; // 10级获得2张优惠券
        if (level >= 20) couponCount = 5; // 20级获得5张优惠券
        benefits.put("couponCount", couponCount);
        
        return benefits;
    }

    /**
     * 获取用户的曝光率加成
     */
    public double getExposureBoost(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return 1.0;
        }
        int level = user.getLv() != null ? user.getLv() : 1;
        Map<String, Object> benefits = getLevelBenefits(level);
        return (Double) benefits.get("exposureBoost");
    }

    /**
     * 获取用户的最大并发接单数
     */
    public int getMaxConcurrentOrders(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return 3; // 默认3单
        }
        int level = user.getLv() != null ? user.getLv() : 1;
        Map<String, Object> benefits = getLevelBenefits(level);
        return (Integer) benefits.get("maxConcurrentOrders");
    }

    /**
     * 获取用户的接单优先级
     */
    public int getOrderPriority(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return 10; // 默认优先级
        }
        int level = user.getLv() != null ? user.getLv() : 1;
        Map<String, Object> benefits = getLevelBenefits(level);
        return (Integer) benefits.get("priority");
    }
}
