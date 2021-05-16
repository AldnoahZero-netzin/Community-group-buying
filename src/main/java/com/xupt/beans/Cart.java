package com.xupt.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auther: yhn
 * @Date: 2021/5/9 12:17
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart {

    private Integer id;

    private String openId;

    private String cargoIds;

    private String cargoNumbs;
}
