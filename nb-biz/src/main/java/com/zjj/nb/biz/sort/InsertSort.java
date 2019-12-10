package com.zjj.nb.biz.sort;

/**
 * 插入排序
 * @author zengjinju
 * @date 2019/11/28 下午4:35
 */
public class InsertSort {

	public static void sort(Integer[] a,int lo,int hi){
		for (int i = lo + 1;i<=hi;i++){
			//将a[i] 插入到a[i-1],a[i-2],a[i-3]...之中
			for (int j=i;j>0 && a[j] < a[j-1];j--){
				int t = a[j-1];
				a[j-1] = a[j];
				a[j] = t;
			}
		}
	}

	public static void main(String[] args){
		Integer[] a = {10,9,11,15,13,18,17,2,8,14};
		sort(a,0,a.length - 1);
	}
}
