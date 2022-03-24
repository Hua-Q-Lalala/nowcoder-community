package com.hua.community.dao.impl;

import com.hua.community.dao.AlphaDao;
import org.springframework.stereotype.Repository;

/**
 * @create 2022-03-21 20:40
 */

@Repository(value = "alphaDaoHibernate")
public class AlphaDaoHibernateImpl implements AlphaDao {
    @Override
    public String select() {
        return "Hibernate....";
    }
}
