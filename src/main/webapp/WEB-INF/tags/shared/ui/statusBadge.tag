<%@ tag pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="status" required="true" %>
<%@ attribute name="size" required="false" %>

<c:set var="sizeClass" value="${size == 'lg' ? 'px-3 py-1' : 'px-2 py-0.5'}" />

<c:choose>
    <c:when test="${status == 'PUBLISHED'}">
        <c:set var="colorClass" value="bg-green-100 text-green-700" />
    </c:when>
    <c:when test="${status == 'DRAFT'}">
        <c:set var="colorClass" value="bg-yellow-100 text-yellow-700" />
    </c:when>
    <c:when test="${status == 'ARCHIVED'}">
        <c:set var="colorClass" value="bg-zinc-100 text-zinc-600" />
    </c:when>
    <c:otherwise>
        <c:set var="colorClass" value="bg-zinc-100 text-zinc-600" />
    </c:otherwise>
</c:choose>

<span class="inline-flex items-center rounded-full ${sizeClass} text-xs font-medium ${colorClass}">
    ${status}
</span>
