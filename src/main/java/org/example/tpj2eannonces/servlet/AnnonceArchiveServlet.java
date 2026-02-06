package org.example.tpj2eannonces.servlet;

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

@WebServlet(name = "annonceArchiveServlet", urlPatterns = "/AnnonceArchive")
public class AnnonceArchiveServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(AnnonceArchiveServlet.class);

    private final transient AnnonceService annonceService = new AnnonceService();

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            UUID id = ValidationUtils.validateId(request.getParameter("id"));
            Annonce annonce = annonceService.archive(id);

            if (annonce == null) {
                forwardTo(request, response, VIEW_404);
                return;
            }

            redirectTo(response, request.getContextPath() + "/AnnonceDetail?id=" + id + "&success=archive");
        } catch (ValidationException e) {
            handleNotFoundError(request, response, e.getMessage());
        } catch (ServiceException e) {
            handleDatabaseError(request, response, e.getMessage());
        } catch (Exception e) {
            handleError(response, e);
        }
    }
}
