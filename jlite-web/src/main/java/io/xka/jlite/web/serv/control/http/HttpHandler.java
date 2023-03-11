package io.xka.jlite.web.serv.control.http;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HttpHandler extends HttpControl {
    public HttpHandler(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        super(httpServletRequest, httpServletResponse);
    }
}
