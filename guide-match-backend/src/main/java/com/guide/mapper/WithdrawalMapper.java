package com.guide.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guide.entity.Withdrawal;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WithdrawalMapper extends BaseMapper<Withdrawal> {
}
