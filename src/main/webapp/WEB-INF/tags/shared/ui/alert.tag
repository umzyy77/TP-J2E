<%@ tag pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="message" required="true" %>
<%@ attribute name="variant" required="false" %>

<c:if test="${not empty message}">
    <c:set var="tone" value="${empty variant ? 'info' : variant}" />

    <c:choose>
        <c:when test="${tone == 'success'}">
            <c:set var="colorClass" value="border-green-200 bg-green-50 text-green-800" />
            <c:set var="iconName" value="circle-check" />
        </c:when>
        <c:when test="${tone == 'error'}">
            <c:set var="colorClass" value="border-red-200 bg-red-50 text-red-800" />
            <c:set var="iconName" value="circle-alert" />
        </c:when>
        <c:otherwise>
            <c:set var="colorClass" value="border-zinc-200 bg-zinc-50 text-zinc-700" />
            <c:set var="iconName" value="info" />
        </c:otherwise>
    </c:choose>

    <div class="flex items-center gap-3 rounded-lg border ${colorClass} px-4 py-3 text-sm">
        <i data-lucide="${iconName}" class="h-4 w-4 shrink-0"></i>
        <c:out value="${message}" />
    </div>
</c:if>
