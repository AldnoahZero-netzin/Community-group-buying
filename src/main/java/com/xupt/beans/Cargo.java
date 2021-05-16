package com.xupt.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auther: yhn
 * @Date: 2021/5/5 10:11
 *
 * 商品类
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cargo {

    private Integer id;

    private String name;

    private String detail;

    private String type;

    private Double price;

    private Integer weight;

    private Integer stock;

    private Integer sales;

    private String status;

    private String imageUrl;

    private String insertTime;

    private String updateTime;
}
