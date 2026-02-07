<%@ tag pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="href" required="true" %>
<%@ attribute name="label" required="true" %>
<%@ attribute name="variant" required="false" %>
<%@ attribute name="iconPath" required="false" %>
<%@ attribute name="onclick" required="false" %>

<c:set var="style" value="${empty variant ? 'default' : variant}" />

<c:choose>
    <c:when test="${style == 'danger'}">
        <c:set var="classes" value="inline-flex items-center rounded-md bg-red-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-red-700" />
    </c:when>
    <c:when test="${style == 'success'}">
        <c:set var="classes" value="inline-flex items-center rounded-md bg-green-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-green-700" />
    </c:when>
    <c:when test="${style == 'warning'}">
        <c:set var="classes" value="inline-flex items-center rounded-md bg-yellow-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-yellow-700" />
    </c:when>
    <c:otherwise>
        <c:set var="classes" value="inline-flex items-center rounded-md border border-zinc-300 bg-white px-4 py-2 text-sm font-medium text-zinc-700 shadow-sm hover:bg-zinc-50" />
    </c:otherwise>
</c:choose>

<a href="${href}" class="${classes}"
    <c:if test="${not empty onclick}">onclick="${onclick}"</c:if>>
    <c:if test="${not empty iconPath}">
        <svg class="mr-2 h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="${iconPath}" />
        </svg>
    </c:if>
    <c:out value="${label}" />
</a>
