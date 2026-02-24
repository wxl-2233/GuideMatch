package com.guide.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guide.entity.PasswordResetCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PasswordResetCodeMapper extends BaseMapper<PasswordResetCode> {
    /**
     * 根据邮箱和验证码查询未使用的验证码
     */
    @Select("SELECT * FROM password_reset_codes WHERE email = #{email} AND code = #{code} AND used = 0 AND expire_time > GETDATE() ORDER BY create_time DESC")
    PasswordResetCode findValidCode(@Param("email") String email, @Param("code") String code);
    
    /**
     * 标记验证码为已使用
     */
    @Update("UPDATE password_reset_codes SET used = 1 WHERE id = #{id}")
    int markAsUsed(@Param("id") Integer id);
}

