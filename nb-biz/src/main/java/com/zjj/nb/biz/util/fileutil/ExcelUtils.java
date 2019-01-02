package com.zjj.nb.biz.util.fileutil;

import com.zjj.nb.biz.annotation.ExcelFieldAnnotation;
import com.zjj.nb.biz.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.util.CollectionUtils;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by jinju.zeng on 2017/6/13.
 */
@Slf4j
public class ExcelUtils {

    public static <T> List<T> importExcel(InputStream input, Class<T> t) {
        List<T> list = new ArrayList<>();
        try {
            HSSFWorkbook wb = new HSSFWorkbook(input);
            HSSFSheet sheet = wb.getSheetAt(0);
            Field[] fields = t.getDeclaredFields();
            Map<Integer, Integer> map = getRelationXlsWithObject(sheet, fields);
            if (CollectionUtils.isEmpty(map)) {
                log.info("xml文件标题和对象字段对应关系解析错误");
                return list;
            }
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                HSSFRow row = sheet.getRow(i);
                list.add(doProcess(row, map, fields,t));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static Map getRelationXlsWithObject(HSSFSheet sheet, Field[] fields) {
        //保存excel文件中的标题字段和T对象中字段的对应关系
        Map<Integer, Integer> map = new HashMap<>();
        HSSFRow row = sheet.getRow(0);
        for (int i = 0; i < row.getLastCellNum(); i++) {
            HSSFCell cell = row.getCell(i);
            for (int j = 0; j < fields.length; j++) {
                ExcelFieldAnnotation annotation = fields[j].getAnnotation(ExcelFieldAnnotation.class);
                //当注解中的值和xls文件中的标题相同时设置对应关系
                if (annotation != null && cell.getStringCellValue().equals(annotation.ExcelField())) {
                    map.put(i, j);
                    break;
                }
            }
        }
        return map;
    }

    private static <T> T doProcess(HSSFRow row, Map<Integer, Integer> map, Field[] fields,Class<T> t) {
        T var1 = null;
        try {
            var1 =  t.getDeclaredConstructor().newInstance();
            for (int i = 0; i < row.getLastCellNum(); i++) {
                Field field = fields[map.get(i)];
                if (!field.isAccessible()) {
                    //取消对Java的访问检查
                    field.setAccessible(true);
                }
                String value="";
                Cell cell = row.getCell(i);
                if (cell == null){
                    continue;
                }
                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_NUMERIC:
                        //日期类型
                        if (HSSFDateUtil.isCellDateFormatted(cell)) {
                            value = DateUtil.parseDateToString(cell.getDateCellValue());
                        } else {
                            value = cell.toString();
                        }
                        break;
                    default:
                        value = cell.toString();
                }
                if("".equals(value)){
                    continue;
                }
                Type type = field.getGenericType();
                if (type.equals(Integer.class)) {
                    field.set(var1, Math.abs(Double.valueOf(value).intValue()));
                } else if (type.equals(Long.class)) {
                    field.set(var1, Double.valueOf(value).longValue());
                } else if (type.equals(Double.class)) {
                    field.set(var1, Double.parseDouble(value));
                } else if (type.equals(Byte.class)) {
                    field.set(var1, Double.valueOf(value).byteValue());
                } else if (type.equals(Boolean.class)) {
                    field.set(var1, Boolean.parseBoolean(value));
                } else if (type.equals(short.class)) {
                    field.set(var1, Double.valueOf(value).shortValue());
                } else if (type.equals(float.class)) {
                    field.set(var1, Float.parseFloat(value));
                } else if (type.equals(char.class)) {
                    field.set(var1, value.charAt(0));
                }else if(type.equals(String.class)){
                    field.set(var1,value);
                }else if(type.equals(Date.class)){
                    field.set(var1,DateUtil.parseStr2Date(value,"yyyy-MM-dd HH:mm:ss"));
                }
            }
        } catch (Exception e) {
            log.error("Excel解析出现异常",e);
        }
        return var1;
    }
}
