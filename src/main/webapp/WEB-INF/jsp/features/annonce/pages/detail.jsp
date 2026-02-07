<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/shared/ui" %>
<%@ taglib prefix="annonce" tagdir="/WEB-INF/tags/features/annonce/components" %>

<layout:page title="${annonce.title} - MasterAnnonce">
    <div class="space-y-6">
        <ui:breadcrumb parentLabel="Annonces"
            parentHref="${pageContext.request.contextPath}/AnnonceList"
            currentLabel="${annonce.title}">
            <ui:backButton />
        </ui:breadcrumb>

        <c:set var="successMessages" value="${{
            'publish': 'Annonce publiée avec succès !',
            'archive': 'Annonce archivée avec succès !'
        }}" />
        <c:if test="${not empty successMessages[param.success]}">
            <ui:alert variant="success" message="${successMessages[param.success]}" />
        </c:if>

        <div class="rounded-lg border border-zinc-200 bg-white shadow-sm">
            <%-- Header --%>
            <div class="border-b border-zinc-200 p-6">
                <div class="flex flex-wrap items-start justify-between gap-4">
                    <div>
                        <h1 class="text-2xl font-bold tracking-tight">
                            <c:out value="${annonce.title}" />
                        </h1>
                        <p class="mt-1 text-sm text-zinc-600">
                            Publié le
                            <fmt:formatDate value="${annonce.dateAsDate}" pattern="dd/MM/yyyy à HH:mm" />
                        </p>
                    </div>
                    <ui:statusBadge status="${annonce.status}" size="lg" />
                </div>
            </div>

            <div class="p-6 space-y-6">
                <div class="grid gap-4 sm:grid-cols-2">
                    <c:if test="${not empty annonce.author}">
                        <div class="flex items-center gap-3">
                            <div class="flex h-10 w-10 items-center justify-center rounded-full bg-zinc-100 text-sm font-medium text-zinc-600">
                                <c:out value="${annonce.author.username.substring(0,1).toUpperCase()}" />
                            </div>
                            <div>
                                <p class="text-sm font-medium text-zinc-900">
                                    <c:out value="${annonce.author.username}" />
                                </p>
                                <p class="text-xs text-zinc-500">Auteur</p>
                            </div>
                        </div>
                    </c:if>
                    <c:if test="${not empty annonce.category}">
                        <div class="flex items-center gap-3">
                            <div class="flex h-10 w-10 items-center justify-center rounded-full bg-zinc-100">
                                <svg class="h-5 w-5 text-zinc-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
                                    <path stroke-linecap="round" stroke-linejoin="round" d="M2.25 12.75V12A2.25 2.25 0 014.5 9.75h15A2.25 2.25 0 0121.75 12v.75m-8.69-6.44l-2.12-2.12a1.5 1.5 0 00-1.061-.44H4.5A2.25 2.25 0 002.25 6v12a2.25 2.25 0 002.25 2.25h15A2.25 2.25 0 0021.75 18V9a2.25 2.25 0 00-2.25-2.25h-5.379a1.5 1.5 0 01-1.06-.44z" />
                                </svg>
                            </div>
                            <div>
                                <p class="text-sm font-medium text-zinc-900">
                                    <c:out value="${annonce.category.label}" />
                                </p>
                                <p class="text-xs text-zinc-500">Catégorie</p>
                            </div>
                        </div>
                    </c:if>
                </div>

                <div>
                    <h2 class="text-sm font-medium text-zinc-900">Description</h2>
                    <p class="mt-2 text-zinc-600">
                        <c:out value="${annonce.description}" />
                    </p>
                </div>

                <div class="rounded-lg bg-zinc-50 p-4">
                    <h2 class="text-sm font-medium text-zinc-900 mb-3">Contact</h2>
                    <div class="space-y-2 text-sm">
                        <p class="flex items-center gap-2">
                            <svg class="h-4 w-4 text-zinc-500" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
                                <path stroke-linecap="round" stroke-linejoin="round" d="M15 10.5a3 3 0 11-6 0 3 3 0 016 0z" />
                                <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 10.5c0 7.142-7.5 11.25-7.5 11.25S4.5 17.642 4.5 10.5a7.5 7.5 0 1115 0z" />
                            </svg>
                            <span class="text-zinc-700">
                                <c:out value="${annonce.adress}" />
                            </span>
                        </p>
                        <p class="flex items-center gap-2">
                            <svg class="h-4 w-4 text-zinc-500" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
                                <path stroke-linecap="round" stroke-linejoin="round" d="M21.75 6.75v10.5a2.25 2.25 0 01-2.25 2.25h-15a2.25 2.25 0 01-2.25-2.25V6.75m19.5 0A2.25 2.25 0 0019.5 4.5h-15a2.25 2.25 0 00-2.25 2.25m19.5 0v.243a2.25 2.25 0 01-1.07 1.916l-7.5 4.615a2.25 2.25 0 01-2.36 0L3.32 8.91a2.25 2.25 0 01-1.07-1.916V6.75" />
                            </svg>
                            <a href="mailto:${annonce.mail}" class="text-zinc-900 hover:underline">
                                <c:out value="${annonce.mail}" />
                            </a>
                        </p>
                    </div>
                </div>
            </div>

            <div class="border-t border-zinc-200 p-6">
                <div class="flex flex-wrap items-center gap-3">
                    <c:if test="${not empty sessionScope.loggedUser && not empty annonce.author}">
                        <c:if test="${annonce.author.id == sessionScope.loggedUser.id}">
                            <annonce:statusAction annonce="${annonce}" />

                            <ui:actionButton
                                href="${pageContext.request.contextPath}/AnnoncePatch?id=${annonce.id}"
                                label="Modifier"
                                iconPath="M16.862 4.487l1.687-1.688a1.875 1.875 0 112.652 2.652L10.582 16.07a4.5 4.5 0 01-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 011.13-1.897l8.932-8.931zm0 0L19.5 7.125M18 14v4.75A2.25 2.25 0 0115.75 21H5.25A2.25 2.25 0 013 18.75V8.25A2.25 2.25 0 015.25 6H10" />

                            <ui:actionButton
                                href="${pageContext.request.contextPath}/AnnonceDelete?id=${annonce.id}"
                                label="Supprimer"
                                variant="danger"
                                onclick="return confirm('Êtes-vous sûr de vouloir supprimer cette annonce ?')"
                                iconPath="M14.74 9l-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 01-2.244 2.077H8.084a2.25 2.25 0 01-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 00-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 013.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 00-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 00-7.5 0" />
                        </c:if>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</layout:page>
