package com.hzy.im.ws;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hzy.im.dao.MessageDao;
import com.hzy.im.pojo.Message;
import com.hzy.im.service.MessageService;
import com.hzy.im.util.MessageUtils;
import com.hzy.im.util.SpringUtil;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 在ServerEndpoint注解下，@Autowired无法实现自动注入，所以使用原生的注入，用getBean实现
 *
 * */
@ServerEndpoint(value = "/chat",configurator = GetHttpSessionConfigurator.class)
@Component
public class ChatEndPoint {

    private MessageService messageService = SpringUtil.getBean(MessageService.class);

    //用来存储每一个客户端对象对应的ChatEndPoint对象
    private static Map<String,ChatEndPoint> onlineUsers = new ConcurrentHashMap<>();

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
        //将当前在线用户的用户名推送给所有的客户端
        //1、获取消息
        String mess = MessageUtils.getMessage(true,null,getNames());
        //2、调用方法发送全体消息
        broadcastAllUsers(mess);
    }


    //得到当前在线所有用户名列表
    private Set<String> getNames(){
        return onlineUsers.keySet();
    }

    //将在线消息推送给所有用户
    private void broadcastAllUsers(String message){
        //将该消息推送给所有的客户端
        Set<String> names = getNames();
        for (String name:names){
            //遍历，拿到每一个ChatEndPoint对象
            ChatEndPoint chatEndPoint = onlineUsers.get(name);
            try {
                //利用session的sendText进行服务端向客户端推送
                chatEndPoint.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @OnMessage
    public void onMessage(String message,Session session){
        ObjectMapper mapper = new ObjectMapper();
        try {
            Message mess = mapper.readValue(message,Message.class);
            //获取要将数据发送给的用户
            String toName = mess.getToName();
            //获取消息数据
            String data = mess.getMessage();
            //获取当前登录的用户的用户名
            String username = (String) httpSession.getAttribute("username");
            //获取推送给指定用户的消息格式的数据
            String resultMessage =MessageUtils.getMessage(false,username,data);
            //发送数据
            onlineUsers.get(toName).session.getBasicRemote().sendText(resultMessage);
            messageService.addMsg(username,toName,false,data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
