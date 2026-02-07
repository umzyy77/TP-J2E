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
                    <svg class="h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M15 10.5a3 3 0 11-6 0 3 3 0 016 0z" />
                        <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 10.5c0 7.142-7.5 11.25-7.5 11.25S4.5 17.642 4.5 10.5a7.5 7.5 0 1115 0z" />
                    </svg>
                    <c:out value="${annonce.adress}" />
                </span>
                <span class="inline-flex items-center gap-1">
                    <svg class="h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M21.75 6.75v10.5a2.25 2.25 0 01-2.25 2.25h-15a2.25 2.25 0 01-2.25-2.25V6.75m19.5 0A2.25 2.25 0 0019.5 4.5h-15a2.25 2.25 0 00-2.25 2.25m19.5 0v.243a2.25 2.25 0 01-1.07 1.916l-7.5 4.615a2.25 2.25 0 01-2.36 0L3.32 8.91a2.25 2.25 0 01-1.07-1.916V6.75" />
                    </svg>
                    <c:out value="${annonce.mail}" />
                </span>
                <c:if test="${not empty annonce.author}">
                    <span class="inline-flex items-center gap-1">
                        <svg class="h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
                            <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 6a3.75 3.75 0 11-7.5 0 3.75 3.75 0 017.5 0zM4.501 20.118a7.5 7.5 0 0114.998 0A17.933 17.933 0 0112 21.75c-2.676 0-5.216-.584-7.499-1.632z" />
                        </svg>
                        <c:out value="${annonce.author.username}" />
                    </span>
                </c:if>
                <c:if test="${not empty annonce.category}">
                    <span class="inline-flex items-center gap-1">
                        <svg class="h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
                            <path stroke-linecap="round" stroke-linejoin="round" d="M2.25 12.75V12A2.25 2.25 0 014.5 9.75h15A2.25 2.25 0 0121.75 12v.75m-8.69-6.44l-2.12-2.12a1.5 1.5 0 00-1.061-.44H4.5A2.25 2.25 0 002.25 6v12a2.25 2.25 0 002.25 2.25h15A2.25 2.25 0 0021.75 18V9a2.25 2.25 0 00-2.25-2.25h-5.379a1.5 1.5 0 01-1.06-.44z" />
                        </svg>
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
                        url: '${pageContext.request.contextPath}/AnnonceDelete?id=${annonce.id}',
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
