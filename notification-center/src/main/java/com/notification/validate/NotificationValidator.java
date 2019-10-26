package com.notification.validate;

import java.util.Objects;

import com.notification.domain.model.NotificationMessage;
import com.notification.domain.vo.RestfulParamVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author youben
 * @Description: 消息通知验证器
 */
@Component
@Slf4j
public class NotificationValidator {

    public void validateReceive(RestfulParamVo<NotificationMessage> restfulParamVo) {
        Assert.notNull(restfulParamVo, "消息通知信息不能为空.");
        Assert.isTrue(StringUtils.isNotBlank(restfulParamVo.getToken()), "token不能为空.");
        Assert.isTrue(Objects.equals("7ec864cfa06538146515ff2f21824f60", restfulParamVo.getToken()), "token验证失败.");
    }
}
