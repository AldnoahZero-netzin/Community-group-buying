package com.xupt.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auther: yhn
 * @Date: 2021/5/15 17:43
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderNumbs {

    private Integer all;

    private Integer notShipped;

    private Integer toBeReceived;

    private Integer refund;
}
