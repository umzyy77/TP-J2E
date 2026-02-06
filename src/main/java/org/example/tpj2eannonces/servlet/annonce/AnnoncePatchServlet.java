package org.example.tpj2eannonces.servlet.annonce;

import java.util.Optional;
import java.util.UUID;

import org.example.tpj2eannonces.exception.ValidationException;
import org.example.tpj2eannonces.model.Annonce;
import org.example.tpj2eannonces.model.AnnonceStatus;
import org.example.tpj2eannonces.service.AnnonceService;
import org.example.tpj2eannonces.service.ServiceException;
import org.example.tpj2eannonces.servlet.BaseServlet;
import org.example.tpj2eannonces.utils.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "annoncePatchServlet", urlPatterns = "/AnnoncePatch")
public class AnnoncePatchServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(AnnoncePatchServlet.class);
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
        String action = request.getParameter("action");
        
        try {
            UUID id = ValidationUtils.validateId(request.getParameter("id"));
            
            // Action de changement de statut (publish, archive, etc.) ?
            if (AnnonceStatus.fromAction(action).isPresent()) {
                annonceService.changeStatus(id, action);
                redirectTo(response, request.getContextPath() + "/AnnonceDetail?id=" + id + "&success=" + action);
                return;
            }
            
            // Action update
            if ("update".equals(action)) {
                handleUpdate(request, response, id);
                return;
            }
            
            handleNotFoundError(request, response, "Action inconnue: " + action);
        } catch (ValidationException e) {
            handleNotFoundError(request, response, e.getMessage());
        } catch (ServiceException e) {
            handleDatabaseError(request, response, e.getMessage());
        } catch (Exception e) {
            handleError(response, e);
        }
    }

    private void handleUpdate(HttpServletRequest request, HttpServletResponse response, UUID id) {
        Annonce annonce = new Annonce();
        annonce.setId(id);
        
        try {
            annonce.setTitle(ValidationUtils.validateTitle(request.getParameter("title")));
            annonce.setDescription(ValidationUtils.validateDescription(request.getParameter("description")));
            annonce.setAdress(ValidationUtils.validateAdress(request.getParameter("adress")));
            annonce.setMail(ValidationUtils.validateEmail(request.getParameter("mail")));

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
                request.setAttribute(ATTR_MESSAGE, "Mise à jour impossible.");
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
        }
    }
}
