package com.zjj.nb.biz.jkd8stream;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author zengjinju
 * @date 2018/9/22 下午3:42
 */
public class StreamTest {

	public static void main(String[] args){

		List<TestDto> list = new ArrayList<>();
		for (int i=0;i<10;i++){
			TestDto dto = new TestDto();
			if(i%10 == 0){
				dto.setName("jdk0");
			}else {
				dto.setName("jdk"+i);
			}
			dto.setValue(i+"");
			list.add(dto);
		}

		List<String> list1 = testList(list);
		System.out.println(list1);

	}

	/**
	 * 把Stream 元素组合起来
	 */
	private static void testReduce(){
		List<Integer> list = new ArrayList<>();
		for (int i=0;i<10;i++){
			list.add(i);
		}
		Optional<Integer> optional = list.stream().reduce((item1,item2)->item1+item2);
		System.out.println(optional.get());
	}

	/**
	 * 生成新的
	 */
	private static List<TestDto> testCollect(List<TestDto> list,String tag){
		return list.stream().filter(item->item.getName().contains(tag)).collect(Collectors.toList());
	}

	private static void testMin(){
		List<Integer> list = new ArrayList<>();
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);
		list.add(5);
		System.out.println(list.stream().max((item1,item2)->item1<item2 ? 1 : -1).get());
	}

	/**
	 * 输出某个字段
	 * @param list
	 * @return
	 */
	private static  List<String> testList(List<TestDto> list){
		return list.stream().map(TestDto::getName).collect(Collectors.toList());
	}

	private static Map<String,TestDto> toMap(List<TestDto> list){
		return list.stream().collect(Collectors.toMap(TestDto::getName,Function.identity()));
	}

	private static Map<String,List<TestDto>> groupBy(List<TestDto> list){
		return list.stream().collect(Collectors.groupingBy(TestDto::getName));
	}









	public static class TestDto{
		private String name;

		private String value;

		public TestDto(){}

		public TestDto(String name, String value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
}
