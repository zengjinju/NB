package com.zjj.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

/**
 * Created by admin on 2017/3/13.
 */
public class QuartzJob extends QuartzJobBean {

    private TwoBean twoBean;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        twoBean.out();
    }

    public void setTwoBean(TwoBean twoBean) {
        this.twoBean = twoBean;
    }
}
