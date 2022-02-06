package com.hzy.im.service;

import com.hzy.im.result.ResultMessage;
import org.springframework.stereotype.Service;

@Service
public interface MessageService {

    /**
     * 消息记录的入库
     * */
    public void addMsg(String from,String to,boolean isSystem,String message);
}
