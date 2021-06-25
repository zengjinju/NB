package com.zjj.nb.biz.sort;

import java.util.*;

/**
 * @author zengjinju
 * @date 2019/11/29 上午11:29
 */
public class QuickSort {

	public static void sort(Integer[] a){
		sort(a,0,a.length - 1);
	}

	private static void sort(Integer[] a,int lo,int hi){
		//小的子数组改为插入排序提高性能
//		if (hi <= lo + 15){
//			InsertSort.sort(a,lo,hi);
//			return;
//		}
		int j = partition(a,lo,hi);
		sort(a,lo,j-1);   //左半部分排序
		sort(a,j+1,hi);   //右半部分排序
	}

	/**
	 * 适用于大量重复元素的三向快速排序
	 * 三向快速排序，将数组分成三部分
	 * 1. 小于v的子数组
	 * 2. 等于v的子数组(不进行排序)
	 * 3. 大于v的子数组
	 * @param a
	 * @param lo
	 * @param hi
	 */
	private static void sort3Away(Integer[] a,int lo,int hi){
		if (hi <= lo){
			return;
		}
		int lt = lo,i = lo + 1,gt = hi;
		while(i <= gt){
			if (a[i] < a[lo]){
				swap(a,lt++,i++);
			} else if (a[i] > a[lo]){
				swap(a,i,gt--);
			} else {
				i++;
			}
		}
		sort(a,lo,lt-1);
		sort(a,gt+1,hi);
	}

	/**
	 * 获取切分元素(将小于切分元素的值放到数组左边，大于切分元素的值放到数组右边)
	 * @param a
	 * @param lo
	 * @param hi
	 * @return
	 */
	private static int partition(Integer[] a,int lo,int hi){
		int v = a[lo], i = lo, j=hi+1;
		while (true){
			//从数组左侧扫描，获取大于v的元素
			while (a[++i] <= v){
				if (i == hi){
					break;
				}
			}
			while(a[--j]>=v){
				if (j == lo){
					break;
				}
			}
			if (i>=j){
				break;
			}
			swap(a,i,j);
		}
		swap(a,lo,j);
		return j;
	}

	private static void swap(Integer[] a,int i,int j){
		int t = a[i];
		a[i] = a[j];
		a[j] = t;
	}

	/**
	 * 找出数组中的第k大的元素
	 * @param k
	 * @return
	 */
	public int selectK(Integer[] a,int k){
		int lo = 0,hi = a.length - 1,target = a.length -k;
		while (hi > lo){
			//获取切分元素，切分元素左边的都比它小，右边的都比它大
			int mid = partition(a,lo,hi);
			if(mid == target){
				return a[mid];
			}
			if (mid < target){
				lo = mid + 1;
			} else {
				hi = mid - 1;
			}
		}
		return a[lo];
	}

	public static void main(String[] args){
		Integer[] values = {3,1,7,4,9,6};
		QuickSort quickSort = new QuickSort();
		int num = quickSort.selectK(values,1);
		System.out.println(num);
	}
}
