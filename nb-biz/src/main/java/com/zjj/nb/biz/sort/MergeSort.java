package com.zjj.nb.biz.sort;

import java.util.Arrays;

/**
 * 归并排序实现
 * @author zengjinju
 * @date 2019/11/22 下午2:30
 */
public class MergeSort {

     public static void sort(Integer[] a){
		 Integer[] aux = new Integer[a.length];
     	sort(a,aux,0,a.length-1);
	 }

	/**
	 * 递归的方式实现排序
	 * @param a
	 * @param lo
	 * @param hi
	 */
	 private static void sort(Integer[] a,Integer[] aux,int lo,int hi){
     	if (hi <= lo)
     		return;
     	int mid = lo + (hi - lo) / 2;
     	sort(a,aux,lo,mid);
     	sort(a,aux,mid + 1,hi);
     	merge(a,aux,lo,mid,hi);
	 }

	 private static void merge(Integer[] a,Integer[] aux,int lo,int mid,int hi){
     	int i=lo,j=mid + 1;
     	//排序优化，当子数组长度小于15的时候使用插入排序
		 if (hi - lo <= 15){
			 InsertSort.sort(a,lo,hi);
			 return;
		 }
     	System.arraycopy(a,lo,aux,lo,hi - lo+1);
     	for (int k = lo;k<=hi;k++){
     		if (i>mid){ //左半边用完取右半边的值
     			a[k] = aux[j++];
			} else if (j>hi){
				a[k] = aux[i++];
			} else if (aux[j] < aux[i]){
     			a[k] = aux[j++];
			} else {
     			a[k] = aux[i++];
			}
		}
	 }

	 public static void main(String[] args){
     	//,3,5,13,9,6,12,18,4,2,7
		 Integer[] a = {10,19,15,3,5,13,9,6,12,18,4,2,7,1,4,16,12,11,20,30,25,27,19,28,26,40,50,44,55,43,50,41,48,49};
     	sort(a);
	 }
}
