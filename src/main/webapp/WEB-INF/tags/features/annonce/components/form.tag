<%@ tag pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/shared/ui" %>
<%@ taglib prefix="annonce" tagdir="/WEB-INF/tags/features/annonce/components" %>
<%@ attribute name="title" required="true" %>
<%@ attribute name="subtitle" required="true" %>
<%@ attribute name="actionUrl" required="true" %>
<%@ attribute name="submitLabel" required="true" %>
<%@ attribute name="annonceObj" required="true" type="org.example.tpj2eannonces.model.Annonce" %>
<%@ attribute name="annonceId" required="false" %>
<%@ attribute name="errorMessage" required="false" %>

<div class="mx-auto max-w-2xl space-y-6">
    <div class="flex items-center justify-between">
        <div>
            <h1 class="text-2xl font-bold tracking-tight"><c:out value="${title}"/></h1>
            <p class="mt-1 text-sm text-zinc-500"><c:out value="${subtitle}"/></p>
        </div>
        <ui:backButton />
    </div>

    <c:if test="${not empty errorMessage}">
        <ui:alert variant="error" message="${errorMessage}" />
    </c:if>

    <div class="rounded-lg border border-zinc-200 bg-white p-6 shadow-sm">
        <form class="space-y-6" method="post" action="${actionUrl}">
            <c:if test="${not empty annonceId}">
                <input type="hidden" name="id" value="${annonceId}">
                <input type="hidden" name="action" value="update">
            </c:if>
            <annonce:formFields annonce="${annonceObj}" />
            <div class="flex items-center gap-3 border-t border-zinc-200 pt-6">
                <button class="inline-flex h-10 items-center justify-center rounded-md bg-zinc-900 px-6 text-sm font-medium text-white shadow transition-colors hover:bg-zinc-800"
                        type="submit"><c:out value="${submitLabel}"/></button>
                <a class="inline-flex h-10 items-center justify-center rounded-md border border-zinc-200 bg-white px-6 text-sm font-medium text-zinc-700 shadow-sm transition-colors hover:bg-zinc-50"
                   href="${pageContext.request.contextPath}/AnnonceList">Annuler</a>
            </div>
        </form>
    </div>
</div>
