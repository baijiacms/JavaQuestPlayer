package com.baijiacms.qsp.handler;

import com.baijiacms.qsp.template.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

public class EngineHandler extends AbstractHandler {
    private HtmlHandler htmlHandler;

    private UserTemplate userTemplate;
    private ConsoleTemplate consoleTemplate;
    private ActionTemplate actionTemplate;
    private HtmlTemplate htmlTemplate;
    private IndexTemplate indexTemplate;
    private GameSelectTemplate gameSelectTemplate;
    private LoadingTemplate loadingTemplate;
    private GameSaveTemplate gameSaveTemplate;

    public EngineHandler() {
        initVelocityEngine();
        htmlHandler = new HtmlHandler(this);
    }

    private void initVelocityEngine() {
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(Velocity.RESOURCE_LOADER, Velocity.RESOURCE_LOADER_CLASS);
        ve.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        ve.init();
        this.userTemplate = new UserTemplate(ve);
        this.actionTemplate = new ActionTemplate(ve);
        this.consoleTemplate = new ConsoleTemplate(ve);
        this.htmlTemplate = new HtmlTemplate(ve);
        this.indexTemplate = new IndexTemplate(ve);
        this.gameSelectTemplate = new GameSelectTemplate(ve);
        this.loadingTemplate = new LoadingTemplate(ve);
        this.gameSaveTemplate = new GameSaveTemplate(ve);
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        String urlPath = request.getRequestURL().toString();
        if (StringUtils.isEmpty(request.getParameter("actionScript")) == false) {
            urlPath = urlPath + "?actionScript=" + request.getParameter("actionScript");
        }
        htmlHandler.handle(new URL(urlPath), target, response);
    }

    public UserTemplate getUserTemplate() {
        return userTemplate;
    }

    public ConsoleTemplate getConsoleTemplate() {
        return consoleTemplate;
    }

    public ActionTemplate getActionTemplate() {
        return actionTemplate;
    }

    public HtmlTemplate getHtmlTemplate() {
        return htmlTemplate;
    }

    public IndexTemplate getIndexTemplate() {
        return indexTemplate;
    }

    public GameSelectTemplate getGameSelectTemplate() {
        return gameSelectTemplate;
    }

    public LoadingTemplate getLoadingTemplate() {
        return loadingTemplate;
    }

    public GameSaveTemplate getGameSaveTemplate() {
        return gameSaveTemplate;
    }


}
