package io.xka.jlite.web.serv.control;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class HttpServletControl extends HttpServlet {

    Logger logger = LoggerFactory.getLogger(HttpServletControl.class);

    private void handle(HttpServletRequest req, HttpServletResponse resp) {
        String path = req.getRequestURI().substring(req.getContextPath().length());
        Function<IHandler, Boolean> handler = HandlerFactory.get(path);
        if (handler != null) {
            Boolean apply = handler.apply(new IHandler(req, resp));
            if (apply != null && !apply) {
                return;
            }
        }
        Controls controls = ControlFactory.get(req.getRequestURI().substring(req.getContextPath().length()), HttpMethod.valueOf(req.getMethod()));
        if (controls == null || controls.getControl() == null) {
            resp.setStatus(404);
            return;
        }
        if (controls.getKvs() != null && !controls.getKvs().isEmpty()) {
            controls.getKvs().forEach(req::setAttribute);
        }
        controls.getControl().accept(new IControl(req, resp));
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
