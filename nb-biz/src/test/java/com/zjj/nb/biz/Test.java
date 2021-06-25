package com.zjj.nb.biz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.zjj.nb.biz.util.BeanUtil;
import com.zjj.nb.biz.util.DateUtil;
import com.zjj.nb.biz.util.MD5Util;
import com.zjj.nb.biz.util.fileutil.FileScanUtil;
import com.zjj.nb.biz.util.http.HttpUtil;
import com.zjj.nb.biz.util.http.OkHttpUtil;
import com.zjj.nb.dao.entity.userDAO;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

/**
 * Created by admin on 2017/12/15.
 */
public class Test {

	private static final ExecutorService threadPool=Executors.newFixedThreadPool(5);
	private static int sum=0;
	private static CountDownLatch downLatch=new CountDownLatch(100);
	public static void main(String[] args){
		Map<String,String> map = new HashMap<>();
		map.put("住宿费","4001_3,4,5");
		map.put("餐费","_6,7");
		map.put("交通费","_8,9,10,12,13");
		map.put("办公用品","");
		map.put("团建费","");
		map.put("其他","_14,15,16");
		JSONArray array = JSONObject.parseArray("[{\n" +
				"\t\"componentType\": \"TextField\",\n" +
				"\t\"label\": \"费用发生日期\",\n" +
				"\t\"value\": \"2020-6-29\",\n" +
				"\t\"key\": \"TextField-KBYWC9RV\"\n" +
				"}, {\n" +
				"\t\"componentType\": \"TextField\",\n" +
				"\t\"label\": \"费用截止日期(出差填)\",\n" +
				"\t\"value\": \"2020-6-29\",\n" +
				"\t\"key\": \"TextField-KBYWC9RW\"\n" +
				"}, {\n" +
				"\t\"componentType\": \"TextField\",\n" +
				"\t\"label\": \"报销类别\",\n" +
				"\t\"value\": \"住宿费\",\n" +
				"\t\"key\": \"TextField-KBYVB40X\"\n" +
				"}, {\n" +
				"\t\"componentType\": \"TextareaField\",\n" +
				"\t\"label\": \"住宿费明细\",\n" +
				"\t\"value\": \"住宿\",\n" +
				"\t\"key\": \"TextareaField-KBYVDSLI\"\n" +
				"}, {\n" +
				"\t\"componentType\": \"TextField\",\n" +
				"\t\"label\": \"住宿费报销金额(元)\",\n" +
				"\t\"value\": \"300\",\n" +
				"\t\"key\": \"TextField-KBYWIUDO\"\n" +
				"}, {\n" +
				"\t\"componentType\": \"TextField\",\n" +
				"\t\"label\": \"住宿费专票进项税额\",\n" +
				"\t\"value\": \"\",\n" +
				"\t\"key\": \"TextField-KBYWIUDP\"\n" +
				"}, {\n" +
				"\t\"componentType\": \"TextareaField\",\n" +
				"\t\"label\": \"餐费明细\",\n" +
				"\t\"value\": \"\",\n" +
				"\t\"key\": \"TextareaField-KBYVOM87\"\n" +
				"}, {\n" +
				"\t\"componentType\": \"TextField\",\n" +
				"\t\"label\": \"餐费报销金额(元)\",\n" +
				"\t\"value\": \"\",\n" +
				"\t\"key\": \"TextField-KBYWIUDQ\"\n" +
				"}, {\n" +
				"\t\"componentType\": \"TextareaField\",\n" +
				"\t\"label\": \"交通费明细\",\n" +
				"\t\"value\": \"\",\n" +
				"\t\"key\": \"TextareaField-KBYVSVP1\"\n" +
				"}, {\n" +
				"\t\"componentType\": \"TextField\",\n" +
				"\t\"label\": \"火车票金额\",\n" +
				"\t\"value\": \"\",\n" +
				"\t\"key\": \"TextField-KBYWES6G\"\n" +
				"}, {\n" +
				"\t\"componentType\": \"TextField\",\n" +
				"\t\"label\": \"机票金额\",\n" +
				"\t\"value\": \"\",\n" +
				"\t\"key\": \"TextField-KBYWIUDJ\"\n" +
				"}, {\n" +
				"\t\"label\": \"火车票金额\",\n" +
				"\t\"value\": \"\"\n" +
				"}, {\n" +
				"\t\"componentType\": \"TextField\",\n" +
				"\t\"label\": \"网约车及巴士金额\",\n" +
				"\t\"value\": \"\",\n" +
				"\t\"key\": \"TextField-KBYWIUDK\"\n" +
				"}, {\n" +
				"\t\"componentType\": \"TextField\",\n" +
				"\t\"label\": \"其他交通工具金额\",\n" +
				"\t\"value\": \"\",\n" +
				"\t\"key\": \"TextField-KBYWIUDL\"\n" +
				"}, {\n" +
				"\t\"componentType\": \"TextareaField\",\n" +
				"\t\"label\": \"其他费用明细\",\n" +
				"\t\"value\": \"\",\n" +
				"\t\"key\": \"TextareaField-KBYW108M\"\n" +
				"}, {\n" +
				"\t\"componentType\": \"TextField\",\n" +
				"\t\"label\": \"其他费用报销金额(元)\",\n" +
				"\t\"value\": \"\",\n" +
				"\t\"key\": \"TextField-KBYWIUDM\"\n" +
				"}, {\n" +
				"\t\"componentType\": \"TextField\",\n" +
				"\t\"label\": \"其他费用进项税额\",\n" +
				"\t\"value\": \"\",\n" +
				"\t\"key\": \"TextField-KBYWIUDN\"\n" +
				"}, {\n" +
				"\t\"componentType\": \"DDPhotoField\",\n" +
				"\t\"label\": \"住宿费发票照片(收据无效)\",\n" +
				"\t\"value\": \"[\\\"https://resource/NGNlNGFiOTZhY2IwZWIwNDNhOTQ0NzNlMjM3MzkwYmM=.image\\\"]\",\n" +
				"\t\"key\": \"DDPhotoField-KBYVMMN3\"\n" +
				"}, {\n" +
				"\t\"componentType\": \"DDPhotoField\",\n" +
				"\t\"label\": \"餐费发票照片(收据无效)\",\n" +
				"\t\"value\": \"[]\",\n" +
				"\t\"key\": \"DDPhotoField-KBYVOM89\"\n" +
				"}, {\n" +
				"\t\"componentType\": \"DDPhotoField\",\n" +
				"\t\"label\": \"交通费发票照片\",\n" +
				"\t\"value\": \"[]\",\n" +
				"\t\"key\": \"DDPhotoField-KBYVSVP6\"\n" +
				"}, {\n" +
				"\t\"componentType\": \"DDPhotoField\",\n" +
				"\t\"label\": \"其他费用发票照片\",\n" +
				"\t\"value\": \"[]\",\n" +
				"\t\"key\": \"DDPhotoField-KBYW108P\"\n" +
				"}]");

		String type = array.getJSONObject(2).getString("value");
		String[] values = map.get(type).split("_");
		if (values.length >1) {
			String[] indexs = values[1].split(",");
			String text = array.getJSONObject(Integer.valueOf(indexs[0])).getString("label") + ":" + array.getJSONObject(Integer.valueOf(indexs[0])).getString("value");
			System.out.println(text);
//			entry.setAccountCode(values[0]);
			BigDecimal amount = BigDecimal.ZERO;
			for (int i=1;i<indexs.length;i++){
				String value = array.getJSONObject(Integer.valueOf(indexs[i])).getString("value");
				if (StringUtils.isEmpty(value)){
					continue;
				}
				amount = amount.add(new BigDecimal(value));
			}
			System.out.println(amount);
		}

	}

	public static int getSum(int a, int b) {
		if(b==0) return a;
		int ans=0;
		while(b!=0){
			ans=a^b;
			b=(a&b)<<1;
			a=ans;
		}
		return ans;
	}

	/**
	 * 统计二进制中1的个数
	 * @param n
	 * @return
	 */
	public static int count_one(long n){
		//0xAAAAAAAA，0x55555555分别是以“1位”为单位提取奇偶位
		n = ((n & 0xAAAAAAAA) >> 1) + (n & 0x55555555);

		//0xCCCCCCCC，0x33333333分别是以“2位”为单位提取奇偶位
		n = ((n & 0xCCCCCCCC) >> 2) + (n & 0x33333333);

		//0xF0F0F0F0，0x0F0F0F0F分别是以“4位”为单位提取奇偶位
		n = ((n & 0xF0F0F0F0) >> 4) + (n & 0x0F0F0F0F);

		//0xFF00FF00，0x00FF00FF分别是以“8位”为单位提取奇偶位
		n = ((n & 0xFF00FF00) >> 8) + (n & 0x00FF00FF);

       //0xFFFF0000，0x0000FFFF分别是以“16位”为单位提取奇偶位
		n = ((n & 0xFFFF0000) >> 16) + (n & 0x0000FFFF);
		return (int)n;
	}

//	public static int fastM(int a,int b,int c){
//		if(b==0){
//			return 1;
//		}
//		int r=
//	}


	private static class TestFork extends RecursiveAction{

		private List<String> list;
		public TestFork(List<String> list){
			this.list = list;
		}

		@Override
		protected void compute() {
			List<TestFork> forkList = new ArrayList<>();
			if (list.size() >= 2500){
				List<String> leftSubList = list.subList(0,list.size() /2);
				List<String> rightSubList = list.subList(list.size() / 2,list.size());
				forkList.add(new TestFork(leftSubList));
				forkList.add(new TestFork(rightSubList));
			} else {
				List<String> list1 = new ArrayList<>();
				for (String item : list){
					list1.add(item);
				}
			}
			if (!list.isEmpty()) {
				// 在当前的 ForkJoinPool 上调度所有的子任务
				for (TestFork task : invokeAll(forkList)) {
					task.join();
				}
			}
		}
	}

}
