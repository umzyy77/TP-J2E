<%@ tag pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="annonceId" required="true" %>
<%@ attribute name="size" required="false" %>

<c:choose>
    <c:when test="${size == 'lg'}">
        <c:set var="btnClass" value="inline-flex items-center rounded-md bg-red-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-red-700" />
        <c:set var="iconClass" value="mr-2 h-4 w-4" />
    </c:when>
    <c:otherwise>
        <c:set var="btnClass" value="inline-flex h-8 items-center justify-center rounded-md border border-red-200 bg-white px-3 text-xs font-medium text-red-600 shadow-sm transition-colors hover:bg-red-50" />
        <c:set var="iconClass" value="mr-1.5 h-3.5 w-3.5" />
    </c:otherwise>
</c:choose>

<form method="post" action="${pageContext.request.contextPath}/AnnonceDelete" class="inline"
      onsubmit="return confirm('Êtes-vous sûr de vouloir supprimer cette annonce ?')">
    <input type="hidden" name="id" value="${annonceId}" />
    <button type="submit" class="${btnClass}">
        <i data-lucide="trash-2" class="${iconClass}"></i>
        Supprimer
    </button>
</form>
