package com.zjj.nb.biz.util;

import java.util.List;

/**
 * Created by admin on 2017/12/7.
 */
public class MathUtils {

	/**
	 * 标准方差计算
	 * @param list
	 * @param dimention
	 * @return
	 */
	public static double variance(List<double[]> list,int dimention){
		int m=list.size();
		double sum=0;
		for(double[] data : list){
			sum+=data[dimention];
		}
		double ave=sum/m;
		double var=0;
		for(double[] d : list){
			var+=(d[dimention]-ave)*(d[dimention]-ave);
		}
		return var/m;
	}

	/**
	 * 余弦相似度
	 * @param a
	 * @param b
	 * @return
	 */
	public static double consineDistance(double[] a,double[] b){
		double molecular=0;
		double sqrt_a=0;
		double sqrt_b=0;
		for(int i=0;i<a.length;i++){
			molecular+=a[i]*b[i];
			sqrt_a+=Math.pow(a[i],2);
			sqrt_b+=Math.pow(b[i],2);
		}
		return molecular/(sqrt_a*sqrt_b);
	}

	/**
	 * 欧几里得距离(两个向量之间的距离)
	 * @param a
	 * @param b
	 * @return
	 */
	public static double euclidDistance(double[] a,double[] b){
		double sum=0;
		for(int i=0;i<a.length;i++){
			sum+=Math.pow(a[i]-b[i],2);
		}
		return Math.sqrt(sum);
	}

	public static void main(String[] args){
		double[] d1={79,54};
		double[] d2={84,49};
		System.out.println(euclidDistance(d1,d2));
	}
}
