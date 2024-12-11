package org.myproject.shortlink.admin.common.biz.user;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import groovy.util.logging.Slf4j;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.myproject.shortlink.admin.common.convention.exception.ClientException;
import org.myproject.shortlink.admin.common.convention.result.Results;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.io.PrintWriter;
import java.util.Optional;

import static org.myproject.shortlink.admin.common.convention.errorcode.BaseErrorCode.FLOW_LIMIT_ERROR;


@lombok.extern.slf4j.Slf4j
@Slf4j
@RequiredArgsConstructor
public class UserFlowRiskControlFilter implements Filter {
    private final StringRedisTemplate stringRedisTemplate;
    private final String timeWindow;
    private final Long maxAccessCount;
    private static final String USER_FLOW_RISK_CONTROL_LUA_SCRIPT_PATH = "lua/user-flow-risk-control.lua";


    @SneakyThrows
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(USER_FLOW_RISK_CONTROL_LUA_SCRIPT_PATH)));
        redisScript.setResultType(Long.class);
        String username = Optional.ofNullable(UserContext.getUsername()).orElse("other");
        Long result = null;
        try {
            result = stringRedisTemplate.execute(redisScript, Lists.newArrayList(username), timeWindow);
        }catch (Exception e){
            log.error("执行用户请求流量控制出错：",e);
            returnJson((HttpServletResponse) response, JSON.toJSONString(Results.failure(new ClientException(FLOW_LIMIT_ERROR))));
        }
        if (result == null || result > maxAccessCount) {
            returnJson((HttpServletResponse) response, JSON.toJSONString(Results.failure(new ClientException(FLOW_LIMIT_ERROR))));
        }
        chain.doFilter(request, response);
    }


    private void returnJson(HttpServletResponse response, String json) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.print(json);
        }
    }
}
