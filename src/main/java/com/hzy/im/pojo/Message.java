package com.hzy.im.pojo;

import lombok.Data;

@Data
public class Message {

    private String toName;
    private String message;
    private boolean group;
}
