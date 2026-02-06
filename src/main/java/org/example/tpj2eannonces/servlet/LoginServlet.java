package org.example.tpj2eannonces.servlet;

import java.util.Optional;

import org.example.tpj2eannonces.model.User;
import org.example.tpj2eannonces.service.ServiceException;
import org.example.tpj2eannonces.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "loginServlet", urlPatterns = "/login")
public class LoginServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    private static final String VIEW_LOGIN = "/WEB-INF/jsp/auth/login.jsp";
    public static final String SESSION_USER = "loggedUser";

    private final transient UserService userService = new UserService();

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Si déjà connecté, rediriger vers la liste
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute(SESSION_USER) != null) {
                redirectTo(response, request.getContextPath() + "/AnnonceList");
                return;
            }
            forwardTo(request, response, VIEW_LOGIN);
        } catch (Exception e) {
            handleError(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            if (username == null || username.isBlank() || password == null || password.isBlank()) {
                request.setAttribute(ATTR_MESSAGE, "Veuillez remplir tous les champs.");
                request.setAttribute("username", username);
                forwardTo(request, response, VIEW_LOGIN);
                return;
            }

            Optional<User> userOpt = userService.authenticate(username, password);
            if (userOpt.isEmpty()) {
                request.setAttribute(ATTR_MESSAGE, "Identifiant ou mot de passe incorrect.");
                request.setAttribute("username", username);
                forwardTo(request, response, VIEW_LOGIN);
                return;
            }

            // Créer la session
            HttpSession session = request.getSession(true);
            session.setAttribute(SESSION_USER, userOpt.get());
            session.setMaxInactiveInterval(30 * 60); // 30 minutes

            // Rediriger vers la page demandée ou la liste
            String redirectUrl = (String) session.getAttribute("redirectAfterLogin");
            if (redirectUrl != null) {
                session.removeAttribute("redirectAfterLogin");
                redirectTo(response, redirectUrl);
            } else {
                redirectTo(response, request.getContextPath() + "/AnnonceList");
            }
        } catch (ServiceException e) {
            handleDatabaseError(request, response, e.getMessage());
        } catch (Exception e) {
            handleError(response, e);
        }
    }
}
