package com.notification.exception;

public class NotSerializableException extends RuntimeException{

	public NotSerializableException( final Class type) {
		super(String.format("class %s must implement java.io.Serializable", type.getName()));
	}
}
