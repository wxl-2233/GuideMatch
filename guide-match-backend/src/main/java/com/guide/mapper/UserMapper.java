package com.guide.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guide.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    /**
     * 根据昵称查询用户
     */
    @Select("SELECT * FROM users WHERE nickname = #{nickname}")
    User findByNickname(@Param("nickname") String nickname);

    /**
     * 根据 ID 查询用户
     */
    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(@Param("id") Integer id);

    /**
     * 根据邮箱查询用户
     */
    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(@Param("email") String email);

    /**
     * 插入新用户
     */
    @Insert("INSERT INTO users (nickname, password, role, status, full_name, email, phonenumber, avatar_path, lv, exp) " +
        "VALUES (#{nickname}, #{password}, #{role}, #{status}, #{fullName}, #{email}, #{phonenumber}, #{avatarPath}, #{lv}, #{exp})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
}
