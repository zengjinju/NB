package com.zjj.nb.biz.quartz;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Created by admin on 2017/3/13.
 */
@Service
@Slf4j
public class TwoBean {

    public void out(){
        log.info("twoBean TestExample");
        System.out.println("twoBean TestExample");
    }
}
