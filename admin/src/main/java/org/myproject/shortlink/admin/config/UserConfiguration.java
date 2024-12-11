package org.myproject.shortlink.admin.config;

import org.myproject.shortlink.admin.common.biz.user.UserFlowRiskControlFilter;
import org.myproject.shortlink.admin.common.biz.user.UserTransmitFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 用户配置自动装配
 */
@Configuration
public class UserConfiguration {

    /**
     * 用户信息传递过滤器
     */
    @Bean
    public FilterRegistrationBean<UserTransmitFilter> globalUserTransmitFilter(StringRedisTemplate stringRedisTemplate) {
        FilterRegistrationBean<UserTransmitFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new UserTransmitFilter(stringRedisTemplate));
        registration.addUrlPatterns("/*");
        registration.setOrder(0);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<UserFlowRiskControlFilter> globalUserFlowRiskControlFilter(
            StringRedisTemplate stringRedisTemplate,
            @Value("${short-link.flow-limit.time-window}") String timeWindow,
            @Value("${short-link.flow-limit.max-access-count}") Long maxAccessCount) {
        FilterRegistrationBean<UserFlowRiskControlFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new UserFlowRiskControlFilter(stringRedisTemplate,timeWindow,maxAccessCount));
        registration.addUrlPatterns("/*");
        registration.setOrder(10);
        return registration;
    }
}

