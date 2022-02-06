package com.hzy.im.util;

import com.hzy.im.dao.MessageDao;
import com.hzy.im.pojo.MessageDB;
import com.hzy.im.result.ResultMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import com.alibaba.fastjson.JSONObject;

/**
 * @Description: 用来封装消息的工具类
 * */

@Component
public class MessageUtils {
    @Autowired
    public  MessageDao messageDao;

    public static String getMessage(boolean isSystemMessage,String fromName,Object message){
        ResultMessage result = new ResultMessage();
        result.setSystem(isSystemMessage);
        result.setMessage(message);
        if(!ObjectUtils.isEmpty(fromName)){
            //不是null才封装进去
            result.setFromName(fromName);
        }
        return JSONObject.toJSONString(result);
    }

}
