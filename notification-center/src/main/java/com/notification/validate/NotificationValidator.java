package com.notification.validate;

import com.notification.enums.ResultCode;
import com.notification.exception.BadRequestException;
import com.notification.model.NotificationMessage;
import com.notification.vo.RestfulParamVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author youben
 * @Description: 消息通知验证器
 */
@Component
public class NotificationValidator {

    public void validateReceive(RestfulParamVo<NotificationMessage> param) {
        if (param == null || StringUtils.isEmpty(param.getToken())) {
            throw new BadRequestException(ResultCode.ILLEGAL_PARAMETER);
        }

        if (!param.getToken().equalsIgnoreCase("7ec864cfa06538146515ff2f21824f60")) {
            throw new BadRequestException(ResultCode.TOKEN_VALIDATE_FIALD);
        }
    }
}
