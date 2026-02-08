package org.example.tpj2eannonces.servlet.annonce;

import java.util.Optional;
import java.util.UUID;

import org.example.tpj2eannonces.exception.ValidationException;
import org.example.tpj2eannonces.model.Annonce;
import org.example.tpj2eannonces.service.AnnonceService;
import org.example.tpj2eannonces.servlet.BaseServlet;
import org.example.tpj2eannonces.utils.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "annonceDeleteServlet", urlPatterns = "/AnnonceDelete")
public class AnnonceDeleteServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(AnnonceDeleteServlet.class);

    private final transient AnnonceService annonceService = new AnnonceService();

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            Long id = ValidationUtils.validateLongId(request.getParameter("id"));
            UUID loggedUserId = requireLoggedUserId(request);
            Optional<Annonce> annonceOpt = annonceService.findByIdWithRelations(id);
            if (annonceOpt.isEmpty()) {
                forwardTo(request, response, VIEW_404);
                return;
            }
            if (isOwnedBy(annonceOpt.get(), loggedUserId)) {
                sendForbidden(response);
                return;
            }

            boolean deleted = annonceService.delete(id);
            if (!deleted) {
                forwardTo(request, response, VIEW_404);
                return;
            }
            redirectTo(response, request.getContextPath() + "/AnnonceList?success=delete");
        } catch (ValidationException e) {
            handleNotFoundError(request, response, e.getMessage());
        } catch (RuntimeException e) {
            handleDatabaseError(request, response, e.getMessage());
        } catch (Exception e) {
            handleError(response, e);
        }
    }
}
