package com.zjj.nb.biz.pachong;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by admin on 2017/6/27.
 */
public class CityInfo {

    private String name;
    private String locationId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }
}
