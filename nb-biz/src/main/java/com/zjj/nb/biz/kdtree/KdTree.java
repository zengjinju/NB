package com.zjj.nb.biz.kdtree;

import com.zjj.nb.biz.util.MathUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

/**
 * Created by admin on 2017/12/7.
 */
public class KdTree {
	private KdNode kdNode;

	private KdTree() {
	}

	public static KdTree build(List<KdNodeFeature> datas) {
		if (CollectionUtils.isEmpty(datas)) {
			return null;
		}
		KdTree tree = new KdTree();
		tree.kdNode = new KdNode();
		tree.build(tree.kdNode, datas, 0);
		return tree;
	}

	/**
	 * 循环构建kd-tree
	 *
	 * @param node
	 * @param datas
	 * @param dimentions 父节点分割的维度
	 */
	public void build(KdNode node, List<KdNodeFeature> datas, int dimentions) {
		if (datas == null || datas.size() == 0) {
			return;
		}
		//如果数据集的大小为1，则确定为叶子节点
		if (datas.size() <= 1) {
			node.setLeaf(1);
			node.setKd_feature(datas.get(0));
			//叶子节点，设置父节点的分割维度
			node.setKi(dimentions);
			node.setSplit(node.getKd_feature().getHash_vector()[node.getKi()]);
			return;
		}
		//分割维度
		dimentions = getSplitDimentions(datas);
		node.setKi(dimentions);
		//确定分割值
		final int diment = dimentions;
		Collections.sort(datas, new Comparator<KdNodeFeature>() {
			@Override
			public int compare(KdNodeFeature o1, KdNodeFeature o2) {
				if (o1.getHash_vector()[diment] > o2.getHash_vector()[diment]) {
					return 1;
				} else if (o1.getHash_vector()[diment] < o2.getHash_vector()[diment]) {
					return -1;
				} else {
					return 0;
				}
			}
		});
		//获取中间数的位置
		int median = (int) Math.floor(datas.size() >> 1);
		node.setKd_feature(datas.get(median));
		node.setSplit(datas.get(median).getHash_vector()[dimentions]);
		//循环生成子树
		List<KdNodeFeature> left = datas.subList(0, median);
		List<KdNodeFeature> right = datas.subList(median + 1, datas.size());
		if (left.size() > 0) {
			KdNode leftNode = new KdNode();
			node.setKd_left(leftNode);
			build(leftNode, left, dimentions);
		}
		if (right.size() > 0) {
			KdNode rightNode = new KdNode();
			node.setKd_right(rightNode);
			build(rightNode, right, dimentions);
		}
	}

	public KdNodeFeature find(KdNodeFeature feature) {
		KdNode node = kdNode;
		//回溯查找堆栈
		Stack<KdNode> stack = new Stack<>();
		stack.push(node);
		double[] source = feature.getHash_vector();
		//循环查找直到叶子节点
		while (node != null && node.getLeaf() == 0) {
			if (source[node.getKi()] <= node.getSplit()) {
				if (node.getKd_left() != null) {
					stack.push(node.getKd_left());
					node = node.getKd_left();
				} else {
					break;
				}
			} else {
				if (node.getKd_right() != null) {
					stack.push(node.getKd_right());
					node = node.getKd_right();
				} else {
					break;
				}
			}
		}
		//计算相对较近的叶子节点到查询节点的距离
		double distance = MathUtils.euclidDistance(source, node.getKd_feature().getHash_vector());
		return queryBackTracking(source, distance, node, stack);
	}

	private KdNodeFeature queryBackTracking(double[] source, double minDistance, KdNode nearest, Stack<KdNode> stack) {
		KdNode node = null;
		double tdis;
		while (stack.size() != 0) {
			//弹出相对较近点的上一级节点
			node = stack.pop();
			tdis = MathUtils.euclidDistance(source, node.getKd_feature().getHash_vector());
			if (tdis <= minDistance) {
				minDistance = tdis;
				nearest = node;
				if (node.getKd_left() != null) {
					stack.push(node.getKd_left());
				}
				if (node.getKd_right() != null) {
					stack.push(node.getKd_right());
				}
			}
			//判读在维度k上|Q(k)-split|是否小于最小值,如果小于说明以最小值半径的球面和以维度k所在的平面有交集
			if (node.getLeaf() == 0 && Math.abs(source[node.getKi()] - node.getSplit()) < minDistance) {
				//如果source在维度k的值小于node的值，则到node的右子空间进行查询
				if(source[node.getKi()]<=node.getSplit()) {
					if (node.getKd_right() != null && !stack.contains(node.getKd_right())) {
						stack.push(node.getKd_right());
					}
				}else {
					if (node.getKd_left()!=null && !stack.contains(node.getKd_left())) {
						stack.push(node.getKd_left());
					}
				}
			}
		}
		return nearest.getKd_feature();
	}

	/**
	 * 获取分割的维度
	 *
	 * @param datas
	 * @return
	 */
	private int getSplitDimentions(List<KdNodeFeature> datas) {
		List<double[]> list = new ArrayList<>();
		for (KdNodeFeature kd : datas) {
			list.add(kd.getHash_vector());
		}
		int dimentions = list.get(0).length;
		//计算方差
		double vars = 0.0D;
		double temp_vars = 0.0D;
		int dimen = 0;
		for (int i = 0; i < dimentions; i++) {
			temp_vars = MathUtils.variance(list, i);
			if (temp_vars > vars) {
				vars = temp_vars;
				dimen = i;
			}
		}
		return dimen;
	}

	public void insertNode(KdNodeFeature feature){
		if(kdNode==null){
			return;
		}
		KdNode node=kdNode;
		double[] source=feature.getHash_vector();
		while (node!=null){{
			if(source[node.getKi()]<=node.getSplit()){
				if(node.getKd_left()!=null){
					node=node.getKd_left();
				}else{
					//第一个节点
					if(node.getKd_feature()==null) {
						node.setSplit(source[node.getKi()]);
						node.setKd_feature(feature);
					}else{
						KdNode newNode = new KdNode();
						newNode.setKi(node.getKi());
						newNode.setSplit(source[node.getKi()]);
						newNode.setKd_feature(feature);
						node.setKd_left(newNode);
					}
				}
			}else{

			}
		}}
	}


	public static void main(String[] args) {
		Random random = new Random();
		List<KdNodeFeature> list = new ArrayList<>();
		KdNodeFeature f1 = new KdNodeFeature();
		double[] d1 = {2, 3}, d2 = {5, 4}, d3 = {9, 6}, d4 = {4, 7}, d5 = {8, 1}, d6 = {7, 2};
		f1.setHash_vector(d1);
		KdNodeFeature f2 = new KdNodeFeature();
		f2.setHash_vector(d2);
		KdNodeFeature f3 = new KdNodeFeature();
		f3.setHash_vector(d3);
		KdNodeFeature f4 = new KdNodeFeature();
		f4.setHash_vector(d4);
		KdNodeFeature f5 = new KdNodeFeature();
		f5.setHash_vector(d5);
		KdNodeFeature f6 = new KdNodeFeature();
		f6.setHash_vector(d6);
		list.add(f1);
		list.add(f2);
		list.add(f3);
		list.add(f4);
		list.add(f5);
		list.add(f6);
		KdTree tree = build(list);
		double[] d={8,3};
		KdNodeFeature feature1=new KdNodeFeature();
		feature1.setHash_vector(d);
		KdNodeFeature feature=tree.find(feature1);
		System.out.println(feature);


	}
}
