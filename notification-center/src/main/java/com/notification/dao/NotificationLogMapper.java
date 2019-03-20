package com.notification.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.notification.model.NotificationLog;



public interface NotificationLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(NotificationLog record);

    int insertSelective(NotificationLog record);

    NotificationLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(NotificationLog record);

    int updateByPrimaryKey(NotificationLog record);
    
    List<NotificationLog> selectMessages(@Param("srcAppCode") String srcAppCode, @Param("userName") String userName);
    
    int updateStatusByPrimaryKey(@Param("msgId") Long msgId, @Param("status") int status);
     
}