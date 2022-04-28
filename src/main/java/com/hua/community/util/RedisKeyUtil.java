package com.hua.community.util;

/**
 * 用于生成redis中的key
 * @create 2022-04-07 10:00
 */
public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";


    //某个实体的赞
    //存储格式： like:entity:entityType:entityId -> set(userId)
    public static String getEntityLikeKey(int entityType, int entityid){
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityid;
    }

    //某个用户的赞
    //存储格式： like:user:userId -> String
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    //某个用户关注的实体
    //存储格式： followee:userId:entityType ->zset(entityId, nowTime)
    public static String getFolloweeKey(int userId, int entityType){
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    //某个实体拥有的粉丝
    //存储格式： follower:entityType:entityId ->zset(userId, nowTime)
    public static String getFollowerKey(int entityType, int entityId){
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    //登录验证码
    public static String getKaptcha(String owner){
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    //登录凭证
    public static String getTicketKey(String ticket){
        return PREFIX_TICKET + SPLIT + ticket;
    }

    //用户
    public static String getUserKey(int userId){
        return PREFIX_USER + SPLIT + userId;
    }
}
