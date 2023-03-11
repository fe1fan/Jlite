package io.xka.jlite.web.serv.control.http;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class BasicHttpServletControl extends HttpServlet {

    Logger logger = LoggerFactory.getLogger(BasicHttpServletControl.class);

    private void handle(HttpServletRequest req, HttpServletResponse resp) {
        String path = req.getRequestURI().substring(req.getContextPath().length());
        Function<HttpHandler, Boolean> handler = HttpHandlerFactory.get(path);
        if (handler != null) {
            Boolean apply = handler.apply(new HttpHandler(req, resp));
            if (apply != null && !apply) {
                return;
            }
        }
        HttpControls httpControls = HttpControlFactory.get(
                req.getRequestURI().substring(req.getContextPath().length()),
                HttpMethod.valueOf(req.getMethod()));
        if (httpControls == null || httpControls.getControl() == null) {
            resp.setStatus(404);
            return;
        }
        if (httpControls.getKvs() != null && !httpControls.getKvs().isEmpty()) {
            httpControls.getKvs().forEach(req::setAttribute);
        }
        httpControls.getControl().accept(new HttpControl(req, resp));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        this.handle(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        this.handle(req, resp);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) {
        this.handle(req, resp);
    }


    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        this.handle(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        this.handle(req, resp);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        this.handle(req, resp);
    }

    @Override
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) {
        this.handle(req, resp);
    }
}
