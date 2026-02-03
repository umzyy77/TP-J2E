<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="ui" tagdir="/WEB-INF/tags/ui" %>
            <%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>

                <layout:page title="Liste des annonces">

                    <div class="space-y-6">
                        <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
                            <div>
                                <h1 class="text-2xl font-bold tracking-tight">Annonces</h1>
                                <p class="mt-1 text-sm text-zinc-500">
                                    <c:out value="${requestScope.annonceCount}" /> annonce(s) au total
                                </p>
                            </div>
                            <a class="inline-flex h-10 items-center justify-center rounded-md bg-zinc-900 px-4 text-sm font-medium text-white shadow transition-colors hover:bg-zinc-800"
                                href="${pageContext.request.contextPath}/AnnonceAdd">
                                <svg class="mr-2 h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none"
                                    viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                                    <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
                                </svg>
                                Nouvelle annonce
                            </a>
                        </div>

                        <c:if test="${param.success == 'create'}">
                            <ui:alert variant="success" message="Annonce créée avec succès." />
                        </c:if>
                        <c:if test="${param.success == 'update'}">
                            <ui:alert variant="success" message="Annonce mise à jour." />
                        </c:if>
                        <c:if test="${param.success == 'delete'}">
                            <ui:alert variant="success" message="Annonce supprimée." />
                        </c:if>

                        <c:if test="${empty requestScope.annonceList}">
                            <div
                                class="flex flex-col items-center justify-center rounded-lg border border-dashed border-zinc-300 bg-white py-12">
                                <div class="flex h-12 w-12 items-center justify-center rounded-full bg-zinc-100">
                                    <svg class="h-6 w-6 text-zinc-400" xmlns="http://www.w3.org/2000/svg" fill="none"
                                        viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
                                        <path stroke-linecap="round" stroke-linejoin="round"
                                            d="M19.5 14.25v-2.625a3.375 3.375 0 00-3.375-3.375h-1.5A1.125 1.125 0 0113.5 7.125v-1.5a3.375 3.375 0 00-3.375-3.375H8.25m2.25 0H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 00-9-9z" />
                                    </svg>
                                </div>
                                <h3 class="mt-4 text-sm font-medium text-zinc-900">Aucune annonce</h3>
                                <p class="mt-1 text-sm text-zinc-500">Commencez par créer une nouvelle annonce.</p>
                                <a class="mt-4 inline-flex h-9 items-center justify-center rounded-md bg-zinc-900 px-4 text-sm font-medium text-white shadow transition-colors hover:bg-zinc-800"
                                    href="${pageContext.request.contextPath}/AnnonceAdd">Créer une annonce</a>
                            </div>
                        </c:if>

                        <c:if test="${not empty requestScope.annonceList}">
                            <div class="grid gap-4">
                                <c:forEach items="${requestScope.annonceList}" var="annonce">
                                    <div class="rounded-lg border border-zinc-200 bg-white p-4 shadow-sm">
                                        <div class="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
                                            <div class="min-w-0 flex-1">
                                                <h3 class="font-semibold text-zinc-900">
                                                    <c:out value="${annonce.title}" />
                                                </h3>
                                                <p class="mt-1 text-sm text-zinc-600">
                                                    <c:out value="${annonce.description}" />
                                                </p>
                                                <div class="mt-3 flex flex-wrap gap-x-6 gap-y-2 text-sm text-zinc-500">
                                                    <span><strong>Adresse:</strong>
                                                        <c:out value="${annonce.adress}" />
                                                    </span>
                                                    <span><strong>Email:</strong>
                                                        <c:out value="${annonce.mail}" />
                                                    </span>
                                                </div>
                                            </div>
                                            <div class="flex flex-wrap items-center gap-2 sm:flex-nowrap">
                                                <a class="inline-flex h-9 items-center justify-center rounded-md border border-zinc-200 bg-white px-4 text-sm font-medium text-zinc-700 shadow-sm transition-colors hover:bg-zinc-50"
                                                    href="${pageContext.request.contextPath}/AnnonceUpdate?id=${annonce.id}">
                                                    Modifier
                                                </a>
                                                <button type="button" onclick="openDialog({
                                                        url: '${pageContext.request.contextPath}/AnnonceDelete?id=${annonce.id}',
                                                        title: 'Supprimer cette annonce',
                                                        message: 'Êtes-vous sûr de vouloir supprimer cette annonce ? Cette action est irréversible.',
                                                        confirmLabel: 'Supprimer',
                                                        variant: 'danger'
                                                    })"
                                                    class="inline-flex h-9 items-center justify-center rounded-md border border-red-200 bg-white px-4 text-sm font-medium text-red-600 shadow-sm transition-colors hover:bg-red-50">
                                                    Supprimer
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:if>
                    </div>

                </layout:page>