package cn.boundivore.dl.boot.filter;

import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Objects;

/**
 * Description: 复制 Post 请求流，避免数据流只能一次性读取
 * Created by: Boundivore
 * Creation time: 2024/4/9
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
@Component
public class AccessPostMethodFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        AccessHttpServletRequestWrapper customHttpServletRequestWrapper = null;
        HttpServletRequest req = (HttpServletRequest) request;
        try {
            customHttpServletRequestWrapper = new AccessHttpServletRequestWrapper(req);
        } catch (Exception e) {
            log.error(
                    String.format(
                            "请求错误, URI: %s",
                            req.getRequestURI()
                    )
            );
            log.error(ExceptionUtil.stacktraceToString(e));
        }
        chain.doFilter((Objects.isNull(customHttpServletRequestWrapper) ? request : customHttpServletRequestWrapper), response);
    }
}
