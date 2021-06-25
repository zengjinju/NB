package com.zjj.nb.biz.sort.string;

/**
 * 三向切分的字符串快速排序，通用排序
 * @author zengjinju
 * @date 2019/12/18 下午4:16
 */
public class Quick3Sort {

	public static void sort(String[] a){
		sort(a,0,a.length - 1,0);
	}

	private static void sort(String[] a,int lo,int hi,int d){
		if (hi <=lo){
			return;
		}
		int lt = lo,gt = hi;
		int v =charAt(a[lo],d);
		int i = lo + 1;
		//在d这个位置上的字符 lt左边的都是比v小的元素，gt右边的都是比v大的元素，lt~gt之间的都是和v相等的元素
		while (i <= gt){
			int t = charAt(a[i],d);
			if (t < v){
				swap(a,lt++,i++);
			} else if (t > v){
				swap(a,i,gt--);
			} else {
				i++;
			}
		}
		sort(a,lo,lt-1,d);
		if (v >=0){
			sort(a,lt,gt,d+1);
		}
		sort(a,gt+1,hi,d);
	}


	private static int charAt(String a,int d){
		return d < a.length() ? a.charAt(d) : -1;
	}

	private static void swap(String[] a, int i,int j){
		String b = a[j];
		a[j] = a[i];
		a[i] = b;
	}

	public static void main(String[] args){
		String[] a = {"dcbefg","aa123","kfdjkfjdkjfkd","ngvjgnfvjfhnf","er","cdfg","fh","ghghg","1234","bvg","hiyutr","ilkgh","hhab","ibcfg","jjjj","lmn","mln","mlxg","mlgh","mlabc","nbgh","ncfghiyu","kdkdkndkndhajdkdnvjd","ncbhdgnbgdg","abcefg","bbccff"};
		sort(a);
		System.out.println(a);
	}
}
