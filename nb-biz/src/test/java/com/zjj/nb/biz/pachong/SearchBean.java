package com.zjj.nb.biz.pachong;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zjj.nb.biz.util.http.HttpUtil;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jinju.zeng on 2017/6/27.
 * 从车点点平台爬取门店信息
 */
public class SearchBean {

    public static final List<String> PROVINCE_LIST = Arrays.asList("50","63", "37", "14", "61", "51", "12", "35", "45", "46", "53", "54", "64", "65","21", "11", "31", "32", "33", "34", "36", "62", "44", "52","50", "13", "41", "43", "42", "22", "15","23", "63", "37", "14", "61", "51", "12", "35", "45", "46", "53", "54", "64", "65");
    public static final String CITY_URL = "http://www.chediandian.com/join/provincechange?p=";
    private static final String ENTERPRISE_URL = "http://www.chediandian.com//Enterprise/GetEnterprise?";
    public static final Map<String, List<CityInfo>> map = new ConcurrentHashMap<>(16);
    private static final Map<String, List<CityProductInfo>> cityInfoMap = new ConcurrentHashMap<>(256);
    private static final CountDownLatch countDown = new CountDownLatch(PROVINCE_LIST.size());
    private static final Map<String, String> provinceMap = new HashMap<>();
    private static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 6);

    static {
        provinceMap.put("21", "辽宁");
        provinceMap.put("11", "北京");
        provinceMap.put("31", "上海");
        provinceMap.put("32", "江苏");
        provinceMap.put("33", "浙江");
        provinceMap.put("34", "安徽");
        provinceMap.put("36", "江西");
        provinceMap.put("62", "甘肃");
        provinceMap.put("44", "广东");
        provinceMap.put("52", "贵州");
        provinceMap.put("50", "重庆");
        provinceMap.put("13", "河北");
        provinceMap.put("41", "河南");
        provinceMap.put("43", "湖南");
        provinceMap.put("42", "湖北");
        provinceMap.put("22", "吉林");
        provinceMap.put("15", "内蒙古自治区");
        provinceMap.put("23", "黑龙江");
        provinceMap.put("63", "青海");
        provinceMap.put("37", "山东");
        provinceMap.put("14", "山西");
        provinceMap.put("61", "陕西");
        provinceMap.put("51", "四川");
        provinceMap.put("12", "天津");
        provinceMap.put("35", "福建省");
        provinceMap.put("45", "广西壮族自治区");
        provinceMap.put("46", "海南省");
        provinceMap.put("53", "云南省");
        provinceMap.put("54", "西藏自治区");
        provinceMap.put("64", "宁夏回族自治区");
        provinceMap.put("65", "新疆维吾尔自治区");
    }


    private static List<CityInfo> getCity(String province) {
        List<CityInfo> result = new ArrayList<>();
        String jsonResult = HttpUtil.get(CITY_URL + province);
        JSONObject jsonObject = JSON.parseObject(jsonResult);
        JSONObject cityObject = JSON.parseObject(jsonObject.get("Data").toString());
        JSONArray array = JSONArray.parseArray(cityObject.get("city").toString());
        for (int i = 0; i < array.size(); i++) {
            CityInfo cityInfo = new CityInfo();
            JSONObject object = JSON.parseObject(array.get(i).toString());
            cityInfo.setLocationId(object.get("locationId").toString());
            cityInfo.setName(object.get("name").toString());
            result.add(cityInfo);
        }
        return result;
    }

    public static void getProvinceMap() {
        for (final String item : PROVINCE_LIST) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    List<CityInfo> cityList = getCity(item);
                    map.put(item, cityList);
                    countDown.countDown();
                }
            });
        }
    }

    public static void showEnterprise(final CountDownLatch countDownLatch) {
        for (final String key : map.keySet()) {
            List<CityInfo> cityList = map.get(key);
            for (final CityInfo cityInfo : cityList) {
                executorService.execute(new Task(countDownLatch, key, cityInfo));
            }

        }

    }

    private static class Task implements Runnable {
        private CountDownLatch countDownLatch;
        private String provinceId;
        private CityInfo cityInfo;
        private List<CityProductInfo> list = new ArrayList<>();
        private int pageIndex = 0;


        public Task(CountDownLatch countDownLatch, String provinceId, CityInfo cityInfo) {
            this.countDownLatch = countDownLatch;
            this.provinceId = provinceId;
            this.cityInfo = cityInfo;
        }

        @Override
        public void run() {
            while (true) {
                String jsonStr = HttpUtil.get(ENTERPRISE_URL + "pageIndex=" + pageIndex + "&provinceId=" + provinceId + "&cityId=" + cityInfo.getLocationId());
                JSONObject jsonObject = JSON.parseObject(jsonStr);
                JSONArray array = JSON.parseArray(jsonObject.get("listMd").toString());
                if(array.size()==0){
                    break;
                }
                for (int i = 0; i < array.size(); i++) {
                    JSONObject object = JSON.parseObject(array.get(i).toString());
                    try {
                        CityProductInfo cityInfo = new CityProductInfo();
                        cityInfo.setDistrict(object.get("district").toString());
                        cityInfo.setEntName(object.get("entname").toString());
                        cityInfo.setEntAddress(object.get("entaddress").toString());
                        cityInfo.setEntPhone(object.get("entphone").toString());
                        cityInfo.setProvinceId(provinceId);
                        cityInfo.setProvinceName(provinceMap.get(provinceId));
                        cityInfo.setCityId(this.cityInfo.getLocationId());
                        cityInfo.setCityName(this.cityInfo.getName());
                        list.add(cityInfo);
                    }catch (Exception e){
                        System.out.println(object);
                    }
                }
                pageIndex++;
            }
            cityInfoMap.put(cityInfo.getLocationId(), list);
            countDownLatch.countDown();
        }
    }

    public static void main(String[] args) {
//        getProvinceMap();
//        try {
//            countDown.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        int sum = 0;
//        for (List<CityInfo> list : map.values()) {
//            sum += list.size();
//        }
//        CountDownLatch enterpriseCountDown = new CountDownLatch(sum);
//        showEnterprise(enterpriseCountDown);
//        try {
//            enterpriseCountDown.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        writeExcel();

        String str=HttpUtil.get("https://www.tuhu.cn/shops/yunnan25.aspx");
        System.out.println(str);
    }

    /**
     * 将数据导入到Excel
     */
    private static void writeExcel() {
        HSSFWorkbook workBook = new HSSFWorkbook();
        HSSFSheet sheet = workBook.createSheet();
        //创建第0行
        HSSFRow row = sheet.createRow(0);
        //创建表头
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("省份Id");
        cell = row.createCell(1);
        cell.setCellValue("省份名称");
        cell = row.createCell(2);
        cell.setCellValue("城市id");
        cell = row.createCell(3);
        cell.setCellValue("城市名称");
        cell = row.createCell(4);
        cell.setCellValue("区域名称");
        cell = row.createCell(5);
        cell.setCellValue("门店名称");
        cell = row.createCell(6);
        cell.setCellValue("门店地址");
        cell = row.createCell(7);
        cell.setCellValue("门店电话");
        int i = 1;
        for (List<CityProductInfo> list : cityInfoMap.values()) {
            if (!CollectionUtils.isEmpty(list)) {
                for (CityProductInfo cityProductInfo : list) {
                    HSSFRow valueRow = sheet.createRow(i);
                    valueRow.createCell(0).setCellValue(cityProductInfo.getProvinceId());
                    valueRow.createCell(1).setCellValue(cityProductInfo.getProvinceName());
                    valueRow.createCell(2).setCellValue(cityProductInfo.getCityId());
                    valueRow.createCell(3).setCellValue(cityProductInfo.getCityName());
                    valueRow.createCell(4).setCellValue(cityProductInfo.getDistrict());
                    valueRow.createCell(5).setCellValue(cityProductInfo.getEntName());
                    valueRow.createCell(6).setCellValue(cityProductInfo.getEntAddress());
                    valueRow.createCell(7).setCellValue(cityProductInfo.getEntPhone());
                    i++;
                }
            }
        }

        File file = new File("city3.xls");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            workBook.write(out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
