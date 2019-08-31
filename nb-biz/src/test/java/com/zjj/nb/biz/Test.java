package com.zjj.nb.biz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.zjj.nb.biz.util.BeanUtil;
import com.zjj.nb.biz.util.DateUtil;
import com.zjj.nb.biz.util.MD5Util;
import com.zjj.nb.biz.util.fileutil.FileScanUtil;
import com.zjj.nb.dao.entity.userDAO;
import org.apache.commons.lang.math.NumberUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;

import java.io.IOException;
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
		Long sum = 16L;
		System.out.println(Long.bitCount(sum));


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
