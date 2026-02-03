package org.example.tpj2eannonces.servlet;

import org.example.tpj2eannonces.dao.AnnonceDAO;
import org.example.tpj2eannonces.exception.DatabaseException;
import org.example.tpj2eannonces.model.Annonce;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@WebServlet(name = "annonceListServlet", urlPatterns = "/AnnonceList")
public class AnnonceListServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(AnnonceListServlet.class);
    private static final String VIEW_LIST = "/WEB-INF/jsp/annonce/list.jsp";

    private final transient AnnonceDAO annonceDAO = new AnnonceDAO();

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            List<Annonce> annonces = annonceDAO.list();
            request.setAttribute("annonceList", annonces);
            request.setAttribute("annonceCount", annonces.size());
            forwardTo(request, response, VIEW_LIST);
        } catch (DatabaseException e) {
            handleDatabaseError(request, response, e.getMessage());
        } catch (Exception e) {
            handleError(response, e);
        }
    }
}
