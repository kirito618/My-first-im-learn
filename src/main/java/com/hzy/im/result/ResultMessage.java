package com.hzy.im.result;

import lombok.Data;

@Data
public class ResultMessage {

    //是否为系统消息
    private boolean isSystem;
    //是否为群组消息
    private boolean isGroupFlag;

    private String fromName;

    private Object message;
}
