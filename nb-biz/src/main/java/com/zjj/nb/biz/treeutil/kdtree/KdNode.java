package com.zjj.nb.biz.treeutil.kdtree;

/**
 * Created by admin on 2017/12/7.
 */
public class KdNode {
	/**
	 * 关键点直方图方差最大向量系列位置
	 * 向量中方差最大的维度，用来分割平面
	 * 用于分割的维度
	 */
	private int ki;

	/**
	 * 直方图方差最大向量系列中最中间模值
	 * 该维度split域的值
	 */
	private double split;

	/**
	 * 1 表示叶节点，0 其他节点
	 */
	private int leaf=0;

	/**K
	 * 左节点
	 */
	private KdNode kd_left;

	/**
	 * 右节点
	 */
	private KdNode kd_right;

	private KdNode parent;

	/**
	 * 当前分割的特征点
	 */
	private KdNodeFeature kd_feature;

	/**
	 * 在节点查找时判断是否已经比较过
	 */
	private Boolean isCompared=Boolean.FALSE;

	public int getKi() {
		return ki;
	}

	public void setKi(int ki) {
		this.ki = ki;
	}

	public double getSplit() {
		return split;
	}

	public void setSplit(double split) {
		this.split = split;
	}

	public int getLeaf() {
		return leaf;
	}

	public void setLeaf(int leaf) {
		this.leaf = leaf;
	}

	public KdNode getKd_left() {
		return kd_left;
	}

	public void setKd_left(KdNode kd_left) {
		this.kd_left = kd_left;
	}

	public KdNode getKd_right() {
		return kd_right;
	}

	public void setKd_right(KdNode kd_right) {
		this.kd_right = kd_right;
	}

	public KdNodeFeature getKd_feature() {
		return kd_feature;
	}

	public void setKd_feature(KdNodeFeature kd_feature) {
		this.kd_feature = kd_feature;
	}

	public Boolean getCompared() {
		return isCompared;
	}

	public void setCompared(Boolean compared) {
		isCompared = compared;
	}

	public KdNode getParent() {
		return parent;
	}

	public void setParent(KdNode parent) {
		this.parent = parent;
	}
}
