package com.hzy.im.pojo;

import lombok.Data;

@Data
public class MessageDB {

    private int id;

    private String fromName;

    private String toName;

    private boolean isSystem;

    private String message;

    private String createTime;

    public MessageDB(String from,String to,boolean isSystem,String msg){
        this.fromName = from;
        this.isSystem = isSystem;
        this.message = msg;
        this.toName = to;
    }
    public MessageDB(){

    }
}
