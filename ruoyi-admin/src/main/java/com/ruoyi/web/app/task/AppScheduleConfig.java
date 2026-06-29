package com.ruoyi.web.app.task;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启用 Spring 定时任务（订单超时自动关闭等 C 端业务任务）。
 *
 * @author liwangfu
 */
@Configuration
@EnableScheduling
public class AppScheduleConfig
{
}
