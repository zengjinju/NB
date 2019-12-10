package com.zjj.nb.biz.sort;

/**
 * 最大优先队列
 * @author zengjinju
 * @date 2019/12/2 下午4:48
 */
public class MaxPriorityQueue {
	private Integer[] pq;
	private int N = 0;

	public MaxPriorityQueue(int n){
		pq = new Integer[n+1];        //存储于pq[1...N]中，pq[0]不使用
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
		while(k > 1 && pq[k/2] < pq[k]){
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
			if (j < N && pq[j] < pq[j+1]){
				j++;
			}
			if (pq[k]>=pq[j]){
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

	public Integer delMax(){
		Integer max = pq[1];
		//和最后一个节点交换
		swap(1,N);
		N--;
		pq[N+1] = null;
		sink(1);
		return max;
	}


	public static void main(String[] args){
		MaxPriorityQueue pq = new MaxPriorityQueue(10);
		for (int i=0;i<10;i++){
			pq.insert(i);
		}
		System.out.println(pq.delMax());
	}
}
