package org.example.tpj2eannonces.servlet.auth;

import org.example.tpj2eannonces.exception.ValidationException;
import org.example.tpj2eannonces.model.User;
import org.example.tpj2eannonces.service.UserService;
import org.example.tpj2eannonces.servlet.BaseServlet;
import org.example.tpj2eannonces.utils.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "registerServlet", urlPatterns = "/register")
public class RegisterServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(RegisterServlet.class);
    private static final String VIEW_REGISTER = "/WEB-INF/jsp/features/auth/pages/register.jsp";
    private static final String ATTR_USERNAME = "username";
    private static final String ATTR_EMAIL = "email";

    private final transient UserService userService = new UserService();

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            forwardTo(request, response, VIEW_REGISTER);
        } catch (Exception e) {
            handleError(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter(ATTR_USERNAME);
        String email = request.getParameter(ATTR_EMAIL);
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        try {
            validateRegistration(username, email, password, confirmPassword);

            User user = new User(username, email, password);
            userService.create(user);

            redirectTo(response, request.getContextPath() + "/login?registered=success");
        } catch (RuntimeException e) {
            request.setAttribute(ATTR_MESSAGE, e.getMessage());
            request.setAttribute(ATTR_USERNAME, username);
            request.setAttribute(ATTR_EMAIL, email);
            forwardTo(request, response, VIEW_REGISTER);
        } catch (Exception e) {
            handleError(response, e);
        }
    }

    private void validateRegistration(String username, String email, String password, String confirmPassword) {
        if (username == null || username.isBlank()) {
            throw new ValidationException("Le nom d'utilisateur est obligatoire.");
        }
        if (username.length() < 3 || username.length() > 50) {
            throw new ValidationException("Le nom d'utilisateur doit contenir entre 3 et 50 caractères.");
        }
        ValidationUtils.validateEmail(email);
        if (password == null || password.length() < 6) {
            throw new ValidationException("Le mot de passe doit contenir au moins 6 caractères.");
        }
        if (!password.equals(confirmPassword)) {
            throw new ValidationException("Les mots de passe ne correspondent pas.");
        }
    }
}
