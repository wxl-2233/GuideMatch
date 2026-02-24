package com.guide.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guide.entity.Guide;
import com.guide.entity.Withdrawal;
import com.guide.mapper.GuideMapper;
import com.guide.mapper.WithdrawalMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/withdrawals")
public class WithdrawalController {

    @Autowired
    private WithdrawalMapper withdrawalMapper;

    @Autowired
    private GuideMapper guideMapper;

    /**
     * 申请提现（申请即成功，无需审核）
     */
    @PostMapping
    public ResponseEntity<?> createWithdrawal(
            @RequestAttribute Integer userId,
            @RequestBody Withdrawal withdrawal) {

        // 查询向导信息
        QueryWrapper<Guide> guideQuery = new QueryWrapper<>();
        guideQuery.eq("user_id", userId);
        Guide guide = guideMapper.selectOne(guideQuery);
        
        if (guide == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("请先创建向导资料"));
        }

        // 计算已提现总金额
        QueryWrapper<Withdrawal> withdrawalQuery = new QueryWrapper<>();
        withdrawalQuery.eq("guide_id", userId);
        withdrawalQuery.eq("status", "completed");
        List<Withdrawal> completedWithdrawals = withdrawalMapper.selectList(withdrawalQuery);
        BigDecimal totalWithdrawn = completedWithdrawals.stream()
            .map(Withdrawal::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 计算可提现金额
        BigDecimal totalIncome = guide.getTotalIncome() != null ? guide.getTotalIncome() : BigDecimal.ZERO;
        BigDecimal availableBalance = totalIncome.subtract(totalWithdrawn);

        // 验证提现金额
        if (withdrawal.getAmount() == null || withdrawal.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body(createErrorResponse("提现金额必须大于0"));
        }

        if (withdrawal.getAmount().compareTo(availableBalance) > 0) {
            return ResponseEntity.badRequest().body(createErrorResponse("提现金额不能超过可提现金额"));
        }

        // 创建提现记录
        withdrawal.setGuideId(userId);
        withdrawal.setStatus("completed"); // 申请即成功，状态设为completed
        withdrawal.setProcessTime(LocalDateTime.now());
        withdrawal.setCreateTime(LocalDateTime.now());
        withdrawalMapper.insert(withdrawal);

        // 注意：这里不更新 Guide.totalIncome，因为 totalIncome 是累计收入
        // 可提现金额 = totalIncome - 已提现总金额（通过查询计算）

        return ResponseEntity.ok(withdrawal);
    }

    /**
     * 获取提现记录
     */
    @GetMapping
    public ResponseEntity<?> getWithdrawals(@RequestAttribute Integer userId) {
        QueryWrapper<Withdrawal> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("guide_id", userId);
        queryWrapper.orderByDesc("create_time");
        List<Withdrawal> withdrawals = withdrawalMapper.selectList(queryWrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("list", withdrawals);
        result.put("total", withdrawals.size());

        return ResponseEntity.ok(result);
    }

    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}
