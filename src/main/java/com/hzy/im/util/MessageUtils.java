package com.hzy.im.util;

import com.hzy.im.dao.MessageDao;
import com.hzy.im.pojo.MessageDB;
import com.hzy.im.result.ResultMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONObject;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 用来封装消息的工具类
 * */

@Component
public class MessageUtils {
    @Autowired
    public  MessageDao messageDao;

    public static RedisTemplate redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        MessageUtils.redisTemplate = redisTemplate;
    }

    //封装消息格式，是否为系统消息，消息来源，消息主体
    public static String getMessage(boolean isSystemMessage,boolean isGroup,String fromName,Object message){
        ResultMessage result = new ResultMessage();
        result.setSystem(isSystemMessage);
        result.setGroupFlag(isGroup);
        result.setMessage(message);
        if(!ObjectUtils.isEmpty(fromName)){
            //不是null才封装进去
            result.setFromName(fromName);
        }
        return JSONObject.toJSONString(result);
    }

    //将session存入redis
    public boolean addSessionToRedis(String username, String sessionJson){
        //String json = JSONObject.toJSONString(session);
        //将用户的token加上用户的username，然后设置有效期为24h，存入redis
        redisTemplate.opsForValue().set(username,sessionJson,3600 * 24, TimeUnit.SECONDS);
        return true;
    }


    public boolean removeFromRedis(String username){
        return redisTemplate.delete(username);
    }
}
