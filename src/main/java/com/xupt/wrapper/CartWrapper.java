package com.xupt.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auther: yhn
 * @Date: 2021/5/10 21:16
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartWrapper {

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

    //数量
    private Integer numbs;
}
