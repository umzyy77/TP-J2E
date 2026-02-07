<%@ tag pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="annonce" required="true" type="org.example.tpj2eannonces.model.Annonce" %>

<c:if test="${annonce.status.hasAction()}">
    <c:set var="action" value="${annonce.status.actionName}" />
    <c:set var="label" value="${annonce.status.actionLabel}" />

    <c:choose>
        <c:when test="${action == 'publish'}">
            <c:set var="btnClass" value="bg-green-600 hover:bg-green-700" />
            <c:set var="iconPath" value="M5 13l4 4L19 7" />
        </c:when>
        <c:when test="${action == 'archive'}">
            <c:set var="btnClass" value="bg-yellow-600 hover:bg-yellow-700" />
            <c:set var="iconPath" value="M20.25 7.5l-.625 10.632a2.25 2.25 0 01-2.247 2.118H6.622a2.25 2.25 0 01-2.247-2.118L3.75 7.5m8.25 3v6.75m0 0l-3-3m3 3l3-3M3.375 7.5h17.25c.621 0 1.125-.504 1.125-1.125v-1.5c0-.621-.504-1.125-1.125-1.125H3.375c-.621 0-1.125.504-1.125 1.125v1.5c0 .621.504 1.125 1.125 1.125z" />
        </c:when>
    </c:choose>

    <form action="${pageContext.request.contextPath}/AnnoncePatch" method="POST" class="inline">
        <input type="hidden" name="id" value="${annonce.id}">
        <input type="hidden" name="action" value="${action}">
        <button type="submit"
            class="inline-flex items-center rounded-md ${btnClass} px-4 py-2 text-sm font-medium text-white shadow-sm">
            <svg class="mr-2 h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
                <path stroke-linecap="round" stroke-linejoin="round" d="${iconPath}" />
            </svg>
            <c:out value="${label}" />
        </button>
    </form>
</c:if>
