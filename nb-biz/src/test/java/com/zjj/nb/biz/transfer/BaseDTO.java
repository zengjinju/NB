package com.zjj.nb.biz.transfer;

import java.util.Date;

/**
 * Created by jinju.zeng on 2017/9/1.
 */
public class BaseDTO {
    /**
     * 交易代码
     */
    private String transCode;

    /**
     * 集团CIS号
     */
    private String cis;

    /**
     * 归属银行编码
     */
    private String bankCode;

    /**
     * 证书id
     */
    private String id;

    /**
     * 交易日期
     */
    private Date tranDate;

    /**
     * 交易时间
     */
    private Date tranTime;

    /**
     * 指令包序列号
     */
    private String packageId;

    public String getTransCode() {
        return transCode;
    }

    public void setTransCode(String transCode) {
        this.transCode = transCode;
    }

    public String getCis() {
        return cis;
    }

    public void setCis(String cis) {
        this.cis = cis;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTranDate() {
        return tranDate;
    }

    public void setTranDate(Date tranDate) {
        this.tranDate = tranDate;
    }

    public Date getTranTime() {
        return tranTime;
    }

    public void setTranTime(Date tranTime) {
        this.tranTime = tranTime;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }
}
