package com.xupt.controller;

import com.xupt.beans.Test;
import com.xupt.service.TestService;
import com.xupt.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: yhn
 * @Date: 2021/5/2 17:44
 */

@RestController
public class TestController {

    @Autowired
    private TestService testService;

    @GetMapping("/test")
    public Result test() {
        Test test = testService.test();
        System.out.println(test.getInsertTime());
        return Result.success(test);
    }

    @GetMapping("/insert")
    public Result insert() {
        testService.insert();
        return Result.success(null);
    }
}
