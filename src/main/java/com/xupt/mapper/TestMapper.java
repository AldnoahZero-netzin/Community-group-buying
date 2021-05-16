package com.xupt.mapper;

import com.xupt.beans.Test;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * @Auther: yhn
 * @Date: 2021/5/2 17:45
 */

@Mapper
@Repository
public interface TestMapper {
    Test test();

    void insert(@Param("date") Date date);
}
