package com.xupt.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xupt.beans.*;
import com.xupt.enums.CargoStatus;
import com.xupt.enums.Code;
import com.xupt.service.CargoService;
import com.xupt.util.QiNiuVideoUpload;
import com.xupt.util.Result;
import com.xupt.wrapper.OrderWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Auther: yhn
 * @Date: 2021/5/5 12:13
 */

@RestController
@Slf4j
@CrossOrigin
public class CargoController {

    @Autowired
    private CargoService cargoService;

    //新增商品
    @PostMapping("/insertCargo")
    public Result insertCargo(HttpServletRequest request, @RequestBody Cargo cargo) {
        if (ObjectUtils.isEmpty(request.getSession().getAttribute("admin"))) {
            return Result.error("您还未登录，请先登录", Code.NOTLOGGEDIN.getCode());
        }
        if (ObjectUtils.isEmpty(cargo.getName())) {
            return Result.error("商品名称不能为空", Code.PARAMETEREMPTY.getCode());
        }
        if (ObjectUtils.isEmpty(cargo.getDetail())) {
            return Result.error("商品详情不能为空", Code.PARAMETEREMPTY.getCode());
        }
        if (ObjectUtils.isEmpty(cargo.getType())) {
            return Result.error("商品类型不能为空", Code.PARAMETEREMPTY.getCode());
        }
        if (ObjectUtils.isEmpty(cargo.getPrice())) {
            return Result.error("商品单价不能为空", Code.PARAMETEREMPTY.getCode());
        }
        if (ObjectUtils.isEmpty(cargo.getWeight())) {
            return Result.error("商品每份含量不能为空", Code.PARAMETEREMPTY.getCode());
        }
        if (ObjectUtils.isEmpty(cargo.getStock())) {
            return Result.error("商品库存不能为空", Code.PARAMETEREMPTY.getCode());
        }
        if (ObjectUtils.isEmpty(cargo.getImageUrl())) {
            return Result.error("商品图片地址不能为空", Code.PARAMETEREMPTY.getCode());
        }
        cargo.setStatus(CargoStatus.UP.getStatus());
        cargo.setSales(0);
        cargoService.insertCargo(cargo);
        return Result.success(null);
    }

    //上传图片
    @PostMapping("/upload")
    public Result upload(@RequestParam("image") MultipartFile image) {
        try {
            String url = QiNiuVideoUpload.uploadImage(UUID.randomUUID().toString(), image.getInputStream());
            return Result.success(url);
        } catch (IOException e) {
            log.error("图片上传失败");
            return Result.error("图片上传失败", Code.UPLOADPICTUREERROR.getCode());
        }
    }

    //更新商品信息
    @PostMapping("/updateCargo")
    public Result updateCargo(HttpServletRequest request, @RequestBody Cargo cargo) {
        if (ObjectUtils.isEmpty(request.getSession().getAttribute("admin"))) {
            return Result.error("您还未登录，请先登录", Code.NOTLOGGEDIN.getCode());
        }
        cargoService.updateCargo(cargo);
        return Result.success(null);
    }

    //分页查询商品信息（带条件模糊查询）
    //TODO
    @GetMapping("/queryCargo")
    public Result queryCargo(HttpServletRequest request,
                             @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                             @RequestParam(value = "name", defaultValue = "") String name,
                             @RequestParam(value = "type", defaultValue = "") String type,
                             @RequestParam(value = "status", defaultValue = "") String status,
                             @RequestParam(value = "openId", defaultValue = "") String openId) {
        PageHelper.startPage(pageNum, 10);
        if (ObjectUtils.isEmpty(request.getSession().getAttribute("admin"))) {
            if (ObjectUtils.isEmpty(openId)) {
                return Result.error("您还未登录，请先登录", Code.NOTLOGGEDIN.getCode());
            } else {
                List<Cargo> list = cargoService.queryAllCargo(name, type, CargoStatus.UP.getStatus());
                PageInfo<Cargo> pageInfo = new PageInfo<>(list);
                return Result.success(pageInfo);
            }
        }
        List<Cargo> list = cargoService.queryAllCargo(name, type, status);
        PageInfo<Cargo> pageInfo = new PageInfo<>(list);
        return Result.success(pageInfo);
    }

    //查询商品类型
    @GetMapping("/queryType")
    public Result queryType(HttpServletRequest request, @RequestParam(value = "openId", defaultValue = "") String openId) {
        if (ObjectUtils.isEmpty(request.getSession().getAttribute("admin"))) {
            if (ObjectUtils.isEmpty(openId))
                return Result.error("您还未登录，请先登录", Code.NOTLOGGEDIN.getCode());
        }
        List<Type> list = cargoService.queryType();
        return Result.success(list);
    }

    //查询收货地址
    @GetMapping("/queryAddress")
    public Result queryAddress(HttpServletRequest request, @RequestParam(value = "openId", defaultValue = "") String openId) {
        if (ObjectUtils.isEmpty(request.getSession().getAttribute("admin"))) {
            if (ObjectUtils.isEmpty(openId))
                return Result.error("您还未登录，请先登录", Code.NOTLOGGEDIN.getCode());
        }
        List<String> list = cargoService.queryAddress();
        return Result.success(list);
    }

    //新增收货地址
    @PostMapping("/addAddress")
    public Result addAddress(HttpServletRequest request, @RequestBody Address address) {
        if (ObjectUtils.isEmpty(request.getSession().getAttribute("admin"))) {
            return Result.error("您还未登录，请先登录", Code.NOTLOGGEDIN.getCode());
        }
        if (ObjectUtils.isEmpty(address.getAddressName())) {
            return Result.error("收货地址不能为空", Code.PARAMETEREMPTY.getCode());
        }
        cargoService.addAddress(address.getAddressName());
        return Result.success(null);
    }

    //查询订单
    @GetMapping("/queryOrders")
    public Result queryOrders(HttpServletRequest request,
                              @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                              @RequestParam(value = "status", defaultValue = "") String status,
                              @RequestParam(value = "name", defaultValue = "") String name,
                              @RequestParam(value = "openId", defaultValue = "") String openId) {
        if (ObjectUtils.isEmpty(request.getSession().getAttribute("admin"))) {
            if (ObjectUtils.isEmpty(openId))
                return Result.error("您还未登录，请先登录", Code.NOTLOGGEDIN.getCode());
        }
        PageHelper.startPage(pageNum, 10);
        if (!ObjectUtils.isEmpty(openId)) {//用户查询
            List<Order> list = cargoService.queryAllOrder(openId, status);//查询用户所有订单
            List<Cargo> cargoList = cargoService.queryAllCargo(name, null, null);//模糊查询商品
            List<Order> ordersList = new ArrayList<>();
            List<OrderWrapper> orderWrapperList = new ArrayList<>();
            if (ObjectUtils.isEmpty(cargoList)) {
                return Result.success(new PageInfo<>(orderWrapperList));
            }
            if (ObjectUtils.isEmpty(list))
                return Result.success(new PageInfo<>(orderWrapperList));
            for (Order order : list) {
                for (Cargo cargo : cargoList) {
                    if (order.getCargoIds().contains(String.valueOf(cargo.getId()))) {
                        ordersList.add(order);
                        break;
                    }
                }
            }
            for (Order order : ordersList) {
                OrderWrapper orderWrapper = new OrderWrapper();
                orderWrapper.setOrder(order);
                String[] split = order.getCargoIds().split(",");
                cargoList = new ArrayList<>();
                for (int i = 0; i < split.length; i++) {
                    cargoList.add(cargoService.queryCargoById(Integer.parseInt(split[i])));
                }
                orderWrapper.setCargoList(cargoList);
                orderWrapperList.add(orderWrapper);
            }
            PageInfo<OrderWrapper> pageInfo = new PageInfo<>(orderWrapperList);
            return Result.success(pageInfo);
        } else {//商家查询
            List<Order> orderList = cargoService.queryAllOrder(null, status);
            List<OrderWrapper> list = new ArrayList<>();
            for (Order order : orderList) {
                OrderWrapper orderWrapper = new OrderWrapper();
                orderWrapper.setOrder(order);
                String[] split = order.getCargoIds().split(",");
                List<Cargo> cargoList = new ArrayList<>();
                for (int i = 0; i < split.length; i++) {
                    cargoList.add(cargoService.queryCargoById(Integer.parseInt(split[i])));
                }
                orderWrapper.setCargoList(cargoList);
                list.add(orderWrapper);
            }
            PageInfo<OrderWrapper> pageInfo = new PageInfo<>(list);
            return Result.success(pageInfo);
        }
    }

    //查询订单所属商品信息
//    @GetMapping("/queryCargoByIds")
//    public Result queryCargoByIds(HttpServletRequest request,
//                                  @RequestParam(value = "openId", defaultValue = "") String openId,
//                                  @RequestParam("orderId") Integer orderId) {
//        if (ObjectUtils.isEmpty(request.getSession().getAttribute("admin"))) {
//            if (ObjectUtils.isEmpty(openId))
//                return Result.error("您还未登录，请先登录", Code.NOTLOGGEDIN.getCode());
//        }
//        Order order = cargoService.queryOrderById(orderId);
//        if (!openId.equals(order.getOpenId())) {
//            return Result.error("此订单不属于您", Code.PARAMETERERROR.getCode());
//        }
//        String cargoIds = order.getCargoIds();
//        String cargoNumbs = order.getCargoNumbs();
//        String[] split = cargoIds.split(",");
//        String[] split1 = cargoNumbs.split(",");
//        if (split.length != split1.length) {
//            return Result.error("商品数量与ID不一致", Code.PARAMETERERROR.getCode());
//        }
//        List<OrdersCargo> list = new ArrayList<>();
//        for (int i = 0; i < split.length; i++) {
//            String name = cargoService.queryCargoById(Integer.parseInt(split[i])).getName();
//            OrdersCargo ordersCargo = new OrdersCargo();
//            ordersCargo.setId(Integer.parseInt(split[i]));
//            ordersCargo.setName(name);
//            ordersCargo.setNumbs(Integer.parseInt(split1[i]));
//            list.add(ordersCargo);
//        }
//        return Result.success(list);
//    }

    //下架商品
    @GetMapping("/downCargo")
    public Result downCargo(HttpServletRequest request, @RequestParam("id") Integer id) {
        if (ObjectUtils.isEmpty(request.getSession().getAttribute("admin"))) {
            return Result.error("您还未登录，请先登录", Code.NOTLOGGEDIN.getCode());
        }
        Cargo cargo = new Cargo();
        cargo.setId(id);
        cargo.setStatus(CargoStatus.DOWN.getStatus());
        cargoService.updateCargo(cargo);
        return Result.success(null);
    }

    //上架商品
    @GetMapping("/upCargo")
    public Result upCargo(HttpServletRequest request, @RequestParam("id") Integer id) {
        if (ObjectUtils.isEmpty(request.getSession().getAttribute("admin"))) {
            return Result.error("您还未登录，请先登录", Code.NOTLOGGEDIN.getCode());
        }
        Cargo cargo = new Cargo();
        cargo.setId(id);
        cargo.setStatus(CargoStatus.UP.getStatus());
        cargoService.updateCargo(cargo);
        return Result.success(null);
    }
}
