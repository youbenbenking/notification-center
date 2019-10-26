package com.notification.converter;

import java.util.Date;

import com.notification.domain.model.NotificationLog;
import com.notification.domain.model.NotificationMessage;
import com.notification.domain.vo.RestfulParamVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author youben
 * @Description: 消息通知转换器
 */
@Component
@Slf4j
public class NotificationConverter {

    private  GeneralCopyConverter generalCopyConverter;

    @Autowired
    public NotificationConverter(GeneralCopyConverter generalCopyConverter) {
        this.generalCopyConverter = generalCopyConverter;
    }

    public NotificationLog convertToDto(RestfulParamVo<NotificationMessage> param) {
        NotificationLog notificationLog = new NotificationLog();
        notificationLog.setSrcAppCode(param.getParam().getSrcAppCode());
        notificationLog.setTargetUserName(param.getParam().getDataEntity().getTargetUsername());
        notificationLog.setMsgTitle(param.getParam().getDataEntity().getMessage().getTitle());
        notificationLog.setMsgDesc(param.getParam().getDataEntity().getMessage().getDesc());
        notificationLog.setMsgLink(param.getParam().getDataEntity().getMessage().getLink());
        notificationLog.setMsgType(param.getParam().getDataEntity().getMessage().getType());
        notificationLog.setCreateTime(new Date());

        return notificationLog;
    }
}
