package com.hzy.im.result;

import lombok.Data;

@Data
public class ResultMessage {

    //是否为系统消息
    private boolean isSystem;

    private String fromName;

    private Object message;
}
