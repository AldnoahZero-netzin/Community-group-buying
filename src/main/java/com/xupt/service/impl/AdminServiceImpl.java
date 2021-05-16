package com.xupt.service.impl;

import com.xupt.beans.Admin;
import com.xupt.mapper.AdminMapper;
import com.xupt.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: yhn
 * @Date: 2021/5/3 14:42
 */
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public Admin loginQuery(String userName, String password) {
        return adminMapper.loginQuery(userName, password);
    }
}
