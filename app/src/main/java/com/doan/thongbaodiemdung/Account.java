package com.doan.thongbaodiemdung;

/**
 * Created by HongHa on 4/20/2017.
 */

public class Account {

    private String id;
    private String name;
    private String avatarURL;

    public Account()
    {

    }

    public Account(String id, String name, String avatarURL)
    {
        this.id = id;
        this.name = name;
        this.avatarURL = avatarURL;
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

    public String getAvatarURL() {return avatarURL;}
}
