package com.zjj.nb.biz.service;

import com.zjj.nb.dao.entity.userDAO;
import com.zjj.nb.dao.mapper.userDAOMapper;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by admin on 2017/4/12.
 */
@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private userDAOMapper userdaoMapper;

    @Override
    public userDAO selectByUserNameAndPwd(String userName, String pwd) {
        return userdaoMapper.selectByNameAndPwd(userName,pwd);
    }

    @Transactional
    @Override
    public int insert(userDAO userDAO) {
        userDAO.setUserName("abc");
        return userdaoMapper.insertSelective(userDAO);
    }


}
