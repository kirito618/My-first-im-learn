package com.hzy.im.response;

import lombok.Data;

/**
 * @author hzy
 * @data 2021/1/27
 * 返回数据
 **/
@Data
public class R<T> {

    //错误码
    private Integer code;

    //提示信息
    private String msg;

    //具体内容
    private T data;

    private boolean flag;

    //私有构造类 构造返回对象
    private R(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    //通过字符串构造返回类
    public R(String s) {
        this.code = 100;
        this.msg = s;
        this.data = null;
    }

    public R(Integer code, String msg, T data,boolean flag){
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.flag = flag;
    }


    //成功的返回类,返回码为100
    public static<S> R success(S data) {
        return new R(100,"查询成功！", data,true);  //将成功返回的data数据传进去
    }
    public static<S> R success(S data,String msg) {
        return new R(100,msg, data,true);  //将成功返回的data数据传进去
    }

    //失败的返回类，返回码为202
    public static<S> R error(){ return new R(202,"操作失败！",null,false);}

    public static<S> R error(String msg){ return new R(202,msg,null,false);}

    public static<S> R tokenError(String msg){
        return new R(400,msg,null,false);
    }
}