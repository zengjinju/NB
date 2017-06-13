package com.zjj.nb.biz.util.fileutil;

import com.zjj.nb.biz.annotation.ExcelFieldAnnotation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.util.CollectionUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jinju.zeng on 2017/6/13.
 */
@Slf4j
public class ExcelUtils {

    public static <T> List<T> processXls(InputStream input, T t) {
        List<T> list = new ArrayList<>();
        try {
            HSSFWorkbook wb = new HSSFWorkbook(input);
            HSSFSheet sheet = wb.getSheetAt(0);
            Field[] fields = t.getClass().getDeclaredFields();
            Map<Integer, Integer> map = getRelationXlsWithObject(sheet, fields);
            if (CollectionUtils.isEmpty(map)) {
                log.info("xml文件标题和对象字段对应关系解析错误");
                return null;
            }
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                HSSFRow row = sheet.getRow(i);
                list.add(doProcess(row, map, t));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static Map getRelationXlsWithObject(HSSFSheet sheet, Field[] fields) {
        //保存xls文件中的标题字段和T对象中字段的对应关系
        Map<Integer, Integer> map = new HashMap<>();
        HSSFRow row = sheet.getRow(0);
        for (int i = 0; i < row.getLastCellNum(); i++) {
            HSSFCell cell = row.getCell(i);
            for (int j = 0; j < fields.length; j++) {
                ExcelFieldAnnotation annotation = fields[j].getAnnotation(ExcelFieldAnnotation.class);
                //当注解中的值和xls文件中的标题相同时设置对应关系
                if (annotation != null && cell.getStringCellValue().equals(annotation.XlsField())) {
                    map.put(i, j);
                    break;
                }
            }
        }
        return map;
    }

    private static <T> T doProcess(HSSFRow row, Map<Integer, Integer> map, T t) {
        T var1 = null;
        try {
            var1 = (T) t.getClass().newInstance();
            Field[] fields = var1.getClass().getDeclaredFields();
            for (int i = 0; i < row.getLastCellNum(); i++) {
                Field field = fields[map.get(i)];
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                String value="";
                switch(row.getCell(i).getCellType()){
                    case HSSFCell.CELL_TYPE_NUMERIC:
                        value=String.valueOf(row.getCell(i).getNumericCellValue());
                        break;
                    case HSSFCell.CELL_TYPE_STRING:
                        value=row.getCell(i).getStringCellValue();
                        break;
                    default:
                        value=row.getCell(i).toString();
                }
                if("".equals(value)){
                    continue;
                }
                Type type = field.getGenericType();
                if (type.equals(Integer.class)) {
                    field.set(var1, Integer.parseInt(value));
                } else if (type.equals(Long.class)) {
                    field.set(var1, Long.parseLong(value));
                } else if (type.equals(Double.class)) {
                    field.set(var1, Double.parseDouble(value));
                } else if (type.equals(Byte.class)) {
                    field.set(var1, Byte.parseByte(value));
                } else if (type.equals(Boolean.class)) {
                    field.set(var1, Boolean.parseBoolean(value));
                } else if (type.equals(short.class)) {
                    field.set(var1, Short.parseShort(value));
                } else if (type.equals(float.class)) {
                    field.set(var1, Float.parseFloat(value));
                } else if (type.equals(char.class)) {
                    field.set(var1, value.charAt(0));
                }else if(type.equals(String.class)){
                    field.set(var1,value);
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return var1;
    }
}
