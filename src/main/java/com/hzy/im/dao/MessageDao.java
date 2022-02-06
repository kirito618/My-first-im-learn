package com.hzy.im.dao;

import com.hzy.im.pojo.MessageDB;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Mapper
public interface MessageDao {

    public void insert(@Param("fromName")String fromName,
                       @Param("toName")String toName,
                       @Param("message")String message,
                       @Param("isSystem")int isSystem);
}
