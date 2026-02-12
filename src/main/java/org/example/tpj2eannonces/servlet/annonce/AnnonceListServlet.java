package org.example.tpj2eannonces.servlet.annonce;

import java.util.UUID;

import org.example.tpj2eannonces.dtos.AnnonceListDTO;
import org.example.tpj2eannonces.model.AnnonceStatus;
import org.example.tpj2eannonces.service.AnnonceService;
import org.example.tpj2eannonces.service.CategoryService;
import org.example.tpj2eannonces.servlet.BaseServlet;
import org.example.tpj2eannonces.utils.ConditionalResolver;
import org.example.tpj2eannonces.utils.PaginationUtils;
import org.example.tpj2eannonces.utils.RequestParamUtils;
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
            int page = RequestParamUtils.parseNonNegativeInt(request.getParameter("page"), 0);
            String authorParam = request.getParameter("author");
            String categoryParam = request.getParameter("category");
            String statusParam = request.getParameter("status");
            String searchQuery = RequestParamUtils.normalizeBlankToNull(request.getParameter("q"));

            AnnonceListDTO listData = loadAnnonceListData(
                    request, page, searchQuery, authorParam, categoryParam, statusParam);
            String baseUrl = PaginationUtils.buildBaseUrl(
                    request.getContextPath(), "/AnnonceList",
                    new PaginationUtils.QueryParam("q", searchQuery),
                    new PaginationUtils.QueryParam("author", authorParam),
                    new PaginationUtils.QueryParam("category", categoryParam),
                    new PaginationUtils.QueryParam("status", statusParam));

            request.setAttribute("categories", categoryService.findAll());
            request.setAttribute("statuses", AnnonceStatus.values());
            request.setAttribute("annonceList", listData.annonces());
            request.setAttribute("annonceCount", listData.totalCount());
            request.setAttribute("currentPage", page);
            request.setAttribute("pageSize", PAGE_SIZE);
            request.setAttribute("totalPages", (int) Math.ceil((double) listData.totalCount() / PAGE_SIZE));
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

    private AnnonceListDTO loadAnnonceListData(
            HttpServletRequest request, int page, String searchQuery,
            String authorParam, String categoryParam, String statusParam) {
        return ConditionalResolver.resolve(
                () -> loadDefaultData(page),
                new ConditionalResolver.Rule<>(() -> searchQuery != null, () -> loadSearchData(request, page, searchQuery, categoryParam, statusParam)),
                new ConditionalResolver.Rule<>(
                        () -> RequestParamUtils.hasValue(authorParam),
                        () -> loadAuthorData(request, page, authorParam)),
                new ConditionalResolver.Rule<>(
                        () -> RequestParamUtils.hasValue(categoryParam) || RequestParamUtils.hasValue(statusParam),
                        () -> loadCategoryStatusData(request, page, categoryParam, statusParam)));
    }

    private AnnonceListDTO loadSearchData(
            HttpServletRequest request, int page, String searchQuery, String categoryParam, String statusParam) {
        Long categoryId = parseOptionalCategoryId(categoryParam);
        AnnonceStatus status = parseOptionalStatus(statusParam);

        request.setAttribute("searchQuery", searchQuery);
        if (categoryId != null) {
            request.setAttribute("filterByCategory", true);
            request.setAttribute("selectedCategory", categoryParam);
        }
        if (status != null) {
            request.setAttribute("filterByStatus", true);
            request.setAttribute("selectedStatus", statusParam);
        }

        return new AnnonceListDTO(
                annonceService.searchByFilters(searchQuery, categoryId, status, page, PAGE_SIZE),
                annonceService.countBySearchAndFilters(searchQuery, categoryId, status));
    }

    private AnnonceListDTO loadAuthorData(HttpServletRequest request, int page, String authorParam) {
        UUID authorId = UUID.fromString(authorParam);
        request.setAttribute("filterByAuthor", true);
        return new AnnonceListDTO(
                annonceService.findByAuthor(authorId, page, PAGE_SIZE),
                annonceService.countByAuthor(authorId));
    }

    private AnnonceListDTO loadCategoryStatusData(
            HttpServletRequest request, int page, String categoryParam, String statusParam) {
        Long categoryId = parseOptionalCategoryId(categoryParam);
        AnnonceStatus status = parseOptionalStatus(statusParam);

        if (categoryId != null) {
            request.setAttribute("filterByCategory", true);
            request.setAttribute("selectedCategory", categoryParam);
        }
        if (status != null) {
            request.setAttribute("filterByStatus", true);
            request.setAttribute("selectedStatus", statusParam);
        }

        return new AnnonceListDTO(
                annonceService.findByFilters(categoryId, status, page, PAGE_SIZE),
                annonceService.countByFilters(categoryId, status));
    }

    private AnnonceListDTO loadDefaultData(int page) {
        return new AnnonceListDTO(annonceService.findAll(page, PAGE_SIZE), annonceService.count());
    }

    private Long parseOptionalCategoryId(String categoryParam) {
        return RequestParamUtils.hasValue(categoryParam) ? Long.parseLong(categoryParam) : null;
    }

    private AnnonceStatus parseOptionalStatus(String statusParam) {
        return RequestParamUtils.hasValue(statusParam) ? AnnonceStatus.valueOf(statusParam) : null;
    }

}
