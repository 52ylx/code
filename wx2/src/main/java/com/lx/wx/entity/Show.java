package com.lx.wx.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by 游林夕 on 2019/10/26.
 */
public class Show {
    private String add_time,title;
    private Order.Status status;
    private BigDecimal fx;

    public Show(){}
    public Show(Order order){
        this.add_time = order.getAdd_time();
        this.title = order.getTitle();
        this.status = order.getStatus();
        this.fx = order.getFx();
    }
    public String getAdd_time() {
        return add_time;
    }
    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title!=null & title.length()>30){
            title = title.substring(0,30);
        }
        this.title = title;
    }

    public Order.Status getStatus() {
        return status;
    }

    public void setStatus(Order.Status status) {
        this.status = status;
    }

    public BigDecimal getFx() {
        return fx;
    }

    public void setFx(BigDecimal fx) {
        this.fx = fx.setScale(2, RoundingMode.CEILING);
    }

    @Override
    public String toString() {
        return "Show{" +
                "time='" + add_time + '\'' +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                ", fx='" + fx + '\'' +
                '}';
    }
}
