package org.poem.common.helper;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import org.poem.entity.InitializationConfiguration;
import org.poem.entity.upload.PrepareUploadJson;
import org.poem.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Component
public class LargeFileUploaderHelper {

    private static final String COOKIE_NAME = "largeFileCookieName";

    @Autowired
    private SchoolThreadLocalContainer schoolThreadLocalContainer;

    /**
     * 获取配置
     *
     * @return
     */
    public InitializationConfiguration getConfig() {
        HttpServletRequest request = schoolThreadLocalContainer.getRequest();
        HttpServletResponse response = schoolThreadLocalContainer.getResponse();
        Object result = RedisUtils.get(getClientId(request,response).toString());
        if(null != result){
            return  (InitializationConfiguration)result;
        }
        //没有就写入
        InitializationConfiguration initializationConfiguration = new InitializationConfiguration();
        RedisUtils.set(getClientId(request,response).toString(),initializationConfiguration);
        return  initializationConfiguration;
    }

    /**
     * 把信息写入到Response中，发送给客户端
     * @param jsonObject
     * @throws java.io.IOException
     */
    public void writeToResponse(Serializable jsonObject) {
        HttpServletResponse response = schoolThreadLocalContainer.getResponse();
        response.setContentType("application/json");
        try {
            response.getWriter().print(JSON.toJSONString(jsonObject));
            response.getWriter().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取文件目录信息
     * @param fromJson
     * @return
     */
    public Map<String,UUID> prepareUpload(PrepareUploadJson[] fromJson){
        InitializationConfiguration initializationConfiguration = this.getConfig();
        Map<String,UUID> f = Maps.newHashMap();
        for (PrepareUploadJson prepareUploadJson : fromJson) {

        }
        return  f;
    }
    /**
     * 获取客户id
     *
     * @param request
     * @param response
     * @return
     */
    private UUID getClientId(HttpServletRequest request, HttpServletResponse response) {
        //从session中获取数据
        UUID clientId = (UUID) request.getSession().getAttribute(COOKIE_NAME);
        //没有，就从cookie中获取
        if (clientId == null) {
            clientId = getClientFromCookie(request);
        }
        //还是没有，则随机创建一个
        if (clientId == null){
            clientId = UUID.randomUUID();
        }
        writeToCookie(request,response,clientId);
        return clientId;
    }

    /**
     * 写入cookie
     * @param request
     * @param response
     * @param client
     */
    private void writeToCookie(HttpServletRequest request, HttpServletResponse response, UUID client) {
        clearAllCookie(request, response);
        request.getSession().setAttribute(COOKIE_NAME, client);
        Cookie cookie = new Cookie(COOKIE_NAME, client.toString());
        cookie.setMaxAge((int) TimeUnit.DAYS.toSeconds(31));
        response.addCookie(cookie);
    }

    /**
     * cookie  中获取clientID
     * @param request
     * @return
     */
    private UUID getClientFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (null != cookies && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(COOKIE_NAME)) {
                    return UUID.fromString(cookie.getValue());
                }
            }
        }
        return null;
    }

    /**
     * 清除所有的数据
     */
    private void clearAllCookie(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().removeAttribute(COOKIE_NAME);
        Cookie[] cookies = request.getCookies();
        if (null != cookies && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(COOKIE_NAME)) {
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    break;
                }
            }
        }
    }

}
