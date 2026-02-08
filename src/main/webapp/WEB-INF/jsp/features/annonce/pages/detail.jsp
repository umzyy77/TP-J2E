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
                                <i data-lucide="folder" class="h-5 w-5 text-zinc-600"></i>
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
                            <i data-lucide="map-pin" class="h-4 w-4 text-zinc-500"></i>
                            <span class="text-zinc-700">
                                <c:out value="${annonce.adress}" />
                            </span>
                        </p>
                        <p class="flex items-center gap-2">
                            <i data-lucide="mail" class="h-4 w-4 text-zinc-500"></i>
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
                                icon="square-pen" />

                            <annonce:deleteButton annonceId="${annonce.id}" size="lg" />
                        </c:if>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</layout:page>
