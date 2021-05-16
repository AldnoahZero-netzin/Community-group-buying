package com.xupt.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xupt.beans.Cargo;
import com.xupt.beans.Cart;
import com.xupt.beans.Order;
import com.xupt.enums.Code;
import com.xupt.enums.OrderStatus;
import com.xupt.listen.Note;
import com.xupt.service.CargoService;
import com.xupt.util.Result;
import com.xupt.wrapper.CartWrapper;
import com.xupt.wrapper.OrderAndCartWrapper;
import com.xupt.wrapper.OrderNumbs;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @Auther: yhn
 * @Date: 2021/5/6 18:41
 */

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private CargoService cargoService;

    @Autowired
    private Note note;

//    //登录，保存用户id
//    @GetMapping("/login")
//    public Result login(HttpServletRequest request, HttpServletResponse response, @RequestParam("openId") String openId) {
//        return Result.success(null);
//    }

    //生成订单
    @PostMapping("/generateOrder")
    public Result generateOrder(@RequestBody OrderAndCartWrapper oac) {
        String openId = oac.getOpenId();
        String cargoIds = oac.getCargoIds();
        String cargoNumbs = oac.getCargoNumbs();
        String address = oac.getAddress();
        String name = oac.getName();
        String phone = oac.getPhone();
        if (ObjectUtils.isEmpty(openId)) {
            return Result.error("openId不能为空", Code.PARAMETEREMPTY.getCode());
        }
        if (ObjectUtils.isEmpty(cargoIds)) {
            return Result.error("商品id不能为空", Code.PARAMETEREMPTY.getCode());
        }
        if (ObjectUtils.isEmpty(cargoNumbs)) {
            return Result.error("商品数量不能为空", Code.PARAMETEREMPTY.getCode());
        }
        if (ObjectUtils.isEmpty(address)) {
            return Result.error("收货地址不能为空", Code.PARAMETEREMPTY.getCode());
        }
        if (ObjectUtils.isEmpty(name)) {
            return Result.error("收货人名字不能为空", Code.PARAMETEREMPTY.getCode());
        }
        if (ObjectUtils.isEmpty(phone)) {
            return Result.error("手机号不能为空", Code.PARAMETEREMPTY.getCode());
        }
        String[] split = cargoIds.split(",");
        String[] split1 = cargoNumbs.split(",");
        if (split.length != split1.length) {
            return Result.error("商品数量与ID不一致", Code.PARAMETERERROR.getCode());
        }
        //查看库存是否充足
        List<Integer> list = new ArrayList<>();
        double totalPrice = 0.0;
        int totalNumbs = 0;
        List<Integer> saleList = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            int cargoId = Integer.parseInt(split[i]);
            int numbs = Integer.parseInt(split1[i]);
            totalNumbs += numbs;
            Cargo cargo = cargoService.queryCargoById(cargoId);
            saleList.add(cargo.getSales());
            int stock = cargo.getStock() - cargo.getWeight() * numbs;
            if (stock < 0) {
                return Result.error(cargo.getName() + "库存不足，请重新选择", Code.STOCKNOTENOUGH.getCode());
            }
            list.add(stock);
            totalPrice += cargo.getPrice() * numbs;
        }
        //减库存加销量
        for (int i = 0; i < split.length; i++) {
            Cargo cargo = new Cargo();
            cargo.setId(Integer.parseInt(split[i]));
            cargo.setStock(list.get(i));
            cargo.setSales(Integer.parseInt(split1[i]) + saleList.get(i));
            cargoService.updateCargo(cargo);
        }
        //生成订单
        Order order = new Order();
        order.setOpenId(openId);
        order.setOrderId(UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        order.setName(name);
        order.setPhone(phone);
        order.setCargoIds(cargoIds);
        order.setCargoNumbs(cargoNumbs);
        order.setNumbs(totalNumbs);
        order.setTotalPrice(totalPrice);
        order.setStatus(OrderStatus.NEW.getStatus());
        order.setAddress(address);
        cargoService.insertOrder(order);
        note.newOrderNumbs++;
        //如果是从购物车下单，需要跟新购物车信息
        if ("gwc".equals(oac.getType())) {
            for (int i = 0; i < split.length; i++)
                deleteCart(openId, split[i]);
        }
        return Result.success(null);
    }

    //取消订单
    @GetMapping("/cancel")
    public Result cancel(@RequestParam("id") Integer id, @RequestParam("openId") String openId) {
        Order order = cargoService.queryOrderById(id);
        if (!order.getOpenId().equals(openId)) {
            return Result.error("此订单不属于您", Code.PARAMETERERROR.getCode());
        }
        cargoService.changeOrderStatus(id, OrderStatus.CANCEL.getStatus());
        String cargoIds = order.getCargoIds();
        String cargoNumbs = order.getCargoNumbs();
        String[] split = cargoIds.split(",");
        String[] split1 = cargoNumbs.split(",");
        for (int i = 0; i < split.length; i++) {
            int cargoId = Integer.parseInt(split[0]);
            int numbs = Integer.parseInt(split1[i]);
            Cargo cargo = cargoService.queryCargoById(cargoId);
            Cargo cargo1 = new Cargo();
            cargo1.setId(cargo.getId());
            cargo1.setSales(cargo.getSales() - numbs);
            cargo1.setStock(cargo.getWeight() * numbs + cargo.getStock());
            cargoService.updateCargo(cargo1);
        }
        note.cancelOrderNumbs++;
        return Result.success(null);
    }

    //退货
    @GetMapping("/back")
    public Result back(@RequestParam("id") Integer id, @RequestParam("openId") String openId) {
        Order order = cargoService.queryOrderById(id);
        if (!order.getOpenId().equals(openId)) {
            return Result.error("此订单不属于您", Code.PARAMETERERROR.getCode());
        }
        cargoService.changeOrderStatus(id, OrderStatus.RETURN.getStatus());
        note.backOrderNumbs++;
        return Result.success(null);
    }

    //确认收货
    @GetMapping("/success")
    public Result success(@RequestParam("id") Integer id, @RequestParam("openId") String openId) {
        Order order = cargoService.queryOrderById(id);
        if (!order.getOpenId().equals(openId)) {
            return Result.error("此订单不属于您", Code.PARAMETERERROR.getCode());
        }
        cargoService.changeOrderStatus(id, OrderStatus.SUCCESS.getStatus());
        return Result.success(null);
    }

    //添加购物车
    @PostMapping("/addCart")
    public Result addCart(@RequestBody Cart cart1) {
        String openId = cart1.getOpenId();
        String cargoIds = cart1.getCargoIds();
        String cargoNumbs = cart1.getCargoNumbs();
        if (ObjectUtils.isEmpty(openId)) {
            return Result.error("openId不能为空", Code.PARAMETEREMPTY.getCode());
        }
        if (ObjectUtils.isEmpty(cargoIds)) {
            return Result.error("商品ID不能为空", Code.PARAMETEREMPTY.getCode());
        }
        if (ObjectUtils.isEmpty(cargoNumbs)) {
            return Result.error("商品数量不能为空不能为空", Code.PARAMETEREMPTY.getCode());
        }
        Cart cart = cargoService.queryCartByOpenId(openId);
        if (ObjectUtils.isEmpty(cart)) {
            //直购物车为空，接添加
            cargoService.addCart(cart1);
            return Result.success(null);
        }
        //购物车不为空，进行判断是否有相同商品
        List<String> list = Arrays.asList(cart.getCargoIds().split(","));
        List<String> list1 = Arrays.asList(cart.getCargoNumbs().split(","));
        StringBuilder sb = new StringBuilder();
        StringBuilder sb1 = new StringBuilder();
        String[] split = cargoIds.split(",");
        String[] split1 = cargoNumbs.split(",");
        for (int i = 0; i < split.length; i++) {
            if (list.contains(split[i])) {
                for (int j = 0; j < list.size(); j++) {
                    if (list.get(j).equals(split[i])) {
                        Integer numbs = Integer.parseInt(list1.get(j)) + Integer.parseInt(split1[i]);
                        list1.set(j, String.valueOf(numbs));
                    }
                }
            } else {
                sb.append(split[i]).append(",");
                sb1.append(split1[i]).append(",");
            }
        }
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i)).append(",");
            sb1.append(list1.get(i)).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb1.deleteCharAt(sb1.length() - 1);
        cart.setCargoIds(sb.toString());
        cart.setCargoNumbs(sb1.toString());
        cargoService.updateCart(cart);
        return Result.success(null);
    }

    //查看购物车
    @GetMapping("/queryCart")
    public Result queryCart(@RequestParam("openId") String openId,
                            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        PageHelper.startPage(pageNum, 10);
        Cart cart = cargoService.queryCartByOpenId(openId);
        List<CartWrapper> list = new ArrayList<>();
        if (ObjectUtils.isEmpty(cart)) {
            return Result.success(new PageInfo<>(list));
        }
        String[] split = cart.getCargoIds().split(",");
        String[] split1 = cart.getCargoNumbs().split(",");
        for (int i = 0; i < split.length; i++) {
            Cargo cargo = cargoService.queryCargoById(Integer.parseInt(split[i]));
            CartWrapper cartWrapper = new CartWrapper();
            //copy属性
            BeanUtils.copyProperties(cargo, cartWrapper);
            cartWrapper.setNumbs(Integer.parseInt(split1[i]));
            list.add(cartWrapper);
        }
        PageInfo<CartWrapper> pageInfo = new PageInfo<>(list);
        return Result.success(pageInfo);
    }

    //购物车全部删除
    @GetMapping("/deleteAllCart")
    public Result deleteAllCart(@RequestParam("openId") String openId) {
        cargoService.deleteCart(openId);
        return Result.success(null);
    }

    //购物车删除某几件商品
    @GetMapping("/deleteCart")
    public Result deleteCart(@RequestParam("openId") String openId,
                             @RequestParam("cargoId") String cargoIds) {
        Cart cart = cargoService.queryCartByOpenId(openId);
        StringBuilder sb = new StringBuilder();
        StringBuilder sb1 = new StringBuilder();
        String[] split = cart.getCargoIds().split(",");
        String[] split1 = cart.getCargoNumbs().split(",");
        for (int i = 0; i < split.length; i++) {
            if (!cargoIds.contains(split[i])) {
                sb.append(split[i]).append(",");
                sb1.append(split1[i]).append(",");
            }
        }
        if (ObjectUtils.isEmpty(sb)) {
            cargoService.deleteCart(openId);
            return Result.success(null);
        }
        sb.deleteCharAt(sb.length() - 1);
        sb1.deleteCharAt(sb1.length() - 1);
        cart.setCargoIds(sb.toString());
        cart.setCargoNumbs(sb1.toString());
        cargoService.updateCart(cart);
        return Result.success(null);
    }

    //购物车商品加一
    @GetMapping("/incrby")
    public Result incrby(@RequestParam("openId") String openId,
                         @RequestParam("cargoId") String cargoId) {
        Cart cart = cargoService.queryCartByOpenId(openId);
        String[] split = cart.getCargoIds().split(",");
        String[] split1 = cart.getCargoNumbs().split(",");
        StringBuilder sb = new StringBuilder();
        StringBuilder sb1 = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            if (split[i].equals(cargoId)) {
                split1[i] = String.valueOf(Integer.parseInt(split1[i]) + 1);
            }
            sb.append(split[i]).append(",");
            sb1.append(split1[i]).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb1.deleteCharAt(sb1.length() - 1);
        cart.setCargoIds(sb.toString());
        cart.setCargoNumbs(sb1.toString());
        cargoService.updateCart(cart);
        return Result.success(null);
    }

    //购物车商品减一
    @GetMapping("/decrby")
    public Result decrby(@RequestParam("openId") String openId,
                         @RequestParam("cargoId") String cargoId) {
        Cart cart = cargoService.queryCartByOpenId(openId);
        String[] split = cart.getCargoIds().split(",");
        String[] split1 = cart.getCargoNumbs().split(",");
        StringBuilder sb = new StringBuilder();
        StringBuilder sb1 = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            if (split[i].equals(cargoId)) {
                int numbs = Integer.parseInt(split1[i]) - 1;
                if (numbs == 0) {
                    deleteCart(openId, cargoId);
                    return Result.success(null);
                }
                split1[i] = String.valueOf(numbs);
            }
            sb.append(split[i]).append(",");
            sb1.append(split1[i]).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb1.deleteCharAt(sb1.length() - 1);
        cart.setCargoIds(sb.toString());
        cart.setCargoNumbs(sb1.toString());
        cargoService.updateCart(cart);
        return Result.success(null);
    }

    //商品详情
    @GetMapping("/cargoDetail")
    public Result cargoDetail(@RequestParam("openId") String openId,
                              @RequestParam("cargoId") String cargoId) {
        Cargo cargo = cargoService.queryCargoById(Integer.parseInt(cargoId));
        return Result.success(cargo);
    }

    //查看所有订单数、未发货、待收货、退货数量
    @GetMapping("/queryOrderNumbs")
    public Result queryOrderNumbs(@RequestParam("openId") String openId) {
        List<Order> orderList = cargoService.queryAllOrder(openId, null);
        OrderNumbs orderNumbs = new OrderNumbs();
        orderNumbs.setAll(orderList.size());
        orderNumbs.setNotShipped(0);
        orderNumbs.setToBeReceived(0);
        orderNumbs.setRefund(0);
        for (Order order : orderList) {
            if (order.getStatus().equals(OrderStatus.NEW.getStatus()))
                orderNumbs.setNotShipped(orderNumbs.getNotShipped() + 1);
            else if (order.getStatus().equals(OrderStatus.SEND.getStatus()))
                orderNumbs.setToBeReceived(orderNumbs.getToBeReceived() + 1);
            else if (order.getStatus().equals(OrderStatus.RETURN.getStatus()))
                orderNumbs.setRefund(orderNumbs.getRefund() + 1);
        }
        return Result.success(orderNumbs);
    }
}