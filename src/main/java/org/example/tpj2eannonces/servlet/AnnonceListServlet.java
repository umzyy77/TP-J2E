package org.example.tpj2eannonces.servlet;

import java.util.List;

import org.example.tpj2eannonces.model.Annonce;
import org.example.tpj2eannonces.service.AnnonceService;
import org.example.tpj2eannonces.service.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "annonceListServlet", urlPatterns = "/AnnonceList")
public class AnnonceListServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(AnnonceListServlet.class);
    private static final String VIEW_LIST = "/WEB-INF/jsp/annonce/list.jsp";
    private static final int PAGE_SIZE = 20;

    private final transient AnnonceService annonceService = new AnnonceService();

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            int page = getPageParam(request);
            List<Annonce> annonces = annonceService.findAll(page, PAGE_SIZE);
            long totalCount = annonceService.count();

            request.setAttribute("annonceList", annonces);
            request.setAttribute("annonceCount", totalCount);
            request.setAttribute("currentPage", page);
            request.setAttribute("pageSize", PAGE_SIZE);
            request.setAttribute("totalPages", (int) Math.ceil((double) totalCount / PAGE_SIZE));

            forwardTo(request, response, VIEW_LIST);
        } catch (ServiceException e) {
            handleDatabaseError(request, response, e.getMessage());
        } catch (Exception e) {
            handleError(response, e);
        }
    }

    private int getPageParam(HttpServletRequest request) {
        String pageStr = request.getParameter("page");
        if (pageStr != null) {
            try {
                return Math.max(0, Integer.parseInt(pageStr));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
}
