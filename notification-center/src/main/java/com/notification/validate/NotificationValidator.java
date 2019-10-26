package com.notification.validate;

import java.util.Objects;

import com.notification.enums.ResultCode;
import com.notification.exception.BadRequestException;
import com.notification.model.NotificationMessage;
import com.notification.vo.RestfulParamVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author youben
 * @Description: 消息通知验证器
 */
@Component
public class NotificationValidator {

    public void validateReceive(RestfulParamVo<NotificationMessage> restfulParamVo) {
        Assert.notNull(restfulParamVo, "消息通知信息不能为空.");
        Assert.isTrue(StringUtils.isNotBlank(restfulParamVo.getToken()), "token不能为空.");
        Assert.isTrue(Objects.equals("7ec864cfa06538146515ff2f21824f60", restfulParamVo.getToken()), "token验证失败.");
    }
}
