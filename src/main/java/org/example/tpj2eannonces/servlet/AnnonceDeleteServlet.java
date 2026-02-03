package org.example.tpj2eannonces.servlet;

import org.example.tpj2eannonces.dao.AnnonceDAO;
import org.example.tpj2eannonces.exception.DatabaseException;
import org.example.tpj2eannonces.exception.ValidationException;
import org.example.tpj2eannonces.utils.ValidationUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@WebServlet(name = "annonceDeleteServlet", urlPatterns = "/AnnonceDelete")
public class AnnonceDeleteServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(AnnonceDeleteServlet.class);

    private final transient AnnonceDAO annonceDAO = new AnnonceDAO();

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            UUID id = ValidationUtils.validateId(request.getParameter("id"));
            boolean deleted = annonceDAO.delete(id);
            if (!deleted) {
                forwardTo(request, response, VIEW_404);
                return;
            }
            redirectTo(response, request.getContextPath() + "/AnnonceList?success=delete");
        } catch (ValidationException e) {
            handleNotFoundError(request, response, e.getMessage());
        } catch (DatabaseException e) {
            handleDatabaseError(request, response, e.getMessage());
        } catch (Exception e) {
            handleError(response, e);
        }
    }
}
