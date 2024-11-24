package org.myproject.shortlink.admin.common.biz.user;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;

/**
 * 用户信息传输过滤器
 */
@RequiredArgsConstructor
public class UserTransmitFilter implements Filter {
    private final StringRedisTemplate stringRedisTemplate;

    private final static HashSet<String> IGNORE_URL = new HashSet<>(Lists.newArrayList(
            "/api/shortlink/admin/v1/user/has-username",
            "/api/shortlink/admin/v1/user/login",
            "/api/shortlink/admin/v1/user/register"
    ));

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        Collections.list(httpServletRequest.getHeaderNames())
                .forEach(headerName -> System.out.println(headerName + ": " + httpServletRequest.getHeader(headerName)));
        String realUri = httpServletRequest.getRequestURI();
        if (!IGNORE_URL.contains(realUri)) {
            String username = httpServletRequest.getHeader("username");
            String token = httpServletRequest.getHeader("token");
            if (StrUtil.isNotBlank(username) && StrUtil.isNotBlank(token)) {
                Object userInfoJsonString = stringRedisTemplate.opsForHash().get("login_" + username, token);
                if (userInfoJsonString != null) {
                    UserInfoDTO userInfoDTO = JSON.parseObject(userInfoJsonString.toString(), UserInfoDTO.class);
                    UserContext.setUser(userInfoDTO);
                }
            }
        }
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            UserContext.removeUser();
        }
    }
}
