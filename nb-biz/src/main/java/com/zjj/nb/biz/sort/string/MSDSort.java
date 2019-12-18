package com.zjj.nb.biz.sort.string;

import com.zjj.nb.biz.sort.InsertSort;

/**
 * 高位字符串排序，适用于随机字符串
 * @author zengjinju
 * @date 2019/12/17 下午6:45
 */
public class MSDSort {
	private static int R = 256;
	//小数组的切分阈值
	private static final int M = 15;
	private static String[] aux;

	private static int charAt(String s,int d){
		return d < s.length() ? s.charAt(d) : -1;
	}

	private static void sort(String[] a,int lo,int hi,int d){
		//已第d个字符作为键将a[lo]至a[hi]进行排序
		if (hi < lo + M ){
			//较小的子数组进行插入排序
			InsertSort.sort(a,lo,hi,d);
			return;
		}
		//计算字符出现的频率
		int[] count = new int[R+2];
		for (int i=lo;i<=hi;i++){
			count[charAt(a[i],d) + 2]++;
		}
		//将频率转换为索引
		for (int r=0;r<R+1;r++){
			count[r+1]+=count[r];
		}
		for (int i=lo;i<=hi;i++){
			aux[count[charAt(a[i],d)+1]++] = a[i];
		}
		for (int i=lo;i<=hi;i++){
			a[i] = aux[i-lo];
		}
		//递归的以每个字符为键进行排序
		for (int r=0;r<R;r++){
		    sort(a,lo+count[r],lo+count[r+1]-1,d+1);
		}
	}

	public static void sort(String[] a){
		int N = a.length;
		aux = new String[N];
		sort(a,0,N - 1,0);
	}

	public static void main(String[] args){
		String[] a = {"dcbefg","aa123","kfdjkfjdkjfkd","ngvjgnfvjfhnf","er","cdfg","fh","ghghg","1234","bvg","hiyutr","ilkgh","hhab","ibcfg","jjjj","lmn","mln","mlxg","mlgh","mlabc","nbgh","ncfghiyu","kdkdkndkndhajdkdnvjd","ncbhdgnbgdg","abcefg","bbccff"};
		sort(a);
		System.out.println(a);
	}
}
