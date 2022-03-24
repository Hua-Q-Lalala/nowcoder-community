package com.hua.community.dao.impl;

import com.hua.community.dao.AlphaDao;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

/**
 * @create 2022-03-21 20:43
 */
@Repository
@Primary    //当按类型获取bean时，容器中会优先返回有Primary声明的bean
public class AlphaDaoMyBatisImpl implements AlphaDao {
    @Override
    public String select() {
        return "MyBatis.....";
    }
}
