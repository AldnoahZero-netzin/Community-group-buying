package com.xupt.listen;

import org.springframework.stereotype.Component;

/**
 * @Auther: yhn
 * @Date: 2021/5/6 20:46
 */

@Component
public class Note {

    //新订单数量
    public Integer newOrderNumbs = 0;

    //新取消的订单数量
    public Integer cancelOrderNumbs = 0;

    //新退货订单数量
    public Integer backOrderNumbs = 0;
}
