package com.xupt.service;


import com.xupt.beans.Cargo;
import com.xupt.beans.Cart;
import com.xupt.beans.Order;
import com.xupt.beans.Type;

import java.util.List;

/**
 * @Auther: yhn
 * @Date: 2021/5/5 12:13
 */

public interface CargoService {

    void insertCargo(Cargo cargo);

    void updateCargo(Cargo cargo);

    List<Cargo> queryAllCargo(String name, String type, String status);

    List<Type> queryType();

    List<String> queryAddress();

    void addAddress(String addressName);

    Cargo queryCargoById(int cargoId);

    void insertOrder(Order order);

    List<Order> queryAllOrder(String openId, String status);

    void changeOrderStatus(Integer id, String status);

    Order queryOrderById(Integer id);

    Cart queryCartByOpenId(String openId);

    void addCart(Cart cart);

    void updateCart(Cart cart);

    void deleteCart(String openId);
}
