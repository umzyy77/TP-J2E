<%@ tag pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/shared/ui" %>
<%@ attribute name="annonce" required="true" type="org.example.tpj2eannonces.model.Annonce" %>

<div class="rounded-lg border border-zinc-200 bg-white p-4 shadow-sm hover:border-zinc-300 transition-colors">
    <div class="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
        <div class="min-w-0 flex-1">
            <div class="flex items-center gap-2">
                <a href="${pageContext.request.contextPath}/AnnonceDetail?id=${annonce.id}"
                    class="font-semibold text-zinc-900 hover:underline">
                    <c:out value="${annonce.title}" />
                </a>
                <ui:statusBadge status="${annonce.status}" />
            </div>
            <p class="mt-1 text-sm text-zinc-600 line-clamp-2">
                <c:out value="${annonce.description}" />
            </p>
            <div class="mt-3 flex flex-wrap gap-x-6 gap-y-2 text-sm text-zinc-500">
                <span class="inline-flex items-center gap-1">
                    <i data-lucide="map-pin" class="h-4 w-4"></i>
                    <c:out value="${annonce.adress}" />
                </span>
                <span class="inline-flex items-center gap-1">
                    <i data-lucide="mail" class="h-4 w-4"></i>
                    <c:out value="${annonce.mail}" />
                </span>
                <c:if test="${not empty annonce.author}">
                    <span class="inline-flex items-center gap-1">
                        <i data-lucide="user" class="h-4 w-4"></i>
                        <c:out value="${annonce.author.username}" />
                    </span>
                </c:if>
                <c:if test="${not empty annonce.category}">
                    <span class="inline-flex items-center gap-1">
                        <i data-lucide="folder" class="h-4 w-4"></i>
                        <c:out value="${annonce.category.label}" />
                    </span>
                </c:if>
            </div>
        </div>
        <div class="flex flex-wrap items-center gap-2 sm:flex-nowrap">
            <a class="inline-flex h-9 items-center justify-center rounded-md border border-zinc-200 bg-white px-4 text-sm font-medium text-zinc-700 shadow-sm transition-colors hover:bg-zinc-50"
                href="${pageContext.request.contextPath}/AnnonceDetail?id=${annonce.id}">
                Voir
            </a>
            <c:if test="${not empty sessionScope.loggedUser && not empty annonce.author}">
                <c:if test="${annonce.author.id == sessionScope.loggedUser.id}">
                    <a class="inline-flex h-9 items-center justify-center rounded-md border border-zinc-200 bg-white px-4 text-sm font-medium text-zinc-700 shadow-sm transition-colors hover:bg-zinc-50"
                        href="${pageContext.request.contextPath}/AnnoncePatch?id=${annonce.id}">
                        Modifier
                    </a>
                    <button type="button" onclick="openDialog({
                        actionUrl: '${pageContext.request.contextPath}/AnnonceDelete',
                        entityId: '${annonce.id}',
                        title: 'Supprimer cette annonce',
                        message: 'Êtes-vous sûr de vouloir supprimer cette annonce ? Cette action est irréversible.',
                        confirmLabel: 'Supprimer',
                        variant: 'danger'
                    })" class="inline-flex h-9 items-center justify-center rounded-md border border-red-200 bg-white px-4 text-sm font-medium text-red-600 shadow-sm transition-colors hover:bg-red-50">
                        Supprimer
                    </button>
                </c:if>
            </c:if>
        </div>
    </div>
</div>
