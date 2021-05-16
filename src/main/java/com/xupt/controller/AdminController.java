package com.xupt.controller;

import com.xupt.beans.Admin;
import com.xupt.beans.Cargo;
import com.xupt.beans.Order;
import com.xupt.enums.Code;
import com.xupt.enums.OrderStatus;
import com.xupt.listen.Note;
import com.xupt.service.AdminService;
import com.xupt.service.CargoService;
import com.xupt.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @Auther: yhn
 * @Date: 2021/5/3 14:37
 */

@RestController
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private CargoService cargoService;

    @Autowired
    private Note note;

    @GetMapping("/login")
    public Result login(HttpServletRequest request, HttpServletResponse response, String userName, String password) {
        if (ObjectUtils.isEmpty(userName) || ObjectUtils.isEmpty(password)) {
            return Result.error("用户名或密码为空", Code.PARAMETEREMPTY.getCode());
        }
        Admin admin = adminService.loginQuery(userName, password);
        if (ObjectUtils.isEmpty(admin)) {
            return Result.error("用户名或密码错误", Code.PARAMETERERROR.getCode());
        }
        HttpSession session = request.getSession();
        session.setAttribute("admin", admin);
        if (session.isNew()) {
            response.addCookie(new Cookie("JSESSIONID", session.getId()));
        }
        return Result.success(admin);
    }

    @GetMapping("/note")
    public Result note(HttpServletRequest request) {
        if (ObjectUtils.isEmpty(request.getSession().getAttribute("admin"))) {
            return Result.error("您还未登录，请先登录", Code.NOTLOGGEDIN.getCode());
        }
        Note note1 = new Note();
        note1.newOrderNumbs = note.newOrderNumbs;
        note1.cancelOrderNumbs = note.cancelOrderNumbs;
        note1.backOrderNumbs = note.backOrderNumbs;
        note.newOrderNumbs = 0;
        note.cancelOrderNumbs = 0;
        note.backOrderNumbs = 0;
        return Result.success(note1);
    }

    //发货
    @GetMapping("/send")
    public Result send(HttpServletRequest request, @RequestParam("id") Integer id) {
        if (ObjectUtils.isEmpty(request.getSession().getAttribute("admin"))) {
            return Result.error("您还未登录，请先登录", Code.NOTLOGGEDIN.getCode());
        }
        cargoService.changeOrderStatus(id, OrderStatus.SEND.getStatus());
        return Result.success(null);
    }

    //同意退货
    @GetMapping("/agree")
    public Result agree(HttpServletRequest request, @RequestParam("id") Integer id) {
        if (ObjectUtils.isEmpty(request.getSession().getAttribute("admin"))) {
            return Result.error("您还未登录，请先登录", Code.NOTLOGGEDIN.getCode());
        }
        cargoService.changeOrderStatus(id, OrderStatus.RETURNSUCCESS.getStatus());
        Order order = cargoService.queryOrderById(id);
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
        return Result.success(null);
    }

    //不同意退货
    @GetMapping("/disagree")
    public Result disagree(HttpServletRequest request, @RequestParam("id") Integer id) {
        if (ObjectUtils.isEmpty(request.getSession().getAttribute("admin"))) {
            return Result.error("您还未登录，请先登录", Code.NOTLOGGEDIN.getCode());
        }
        cargoService.changeOrderStatus(id, OrderStatus.SUCCESS.getStatus());
        return Result.success(null);
    }
}
