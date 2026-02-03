package org.example.tpj2eannonces.servlet;

import org.example.tpj2eannonces.dao.AnnonceDAO;
import org.example.tpj2eannonces.exception.DatabaseException;
import org.example.tpj2eannonces.exception.ValidationException;
import org.example.tpj2eannonces.model.Annonce;
import org.example.tpj2eannonces.utils.ValidationUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@WebServlet(name = "annonceUpdateServlet", urlPatterns = "/AnnonceUpdate")
public class AnnonceUpdateServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(AnnonceUpdateServlet.class);
    private static final String VIEW_UPDATE = "/WEB-INF/jsp/annonce/update.jsp";
    private static final String ATTR_ANNONCE = "annonce";

    private final transient AnnonceDAO annonceDAO = new AnnonceDAO();

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            UUID id = ValidationUtils.validateId(request.getParameter("id"));
            Annonce annonce = annonceDAO.find(id);
            if (annonce == null) {
                forwardTo(request, response, VIEW_404);
                return;
            }
            request.setAttribute(ATTR_ANNONCE, annonce);
            forwardTo(request, response, VIEW_UPDATE);
        } catch (ValidationException e) {
            handleNotFoundError(request, response, e.getMessage());
        } catch (DatabaseException e) {
            handleDatabaseError(request, response, e.getMessage());
        } catch (Exception e) {
            handleError(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        Annonce annonce = new Annonce();
        try {
            UUID id = ValidationUtils.validateId(request.getParameter("id"));
            annonce.setId(id);
            annonce.setTitle(ValidationUtils.validateTitle(request.getParameter("title")));
            annonce.setDescription(ValidationUtils.validateDescription(request.getParameter("description")));
            annonce.setAdress(ValidationUtils.validateAdress(request.getParameter("adress")));
            annonce.setMail(ValidationUtils.validateEmail(request.getParameter("mail")));

            Annonce updated = annonceDAO.update(annonce);
            if (updated == null) {
                request.setAttribute(ATTR_MESSAGE, "Mise a jour impossible.");
                request.setAttribute(ATTR_ANNONCE, annonce);
                forwardTo(request, response, VIEW_UPDATE);
                return;
            }

            redirectTo(response, request.getContextPath() + "/AnnonceList?success=update");
        } catch (ValidationException e) {
            annonce.setTitle(request.getParameter("title"));
            annonce.setDescription(request.getParameter("description"));
            annonce.setAdress(request.getParameter("adress"));
            annonce.setMail(request.getParameter("mail"));
            request.setAttribute(ATTR_MESSAGE, e.getMessage());
            request.setAttribute(ATTR_ANNONCE, annonce);
            forwardTo(request, response, VIEW_UPDATE);
        } catch (DatabaseException e) {
            handleDatabaseError(request, response, e.getMessage());
        } catch (Exception e) {
            handleError(response, e);
        }
    }
}
