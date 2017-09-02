package com.zjj.nb.biz.service;

import com.zjj.nb.dao.entity.userDAO;

/**
 * Created by jinju.zeng on 2017/4/12.
 */
public interface UserService {

    userDAO selectByUserNameAndPwd(String userName,String pwd);

    int insert(userDAO userDAO);

    Boolean get(String userName);
}
