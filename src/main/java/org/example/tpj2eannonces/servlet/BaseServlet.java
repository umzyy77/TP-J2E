package org.example.tpj2eannonces.servlet;

import java.io.IOException;

import org.slf4j.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public abstract class BaseServlet extends HttpServlet {
    protected static final String VIEW_404 = "/WEB-INF/jsp/errors/404.jsp";
    protected static final String VIEW_500 = "/WEB-INF/jsp/errors/500.jsp";
    protected static final String ATTR_MESSAGE = "message";

    protected abstract Logger getLogger();

    protected void forwardTo(HttpServletRequest request, HttpServletResponse response, String view) {
        try {
            request.getRequestDispatcher(view).forward(request, response);
        } catch (ServletException | IOException e) {
            handleError(response, e);
        }
    }

    protected void redirectTo(HttpServletResponse response, String url) {
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            handleError(response, e);
        }
    }

    protected void handleNotFoundError(HttpServletRequest request, HttpServletResponse response, String message) {
        request.setAttribute(ATTR_MESSAGE, message);
        forwardTo(request, response, VIEW_404);
    }

    protected void handleDatabaseError(HttpServletRequest request, HttpServletResponse response, String message) {
        request.setAttribute(ATTR_MESSAGE, message);
        forwardTo(request, response, VIEW_500);
    }

    protected void handleError(HttpServletResponse response, Exception e) {
        getLogger().error("Erreur inattendue dans {}", getClass().getSimpleName(), e);
        try {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (IOException _) {
            // Impossible de répondre au client
        }
    }
}
