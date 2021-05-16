package com.xupt.service;

import com.xupt.beans.Admin;

/**
 * @Auther: yhn
 * @Date: 2021/5/3 14:41
 */
public interface AdminService {
    Admin loginQuery(String userName, String password);
}
