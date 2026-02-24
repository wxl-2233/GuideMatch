package com.guide.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guide.entity.Favorite;
import com.guide.entity.Guide;
import com.guide.entity.User;
import com.guide.mapper.FavoriteMapper;
import com.guide.mapper.GuideMapper;
import com.guide.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private GuideMapper guideMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 添加收藏
     */
    @PostMapping
    public ResponseEntity<?> addFavorite(
            @RequestAttribute Integer userId,
            @RequestBody Map<String, Integer> request) {

        Integer guideId = request.get("guideId");
        if (guideId == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("向导ID不能为空"));
        }

        // guideId 是 guide 表的 id，需要转换为 user_id
        Guide guide = guideMapper.selectById(guideId);
        if (guide == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("向导不存在"));
        }

        Integer userIdForGuide = guide.getUserId(); // 获取 user_id

        // 检查是否已收藏（使用 user_id）
        QueryWrapper<Favorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tourist_id", userId);
        queryWrapper.eq("guide_id", userIdForGuide);
        Favorite existing = favoriteMapper.selectOne(queryWrapper);

        if (existing != null) {
            return ResponseEntity.badRequest().body(createErrorResponse("已收藏"));
        }

        Favorite favorite = new Favorite();
        favorite.setTouristId(userId);
        favorite.setGuideId(userIdForGuide); // 存储 user_id
        favoriteMapper.insert(favorite);

        return ResponseEntity.ok(favorite);
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/{guideId}")
    public ResponseEntity<?> removeFavorite(
            @RequestAttribute Integer userId,
            @PathVariable Integer guideId) {

        // guideId 是 guide 表的 id，需要转换为 user_id
        Guide guide = guideMapper.selectById(guideId);
        if (guide == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("向导不存在"));
        }

        Integer userIdForGuide = guide.getUserId(); // 获取 user_id

        QueryWrapper<Favorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tourist_id", userId);
        queryWrapper.eq("guide_id", userIdForGuide); // 使用 user_id 查询
        favoriteMapper.delete(queryWrapper);

        return ResponseEntity.ok(createSuccessResponse("取消收藏成功"));
    }

    /**
     * 获取收藏列表
     */
    @GetMapping
    public ResponseEntity<?> getFavorites(@RequestAttribute Integer userId) {
        QueryWrapper<Favorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tourist_id", userId);
        List<Favorite> favorites = favoriteMapper.selectList(queryWrapper);

        // 关联查询向导和用户信息
        List<Map<String, Object>> favoriteList = new java.util.ArrayList<>();
        for (Favorite favorite : favorites) {
            // favorite.guideId 存储的是 user_id
            // 需要通过 user_id 查询 guide 表获取 guide 的 id
            Guide guide = guideMapper.selectOne(
                new QueryWrapper<Guide>()
                    .eq("user_id", favorite.getGuideId())
            );
            
            if (guide != null) {
                User user = userMapper.selectById(favorite.getGuideId());
                Map<String, Object> favoriteData = new HashMap<>();
                favoriteData.put("id", favorite.getId());
                favoriteData.put("guideId", guide.getId()); // 返回 guide 表的 id
                favoriteData.put("userId", favorite.getGuideId()); // 返回 user_id
                favoriteData.put("nickname", user != null ? user.getNickname() : "");
                favoriteData.put("avatarPath", user != null ? user.getAvatarPath() : null);
                favoriteData.put("createTime", favorite.getCreateTime());
                favoriteList.add(favoriteData);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", favoriteList);
        result.put("total", favoriteList.size());

        return ResponseEntity.ok(result);
    }

    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }

    private Map<String, String> createSuccessResponse(String message) {
        Map<String, String> success = new HashMap<>();
        success.put("message", message);
        return success;
    }
}
