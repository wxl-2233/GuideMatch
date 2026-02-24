package com.guide.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("withdrawals")
public class Withdrawal {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField("guide_id")
    private Integer guideId;
    
    private BigDecimal amount;
    
    @TableField("bank_name")
    private String bankName;
    
    @TableField("bank_account")
    private String bankAccount;
    
    @TableField("account_holder")
    private String accountHolder;
    
    private String status; // pending, approved, rejected, completed
    
    @TableField("rejection_reason")
    private String rejectionReason;
    
    @TableField("process_time")
    private LocalDateTime processTime;
    
    @TableField("create_time")
    private LocalDateTime createTime;
}
