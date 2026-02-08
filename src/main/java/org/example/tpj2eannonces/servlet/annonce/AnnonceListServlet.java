package org.example.tpj2eannonces.servlet.annonce;

import java.util.List;
import java.util.UUID;

import org.example.tpj2eannonces.model.Annonce;
import org.example.tpj2eannonces.model.AnnonceStatus;
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
    private static final String VIEW_LIST = "/WEB-INF/jsp/features/annonce/pages/list.jsp";
    private static final int PAGE_SIZE = 10;

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
            String statusParam = request.getParameter("status");
            String searchParam = request.getParameter("q");

            List<Annonce> annonces;
            long totalCount;

            if (searchParam != null && !searchParam.isBlank()) {
                annonces = annonceService.search(searchParam.trim(), page, PAGE_SIZE);
                totalCount = annonceService.countByKeyword(searchParam.trim());
                request.setAttribute("searchQuery", searchParam.trim());
            } else if (authorParam != null && !authorParam.isEmpty()) {
                UUID authorId = UUID.fromString(authorParam);
                annonces = annonceService.findByAuthor(authorId, page, PAGE_SIZE);
                totalCount = annonceService.countByAuthor(authorId);
                request.setAttribute("filterByAuthor", true);
            } else if (categoryParam != null && !categoryParam.isEmpty()) {
                Long categoryId = Long.parseLong(categoryParam);
                annonces = annonceService.findByCategory(categoryId, page, PAGE_SIZE);
                totalCount = annonceService.countByCategory(categoryId);
                request.setAttribute("filterByCategory", true);
                request.setAttribute("selectedCategory", categoryParam);
            } else if (statusParam != null && !statusParam.isEmpty()) {
                AnnonceStatus status = AnnonceStatus.valueOf(statusParam);
                annonces = annonceService.findByStatus(status, page, PAGE_SIZE);
                totalCount = annonceService.countByStatus(status);
                request.setAttribute("filterByStatus", true);
                request.setAttribute("selectedStatus", statusParam);
            } else {
                annonces = annonceService.findAll(page, PAGE_SIZE);
                totalCount = annonceService.count();
            }

            String baseUrl = request.getContextPath() + "/AnnonceList?_=1";
            if (searchParam != null && !searchParam.isBlank()) {
                baseUrl += "&q=" + searchParam.trim();
            }
            if (authorParam != null && !authorParam.isEmpty()) {
                baseUrl += "&author=" + authorParam;
            }
            if (categoryParam != null && !categoryParam.isEmpty()) {
                baseUrl += "&category=" + categoryParam;
            }
            if (statusParam != null && !statusParam.isEmpty()) {
                baseUrl += "&status=" + statusParam;
            }

            request.setAttribute("categories", categoryService.findAll());
            request.setAttribute("statuses", AnnonceStatus.values());
            request.setAttribute("annonceList", annonces);
            request.setAttribute("annonceCount", totalCount);
            request.setAttribute("currentPage", page);
            request.setAttribute("pageSize", PAGE_SIZE);
            request.setAttribute("totalPages", (int) Math.ceil((double) totalCount / PAGE_SIZE));
            request.setAttribute("paginationBaseUrl", baseUrl);

            forwardTo(request, response, VIEW_LIST);
        } catch (IllegalArgumentException _) {
            handleDatabaseError(request, response, "Paramètre de filtre invalide");
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
