package com.notification.domain.model;

import java.io.Serializable;

public class DataEntity implements Serializable{
	private String targetAppCode;
    private String targetUsername;
    private MessageEntity message;

    public String getTargetAppCode() {
        return targetAppCode;
    }

    public void setTargetAppCode(String targetAppCode) {
        this.targetAppCode = targetAppCode;
    }

    public String getTargetUsername() {
        return targetUsername;
    }

    public void setTargetUsername(String targetUsername) {
        this.targetUsername = targetUsername;
    }

    public MessageEntity getMessage() {
        return message;
    }

    public void setMessage(MessageEntity message) {
        this.message = message;
    }
}
