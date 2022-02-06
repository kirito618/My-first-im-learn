package com.hzy.im.controller;

import com.alibaba.fastjson.JSONObject;
import com.hzy.im.dao.UserDao;
import com.hzy.im.pojo.User;
import com.hzy.im.response.R;
import com.hzy.im.result.LoginResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;

@RestController
public class UserController {
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    UserDao userDao;


    @PostMapping("/login")
    public R login(@RequestBody User user, HttpSession session){
        if(!ObjectUtils.isEmpty(userDao.login(user))){
            String token = System.currentTimeMillis()+"&@_"+Math.random()*100;
            LoginResult result = new LoginResult(user.getUsername(), token);
            redisTemplate.opsForValue().set(token, JSONObject.toJSONString(result) ,3600 * 24, TimeUnit.SECONDS);
            session.setAttribute("username",user.getUsername());
            return R.success(result);
        }else{
            return R.error("登陆失败");
        }
    }

    @PostMapping("/getUserName")
    public String getUserName(@RequestHeader("token")String token,HttpSession session){
        return (String) session.getAttribute("username");
    }






    /**
     * 检验token是否有效
     * */
    public boolean tokenExist(String token){
        Object object = redisTemplate.opsForValue().get(token);
        boolean flag = true;
        if(object == null){
            flag = false;
        }
        return flag;
    }

    /**
     * 传入token，执行识别，得到token中的关键字属性值
     */
    public String getKeyByToken(String token,String key){
        if(!tokenExist(token)){
            //token无效或不存在
            return null;
        }
        Object object = redisTemplate.opsForValue().get(token);
        JSONObject jsonObject = JSONObject.parseObject((String)object);
        return jsonObject.getString(key);
    }

}
