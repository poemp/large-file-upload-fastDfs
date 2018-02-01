package org.poem.common.filter;

import org.poem.common.enums.LargeFileUploadAction;
import org.poem.common.helper.SchoolRequestContainer;
import org.poem.utils.ParameterRequestWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * filter
 */
public class FileUploadManagerFiler implements Filter {

    @Autowired
    SchoolRequestContainer container;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        container.populate((HttpServletRequest) servletRequest);
        Map<String,String[]> m = new HashMap<>(servletRequest.getParameterMap());
        Object orginRangObject =  ((HttpServletRequest) servletRequest).getHeader("Origin-Rang");
        if(null != orginRangObject){
            String[] rang = String.valueOf(orginRangObject).split("-");
            m.put(LargeFileUploadAction.fileOffset.name(),new String[]{rang[0]});
            m.put(LargeFileUploadAction.fileEnd.name(),new String[]{rang[1]});
            m.put(LargeFileUploadAction.partNumber.name(),new String[]{rang[2]});
        }
        servletRequest = new ParameterRequestWrapper((HttpServletRequest)servletRequest, m);
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
