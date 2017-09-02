package com.zjj.nb.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.zjj.nb.biz.manager.redis.LockCallBack;
import com.zjj.nb.biz.manager.redis.RedisLock;
import com.zjj.nb.biz.manager.threadPool.ThreadPool;
import com.zjj.nb.biz.mq.MQConsumer;
import com.zjj.nb.biz.mq.MqCallBack;
import com.zjj.nb.biz.service.UserService;
import com.zjj.nb.biz.service.icbctransfer.PayEntService;
import com.zjj.nb.biz.service.icbctransfer.SettleMentDO;
import com.zjj.nb.biz.service.icbctransfer.TransferConstantsService;
import com.zjj.nb.biz.util.BeanUtil;
import com.zjj.nb.biz.util.IPUtils;
import com.zjj.nb.biz.util.applicationcontext.ApplicationContextHelper;
import com.zjj.nb.biz.util.applicationcontext.ApplicationContextUtil;
import com.zjj.nb.dao.entity.userDAO;
import com.zjj.nb.dao.mapper.userDAOMapper;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by jinju.zeng on 17/2/22.
 */
@Controller
@RequestMapping("demo")
public class DemoTestController {

    @Autowired
    private RedisLock redisLock;
    @Autowired
    private userDAOMapper userdaoMapper;
    @Autowired
    private MQConsumer mqConsumer;
    @Autowired
    private PayEntService payEntService;
    @Autowired
    private userDAOMapper userDAOMapper;

    @RequestMapping("test")
    public void test() {
        //DemoTestController userService= (DemoTestController) ApplicationContextHelper.getInstance().getBean(this.getClass());
        Map<String,UserService> map=ApplicationContextHelper.getInstance().getBeanOfType(UserService.class);
        System.out.println(map);
        System.out.println(ApplicationContextUtil.getBean("userServiceImpl"));
    }

    @RequestMapping("lock")
    public void lockTest() {
        Object obj = redisLock.lock("zjj", new LockCallBack() {
            @Override
            public Object runTask() {
                return testTime();
            }
        });
    }

    public Integer testTime() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 1;
    }

    @RequestMapping("insert")
    public void insert() {
        userDAO userdao = new userDAO();
        userdao.setUserName("zjj");
        userdao.setUserPassword("123456");
        userdao.setGmtCreate(DateTime.now().toDate());
        userdaoMapper.insertSelective(userdao);
    }

    @RequestMapping("get")
    @ResponseBody
    public Object get() {
        userDAO userdao = userdaoMapper.selectByNameAndPwd("abc", "123456");
        System.out.println(userdao);
        return userdao;
    }

    @RequestMapping(value = "post", method = RequestMethod.POST)
    @ResponseBody
    public Object post() {
        userDAO userdao = userdaoMapper.selectByNameAndPwd("abc", "123456");
        return userdao;
    }

    @RequestMapping("index")
    public String index() {
        return "index";
    }

    @RequestMapping("ip")
    public void testIp(HttpServletRequest request) {
        String ip = IPUtils.getRemoteAddress(request);
        System.out.println(ip);
    }

    @RequestMapping("mq")
    public void testMq() throws MQClientException {
        mqConsumer.register(new MqCallBack() {
            @Override
            public String getTags() {
                return "TagA";
            }

            @Override
            public void doCallBack() {
                userDAO userdao = userdaoMapper.selectByNameAndPwd("abc", "123456");
                System.out.println(JSON.toJSONString(userdao));
            }
        });
    }

    @RequestMapping("icbctransfer")
    public void transfer() {
        List<SettleMentDO> list=new ArrayList<>();
        SettleMentDO settleMentDO = new SettleMentDO();
        settleMentDO.setTransCode("PAYENT");
        settleMentDO.setCis("120290001066991");
        settleMentDO.setBankCode("102");
        settleMentDO.setId("201701.y.1202");
        settleMentDO.setTranDate(new Date());
        settleMentDO.setTranTime(new Date());
        settleMentDO.setPackageId(TransferConstantsService.getPackageId());
        settleMentDO.setOnlBatF("1");
        settleMentDO.setSettleMode("0");
        settleMentDO.setTotalNum("1");
        settleMentDO.setTotalAmt("1000");
        settleMentDO.setSignTime(new Date());
        settleMentDO.setiSeqno("1");
        settleMentDO.setPayType("2");
        settleMentDO.setPayAccNo("1202022719927388888");
        settleMentDO.setPayAccNameCN("时贱醇恍剃牙桔好猜迹灿描寂件粉");
        //settleMentDO.setRecAccNo("6214835715666192");
        settleMentDO.setRecAccNo("1202002119900011896");
        //settleMentDO.setRecAccNo("6222021202046457728");
        settleMentDO.setRecAccNameCN("时贱醇良操深积芭迹灿件粉");
        settleMentDO.setSysIOFlg("1");
        settleMentDO.setProp("0");
        if(settleMentDO.getSysIOFlg().equals("2")){
            settleMentDO.setProp("1");
            settleMentDO.setRecCityName("杭州");
            settleMentDO.setRecBankNo("");
            settleMentDO.setRecBankName("高新支行");
        }
        settleMentDO.setCurrType("001");
        settleMentDO.setPayAmt("1000");
//        settleMentDO.setRecICBCCode("1202");
        //对私交易的时候要填写下面两个字段中的一个
        //settleMentDO.setUseCode("123");
        if(settleMentDO.getProp().equals("1")) {
            settleMentDO.setUseCN("对私交易");
        }
        list.add(settleMentDO);
        SettleMentDO settleMentDO1=new SettleMentDO();
        BeanUtils.copyProperties(settleMentDO,settleMentDO1);
        settleMentDO1.setRecAccNo("1202020209003406812");
        settleMentDO1.setRecAccNameCN("时贱疤他芭并漏亭迹灿件粉 ");
        settleMentDO1.setiSeqno("2");
        //list.add(settleMentDO1);
        payEntService.dealPayEnt(list);
    }

    @RequestMapping("search")
    public void update(){
        SettleMentDO settleMentDO=new SettleMentDO();
        settleMentDO.setTransCode("QPAYENT");
        settleMentDO.setCis("120290001066991");
        settleMentDO.setBankCode("102");
        settleMentDO.setId("201701.y.1202");
        settleMentDO.setTranDate(new Date());
        settleMentDO.setTranTime(new Date());
        settleMentDO.setOldPackageId("201708310000193");
        settleMentDO.setiSeqno("2");
        settleMentDO.setPackageId(TransferConstantsService.getPackageId());
        payEntService.search(settleMentDO,"CMM790142456");
    }

}
