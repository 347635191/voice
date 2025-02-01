package com.yf.rj.config;

import com.yf.rj.util.TraceUtil;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TraceInterceptor());
    }

    private static final class TraceInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            String traceId = request.getHeader(TraceUtil.KEY);
            traceId = StringUtils.defaultIfBlank(traceId, TraceUtil.create());
            MDC.put(TraceUtil.KEY, traceId);
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }

        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
            MDC.clear();
            HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
        }
    }
}