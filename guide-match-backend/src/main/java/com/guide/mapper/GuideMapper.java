package com.guide.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guide.entity.Guide;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GuideMapper extends BaseMapper<Guide> {
}
