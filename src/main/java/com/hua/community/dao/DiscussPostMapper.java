package com.hua.community.dao;

import com.hua.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @create 2022-03-22 14:05
 */
@Mapper
public interface DiscussPostMapper {

    /**
     * 按照用户id查询帖子，userId为0时 查询所有帖子
     * @param userID
     * @param offset
     * @param limit
     * @return
     */
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    //@Param注解用于给参数别名
    //如果只有一个参数，并且在<IF>里使用，则必须加别名
    /**
     * 按userid统计用户帖子数量
     * userId为0时 统计所有用户帖子
     * @param userId
     * @return
     */
    int selectDiscussPostRows(@Param("userId") int userId);
}
