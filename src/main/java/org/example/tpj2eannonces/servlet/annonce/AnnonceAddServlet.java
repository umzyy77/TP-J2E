package org.example.tpj2eannonces.servlet.annonce;

import java.util.UUID;

import org.example.tpj2eannonces.exception.ValidationException;
import org.example.tpj2eannonces.model.Annonce;
import org.example.tpj2eannonces.utils.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "annonceAddServlet", urlPatterns = "/AnnonceAdd")
public class AnnonceAddServlet extends AbstractAnnonceFormServlet {
    private static final Logger logger = LoggerFactory.getLogger(AnnonceAddServlet.class);
    private static final String VIEW_ADD = "/WEB-INF/jsp/features/annonce/pages/add.jsp";

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            forwardWithFormData(request, response, new Annonce(), null, null);
        } catch (Exception e) {
            handleError(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        Annonce annonce = buildAnnonceFromRequest(request);

        String categoryIdParam = request.getParameter("categoryId");

        try {
            validateAnnonce(annonce);

            UUID authorId = requireLoggedUserId(request);

            Long categoryId = ValidationUtils.validateLongId(categoryIdParam);

            Annonce created = annonceService.create(annonce, authorId, categoryId);
            if (created == null || created.getId() == null) {
                forwardWithFormData(request, response, annonce, categoryIdParam, "Erreur lors de l'enregistrement.");
                return;
            }

            redirectTo(response, request.getContextPath() + "/AnnonceList?success=create");
        } catch (ValidationException e) {
            forwardWithFormData(request, response, annonce, categoryIdParam, e.getMessage());
        } catch (RuntimeException e) {
            handleDatabaseError(request, response, e.getMessage());
        } catch (Exception e) {
            handleError(response, e);
        }
    }

    private Annonce buildAnnonceFromRequest(HttpServletRequest request) {
        Annonce annonce = new Annonce();
        annonce.setTitle(request.getParameter("title"));
        annonce.setDescription(request.getParameter("description"));
        annonce.setAdress(request.getParameter("adress"));
        annonce.setMail(request.getParameter("mail"));
        return annonce;
    }

    private void validateAnnonce(Annonce annonce) {
        annonce.setTitle(ValidationUtils.validateTitle(annonce.getTitle()));
        annonce.setDescription(ValidationUtils.validateDescription(annonce.getDescription()));
        annonce.setAdress(ValidationUtils.validateAdress(annonce.getAdress()));
        annonce.setMail(ValidationUtils.validateEmail(annonce.getMail()));
    }

    private void forwardWithFormData(
            HttpServletRequest request, HttpServletResponse response, Annonce annonce,
            String categoryIdParam, String message) {
        if (message != null) {
            request.setAttribute(ATTR_MESSAGE, message);
        }
        request.setAttribute(ATTR_ANNONCE, annonce);
        request.setAttribute(ATTR_CATEGORIES, categoryService.findAll());
        request.setAttribute(ATTR_SELECTED_CATEGORY, categoryIdParam);
        forwardTo(request, response, VIEW_ADD);
    }
}
