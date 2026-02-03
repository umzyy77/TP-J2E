<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>

            <layout:page title="Erreur serveur">

                <div class="flex min-h-[calc(100vh-16rem)] flex-col items-center justify-center text-center">
                    <div class="flex h-16 w-16 items-center justify-center rounded-full bg-red-50">
                        <span class="text-3xl font-bold text-red-500">!</span>
                    </div>
                    <h1 class="mt-6 text-2xl font-bold tracking-tight">Erreur serveur</h1>
                    <p class="mt-2 text-sm text-zinc-500">Une erreur interne est survenue. Veuillez réessayer.</p>
                    <c:if test="${not empty requestScope.message}">
                        <div
                            class="mt-4 max-w-md rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                            <c:out value="${requestScope.message}" />
                        </div>
                    </c:if>
                    <a class="mt-6 inline-flex h-10 items-center justify-center rounded-md bg-zinc-900 px-6 text-sm font-medium text-white shadow transition-colors hover:bg-zinc-800"
                        href="${pageContext.request.contextPath}/AnnonceList">Retour à la liste</a>
                </div>

            </layout:page>