package com.xupt.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auther: yhn
 * @Date: 2021/5/10 22:15
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderAndCartWrapper {

    private String openId;

    private String name;

    private String phone;

    private String cargoIds;

    private String cargoNumbs;

    private String address;

    private String type;
}
