package com.zjj.nb.biz.transfer;

/**
 * Created by admin on 2017/12/1.
 */
public class RdDTO {
	private String paySysBnkCode;
	private String bnkName;
	private String code;
	private String parentName;
	private String areaCode;

	public String getPaySysBnkCode() {
		return paySysBnkCode;
	}

	public void setPaySysBnkCode(String paySysBnkCode) {
		this.paySysBnkCode = paySysBnkCode;
	}

	public String getBnkName() {
		return bnkName;
	}

	public void setBnkName(String bnkName) {
		this.bnkName = bnkName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
}
