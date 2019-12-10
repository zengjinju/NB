package com.zjj.nb.biz.sort;

import java.util.Random;

/**
 * 最小堆实现
 *
 * @author zengjinju
 * @date 2019/12/4 下午2:31
 */
public class MinPQ {
	private Integer[] pq;
	private int N = 0;

	public MinPQ(int N) {
		pq = new Integer[N + 1];
	}

	public Boolean isEmpty(){
		return N == 0;
	}

	public int size(){
		return N;
	}

	/**
	 * 由下至上的堆有序化
	 * @param k
	 */
	private void swim(int k){
		while(k > 1 && pq[k/2] > pq[k]){
			swap(k/2,k);
			k = k/2;
		}
	}

	private void swap(int i,int j){
		int t = pq[j];
		pq[j] = pq[i];
		pq[i] = t;
	}

	/**
	 * 由上至下的堆有序化
	 * @param k
	 */
	private void sink(int k){
		while (2*k <= N){
			int j = 2 * k;
			if (j < N && pq[j+1] < pq[j]){
				j++;
			}
			if (pq[k]<=pq[j]){
				break;
			}
			swap(k,j);
			k = j;
		}
	}

	public void insert(Integer v){
		pq[++N] = v;
		swim(N);
	}

	public Integer delMin(){
		Integer min = pq[1];
		//和最后一个节点交换
		swap(1,N);
		N--;
		pq[N+1] = null;
		sink(1);
		return min;
	}

	public static void main(String[] args){
		//去找最大的M个元素
		MinPQ minPQ = new MinPQ(11);
		Random random = new Random();
		Integer[] a = {1,8,10,9,14,15,7,3,6,0,12,13,2,4,5,19,20,16,11,17};
		for (int i =0;i<20;i++){
			minPQ.insert(a[i]);
			if (minPQ.size() > 10){
				minPQ.delMin();   //删除最小的元素
			}
		}
		while (!minPQ.isEmpty()){
			System.out.println(minPQ.delMin());
		}
	}
}
