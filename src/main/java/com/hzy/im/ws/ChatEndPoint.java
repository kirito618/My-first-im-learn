package com.hzy.im.ws;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hzy.im.dao.MessageDao;
import com.hzy.im.pojo.Message;
import com.hzy.im.service.MessageService;
import com.hzy.im.util.MessageUtils;
import com.hzy.im.util.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 在ServerEndpoint注解下，@Autowired无法实现自动注入，所以使用原生的注入，用getBean实现
 * */
@ServerEndpoint(value = "/chat",configurator = GetHttpSessionConfigurator.class)
@Component
public class ChatEndPoint {

    private MessageService messageService = SpringUtil.getBean(MessageService.class);

    private MessageUtils messageUtils = SpringUtil.getBean(MessageUtils.class);

    //用来存储每一个客户端对象对应的ChatEndPoint对象
    private static Map<String,ChatEndPoint> onlineUsers = new ConcurrentHashMap<>();

    //声明Session对象，通过该对象可以发送消息给指定用户
    private Session session;
    private HttpSession httpSession;

    @OnOpen
    public void onOpen(Session session,EndpointConfig config){
        this.session = session;
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        this.httpSession = httpSession;
        String username = (String) httpSession.getAttribute("username");
        //将当前对象以及用户名置为在线状态，存入在线集合Map中去。
        onlineUsers.put(username,this);
        //以用户名为key，对应的 Session 对象作为value，存入redis
        //String json = JSONObject.toJSONString(this.session);
        //messageUtils.addSessionToRedis(username,json);
        //将当前在线用户的用户名推送给所有的客户端
        //1、获取消息
        String mess = MessageUtils.getMessage(true,false,null,getNames());
        //2、调用方法发送全体消息
        broadcastAllUsers(mess);
        //3、获取所有群组信息
        String mess2 = MessageUtils.getMessage(false,true,null,messageService.getJoinedGroups(username));
        //4、推送给当前用户
        try{
            //this.session.getBasicRemote().sendText(mess2);
            //原来使用同步发送，结果失败了，改为异步发送就成功了(可能是因为上面发送全体消息是同步发送的原因)
            onlineUsers.get(username).session.getAsyncRemote().sendText(mess2);
            System.out.println("当前用户已加入：" + mess2);
        }catch (Exception e){
            System.out.println("推送用户所加入的群组的列表时出现错误");
        }
    }

    //得到当前在线所有用户名列表
    private Set<String> getNames(){
        return onlineUsers.keySet();
    }

    //将在线消息推送给所有用户
    private void broadcastAllUsers(String message){
        //将该消息推送给所有的客户端
        //得到所有的用户名
        Set<String> names = getNames();
        //遍历，拿到每一个ChatEndPoint对象
        for (String name:names){
            ChatEndPoint chatEndPoint = onlineUsers.get(name);
            try {
                //如果session未被关闭
                if (session.isOpen()){
                    //利用session的sendText进行服务端向客户端推送
                    chatEndPoint.session.getBasicRemote().sendText(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //接收到客户端消息的时候，会调用此方法
    @OnMessage
    public void onMessage(String message,Session session){
        ObjectMapper mapper = new ObjectMapper();
        try {
            //这里实际上就是 前端页面将发送的数据和发送给谁  封装成json字符串，我们拿到字符串，转成我们的实体类，再从中获取信息
            Message mess = mapper.readValue(message,Message.class);
            //获取要将数据发送给的用户
            String toName = mess.getToName();
            //获取消息数据
            String data = mess.getMessage();
            //获取当前登录的用户的用户名
            String username = (String) httpSession.getAttribute("username");
            //获取推送给指定用户的消息格式的数据
            String resultMessage =MessageUtils.getMessage(false,false,username,data);
            if (!mess.isGroup()){
                //发送数据(同步发送)
                onlineUsers.get(toName).session.getBasicRemote().sendText(resultMessage);
            }
            //异步发送数据
            //onlineUsers.get(toName).session.getAsyncRemote().sendText(resultMessage);
            //如果所有环节都没问题，那么将消息入库
            messageService.addMsg(username,toName,false,mess.isGroup(),data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //断开连接时会调用此方法
    @OnClose
    public void onClose(Session session){
        try {
            onlineUsers.remove((String)(this.httpSession.getAttribute("username")));
            broadcastAllUsers(getNames().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
