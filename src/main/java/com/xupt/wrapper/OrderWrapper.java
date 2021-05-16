package com.xupt.wrapper;

import com.xupt.beans.Cargo;
import com.xupt.beans.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Auther: yhn
 * @Date: 2021/5/9 15:25
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderWrapper {

    private Order order;

    private List<Cargo> cargoList;

}
