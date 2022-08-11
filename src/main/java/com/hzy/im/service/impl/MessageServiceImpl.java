package com.hzy.im.service.impl;

import com.hzy.im.dao.MessageDao;
import com.hzy.im.pojo.MessageDB;
import com.hzy.im.result.ResultMessage;
import com.hzy.im.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    MessageDao messageDao;

    @Override
    public void addMsg(String from,String to,boolean isSystem,boolean isGroup,String message){
        if (isSystem){
            messageDao.insert(from,to,message,1,0,0);
        }else if (isGroup){
            Integer groupId = messageDao.getGroupId(to);
            System.out.println(groupId);
            messageDao.insert(from,to,message,0,1,groupId);
        }else{
            messageDao.insert(from,to,message,0,0,0);
        }
    }


    /**
     * 获取用户已经加入的群组
     * */
    @Override
    public Set<String> getJoinedGroups(String username){
        return messageDao.getAllGroups(username);
     }


    /**
     * 获取某个群组内的历史聊天记录
     * */
    @Override
    public List<ResultMessage> getMessagesRecordByGroupName(String groupName){
        Integer groupId = messageDao.getGroupId(groupName);
        List<ResultMessage> resultMessages = messageDao.getMessagesByGroupName(groupId);
        for (int i=0;i<resultMessages.size();i++){
            resultMessages.get(i).setGroupFlag(true);
        }
        return resultMessages;
    }



    /**
     * 获取与某人的历史聊天记录
     * */
    @Override
    public List<ResultMessage> getMessagesRecordByToName(String fromName,String toName){
        return messageDao.getMessagesByFromOrTo(fromName,toName);
    }
}
