package com.hzy.im.dao;

import com.hzy.im.pojo.MessageDB;
import com.hzy.im.result.ResultMessage;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Mapper
public interface MessageDao {

    public void insert(@Param("fromName")String fromName,
                       @Param("toName")String toName,
                       @Param("message")String message,
                       @Param("isSystem")int isSystem,
                       @Param("isGroup")int isGroup,
                       @Param("groupId")int groupId);


    public Set<String> getAllGroups(@Param("username")String username);
    public List<ResultMessage> getMessagesByGroupName(@Param("groupId")Integer groupId);
    public List<ResultMessage> getMessagesByFromOrTo(@Param("fromName")String fromName,@Param("toName")String toName);

    @Select("SELECT id FROM `group` WHERE NAME = #{groupName}")
    public Integer getGroupId(@Param("groupName")String groupName);
}
