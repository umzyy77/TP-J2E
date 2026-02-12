<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/shared/ui" %>
<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="annonce" tagdir="/WEB-INF/tags/features/annonce/components" %>

<layout:page title="Liste des annonces">
    <div class="space-y-6">
        <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
            <div>
                <h1 class="text-2xl font-bold tracking-tight">
                    <c:choose>
                        <c:when test="${requestScope.filterByAuthor}">Mes annonces</c:when>
                        <c:otherwise>Annonces</c:otherwise>
                    </c:choose>
                </h1>
                <p class="mt-1 text-sm text-zinc-500">
                    <c:out value="${requestScope.annonceCount}" /> annonce(s)
                    <c:if test="${requestScope.filterByAuthor || requestScope.filterByCategory || requestScope.filterByStatus}">
                        - <a href="${pageContext.request.contextPath}/AnnonceList"
                            class="text-zinc-700 hover:underline">Voir toutes</a>
                    </c:if>
                </p>
            </div>
            <c:if test="${not empty sessionScope.loggedUser}">
                <a class="inline-flex h-10 items-center justify-center rounded-md bg-zinc-900 px-4 text-sm font-medium text-white shadow transition-colors hover:bg-zinc-800"
                    href="${pageContext.request.contextPath}/AnnonceAdd">
                    <i data-lucide="plus" class="mr-2 h-4 w-4"></i>
                    Nouvelle annonce
                </a>
            </c:if>
        </div>

        <ui:searchBar action="${pageContext.request.contextPath}/AnnonceList"
            value="${requestScope.searchQuery}"
            placeholder="Rechercher par mot-clé..."
            clearUrl="${pageContext.request.contextPath}/AnnonceList" />

        <div class="flex flex-wrap items-center gap-4">
            <annonce:categoryFilter categories="${requestScope.categories}" selectedCategory="${requestScope.selectedCategory}" />
            <annonce:statusFilter statuses="${requestScope.statuses}" selectedStatus="${requestScope.selectedStatus}" />
        </div>

        <c:set var="successMessages" value="${{
            'create': 'Annonce créée avec succès.',
            'update': 'Annonce mise à jour.',
            'delete': 'Annonce supprimée.'
        }}" />
        <c:if test="${not empty successMessages[param.success]}">
            <ui:alert variant="success" message="${successMessages[param.success]}" />
        </c:if>

        <c:if test="${empty requestScope.annonceList}">
            <ui:emptyState title="Aucune annonce"
                message="Commencez par créer une nouvelle annonce."
                actionHref="${not empty sessionScope.loggedUser ? pageContext.request.contextPath.concat('/AnnonceAdd') : ''}"
                actionLabel="Créer une annonce" />
        </c:if>

        <c:if test="${not empty requestScope.annonceList}">
            <div class="grid gap-4">
                <c:forEach items="${requestScope.annonceList}" var="item">
                    <annonce:listItem annonce="${item}" />
                </c:forEach>
            </div>
            <ui:pagination currentPage="${requestScope.currentPage}" totalPages="${requestScope.totalPages}"
                baseUrl="${requestScope.paginationBaseUrl}" />
        </c:if>
    </div>
</layout:page>
