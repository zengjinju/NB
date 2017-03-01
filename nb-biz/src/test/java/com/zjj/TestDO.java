package com.zjj;

import lombok.Data;

/**
 * Created by jinju.zeng on 17/2/24.
 */
public class TestDO extends BasicClass implements BasicInterface {

    public TestDO(){

    }
    public TestDO(Integer id,String name){
        this.id=id;
        this.name=name;
    }

    public static TestDO getInstance(){
        return new TestDO();
    }
    private Integer id;

    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void testCode() {

    }
}
