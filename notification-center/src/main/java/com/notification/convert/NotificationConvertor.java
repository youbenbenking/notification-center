package com.notification.convert;

import java.util.Date;

import com.notification.model.NotificationLog;
import com.notification.model.NotificationMessage;
import com.notification.vo.RestfulParamVo;
import org.springframework.stereotype.Component;

/**
 * @author youben
 * @Description: 消息通知转换器
 */
@Component
public class NotificationConvertor {

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
