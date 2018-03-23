package com.zjj.nb.biz.service;

import com.zjj.nb.dao.entity.userDAO;
import com.zjj.nb.dao.mapper.userDAOMapper;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

/**
 * Created by admin on 2017/4/12.
 */
@Service
public class UserServiceImpl implements UserService,ApplicationContextAware,BeanFactoryAware,ResourceLoaderAware,BeanClassLoaderAware{

    @Autowired
    private userDAOMapper userdaoMapper;

    private ApplicationContext applicationContext;
    private BeanFactory beanFactory;
    private ResourceLoader resourceLoader;


    @PostConstruct
    private void beforeInit(){
//        UserService userService=(UserService) beanFactory.getBean(this.getClass());
//        userService.toString();

    }

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

    @Override
    public Boolean get(String userName) {
        return null;
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory=beanFactory;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader=resourceLoader;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
    }
}
