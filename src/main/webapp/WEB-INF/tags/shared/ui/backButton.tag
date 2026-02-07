<%@ tag pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="href" required="false" %>
<%@ attribute name="label" required="false" %>

<c:set var="backUrl" value="${not empty href ? href : pageContext.request.contextPath.concat('/AnnonceList')}" />
<c:set var="backLabel" value="${not empty label ? label : 'Retour'}" />

<a class="inline-flex h-9 items-center justify-center rounded-md border border-zinc-200 bg-white px-4 text-sm font-medium text-zinc-700 shadow-sm transition-colors hover:bg-zinc-50"
   href="${backUrl}">
    <svg class="mr-2 h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
        <path stroke-linecap="round" stroke-linejoin="round" d="M10 19l-7-7m0 0l7-7m-7 7h18"/>
    </svg>
    <c:out value="${backLabel}"/>
</a>
