package com.lx.entity;

/**
 * Created by 游林夕 on 2019/10/24.
 */
public class TW extends GZH{
    //标题,解释,图片地址,点击地址
    public String description,picUrl,url;
    public TW(String text, String description, String picUrl, String url) {
        this.text = text;
        this.description = description;
        this.picUrl = picUrl;
        this.url = url;
    }

    @Override
    public String toString() {
        return "TW{" +
                " text='" + text + '\'' +
                ", description='" + description + '\'' +
                ", picUrl='" + picUrl + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
