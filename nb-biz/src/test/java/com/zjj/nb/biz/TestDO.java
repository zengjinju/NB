package com.zjj.nb.biz;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by jinju.zeng on 17/2/24.
 */
public class TestDO {
    private String hostName;
    private String signPort;
    private String transferPort;
    private String cis;
    private String id;
    private String bankNo;
    private String bankCode;
    private String bankName;
    private String bankCardNo;
    @JSONField(serialize = false)
    private String bankCardName;

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getSignPort() {
        return signPort;
    }

    public void setSignPort(String signPort) {
        this.signPort = signPort;
    }

    public String getTransferPort() {
        return transferPort;
    }

    public void setTransferPort(String transferPort) {
        this.transferPort = transferPort;
    }

    public String getCis() {
        return cis;
    }

    public void setCis(String cis) {
        this.cis = cis;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCardNo() {
        return bankCardNo;
    }

    public void setBankCardNo(String bankCardNo) {
        this.bankCardNo = bankCardNo;
    }

    public String getBankCardName() {
        return bankCardName;
    }

    public void setBankCardName(String bankCardName) {
        this.bankCardName = bankCardName;
    }

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }
}
