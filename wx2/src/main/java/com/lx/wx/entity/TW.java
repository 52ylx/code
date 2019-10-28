package com.lx.wx.entity;

/**
 * Created by 游林夕 on 2019/10/25.
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
    public TW(){}

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "TW{" +
                "text='" + text + '\'' +
                ", description='" + description + '\'' +
                ", picUrl='" + picUrl + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
