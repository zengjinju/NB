package com.zjj.nb.biz.manager.redis;

import com.google.common.hash.Funnels;
import com.google.common.hash.Hashing;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.Charset;

/**
 * 利用Redis的BitMap实现分布式布隆过滤器
 * @author zengjinju
 * @date 2018/12/29 下午2:47
 */
@Service
public class BloomFilter {

	@Resource
	private JedisPool jedisPool;

	/**预计插入量*/
	private Long expectedInsertions = 1000L;

	/**可接受的错误率*/
	private Double fpp = 0.001D;

	private String prefix = "bf:";

	/**数组长度*/
	private Long numBits = optimalNumOfBits(expectedInsertions,fpp);

	/**Hash函数数量*/
	private int numHashFunctions = optimalNumOfHashFunctions(expectedInsertions,numBits);

	private Jedis jedis;

	public void setExpectedInsertions(Long expectedInsertions) {
		this.expectedInsertions = expectedInsertions;
	}

	public void setFpp(Double fpp) {
		this.fpp = fpp;
	}

	/**
	 * 参考guava实现
	 * @param n
	 * @param p
	 * @return
	 */
	private Long optimalNumOfBits(Long n,Double p){
		if (p == 0){
			p = Double.MIN_NORMAL;
		}
		return (long)(-n * Math.log(p) / (Math.log(2) * Math.log(2)));
	}

	/**
	 * 参考guava实现
	 * @param n
	 * @param m
	 * @return
	 */
	private int optimalNumOfHashFunctions(long n, long m) {
		// (m / n) * log(2), but avoid truncation due to division!
		return Math.max(1, (int) Math.round((double) m / n * Math.log(2)));
	}

	/**
	 * 根据key获取bitmap下标
	 * @param key
	 * @return
	 */
	private long[] getIndexs(String key){
		long hash1 = hash(key);
		long hash2 = hash1 >>> 16;
		long[] result = new long[numHashFunctions];
		for (int i=0;i<numHashFunctions; i++){
			long combinedHash = hash1 + i * hash2;
			if (combinedHash < 0){
				combinedHash = ~ combinedHash;
			}
			result[i] = combinedHash % numBits;
		}
		return result;
	}

	private long hash(String key){
		Charset charset = Charset.forName("UTF-8");
		return Hashing.murmur3_128().hashObject(key, Funnels.stringFunnel(charset)).asLong();
	}

	/**
	 * 判断key是否存在集合中
	 * @param redisKey
	 * @param key
	 * @return
	 */
	public Boolean isExit(String redisKey,String key){
		long[] indexs = getIndexs(key);
		Boolean result = Boolean.FALSE;
		jedis = jedisPool.getResource();
		jedis.select(1);
		//这里使用了Redis管道来降低过滤器运行当中访问Redis次数
		Pipeline pipeline = jedis.pipelined();
		try {
			for (long index : indexs){
				pipeline.getbit(prefix + redisKey,index);
			}
			result = !pipeline.syncAndReturnAll().contains(false);
		}finally {
		}
		return result;
	}

	/**
	 * 将key保持到bitmap中
	 * @param redisKey
	 * @param key
	 */
	public void put(String redisKey,String key){
		long[] indexs = getIndexs(key);
		jedis = jedisPool.getResource();
		jedis.select(1);
		Pipeline pipeline = jedis.pipelined();
		try {
			for (long index : indexs){
				pipeline.setbit(prefix + redisKey,index,true);
			}
			pipeline.sync();
		}finally {

		}
	}
}
