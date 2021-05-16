package com.xupt.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auther: yhn
 * @Date: 2021/5/6 20:07
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    private Integer id;

    private String openId;

    private String orderId;

    private String name;

    private String phone;

    private String cargoIds;//商品id

    private String cargoNumbs;//每件商品对应数量

    private Integer numbs;//件数

    private Double totalPrice;//总额

    private String status;

    private String address;

    private String insertTime;

    private String updateTime;
}
