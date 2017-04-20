package com.doan.thongbaodiemdung;

/**
 * Created by HongHa on 4/20/2017.
 */

public class Account {

    private String id;
    private String name;

    public Account()
    {

    }

    public Account(String id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }
}
