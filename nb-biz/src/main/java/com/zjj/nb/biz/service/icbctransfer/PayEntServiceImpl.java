package com.zjj.nb.biz.service.icbctransfer;

import com.zjj.nb.biz.manager.threadPool.NamedThreadFactory;
import com.zjj.nb.biz.manager.threadPool.ThreadPool;
import com.zjj.nb.biz.util.DateUtil;
import com.zjj.nb.biz.util.http.HttpUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by admin on 2017/8/30.
 */
@Service
public class PayEntServiceImpl implements PayEntService, InitializingBean {

    private static final String CHAR_SET = "GB2312";
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new NamedThreadFactory("ICBCPayEntQuery", false));
    private String sign_url;
    private String transfer_url;

    @Value("${nc_client_ip}")
    private String ncIp;
    @Value("${nc_sign_port}")
    private int sign_port;
    @Value("${nc_transfer_port}")
    private int transfer_port;

    @Override
    public void afterPropertiesSet() throws Exception {
        sign_url = "http://" + ncIp + ":" + sign_port;
        transfer_url = "http://" + ncIp + ":" + transfer_port;
    }

    @Override
    public void dealPayEnt(List<SettleMentDO> settleMentDOs) {
        Assert.notNull(settleMentDOs, "参数不能为空");
        SettleMentDO settleMentDO = settleMentDOs.get(0);
        String payEntXml = TransferConstantsService.settleMentXml(settleMentDOs);
        System.out.println(payEntXml);
        String sign = getSign(payEntXml);
        if (sign == null) {
            throw new RuntimeException("");
        }
        Map<String, String> map = new HashMap<>();
        map.put("Version", "0.0.0.1");
        map.put("TransCode", TransCodeEnum.PAYENT.getValue());
        map.put("BankCode", "102");
        map.put("GroupCIS", "120290001066991");
        map.put("ID", "201701.y.1202");
        map.put("PackageID", settleMentDO.getPackageId());
        map.put("Cert", "");
        map.put("reqData", sign);
        String responseResult = HttpUtil.post(transfer_url + "?userID=201701.y.1202&PackageID=" + settleMentDO.getPackageId() + "&SendTime=" + DateUtil.parseDateToString(new Date(), "yyyyMMddHHmmssSSS"), map, null, CHAR_SET);
        //doDealResponseResult(responseResult, settleMentDO, map, true, null);
        responseResult = unEncryptionForBase64(responseResult);
        System.out.println(responseResult);
    }

    @Override
    public void search(SettleMentDO settleMentDO,String serialNo) {
        Map<String,String> map=new HashMap<>();
        map.put("Version", "0.0.0.1");
        map.put("TransCode", TransCodeEnum.QPAYENT.getValue());
        map.put("BankCode", "102");
        map.put("GroupCIS", "120290001066991");
        map.put("ID", "201701.y.1202");
        map.put("PackageID", settleMentDO.getPackageId());
        map.put("Cert", "");
        map.put("reqData", TransferConstantsService.qPayEntXml(settleMentDO, serialNo));
        String responseStr = HttpUtil.post(transfer_url + "?userID=" + settleMentDO.getId() + "&PackageID=" + settleMentDO.getPackageId() + "&SendTime=" + DateUtil.parseDateToString(new Date(), "yyyyMMddHHmmssSSS"), map, null, "gb2312");
        String result=unEncryptionForBase64(responseStr);
        System.out.println(result);
    }

    private String getSign(String xml) {
        if (xml == null || xml.length() == 0) {
            return null;
        }
        String stt = "";
        byte[] bs = new byte[0];
        try {
            bs = xml.getBytes("GB2312");
            stt = new String(bs, "GB2312");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        try {
            Socket socket = new Socket();
            //设置连接超时时间
            socket.connect(new InetSocketAddress("172.16.21.218", 449), 30000);
            String path = "/";
            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "GB2312"));
            wr.write("POST " + path + " HTTP/1.0\r\n");
            wr.write("Content-Length: " + bs.length + "\r\n");
            wr.write("Content-Type: INFOSEC_SIGN/1.0\r\n");
            wr.write("\r\n");
            wr.write(stt);
            wr.flush();
            BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream(), "gb2312"));
            String txt = null;
            StringBuilder builder = new StringBuilder();
            while ((txt = is.readLine()) != null) {
                builder.append(txt);
            }
            txt = builder.toString();
            System.out.println(txt);
            txt = getSubString(txt, "<sign>", "</sign>");
            return txt;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getSubString(String originalStr, String startStr, String endStr) {
        return originalStr.substring(originalStr.indexOf(startStr) + startStr.length(), originalStr.indexOf(endStr));
    }

    /**
     * base64解码
     *
     * @param str
     * @return
     */
    private String unEncryptionForBase64(String str) {
        int index = str.indexOf("=");
        try {
            return new String(TransferConstantsService.getByteFromBase64(str.substring(index + 1)), CHAR_SET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 处理响应的结果(如果 isPayEnt 为false需要传flag，否则传空)
     *
     * @param responseResult
     * @param settleMentDO
     * @param isPayEnt       true 表示转账，false 表示查询
     * @param flag
     */
    private void doDealResponseResult(String responseResult, SettleMentDO settleMentDO, Map<String, String> map, Boolean isPayEnt, Boolean flag) {
        if (responseResult == null || responseResult.length() == 0) {
            throw new RuntimeException("");
        }
        if (responseResult.contains("errorCode")) {
            int index = responseResult.indexOf("=");
            System.out.println(responseResult.substring(index + 1));
            return;
        }
        //处理响应成功的逻辑
        responseResult = unEncryptionForBase64(responseResult);
        System.out.println(responseResult);
        if (responseResult == null) {
            System.out.println("reqData解码出错");
        }
        String retCode = getSubString(responseResult, "<RetCode>", "</RetCode>");
        if (!"0".equals(retCode)) {
            String retMsg = getSubString(responseResult, "<RetMsg>", "</RetMsg>");
            System.out.println(retMsg);
            if (!isPayEnt) {
                flag = true;
            }
        } else {
            String resultCode = getSubString(responseResult, "<Result>", "</Result>");
            if (ResultStatusEnum.SUCCESS.getValue().equals(resultCode)) {
                System.out.println("转账成功");
                if (!isPayEnt) {
                    flag = true;
                }
            } else if (ResultStatusEnum.REFULE.getValue().equals(resultCode) || ResultStatusEnum.REFULE_GRANT.getValue().equals(resultCode)) {
                String iRetMsg = getSubString(responseResult, "<iRetMsg>", "</iRetMsg>");
                String iRetCode = getSubString(responseResult, "<iRetCode>", "</iRetCode>");
                System.out.println("code:" + iRetCode + "   msg" + iRetMsg);
                if (!isPayEnt) {
                    flag = true;
                }
            } else {
                //转账返回结果为中间状态的时候，发起异步查询操作
                if (isPayEnt) {
                    String serialNo = getSubString(responseResult, "<SerialNo>", "</SerialNo>");
                    //threadPool.execute(new TaskRunnable(serialNo, settleMentDO, map));
                } else {
                    //异步查询的结果为中间状态的时候每隔10分钟查询一次
                    try {
                        TimeUnit.MINUTES.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        //当处理逻辑为结算或异步查询得到最终状态的时候调用下面的逻辑
        if (flag == null || flag) {
            //往数据库跟新数据
        }
    }


    private class TaskRunnable implements Runnable {

        private String serialNo;
        private SettleMentDO settleMentDO;
        private Map<String, String> map;
        private Boolean flag = false;

        public TaskRunnable(String value, SettleMentDO settleMentDO, Map<String, String> map) {
            this.serialNo = value;
            this.settleMentDO = settleMentDO;
            this.map = map;
        }

        @Override
        public void run() {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            settleMentDO.setTransCode(TransCodeEnum.QPAYENT.getValue());
            settleMentDO.setOldPackageId(settleMentDO.getPackageId());
            settleMentDO.setPackageId(TransferConstantsService.getPackageId());
            while (!flag) {
                settleMentDO.setTranTime(new Date());
                settleMentDO.setTranDate(new Date());
                map.put("TransCode", settleMentDO.getTransCode());
                map.put("PackageID", settleMentDO.getPackageId());
                map.put("reqData", TransferConstantsService.qPayEntXml(settleMentDO, serialNo));
                String responseStr = HttpUtil.post(TransferConstantsService.TRANS_URL + "?userID=" + settleMentDO.getId() + "&PackageID=" + settleMentDO.getPackageId() + "&SendTime=" + DateUtil.parseDateToString(new Date(), "yyyyMMddHHmmssSSS"), map, null, "gb2312");
                if (responseStr == null) {
                    try {
                        TimeUnit.MINUTES.sleep(10);
                        continue;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                doDealResponseResult(responseStr, settleMentDO, map, false, flag);
            }

        }

    }
}
