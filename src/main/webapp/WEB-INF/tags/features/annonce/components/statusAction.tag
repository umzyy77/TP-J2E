<%@ tag pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="annonce" required="true" type="org.example.tpj2eannonces.model.Annonce" %>

<c:if test="${annonce.status.hasAction()}">
    <c:set var="action" value="${annonce.status.actionName}" />
    <c:set var="label" value="${annonce.status.actionLabel}" />

    <c:choose>
        <c:when test="${action == 'publish'}">
            <c:set var="btnClass" value="bg-green-600 hover:bg-green-700" />
            <c:set var="iconName" value="check" />
        </c:when>
        <c:when test="${action == 'archive'}">
            <c:set var="btnClass" value="bg-yellow-600 hover:bg-yellow-700" />
            <c:set var="iconName" value="archive" />
        </c:when>
    </c:choose>

    <form action="${pageContext.request.contextPath}/AnnoncePatch" method="POST" class="inline">
        <input type="hidden" name="id" value="${annonce.id}">
        <input type="hidden" name="action" value="${action}">
        <button type="submit"
            class="inline-flex items-center rounded-md ${btnClass} px-4 py-2 text-sm font-medium text-white shadow-sm">
            <i data-lucide="${iconName}" class="mr-2 h-4 w-4"></i>
            <c:out value="${label}" />
        </button>
    </form>
</c:if>
