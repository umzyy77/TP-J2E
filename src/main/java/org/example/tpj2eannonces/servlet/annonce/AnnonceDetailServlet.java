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

@WebServlet(name = "annonceDetailServlet", urlPatterns = "/AnnonceDetail")
public class AnnonceDetailServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(AnnonceDetailServlet.class);
    private static final String VIEW_DETAIL = "/WEB-INF/jsp/features/annonce/pages/detail.jsp";

    private final transient AnnonceService annonceService = new AnnonceService();

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            UUID id = ValidationUtils.validateId(request.getParameter("id"));
            Optional<Annonce> annonceOpt = annonceService.findByIdWithRelations(id);

            if (annonceOpt.isEmpty()) {
                forwardTo(request, response, VIEW_404);
                return;
            }

            request.setAttribute("annonce", annonceOpt.get());
            forwardTo(request, response, VIEW_DETAIL);
        } catch (ValidationException e) {
            handleNotFoundError(request, response, e.getMessage());
        } catch (RuntimeException e) {
            handleDatabaseError(request, response, e.getMessage());
        } catch (Exception e) {
            handleError(response, e);
        }
    }
}
