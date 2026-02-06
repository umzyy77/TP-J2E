package org.example.tpj2eannonces.servlet;

import java.util.Optional;
import java.util.UUID;

import org.example.tpj2eannonces.exception.ValidationException;
import org.example.tpj2eannonces.model.Annonce;
import org.example.tpj2eannonces.service.AnnonceService;
import org.example.tpj2eannonces.service.ServiceException;
import org.example.tpj2eannonces.utils.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "annonceUpdateServlet", urlPatterns = "/AnnonceUpdate")
public class AnnonceUpdateServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(AnnonceUpdateServlet.class);
    private static final String VIEW_UPDATE = "/WEB-INF/jsp/annonce/update.jsp";
    private static final String ATTR_ANNONCE = "annonce";

    private final transient AnnonceService annonceService = new AnnonceService();

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            UUID id = ValidationUtils.validateId(request.getParameter("id"));
            Optional<Annonce> annonceOpt = annonceService.findById(id);
            if (annonceOpt.isEmpty()) {
                forwardTo(request, response, VIEW_404);
                return;
            }
            request.setAttribute(ATTR_ANNONCE, annonceOpt.get());
            forwardTo(request, response, VIEW_UPDATE);
        } catch (ValidationException e) {
            handleNotFoundError(request, response, e.getMessage());
        } catch (ServiceException e) {
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

            // Récupérer l'annonce existante pour préserver le statut et les relations
            Optional<Annonce> existingOpt = annonceService.findById(id);
            if (existingOpt.isEmpty()) {
                forwardTo(request, response, VIEW_404);
                return;
            }

            Annonce existing = existingOpt.get();
            existing.setTitle(annonce.getTitle());
            existing.setDescription(annonce.getDescription());
            existing.setAdress(annonce.getAdress());
            existing.setMail(annonce.getMail());

            Annonce updated = annonceService.update(existing);
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
        } catch (ServiceException e) {
            handleDatabaseError(request, response, e.getMessage());
        } catch (Exception e) {
            handleError(response, e);
        }
    }
}
