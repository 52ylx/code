package com.lx.wx.entity;

import com.lx.util.LX;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by 游林夕 on 2019/10/26.
 */
public class Show {
    private String add_time,tx_time,title,name;
    private Status status;
    private BigDecimal totalPay,fx,yj;
    public enum Status{
        付款,结算,提现,总提现
    }
    public Show(){}
    public Show(String name,String add_time, String title, BigDecimal t, BigDecimal y){
        this.name = name;
        this.title = title;
        this.add_time = add_time;
        this.status = status;
        this.totalPay = t==null? LX.getBigDecimal(0):t;
        this.yj = totalPay.multiply(y==null? LX.getBigDecimal(0):y).divide(LX.getBigDecimal(1000),2, BigDecimal.ROUND_HALF_DOWN);
        this.fx = this.yj.multiply(LX.getBigDecimal(50)).divide(LX.getBigDecimal(100),2, BigDecimal.ROUND_HALF_DOWN);

    }

    public String getTx_time() {
        return tx_time;
    }

    public void setTx_time(String tx_time) {
        this.tx_time = tx_time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public BigDecimal getFx() {
        return fx;
    }

    public void setFx(BigDecimal fx) {
        this.fx = fx.setScale(2, RoundingMode.CEILING);
    }

    public BigDecimal getTotalPay() {
        return totalPay;
    }

    public void setTotalPay(BigDecimal totalPay) {
        this.totalPay = totalPay;
    }

    public BigDecimal getYj() {
        return yj;
    }

    public void setYj(BigDecimal yj) {
        this.yj = yj;
    }

    @Override
    public String toString() {
        return "Show{" +
                "add_time='" + add_time + '\'' +
                ", tx_time='" + tx_time + '\'' +
                ", title='" + title + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", totalPay=" + totalPay +
                ", fx=" + fx +
                ", yj=" + yj +
                '}';
    }
}
