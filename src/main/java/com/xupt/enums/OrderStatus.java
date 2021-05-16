package com.xupt.enums;

/**
 * @Auther: yhn
 * @Date: 2021/5/6 23:49
 */
public enum OrderStatus {
    NEW("待发货"),
    SEND("已发货"),
    RETURN("退货中"),
    RETURNSUCCESS("退货成功"),
    SUCCESS("成功"),
    CANCEL("已取消");

    private String status;

    OrderStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
