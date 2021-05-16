package com.xupt.service.impl;

import com.xupt.beans.Cargo;
import com.xupt.beans.Cart;
import com.xupt.beans.Order;
import com.xupt.beans.Type;
import com.xupt.mapper.CargoMapper;
import com.xupt.service.CargoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Auther: yhn
 * @Date: 2021/5/5 12:14
 */

@Service
public class CargoServiceImpl implements CargoService {

    @Autowired
    private CargoMapper cargoMapper;


    @Override
    public void insertCargo(Cargo cargo) {
        cargoMapper.insertCargo(cargo, new Date());
    }

    @Override
    public void updateCargo(Cargo cargo) {
        cargoMapper.updateCargo(cargo);
    }

    @Override
    public List<Cargo> queryAllCargo(String name, String type, String status) {
        return cargoMapper.queryAllCargo(name, type, status);
    }

    @Override
    public List<Type> queryType() {
        return cargoMapper.queryType();
    }

    @Override
    public List<String> queryAddress() {
        return cargoMapper.queryAddress();
    }

    @Override
    public void addAddress(String addressName) {
        cargoMapper.addAddress(addressName);
    }

    @Override
    public Cargo queryCargoById(int cargoId) {
        return cargoMapper.queryCargoById(cargoId);
    }

    @Override
    public void insertOrder(Order order) {
        cargoMapper.insertOrder(order, new Date());
    }

    @Override
    public List<Order> queryAllOrder(String openId, String status) {
        return cargoMapper.queryAllOrder(openId, status);
    }

    @Override
    public void changeOrderStatus(Integer id, String status) {
        cargoMapper.changeOrderStatus(id, status);
    }

    @Override
    public Order queryOrderById(Integer id) {
        return cargoMapper.queryOrderById(id);
    }

    @Override
    public Cart queryCartByOpenId(String openId) {
        return cargoMapper.queryCartByOpenId(openId);
    }

    @Override
    public void addCart(Cart cart) {
        cargoMapper.addCart(cart);
    }

    @Override
    public void updateCart(Cart cart) {
        cargoMapper.updateCart(cart);
    }

    @Override
    public void deleteCart(String openId) {
        cargoMapper.deleteCart(openId);
    }
}
