package com.zjj.nb.biz.service.icbctransfer;

import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jinju.zeng on 2017/8/9.
 */
public class TransferConstantsService {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat();
    private static final String DEFAULT_FORMAT = "yyyyMMddHHmmssSSS";
    private static AtomicInteger atomic = new AtomicInteger(0);
    private static final Random random = new Random();
    public static final String NC_CLIENT_IP = "172.16.20.8";
    public static final String SIGN_URL = "http://172.16.20.8:449";
    public static final String TRANS_URL = "http://172.16.20.8:448";


    /**
     * 交易类报文封装
     *
     * @param settleMentDO
     * @return
     */
    public static String settleMentXml(List<SettleMentDO> settleMentDOs) {
        StringBuilder builder = new StringBuilder();
        SettleMentDO mentDO = settleMentDOs.get(0);
        builder.append("<in>")
                .append("<OnlBatF>").append(mentDO.getOnlBatF()).append("</OnlBatF>")
                .append("<SettleMode>").append(mentDO.getSettleMode()).append("</SettleMode>")
                .append("<TotalNum>").append(mentDO.getTotalNum()).append("</TotalNum>")
                .append("<TotalAmt>").append(mentDO.getTotalAmt()).append("</TotalAmt>")
                .append("<SignTime>").append(getDateFormat(mentDO.getSignTime())).append("</SignTime>")
                .append("<ReqReserved1>").append(mentDO.getReqReserved1()).append("</ReqReserved1>")
                .append("<ReqReserved2>").append(mentDO.getReqReserved2()).append("</ReqReserved2>");
        for (SettleMentDO settleMentDO : settleMentDOs) {
            StringBuilder builder1 = new StringBuilder();
            builder1.append("<rd>")
                    .append("<iSeqno>").append(settleMentDO.getiSeqno()).append("</iSeqno>")
                    .append("<ReimburseNo>").append(settleMentDO.getReimburseNo()).append("</ReimburseNo>")
                    .append("<ReimburseNum>").append(settleMentDO.getReimburseNum()).append("</ReimburseNum>")
                    //这个时间要大于当天
                    .append("<StartDate>").append(settleMentDO.getStartDate() == null ? "" : getDateFormat(settleMentDO.getStartDate(), "yyyyMMdd")).append("</StartDate>")
                    .append("<StartTime>").append(settleMentDO.getStartTime() == null ? "" : getDateFormat(settleMentDO.getStartTime(), "HHmmss")).append("</StartTime>")
                    .append("<PayType>").append(settleMentDO.getPayType()).append("</PayType>")
                    .append("<PayAccNo>").append(settleMentDO.getPayAccNo()).append("</PayAccNo>")
                    .append("<PayAccNameCN>").append(settleMentDO.getPayAccNameCN()).append("</PayAccNameCN>")
                    .append("<PayAccNameEN>").append(settleMentDO.getPayAccNameEN()).append("</PayAccNameEN>")
                    .append("<RecAccNo>").append(settleMentDO.getRecAccNo()).append("</RecAccNo>")
                    .append("<RecAccNameCN>").append(settleMentDO.getRecAccNameCN()).append("</RecAccNameCN>")
                    .append("<RecAccNameEN>").append(settleMentDO.getRecAccNameEN()).append("</RecAccNameEN>")
                    .append("<SysIOFlg>").append(settleMentDO.getSysIOFlg()).append("</SysIOFlg>")
                    .append("<IsSameCity>").append(settleMentDO.getIsSameCity()).append("</IsSameCity>")
                    .append("<Prop>").append(settleMentDO.getProp()).append("</Prop>")
                    .append("<RecICBCCode>").append(settleMentDO.getRecICBCCode()).append("</RecICBCCode>")
                    .append("<RecCityName>").append(settleMentDO.getRecCityName()).append("</RecCityName>")
                    .append("<RecBankNo>").append(settleMentDO.getRecBankNo()).append("</RecBankNo>")
                    .append("<RecBankName>").append(settleMentDO.getRecBankName()).append("</RecBankName>")
                    .append("<CurrType>").append(settleMentDO.getCurrType()).append("</CurrType>")
                    .append("<PayAmt>").append(settleMentDO.getPayAmt()).append("</PayAmt>")
                    .append("<UseCode>").append(settleMentDO.getUseCode()).append("</UseCode>")
                    .append("<UseCN>").append(settleMentDO.getUseCN()).append("</UseCN>")
                    .append("<EnSummary>").append(settleMentDO.getEnSummary()).append("</EnSummary>")
                    .append("<PostScript>").append(settleMentDO.getPostScript()).append("</PostScript>")
                    .append("<Summary>").append(settleMentDO.getSummary()).append("</Summary>")
                    .append("<Ref>").append(settleMentDO.getRef()).append("</Ref>")
                    .append("<Oref>").append(settleMentDO.getOref()).append("</Oref>")
                    .append("<ERPSqn>").append(settleMentDO.geteRPSqn()).append("</ERPSqn>")
                    .append("<BusCode>").append(settleMentDO.getBusCode()).append("</BusCode>")
                    .append("<ERPcheckno>").append(settleMentDO.geteRPcheckno()).append("</ERPcheckno>")
                    .append("<CrvouhType>").append(settleMentDO.getCrvouhType()).append("</CrvouhType>")
                    .append("<CrvouhName>").append(settleMentDO.getCrvouhName()).append("</CrvouhName>")
                    .append("<CrvouhNo>").append(settleMentDO.getCrvouhNo()).append("</CrvouhNo>")
                    .append("<ReqReserved3>").append(settleMentDO.getReqReserved3()).append("</ReqReserved3>")
                    .append("<ReqReserved4>").append(settleMentDO.getReqReserved4()).append("</ReqReserved4>")
                    .append("</rd>");
            builder.append(builder1.toString());
        }
        builder.append("</in>");
        return titleXml(mentDO, builder.toString());
    }

    /**
     * 查询当日明细报文
     *
     * @param searchDO
     * @return
     */
    public static String searchDetailXmlForToday(SearchDO searchDO) {
        StringBuilder builder = new StringBuilder();
        builder.append("<in>")
                .append("<AccNo>").append(searchDO.getAccNo()).append("</AccNo>")
                .append("<AreaCode>").append(searchDO.getAreaCode()).append("</AreaCode>")
                .append("<MinAmt>").append(searchDO.getMinAmt()).append("</MinAmt>")
                .append("<MaxAmt>").append(searchDO.getMaxAmt()).append("</MaxAmt>")
                .append("<BeginTime>").append(searchDO.getBeginTime() == null ? "" : getDateFormat(searchDO.getBeginTime(), "HHmmss")).append("</BeginTime>")
                .append("<EndTime>").append(searchDO.getEndTime() == null ? "" : getDateFormat(searchDO.getBeginTime(), "HHmmss")).append("</EndTime>")
                .append("<NextTag>").append(searchDO.getNextTag()).append("</NextTag>")
                .append("<ReqReserved1>").append(searchDO.getReqReserved1()).append("</ReqReserved1>")
                .append("<ReqReserved2>").append(searchDO.getReqReserved2()).append("</ReqReserved2>")
                .append("</in>");
        return titleXml(searchDO, builder.toString());
    }

    /**
     * 头报文
     *
     * @param baseDO
     * @param content
     * @return
     */
    private static String titleXml(BaseDO baseDO, String content) {
        StringBuilder builder = new StringBuilder();
        Date date = new Date();
        builder.append("<?xml version=\"1.0\" encoding = \"GBK\"?>")
                .append("<CMS>")
                .append("<eb>");
        builder.append("<pub>")
                .append("<TransCode>").append(baseDO.getTransCode()).append("</TransCode>")
                .append("<CIS>").append(baseDO.getCis()).append("</CIS>")
                .append("<BankCode>").append(baseDO.getBankCode()).append("</BankCode>")
                .append("<ID>").append(baseDO.getId()).append("</ID>")
                .append("<TranDate>").append(getDateFormat(date, "yyyyMMdd")).append("</TranDate>")
                .append("<TranTime>").append(getDateFormat(date, "HHmmssSSS")).append("</TranTime>")
                .append("<fSeqno>").append(baseDO.getPackageId()).append("</fSeqno>")
                .append("</pub>");
        builder.append(content);
        builder.append("</eb>").append("</CMS>");
        return builder.toString();
    }

    public static String qPayEntXml(SettleMentDO settleMentDO, String serialNo) {
        StringBuilder builder = new StringBuilder();
        builder.append("<in>")
                .append("<QryfSeqno>").append(settleMentDO.getOldPackageId()).append("</QryfSeqno>")
                .append("<QrySerialNo>").append(serialNo).append("</QrySerialNo>")
                .append("<ReqReserved1>").append(settleMentDO.getReqReserved1()).append("</ReqReserved1>")
                .append("<ReqReserved2>").append(settleMentDO.getReqReserved2()).append("</ReqReserved2>")
                .append("<rd>")
                .append("<iSeqno>").append(settleMentDO.getiSeqno()).append("</iSeqno>")
                .append("<QryiSeqno>").append(settleMentDO.getiSeqno()).append("</QryiSeqno>")
                .append("<QryOrderNo>").append("").append("</QryOrderNo>")
                .append("<ReqReserved3>").append(settleMentDO.getReqReserved3()).append("</ReqReserved3>")
                .append("<ReqReserved4>").append(settleMentDO.getReqReserved4()).append("</ReqReserved4>")
                .append("</rd>")
                .append("</in>");
        return titleXml(settleMentDO, builder.toString());
    }

    /**
     * 自定义指令包序列号(支持一天最多10000000次请求)
     *
     * @return
     */
    public static String getPackageId() {
        Date date = new Date();
        String data = getDateFormat(date, "yyyyMMdd");
        //每次请求序列号加1，支持并发
        Integer value = atomic.getAndIncrement();
        //当atomic的当前值大于9999999重置atomic为0
        if (value > 99999) {
            atomic = new AtomicInteger(0);
        }
        String valueStr = value.toString();
        for (int i = 0; i < 5 - valueStr.length(); i++) {
            data += "0";
        }
        int n = random.nextInt(99);
        return data + valueStr + (n < 10 ? String.valueOf("0" + n) : String.valueOf(n));
    }

    /**
     * Base64解密
     *
     * @param s
     * @return
     */
    public static byte[] getByteFromBase64(String s) {
        if (s == null || s.length() == 0) {
            return null;
        }
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            return decoder.decodeBuffer(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDateFormat(Date date) {
        return getDateFormat(date, DEFAULT_FORMAT);
    }

    public static String getDateFormat(Date date, String format) {
        DATE_FORMAT.applyPattern(format);
        return DATE_FORMAT.format(date);
    }

    public static Date parseStr2Date(String value, String format) {
        DATE_FORMAT.applyPattern(format);
        try {
            return DATE_FORMAT.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {

//        Date date=new Date();
//        String def=getDateFormat(date,"yyyyMMdd");
//        String def1=getDateFormat(date,"HHmmss");

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        final HashSet set = new HashSet();
        for (int i = 0; i < 100; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    String packageId = getPackageId();
                    System.out.println(packageId);
                    synchronized (set) {
                        set.add(packageId);
                    }
                }
            });
        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(set.size());
    }
}
