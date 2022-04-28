package com.hua.community.service;

import com.hua.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

/**
 * @create 2022-04-07 10:04
 */
@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 点赞
     * 第一次点击 赞加一
     * 第二次点击 取消赞
     *
     * @param userId 当前点赞的用户id, 作为value存入SET类型数据结构中
     * @param entityType    点赞的类型 1：帖子 2：评论
     * @param entityId      //实体id, 当type为1时，为帖子的id type为0时，为评论的id
     * @param entityUserId      //发表此帖子或此评论的userId
     */
    public void like(int userId, int entityType, int entityId, int entityUserId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {

                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                //以点赞实体所属的用户id生成key, 记录用户获得赞的数量
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);

                //判断当前点赞的用户是否已经点赞
                Boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);

                //开启事务
                operations.multi();

                if (isMember){  //取消点赞 数量减一
                    operations.opsForSet().remove(entityLikeKey, userId);
                    operations.opsForValue().decrement(userLikeKey);
                } else{     //以当前点赞用户id为value添加到set集合中 点赞数量加一
                    operations.opsForSet().add(entityLikeKey, userId);
                    operations.opsForValue().increment(userLikeKey);
                }

                //执行事务
                return operations.exec();
            }
        });

    }

    /**
     * 查询某实体点赞的数量
     * @param entityType
     * @param entityId
     * @return
     */
    public long findEntityLikeCount(int entityType, int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        Long count = redisTemplate.opsForSet().size(entityLikeKey);
        return count.longValue();
    }

    /**
     * 查询某人对某实体的点赞状态
     * @param userId
     * @param entityType
     * @param entityId
     * @return  1：已点赞 0：未点赞
     */
    public int findEntityLikeStatus(int userId, int entityType, int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }

    /**
     * 查询某个用户获得的赞
     * @param userId
     * @return
     */
    public int findUserLikeCount(int userId){
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count.intValue();
    }

}
