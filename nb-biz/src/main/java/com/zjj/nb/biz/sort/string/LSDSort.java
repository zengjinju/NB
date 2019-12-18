package com.zjj.nb.biz.sort.string;

/**
 * 适合
 * 低位优先的字符串排序(键索引计数法)
 * 为每个元素均含有W个字符的字符串数组排序
 * @author zengjinju
 * @date 2019/12/15 下午10:05
 */
public class LSDSort {

	/**
	 *
	 * @param a
	 * @param w 字符串长度
	 */
	public static void sort(String[] a,int w){
		int n = a.length;
		int R = 256;
		String[] aux = new String[n];
		for (int d = w-1;d>=0;d--){
			//根据第d个字符用键索引计数法排序
			//计算每个字符出现的频率
			int[]  count = new int[R + 1];
			for (int i=0;i<n;i++){
				count[a[i].charAt(d)+1]++;
			}
			//将频率转化为索引
			for (int r =0;r<R;r++){
				count[r+1]+=count[r];
			}
			//将元素分类
			for (int i=0;i<n;i++){
				aux[count[a[i].charAt(d)]++] = a[i];
			}
			//回写
			for (int i=0;i<n;i++){
				a[i] = aux[i];
			}
		}
	}

	public static void main(String[] args){
		String[] a = {"4PGC938","2IYE230","3CI0720","1ICK750","10HV845","4JZY524"};
		sort(a,7);
	}
}
