package com.zcf.weather.db;

import org.litepal.crud.DataSupport;

public class Province extends DataSupport{
    private int id;
    private String pName;
    private int pCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public int getpCode() {
        return pCode;
    }

    public void setpCode(int pCode) {
        this.pCode = pCode;
    }
}
