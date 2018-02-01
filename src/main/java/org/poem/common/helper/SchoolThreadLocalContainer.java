package org.poem.common.helper;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * thread local
 * can user this parm every where
 */
@Component
public class SchoolThreadLocalContainer {

    private ThreadLocal<HttpServletRequest> requestThreadLocal = new ThreadLocal<>();
    private ThreadLocal<HttpServletResponse> responseThreadLocal = new ThreadLocal<>();

    public void populate(HttpServletRequest request,HttpServletResponse response){
        requestThreadLocal.set(request);
        responseThreadLocal.set(response);
    }

    public HttpServletRequest getRequest(){
        return  requestThreadLocal.get();
    }

    public HttpServletResponse getResponse(){
        return  responseThreadLocal.get();
    }

    public void clear(){
        requestThreadLocal.remove();
        responseThreadLocal.remove();
    }
}
