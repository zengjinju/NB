package com.zjj.nb.biz.sort;

import java.util.Random;

/**
 * 堆排序
 * @author zengjinju
 * @date 2019/12/4 上午11:17
 */
public class HeapSort {

	/**
	 * 由上至下构造堆
	 * @param a
	 * @param k
	 * @param n
	 */
	private void sink(Integer[] a, int k, int n){
		while(2 * k <= n){
			int j = 2 * k;
			if (j< n && a[j] < a[j+1]){
				j++;
			}
			if (a[k] >= a[j]){
				break;
			}
			swap(a,k,j);
			k = j;
		}
	}

	private void swap(Integer[] a,int i,int j){
		int t = a[j];
		a[j] = a[i];
		a[i] = t;
	}

	public void sort(Integer[] a){
		int n = a.length-1;
		//构造堆，
		for (int k = n/2;k>=1;k--){
			sink(a,k,n);
		}
		//下沉排序，并销毁堆
		while (n>1){
			//堆的第一位是最大的元素，让最大元素放到最后面，然后在让前n-1个元素重新构造堆
			swap(a,1,n--);
			sink(a,1,n);
		}
	}

	public static void main(String[] args){
		Integer[] a = new Integer[12];
		Random random = new Random();
		for (int i=1;i<=11;i++){
			a[i] = random.nextInt(20);
		}
		HeapSort heapSort = new HeapSort();
		heapSort.sort(a);
		System.out.println(a);
	}
}
