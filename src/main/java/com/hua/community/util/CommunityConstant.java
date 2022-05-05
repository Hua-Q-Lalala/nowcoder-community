package com.hua.community.util;

/**
 * 常量接口，用于标识激活账号的状态以及其他信息
 * @create 2022-03-25 16:01
 */
public interface CommunityConstant {

    /**
     * ctrl + shift + u 转换大小写
     *激活成功
     */
    int ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    int ACTIVATION_FAILUER = 2;

    /**
     * 默认状态的登录的超时时间
     * 一个小时等于 60分钟 * 60 秒 = 3600
     * 3600 * 12 为十二个小时
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     * 记住状态的登录凭证超时时间
     * 一个小时等于 60分钟 * 60 秒 = 3600
     * 3600 * 24 为一天
     * 3600 * 24 * 100 为100天
     */
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;

    /**
     * 实体类型：帖子
     */

    int ENTITY_TYPE_POST = 1;

    /**
     * 实体类型：评论
     */
    int ENTITY_TYPE_COMMENT = 2;

    /**
     * 实体类型：用户
     */
    int ENTITY_TYPE_USER = 3;

    /**
     * 主题：评论
     */
    String TOPIC_COMMENT = "comment";

    /**
     * 主题：点赞
     */
    String TOPIC_LIKE = "like";

    /**
     * 主题：关注
     */
    String TOPIC_FOLLOW = "follow";

    /**
     * 主题；发帖
     */
    String TOPIC_PUBLISH = "publish";

    /**
     * 系统用户ID
     */
    int SYSTEM_USER_ID = 1;
}
