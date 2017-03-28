package com.zjj.nb.biz.quartz;

import lombok.Setter;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Created by jinju.zeng on 2017/3/13.
 */
public class QuartzJob extends QuartzJobBean {

    @Setter
    private TwoBean twoBean;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        twoBean.out();
    }
}
