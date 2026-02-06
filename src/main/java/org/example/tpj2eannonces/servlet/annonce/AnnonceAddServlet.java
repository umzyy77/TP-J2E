package org.example.tpj2eannonces.servlet.annonce;

import java.util.UUID;

import org.example.tpj2eannonces.exception.ValidationException;
import org.example.tpj2eannonces.model.Annonce;
import org.example.tpj2eannonces.model.User;
import org.example.tpj2eannonces.service.AnnonceService;
import org.example.tpj2eannonces.servlet.BaseServlet;
import org.example.tpj2eannonces.servlet.auth.LoginServlet;
import org.example.tpj2eannonces.utils.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "annonceAddServlet", urlPatterns = "/AnnonceAdd")
public class AnnonceAddServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(AnnonceAddServlet.class);
    private static final String VIEW_ADD = "/WEB-INF/jsp/annonce/add.jsp";
    private static final String ATTR_ANNONCE = "annonce";

    private final transient AnnonceService annonceService = new AnnonceService();

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.setAttribute(ATTR_ANNONCE, new Annonce());
            forwardTo(request, response, VIEW_ADD);
        } catch (Exception e) {
            handleError(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        Annonce annonce = new Annonce();
        annonce.setTitle(request.getParameter("title"));
        annonce.setDescription(request.getParameter("description"));
        annonce.setAdress(request.getParameter("adress"));
        annonce.setMail(request.getParameter("mail"));

        try {
            annonce.setTitle(ValidationUtils.validateTitle(annonce.getTitle()));
            annonce.setDescription(ValidationUtils.validateDescription(annonce.getDescription()));
            annonce.setAdress(ValidationUtils.validateAdress(annonce.getAdress()));
            annonce.setMail(ValidationUtils.validateEmail(annonce.getMail()));

            HttpSession session = request.getSession(false);
            UUID authorId = null;
            if (session != null && session.getAttribute(LoginServlet.SESSION_USER) != null) {
                User user = (User) session.getAttribute(LoginServlet.SESSION_USER);
                authorId = user.getId();
            }

            UUID categoryId = null;

            Annonce created = annonceService.create(annonce, authorId, categoryId);
            if (created == null || created.getId() == null) {
                request.setAttribute(ATTR_MESSAGE, "Erreur lors de l'enregistrement.");
                request.setAttribute(ATTR_ANNONCE, annonce);
                forwardTo(request, response, VIEW_ADD);
                return;
            }

            redirectTo(response, request.getContextPath() + "/AnnonceList?success=create");
        } catch (ValidationException e) {
            request.setAttribute(ATTR_MESSAGE, e.getMessage());
            request.setAttribute(ATTR_ANNONCE, annonce);
            forwardTo(request, response, VIEW_ADD);
        } catch (RuntimeException e) {
            handleDatabaseError(request, response, e.getMessage());
        } catch (Exception e) {
            handleError(response, e);
        }
    }
}

