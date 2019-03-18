package com.notification.job;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import com.notification.dao.CronMapper;


/**
 * 定时删除超过三天前的通知消息
 * (这里采用基于接口的实现,便于动态更改数据库调整执行周期即时生效,基于注解的周期调整需要重新发布,这里定时任务单一,单线程处理即可)
 * @author youben
 *
 */
@Component
//@EnableScheduling
@Configuration
public class CleanDataScheduleTask implements SchedulingConfigurer{
	private static Logger logger=LoggerFactory.getLogger(CleanDataScheduleTask.class);
	
    @Autowired     
    private CronMapper cronMapper;
    
    /**
     * 执行定时任务.
     */
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.addTriggerTask(()->{
			//执行任务
			excete();
		}, triggerContext ->{
			//从数据库获取执行周期
            String cron = cronMapper.getCron("cleanData");
            //合法性校验.
            if (StringUtils.isEmpty(cron)) {
                // Omitted Code ..
            }
            //返回执行周期(Date)
            return new CronTrigger(cron).nextExecutionTime(triggerContext);
		});
		
	}
	
	public void excete() {
		logger.info("执行动态定时任务: "+ LocalDateTime.now().toLocalTime() );
	}

}
