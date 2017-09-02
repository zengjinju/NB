package com.zjj.nb.dao.mapper;

import com.zjj.nb.dao.entity.userDAO;
import org.apache.ibatis.annotations.Param;

public interface userDAOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(userDAO record);

    int insertSelective(userDAO record);

    userDAO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(userDAO record);

    userDAO selectByNameAndPwd(@Param("userName") String userName, @Param("password") String password);

    int updateId();
}