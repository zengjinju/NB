package com.zjj.nb.biz.quartz;

import com.zjj.nb.biz.util.DateUtil;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

/**
 * Created by admin on 2017/6/27.
 */
@Service
public class DateTimeBean {

    private static final DateTime INIT_TIME=new DateTime(2017,5,17,0,0,0);

    public void quartzOut(){
        DateTime now=DateTime.now();
        int days= DateUtil.betweenDays(INIT_TIME.toDate(),now.toDate());
    }
}
