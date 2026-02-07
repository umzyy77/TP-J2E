<%@ tag pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="message" required="true" %>
<%@ attribute name="variant" required="false" %>

<c:if test="${not empty message}">
    <c:set var="tone" value="${empty variant ? 'info' : variant}" />

    <c:choose>
        <c:when test="${tone == 'success'}">
            <c:set var="colorClass" value="border-green-200 bg-green-50 text-green-800" />
            <c:set var="iconPath" value="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
        </c:when>
        <c:when test="${tone == 'error'}">
            <c:set var="colorClass" value="border-red-200 bg-red-50 text-red-800" />
            <c:set var="iconPath" value="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        </c:when>
        <c:otherwise>
            <c:set var="colorClass" value="border-zinc-200 bg-zinc-50 text-zinc-700" />
            <c:set var="iconPath" value="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        </c:otherwise>
    </c:choose>

    <div class="flex items-center gap-3 rounded-lg border ${colorClass} px-4 py-3 text-sm">
        <svg class="h-4 w-4 shrink-0" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="${iconPath}" />
        </svg>
        <c:out value="${message}" />
    </div>
</c:if>
