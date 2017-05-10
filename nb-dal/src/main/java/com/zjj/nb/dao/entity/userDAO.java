package com.zjj.nb.dao.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class userDAO implements Serializable {
    private Integer id;

    private Date gmtCreate;

    private Date gmtModified;

    private String cteater;

    private String modifier;

    private String userName;

    private String userPassword;
}