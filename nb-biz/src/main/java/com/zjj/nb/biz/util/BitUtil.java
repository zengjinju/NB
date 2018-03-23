package com.zjj.nb.biz.util;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

/**
 * Created by admin on 2018/3/21.
 */
public class BitUtil {

	public void swap(int a, int b) {
		a ^= b;
		b ^= a;
		a ^= b;
	}

	/**
	 * 绝对值
	 *
	 * @param x
	 * @return
	 */
	public static int abs(int x) {
		int y;
		y = x >> 31;
		return (x ^ y) - y;
	}

	/**
	 * 平均值
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static int average(int a, int b) {
		return (a & b) + ((a ^ b) >> 1);
	}

	/**
	 * 加法运算
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static int add(int a, int b) {
		int res = a;
		int xor = a ^ b;//得到原位和
		int forward = (a & b) << 1;//得到进位和
		if (forward != 0) {//若进位和不为0，则递归求原位和+进位和
			res = add(xor, forward);
		} else {
			res = xor;//若进位和为0，则此时原位和为所求和
		}
		return res;
	}

	/**
	 * 减法运算
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static int minus(int a, int b) {
		int B = ~(b - 1);// ~(b-1)=-b
		return add(a, B);
	}

	/**
	 * 乘法运算
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static long multi(long a, long b) {
		long i = 0;
		long res = 0;
		while (b != 0) {//乘数为0则结束
			//处理乘数当前位
			if ((b & 1) == 1) {
				res += (a << i);
				b = b >> 1;
				++i;//i记录当前位是第几位
			} else {
				b = b >> 1;
				++i;
			}
		}
		return res;
	}

	/**
	 * 除法运算
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static int sub(int a, int b) {
		int res = -1;
		if (a < b) {
			return 0;
		} else {
			res = sub(minus(a, b), b) + 1;
		}
		return res;
	}


	/**
	 * 统计二进制中1的个数
	 *
	 * @param n
	 * @return
	 */
	public static int count_one(long n) {
		//0xAAAAAAAA，0x55555555分别是以“1位”为单位提取奇偶位
		n = ((n & 0xAAAAAAAA) >> 1) + (n & 0x55555555);

		//0xCCCCCCCC，0x33333333分别是以“2位”为单位提取奇偶位
		n = ((n & 0xCCCCCCCC) >> 2) + (n & 0x33333333);

		//0xF0F0F0F0，0x0F0F0F0F分别是以“4位”为单位提取奇偶位
		n = ((n & 0xF0F0F0F0) >> 4) + (n & 0x0F0F0F0F);

		//0xFF00FF00，0x00FF00FF分别是以“8位”为单位提取奇偶位
		n = ((n & 0xFF00FF00) >> 8) + (n & 0x00FF00FF);

		//0xFFFF0000，0x0000FFFF分别是以“16位”为单位提取奇偶位
		n = ((n & 0xFFFF0000) >> 16) + (n & 0x0000FFFF);
		return (int) n;
	}

	public static void main(String[] args) {
		long start = System.nanoTime();
		long a = multi(12345678L, 87654321L);
		long end = System.nanoTime() - start;
		start = System.nanoTime();
		long b = Math.multiplyExact(12345678L, 87654321L);
		long end1 = System.nanoTime() - start;

		System.out.println(end1 - end);
	}
}
