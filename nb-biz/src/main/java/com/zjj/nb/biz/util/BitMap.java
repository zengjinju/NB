package com.zjj.nb.biz.util;

/**
 * 用于快速查找，去重，排序，压缩数据
 * @author zengjinju
 * @date 2019/1/30 下午4:03
 */
public class BitMap {
	private static final int num = 10000000;
	private int[] bitMap = new int[1 + num / 32];

	/**
	 * 添加元素每个数组里面可以包含32个数
	 * a[0]=0~31,
	 * a[1]=32~63
	 *   .
	 *   .
	 *   .
	 * a[n]=n*32+(0~31)
	 * @param n
	 */
	public void add(int n){
		int row = n / 32;
		bitMap[row] |= 1 << (n & (32-1));
	}

	/**
	 * 判断是否包含元素
	 * @param n
	 * @return
	 */
	public Boolean contain(int n){
		int row = n / 32;
		return (bitMap[row] & (1 << (n & (32-1)))) != 0;
	}

	public static void main(String[] args){
		int[] a={1,5,30,32,64,56,159,120,21,17,35,45};
		BitMap bit = new BitMap();
		for (int i=0;i<a.length;i++){
			bit.add(a[i]);
		}
		System.out.println(bit.contain(31));
	}
}
