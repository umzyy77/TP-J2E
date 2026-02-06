<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>

                <layout:page title="${annonce.title} - MasterAnnonce">
                    <div class="space-y-6">
                        <%-- Breadcrumb --%>
                            <nav class="flex items-center gap-2 text-sm text-zinc-600">
                                <a href="${pageContext.request.contextPath}/AnnonceList"
                                    class="hover:text-zinc-900">Annonces</a>
                                <span>/</span>
                                <span class="text-zinc-900">${annonce.title}</span>
                            </nav>

                            <%-- Success messages --%>
                                <c:if test="${not empty param.success}">
                                    <div
                                        class="rounded-md bg-green-50 border border-green-200 p-4 text-sm text-green-800">
                                        <c:choose>
                                            <c:when test="${param.success == 'publish'}">✓ Annonce publiée avec succès !
                                            </c:when>
                                            <c:when test="${param.success == 'archive'}">✓ Annonce archivée avec succès
                                                !</c:when>
                                            <c:otherwise>✓ Opération réussie !</c:otherwise>
                                        </c:choose>
                                    </div>
                                </c:if>

                                <%-- Main content card --%>
                                    <div class="rounded-lg border border-zinc-200 bg-white shadow-sm">
                                        <%-- Header --%>
                                            <div class="border-b border-zinc-200 p-6">
                                                <div class="flex flex-wrap items-start justify-between gap-4">
                                                    <div>
                                                        <h1 class="text-2xl font-bold tracking-tight">${annonce.title}
                                                        </h1>
                                                        <p class="mt-1 text-sm text-zinc-600">
                                                            Publié le
                                                            <fmt:formatDate value="${annonce.dateAsDate}"
                                                                pattern="dd/MM/yyyy à HH:mm" />
                                                        </p>
                                                    </div>
                                                    <span class="inline-flex items-center rounded-full px-3 py-1 text-xs font-medium
                        <c:choose>
                            <c:when test=" ${annonce.status=='PUBLISHED' }">bg-green-100 text-green-800</c:when>
                                                        <c:when test="${annonce.status == 'DRAFT'}">bg-yellow-100
                                                            text-yellow-800</c:when>
                                                        <c:when test="${annonce.status == 'ARCHIVED'}">bg-zinc-100
                                                            text-zinc-800</c:when>
                                                        </c:choose>">
                                                        ${annonce.status}
                                                    </span>
                                                </div>
                                            </div>

                                            <%-- Body --%>
                                                <div class="p-6 space-y-6">
                                                    <%-- Meta info --%>
                                                        <div class="grid gap-4 sm:grid-cols-2">
                                                            <c:if test="${not empty annonce.author}">
                                                                <div class="flex items-center gap-3">
                                                                    <div
                                                                        class="flex h-10 w-10 items-center justify-center rounded-full bg-zinc-100 text-sm font-medium text-zinc-600">
                                                                        ${annonce.author.username.substring(0,1).toUpperCase()}
                                                                    </div>
                                                                    <div>
                                                                        <p class="text-sm font-medium text-zinc-900">
                                                                            ${annonce.author.username}</p>
                                                                        <p class="text-xs text-zinc-500">Auteur</p>
                                                                    </div>
                                                                </div>
                                                            </c:if>
                                                            <c:if test="${not empty annonce.category}">
                                                                <div class="flex items-center gap-3">
                                                                    <div
                                                                        class="flex h-10 w-10 items-center justify-center rounded-full bg-zinc-100 text-sm font-medium text-zinc-600">
                                                                        📁
                                                                    </div>
                                                                    <div>
                                                                        <p class="text-sm font-medium text-zinc-900">
                                                                            ${annonce.category.label}</p>
                                                                        <p class="text-xs text-zinc-500">Catégorie</p>
                                                                    </div>
                                                                </div>
                                                            </c:if>
                                                        </div>

                                                        <%-- Description --%>
                                                            <div>
                                                                <h2 class="text-sm font-medium text-zinc-900">
                                                                    Description</h2>
                                                                <p class="mt-2 text-zinc-600">${annonce.description}</p>
                                                            </div>

                                                            <%-- Contact info --%>
                                                                <div class="rounded-lg bg-zinc-50 p-4">
                                                                    <h2 class="text-sm font-medium text-zinc-900 mb-3">
                                                                        Contact</h2>
                                                                    <div class="space-y-2 text-sm">
                                                                        <p class="flex items-center gap-2">
                                                                            <span class="text-zinc-500">📍</span>
                                                                            <span
                                                                                class="text-zinc-700">${annonce.adress}</span>
                                                                        </p>
                                                                        <p class="flex items-center gap-2">
                                                                            <span class="text-zinc-500">✉️</span>
                                                                            <a href="mailto:${annonce.mail}"
                                                                                class="text-zinc-900 hover:underline">${annonce.mail}</a>
                                                                        </p>
                                                                    </div>
                                                                </div>
                                                </div>

                                                <%-- Actions footer - ONLY show edit/delete if user is the author --%>
                                                    <div class="border-t border-zinc-200 p-6">
                                                        <div class="flex flex-wrap items-center gap-3">
                                                            <%-- Back button always visible --%>
                                                                <a href="${pageContext.request.contextPath}/AnnonceList"
                                                                    class="inline-flex items-center rounded-md border border-zinc-300 bg-white px-4 py-2 text-sm font-medium text-zinc-700 shadow-sm hover:bg-zinc-50">
                                                                    ← Retour à la liste
                                                                </a>

                                                                <%-- Actions only for logged user who is the author --%>
                                                                    <c:if test="${not empty sessionScope.loggedUser}">
                                                                        <c:set var="isOwner"
                                                                            value="${not empty annonce.author && annonce.author.id == sessionScope.loggedUser.id}" />

                                                                        <c:if test="${isOwner}">
                                                                            <%-- Status actions --%>
                                                                                <c:choose>
                                                                                    <c:when
                                                                                        test="${annonce.status == 'DRAFT'}">
                                                                                        <a href="${pageContext.request.contextPath}/AnnoncePublish?id=${annonce.id}"
                                                                                            class="inline-flex items-center rounded-md bg-green-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-green-700">
                                                                                            ✓ Publier
                                                                                        </a>
                                                                                    </c:when>
                                                                                    <c:when
                                                                                        test="${annonce.status == 'PUBLISHED'}">
                                                                                        <a href="${pageContext.request.contextPath}/AnnonceArchive?id=${annonce.id}"
                                                                                            class="inline-flex items-center rounded-md bg-yellow-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-yellow-700">
                                                                                            📦 Archiver
                                                                                        </a>
                                                                                    </c:when>
                                                                                </c:choose>

                                                                                <%-- Edit button --%>
                                                                                    <a href="${pageContext.request.contextPath}/AnnonceUpdate?id=${annonce.id}"
                                                                                        class="inline-flex items-center rounded-md border border-zinc-300 bg-white px-4 py-2 text-sm font-medium text-zinc-700 shadow-sm hover:bg-zinc-50">
                                                                                        ✏️ Modifier
                                                                                    </a>

                                                                                    <%-- Delete button --%>
                                                                                        <a href="${pageContext.request.contextPath}/AnnonceDelete?id=${annonce.id}"
                                                                                            onclick="return confirm('Êtes-vous sûr de vouloir supprimer cette annonce ?')"
                                                                                            class="inline-flex items-center rounded-md bg-red-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-red-700">
                                                                                            🗑️ Supprimer
                                                                                        </a>
                                                                        </c:if>
                                                                    </c:if>
                                                        </div>
                                                    </div>
                                    </div>
                    </div>
                </layout:page>