package com.guide.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guide.entity.Post;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostMapper extends BaseMapper<Post> {
}
