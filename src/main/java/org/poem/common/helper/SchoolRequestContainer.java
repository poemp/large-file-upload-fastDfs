package org.poem.common.helper;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * thread local
 * can user this parm every where
 */
@Component
public class SchoolRequestContainer {

    private ThreadLocal<HttpServletRequest> requestThreadLocal = new ThreadLocal<>();

    public void populate(HttpServletRequest request){
        requestThreadLocal.set(request);
    }

    public HttpServletRequest getRequest(){
        return  requestThreadLocal.get();
    }

    public void clear(){
        requestThreadLocal.remove();
    }

    public ThreadLocal<HttpServletRequest> getRequestThreadLocal() {
        return requestThreadLocal;
    }

    public void setRequestThreadLocal(ThreadLocal<HttpServletRequest> requestThreadLocal) {
        this.requestThreadLocal = requestThreadLocal;
    }
}
