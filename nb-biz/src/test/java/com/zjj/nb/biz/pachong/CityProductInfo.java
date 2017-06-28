package com.zjj.nb.biz.pachong;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by admin on 2017/6/27.
 */
@Getter
@Setter
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
}
