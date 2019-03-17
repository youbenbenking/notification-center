package com.notification.dao;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


/**
 * 添加了@Mapper注解之后这个接口在编译时会生成相应的实现类
 * 接口方法不支持重载
 */
@Mapper
public interface CronMapper {
	
	 @Select("select cron from wisdom_cron_config where job_type = #{jobType} limit 1")
     public String getCron(@Param("jobType") String jobType);
     
}