package org.example.tpj2eannonces.servlet.annonce;

import java.util.List;
import java.util.UUID;

import org.example.tpj2eannonces.model.Annonce;
import org.example.tpj2eannonces.service.AnnonceService;
import org.example.tpj2eannonces.service.CategoryService;
import org.example.tpj2eannonces.servlet.BaseServlet;
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
    private final transient CategoryService categoryService = new CategoryService();

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            int page = getPageParam(request);
            String authorParam = request.getParameter("author");
            String categoryParam = request.getParameter("category");

            List<Annonce> annonces;
            long totalCount;

            if (authorParam != null && !authorParam.isEmpty()) {
                UUID authorId = UUID.fromString(authorParam);
                annonces = annonceService.findByAuthor(authorId, page, PAGE_SIZE);
                totalCount = annonceService.countByAuthor(authorId);
                request.setAttribute("filterByAuthor", true);
            } else if (categoryParam != null && !categoryParam.isEmpty()) {
                UUID categoryId = UUID.fromString(categoryParam);
                annonces = annonceService.findByCategory(categoryId, page, PAGE_SIZE);
                totalCount = annonceService.countByCategory(categoryId);
                request.setAttribute("filterByCategory", true);
                request.setAttribute("selectedCategory", categoryParam);
            } else {
                annonces = annonceService.findAll(page, PAGE_SIZE);
                totalCount = annonceService.count();
            }

            request.setAttribute("categories", categoryService.findAll());
            request.setAttribute("annonceList", annonces);
            request.setAttribute("annonceCount", totalCount);
            request.setAttribute("currentPage", page);
            request.setAttribute("pageSize", PAGE_SIZE);
            request.setAttribute("totalPages", (int) Math.ceil((double) totalCount / PAGE_SIZE));

            forwardTo(request, response, VIEW_LIST);
        } catch (IllegalArgumentException _) {
            handleDatabaseError(request, response, "ID auteur invalide");
        } catch (RuntimeException e) {
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
            } catch (NumberFormatException _) {
                return 0;
            }
        }
        return 0;
    }
}
