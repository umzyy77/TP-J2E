<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="ui" tagdir="/WEB-INF/tags/ui" %>
            <%@ taglib prefix="annonce" tagdir="/WEB-INF/tags/annonce" %>
                <%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>

                    <layout:page title="Modifier une annonce">

                        <div class="mx-auto max-w-2xl space-y-6">
                            <div class="flex items-center justify-between">
                                <div>
                                    <h1 class="text-2xl font-bold tracking-tight">Modifier l'annonce</h1>
                                    <p class="mt-1 text-sm text-zinc-500">Mettez à jour les informations.</p>
                                </div>
                                <a class="inline-flex h-9 items-center justify-center rounded-md border border-zinc-200 bg-white px-4 text-sm font-medium text-zinc-700 shadow-sm transition-colors hover:bg-zinc-50"
                                    href="${pageContext.request.contextPath}/AnnonceList">
                                    <svg class="mr-2 h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none"
                                        viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                                        <path stroke-linecap="round" stroke-linejoin="round"
                                            d="M10 19l-7-7m0 0l7-7m-7 7h18" />
                                    </svg>
                                    Retour
                                </a>
                            </div>

                            <c:if test="${not empty requestScope.message}">
                                <ui:alert variant="error" message="${requestScope.message}" />
                            </c:if>

                            <div class="rounded-lg border border-zinc-200 bg-white p-6 shadow-sm">
                                <form class="space-y-6" method="post"
                                    action="${pageContext.request.contextPath}/AnnonceUpdate">
                                    <input type="hidden" name="id" value="<c:out value='${requestScope.annonce.id}'/>">
                                    <annonce:formFields annonce="${requestScope.annonce}" />
                                    <div class="flex items-center gap-3 border-t border-zinc-200 pt-6">
                                        <button
                                            class="inline-flex h-10 items-center justify-center rounded-md bg-zinc-900 px-6 text-sm font-medium text-white shadow transition-colors hover:bg-zinc-800"
                                            type="submit">Mettre à jour</button>
                                        <a class="inline-flex h-10 items-center justify-center rounded-md border border-zinc-200 bg-white px-6 text-sm font-medium text-zinc-700 shadow-sm transition-colors hover:bg-zinc-50"
                                            href="${pageContext.request.contextPath}/AnnonceList">Annuler</a>
                                    </div>
                                </form>
                            </div>
                        </div>

                    </layout:page>