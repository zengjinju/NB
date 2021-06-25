package com.zjj.nb.biz;

import com.zjj.nb.biz.util.DateUtil;
import com.zjj.nb.biz.util.MD5Util;
import com.zjj.nb.biz.util.http.HttpUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by jinju.zeng on 17/2/24.
 */
public class Test1Class {

    private static final Logger logger = LoggerFactory.getLogger(Test1Class.class);

    private static final String IP = "121.41.120.141";
    private static final String PORT = "8088";
    private static final String USER_CODE = "13524888633";
    private static final String USER_PWD = "leke123456";
    private static final String ACCONUT = "U8cloud";
    private static final String filePath = "/Users/admin/project/leke/TestExample/h1_card_demo.xml";
    // U8C外部交换平台数据传输服务的地址
    private static final String XCHANGE_URL = "/service/XChangeServlet";

    // 友户通获取token的URL
    private static final String YHT_TOKEN_URL = "/service/YhtUserWebServer";

    public static void main(String[] args)  {
        Date[] days = getOrderRange(365,new Date(1602238708*1000L),new Date(1624246199000L));
        System.out.println(days);
    }

    private static String createToken(){
        String pwd_md5 = MD5Util.md5(USER_PWD);
        String pwd_sha = MD5Util.sha(USER_PWD);
        String url = "http://121.41.120.141:8088"+YHT_TOKEN_URL + "?usercode="+USER_CODE + "&md5pwd="+pwd_md5 +"&shapwd="+pwd_sha;
        Header header = new BasicHeader("Content-type","text/xml");
        return HttpUtil.postData(url,null,new Header[]{header});
    }

    private static Map<String,String> getPathParam(String path){
        Map<String,String> map = new HashMap<>();
        if (StringUtils.isEmpty(path)){
            return map;
        }
        String[] values = path.split("\\?");
        if (values.length <=1){
            return map;
        }
        String[] params = values[1].split("&");
        for (String item : params){
            String[] kv = item.split("=");
            map.put(kv[0],kv[1]);
        }
        return map;
    }

    private static int getProcessId(){
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return Integer.valueOf(runtimeMXBean.getName().split("@")[0])
                .intValue();
    }

    private static Date[] getOrderRange(Integer days,Date payTime,Date activeTime){
        if (days <=0){
            return null;
        }
        //按30天（剩余）为区间进行加减项次数拆分，得到N个区间
        List<Integer[]> orderRangeList = new ArrayList<>();
        int length = days % 30 == 0 ? days / 30 : days / 30 + 1;
        for (int i=1;i<=length;i++){
            Integer[] range = new Integer[2];
            range[0] = (i - 1) * 30;
            range[1] = i * 30 - 1;
            if (i == length){
                range[1] = range[1] +1;
            }
            orderRangeList.add(range);
        }
        //获取当前活跃时间所在区间
        int betweenDays = DateUtil.betweenDays(payTime,activeTime);
        Date[] dates = null;
        for (Integer[] item : orderRangeList){
            if (betweenDays >= item[0] && betweenDays <= item[1]){
                dates = new Date[2];
                Date startTime = dateAsStart(new DateTime(payTime).plusDays(item[0]).toDate());
                Date endTime = dateAsEnd(new DateTime(payTime).plusDays(item[1]).toDate());
                dates[0] = startTime;
                dates[1] = endTime;
                break;
            }
        }
        return dates;
    }

    public static Date dateAsStart(Date date){

        Calendar calendarL = Calendar.getInstance();
        calendarL.setTime(date);
        calendarL.set(Calendar.HOUR_OF_DAY, 0);
        calendarL.set(Calendar.MINUTE, 0);
        calendarL.set(Calendar.SECOND, 0);
        return calendarL.getTime();
    }

    /**
     * 获取一天的结束s
     * @param date
     * @return
     */
    public static Date dateAsEnd(Date date){
        Calendar calendarU = Calendar.getInstance();
        calendarU.setTime(date);
        calendarU.set(Calendar.HOUR_OF_DAY, 23);
        calendarU.set(Calendar.MINUTE, 59);
        calendarU.set(Calendar.SECOND, 59);
        return calendarU.getTime();
    }

}
