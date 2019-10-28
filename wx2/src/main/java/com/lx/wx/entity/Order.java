package com.lx.wx.entity;//说明:

import com.lx.util.LX;

import java.math.BigDecimal;

/**
 * 创建人:游林夕/2019/5/19 19 27
 */
public class Order {
    public enum Status{
        付款,结算,提现,总提现
    }
    //商品类型 默认淘宝0 拼多多1
    private int type =0;
    //订单号,状态,标题,添加时间,创建时间,提现时间
    private String name,orderNo,title,add_time,qr_time,t_time,numiid;
    private Status status;
    //商品总价,佣金比例
    private BigDecimal totalPay,fx,yj;

    public Order (){}
    public Order(String name,String title, BigDecimal t, BigDecimal y) {
        this.name = name;
        this.title = title;
        this.totalPay = t==null? LX.getBigDecimal(0):t;
        this.yj = totalPay.multiply(y==null? LX.getBigDecimal(0):y).divide(LX.getBigDecimal(1000),2, BigDecimal.ROUND_HALF_DOWN);
        this.fx = this.yj.multiply(LX.getBigDecimal(75)).divide(LX.getBigDecimal(100),2, BigDecimal.ROUND_HALF_DOWN);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getT_time() {
        return t_time;
    }

    public void setT_time(String t_time) {
        this.t_time = t_time;
    }

    public BigDecimal getTotalPay() {
        return totalPay;
    }

    public void setTotalPay(BigDecimal totalPay) {
        this.totalPay = totalPay;
    }

    public BigDecimal getFx() {
        return fx;
    }

    public void setFx(BigDecimal fx) {
        this.fx = fx;
    }

    public BigDecimal getYj() {
        return yj;
    }

    public void setYj(BigDecimal yj) {
        this.yj = yj;
    }

    public String getNumiid() {
        return numiid;
    }

    public void setNumiid(String numiid) {
        this.numiid = numiid;
    }

    public String getQr_time() {
        return qr_time;
    }

    public void setQr_time(String qr_time) {
        this.qr_time = qr_time;
    }
}
