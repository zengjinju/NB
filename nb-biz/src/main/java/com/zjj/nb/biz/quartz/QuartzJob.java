package com.zjj.nb.biz.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Created by jinju.zeng on 2017/3/13.
 */
public class QuartzJob extends QuartzJobBean {

    @Autowired
    private TwoBean twoBean;
    @Value("${quartz.run}")
    private String quartzRun;


    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        if ("on".equals(quartzRun)) {
            twoBean.out();
        }
    }
}
