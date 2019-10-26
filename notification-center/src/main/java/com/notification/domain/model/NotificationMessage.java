package com.notification.domain.model;

import java.io.Serializable;

public class NotificationMessage implements Serializable{
	private  String srcAppCode;
    private  DataEntity dataEntity;

    public String getSrcAppCode() {
        return srcAppCode;
    }

    public void setSrcAppCode(String srcAppCode) {
        this.srcAppCode = srcAppCode;
    }

	public DataEntity getDataEntity() {
		return dataEntity;
	}

	public void setDataEntity(DataEntity dataEntity) {
		this.dataEntity = dataEntity;
	}

    
}
