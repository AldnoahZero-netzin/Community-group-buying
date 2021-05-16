package com.xupt.service.impl;

import com.xupt.beans.Test;
import com.xupt.mapper.TestMapper;
import com.xupt.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Auther: yhn
 * @Date: 2021/5/2 17:45
 */

@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private TestMapper testMapper;

    @Override
    public Test test() {
        return testMapper.test();
    }

    @Override
    public void insert() {
        testMapper.insert(new Date());
    }
}
