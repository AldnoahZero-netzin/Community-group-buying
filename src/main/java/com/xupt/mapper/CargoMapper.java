package com.xupt.mapper;

import com.xupt.beans.Cargo;
import com.xupt.beans.Cart;
import com.xupt.beans.Order;
import com.xupt.beans.Type;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @Auther: yhn
 * @Date: 2021/5/5 12:14
 */

@Mapper
@Repository
public interface CargoMapper {


    void insertCargo(@Param("cargo") Cargo cargo, @Param("insertTime") Date date);

    void updateCargo(@Param("cargo") Cargo cargo);

    List<Cargo> queryAllCargo(@Param("name") String name, @Param("type") String type, @Param("status") String status);

    List<Type> queryType();

    List<String> queryAddress();

    void addAddress(@Param("addressName") String addressName);

    Cargo queryCargoById(@Param("cargoId") int cargoId);

    void insertOrder(@Param("order") Order order, @Param("insertTime") Date date);

    List<Order> queryAllOrder(@Param("openId") String openId, @Param("status") String status);

    void changeOrderStatus(@Param("id") Integer id, @Param("status") String status);

    Order queryOrderById(@Param("id") Integer id);

    Cart queryCartByOpenId(@Param("openId") String openId);

    void addCart(@Param("cart") Cart cart);

    void updateCart(@Param("cart") Cart cart);

    void deleteCart(@Param("openId") String openId);
}
