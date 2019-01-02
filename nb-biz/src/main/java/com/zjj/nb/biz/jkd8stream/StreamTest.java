package com.zjj.nb.biz.jkd8stream;

import java.util.*;
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

	private static void test(){
		int capacity = 10000;
		List<TestDto> list = new ArrayList<>(capacity);
		for (int i=0;i<capacity;i++){
			TestDto dto = new TestDto();
			if(i%10 == 0){
				dto.setName("jdk0");
			}else {
				dto.setName("jdk"+i);
			}
			dto.setValue(i+"");
			list.add(dto);
		}
		Long time = System.currentTimeMillis();
		Map<String,List<TestDto>> map = listToMap(list);
		System.out.println("cost time :"+ (System.currentTimeMillis() - time));
//		System.out.println(map);
		Map<String,List<TestDto>> listMap = new HashMap<>(140);
		Long start = System.currentTimeMillis();
		for(TestDto dto : list){
			if (listMap.containsKey(dto.getName())){
				listMap.get(dto.getName()).add(dto);
			}else{
				List<TestDto> list1 = new ArrayList<>();
				list1.add(dto);
				listMap.put(dto.getName(),list1);
			}
		}
		System.out.println("cost time "+(System.currentTimeMillis() - start));
//		System.out.println(listMap);


		start = System.currentTimeMillis();
		List<TestDto> list1 = testCollect(list,"jdk0");
		System.out.println("cost time "+(System.currentTimeMillis() - start));
	}



	private static Map<String,List<TestDto>> listToMap(List<TestDto> list){
		return list.stream().collect(Collectors.groupingBy(TestDto::getName));
	}

	private static void testFilter(){
		List<Integer> list = new ArrayList<>();
		for (int i = 0; i<10; i++) {
			list.add(i);
		}
		list.stream().filter(item ->item % 2 ==0).forEach(System.out::println);
	}

	private static void testSort(){
		List<Integer> list = new ArrayList<>();
		for (int i=0;i<20;i++){
			list.add(i);
		}
		Collections.shuffle(list);
		list.stream().sorted().forEach(System.out::println);
//		list.stream().sorted((obj1,obj2)->obj1<obj2 ? 1 : -1).forEach(System.out::println);
	}

	/**
	 * 原来的链表的每个元素可以按照规则变成相应的元素。
	 */
	private static void testMap(){
		List<Integer> list = new ArrayList<>();
		list.add(1);
		list.add(2);
		list.add(3);
		list.stream().map(item->item %2 ==0 ?true:false).forEach(System.out::println);
	}

	private static void testDistinct(){
		List<Integer> list = new ArrayList<>();
		list.add(1);
		list.add(1);
		list.add(2);
		list.add(3);
		list.stream().distinct().forEach(System.out::println);
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
