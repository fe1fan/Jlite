package io.xka.jlite.web.control;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class IHandler extends IControl {
    protected IHandler(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        super(httpServletRequest, httpServletResponse);
    }
}
