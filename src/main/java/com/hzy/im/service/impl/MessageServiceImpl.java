package com.hzy.im.service.impl;

import com.hzy.im.dao.MessageDao;
import com.hzy.im.pojo.MessageDB;
import com.hzy.im.result.ResultMessage;
import com.hzy.im.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    MessageDao messageDao;

    @Override
    public void addMsg(String from,String to,boolean isSystem,String message){
        if (isSystem){
            messageDao.insert(from,to,message,1);
        }else{
            messageDao.insert(from,to,message,0);
        }
    }
}
