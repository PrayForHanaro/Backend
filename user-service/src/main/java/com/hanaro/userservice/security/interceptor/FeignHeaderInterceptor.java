package com.hanaro.userservice.security.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignHeaderInterceptor implements RequestInterceptor {

  @Override
  public void apply(RequestTemplate template) {

    ServletRequestAttributes attrs =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

    if (attrs == null) return;

    HttpServletRequest request = attrs.getRequest();

    copy(request, template, "Authorization");
    copy(request, template, "X-Auth-User-Id");
    copy(request, template, "X-Auth-User-Name");
    copy(request, template, "X-Auth-User-Role");
    copy(request, template, "X-Auth-Org-Id");
  }

  private void copy(HttpServletRequest req, RequestTemplate template, String name) {
    String value = req.getHeader(name);
    if (value != null) {
      template.header(name, value);
    }
  }
}