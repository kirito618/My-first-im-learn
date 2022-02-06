package com.hzy.im.dao;

import com.hzy.im.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface UserDao {

    @Select("select * from user where username = #{username} and password=#{password}")
    public User login(User user);
}
