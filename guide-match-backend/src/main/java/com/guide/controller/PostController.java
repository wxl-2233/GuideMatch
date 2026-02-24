package com.guide.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guide.entity.Post;
import com.guide.entity.PostLike;
import com.guide.entity.User;
import com.guide.mapper.PostLikeMapper;
import com.guide.mapper.PostMapper;
import com.guide.mapper.UserMapper;
import com.guide.service.NotificationService;
import com.guide.service.UserLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PostLikeMapper postLikeMapper;

    @Autowired
    private UserLevelService userLevelService;

    @Autowired
    private NotificationService notificationService;

    /**
     * 发布帖子
     */
    @PostMapping("/create")
    public ResponseEntity<?> createPost(
            @RequestAttribute Integer userId,
            @RequestAttribute String role,
            @RequestBody Post post) {

        post.setUserId(userId);
        post.setUserRole(role);
        post.setStatus("pending"); // 待审核
        post.setLikesCount(0);
        post.setViewsCount(0);
        post.setExpAwarded(0);
        post.setCreateTime(LocalDateTime.now());
        post.setUpdateTime(LocalDateTime.now());

        postMapper.insert(post);

        try {
            User user = userMapper.selectById(userId);
            String authorName = user != null ? user.getNickname() : "未知用户";
            notificationService.notifyAdminForPostVerification(post.getId(), authorName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(post);
    }

    /**
     * 获取帖子列表
     */
    @GetMapping("/list")
    public ResponseEntity<?> getPostList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestAttribute(required = false) Integer userId) {

        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "approved");
        queryWrapper.orderByDesc("create_time");
        queryWrapper.last("OFFSET " + (page - 1) * size + " ROWS FETCH NEXT " + size + " ROWS ONLY");

        List<Post> posts = postMapper.selectList(queryWrapper);

        List<Map<String, Object>> postsWithUserInfo = new java.util.ArrayList<>();
        for (Post post : posts) {
            Map<String, Object> postMap = new HashMap<>();
            // 复制帖子基本信息
            postMap.put("id", post.getId());
            postMap.put("userId", post.getUserId());
            postMap.put("userRole", post.getUserRole());
            postMap.put("title", post.getTitle());
            postMap.put("content", post.getContent());
            postMap.put("images", post.getImages());
            postMap.put("tags", post.getTags());
            postMap.put("likesCount", post.getLikesCount());
            postMap.put("viewsCount", post.getViewsCount());
            postMap.put("status", post.getStatus());
            postMap.put("createTime", post.getCreateTime());
            postMap.put("updateTime", post.getUpdateTime());
            
            // 查询用户信息
            User user = userMapper.selectById(post.getUserId());
            if (user != null) {
                postMap.put("nickname", user.getNickname());
                postMap.put("avatarPath", user.getAvatarPath());
                postMap.put("avatarStatus", user.getAvatarStatus());
            } else {
                postMap.put("nickname", "未知用户");
                postMap.put("avatarPath", null);
                postMap.put("avatarStatus", null);
            }
            
            boolean isLiked = false;
            if (userId != null) {
                QueryWrapper<PostLike> likeQuery = new QueryWrapper<>();
                likeQuery.eq("post_id", post.getId());
                likeQuery.eq("user_id", userId);
                PostLike existingLike = postLikeMapper.selectOne(likeQuery);
                isLiked = existingLike != null;
            }
            postMap.put("isLiked", isLiked);
            
            postsWithUserInfo.add(postMap);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", postsWithUserInfo);
        result.put("total", postMapper.selectCount(new QueryWrapper<Post>().eq("status", "approved")));

        return ResponseEntity.ok(result);
    }

    /**
     * 点赞/取消点赞
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<?> toggleLike(
            @RequestAttribute Integer userId,
            @PathVariable Integer id) {

        Post post = postMapper.selectById(id);
        if (post == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("帖子不存在"));
        }

        // 检查是否已点赞
        QueryWrapper<PostLike> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("post_id", id);
        queryWrapper.eq("user_id", userId);
        PostLike existingLike = postLikeMapper.selectOne(queryWrapper);

        Map<String, Object> result = new HashMap<>();
        
        if (existingLike != null) {
            postLikeMapper.deleteById(existingLike.getId());
            post.setLikesCount((post.getLikesCount() == null ? 0 : post.getLikesCount()) - 1);
            if (post.getLikesCount() < 0) {
                post.setLikesCount(0);
            }
            postMapper.updateById(post);
            result.put("liked", false);
        } else {
            PostLike postLike = new PostLike();
            postLike.setPostId(id);
            postLike.setUserId(userId);
            postLike.setCreateTime(LocalDateTime.now());
            postLikeMapper.insert(postLike);
            
            post.setLikesCount((post.getLikesCount() == null ? 0 : post.getLikesCount()) + 1);
            postMapper.updateById(post);
            result.put("liked", true);
        }
        
        result.put("likesCount", post.getLikesCount());
        return ResponseEntity.ok(result);
    }
    
    /**
     * 增加浏览量
     */
    @PostMapping("/{id}/view")
    public ResponseEntity<?> incrementView(@PathVariable Integer id) {
        Post post = postMapper.selectById(id);
        if (post != null) {
            post.setViewsCount((post.getViewsCount() == null ? 0 : post.getViewsCount()) + 1);
            postMapper.updateById(post);
        }
        return ResponseEntity.ok(createSuccessResponse("OK"));
    }
    
    private Map<String, String> createSuccessResponse(String message) {
        Map<String, String> success = new HashMap<>();
        success.put("message", message);
        return success;
    }

    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}
