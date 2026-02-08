package org.example.tpj2eannonces.servlet;

import java.io.IOException;
import java.util.UUID;

import org.example.tpj2eannonces.exception.ValidationException;
import org.example.tpj2eannonces.model.OwnableByUser;
import org.example.tpj2eannonces.model.User;
import org.example.tpj2eannonces.servlet.auth.LoginServlet;
import org.slf4j.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public abstract class BaseServlet extends HttpServlet {
    protected static final String VIEW_404 = "/WEB-INF/jsp/features/errors/pages/404.jsp";
    protected static final String VIEW_500 = "/WEB-INF/jsp/features/errors/pages/500.jsp";
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
            // Impossible de repondre au client
        }
    }

    protected UUID requireLoggedUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(LoginServlet.SESSION_USER) == null) {
            throw new ValidationException("Authentification requise");
        }
        User user = (User) session.getAttribute(LoginServlet.SESSION_USER);
        return user.getId();
    }

    protected <T extends OwnableByUser> boolean isOwnedBy(T resource, UUID loggedUserId) {
        if (resource == null || loggedUserId == null) {
            return true;
        }
        UUID ownerId = resource.getOwnerId();
        return ownerId == null || !ownerId.equals(loggedUserId);
    }

    protected void sendForbidden(HttpServletResponse response) {
        try {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        } catch (IOException e) {
            handleError(response, e);
        }
    }
}
