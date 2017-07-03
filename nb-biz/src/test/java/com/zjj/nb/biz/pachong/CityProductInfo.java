package com.zjj.nb.biz.pachong;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by admin on 2017/6/27.
 */
public class CityProductInfo {

    /**
     * 省份id
     */
    private String provinceId;

    private String provinceName;

    /**
     * 城市id
     */
    private String cityId;

    private String cityName;

    /**
     * 区
     */
    private String district;

    /**
     *公司名称
     */
    private String entName;

    /**
     * 公司所在地址
     */
    private String entAddress;

    /**
     * 公司电话
     */
    private String entPhone;

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getEntName() {
        return entName;
    }

    public void setEntName(String entName) {
        this.entName = entName;
    }

    public String getEntAddress() {
        return entAddress;
    }

    public void setEntAddress(String entAddress) {
        this.entAddress = entAddress;
    }

    public String getEntPhone() {
        return entPhone;
    }

    public void setEntPhone(String entPhone) {
        this.entPhone = entPhone;
    }
}
