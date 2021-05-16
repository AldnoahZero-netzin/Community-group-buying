package com.xupt.mapper;

import com.xupt.beans.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @Auther: yhn
 * @Date: 2021/5/3 14:43
 */

@Repository
@Mapper
public interface AdminMapper {


    Admin loginQuery(@Param("userName") String userName, @Param("password") String password);
}
