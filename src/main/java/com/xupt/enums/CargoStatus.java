package com.xupt.enums;

/**
 * @Auther: yhn
 * @Date: 2021/5/7 23:56
 */
public enum CargoStatus {
    UP("已上架"),
    DOWN("已下架");

    public String getStatus() {
        return status;
    }

    CargoStatus(String status) {
        this.status = status;
    }

    private String status;
}
