package com.notification.converter;

import java.util.Objects;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class GeneralCopyConverter {

    public <T, R> R convertTo(final T data, R dto) {
        if (Objects.isNull(data)){
            return null;
        }

        BeanUtils.copyProperties(data, dto);
        return dto;
    }
}
