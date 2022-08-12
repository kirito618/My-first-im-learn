package com.hzy.im.service;

import com.hzy.im.result.ResultMessage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface MessageService {

    /**
     * 消息记录的入库
     * */
    public void addMsg(String from,String to,boolean isSystem,boolean isGroup,String message);


    /**
     * 获取用户已经加入的群组
     * */
    public Set<String> getJoinedGroups(String username);



    /**
     * 获取某个群组内的历史聊天记录
     * */
    public List<ResultMessage> getMessagesRecordByGroupName(String groupName);


    /**
     * 获取与某人的历史聊天记录
     * */
    public List<ResultMessage> getMessagesRecordByToName(String fromName,String toName);


    /**
     * 获取某群组的所有成员名字列表
     * */
    public List<String> getAllMembers(String groupName);
}
