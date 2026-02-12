<%@ tag pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="href" required="false" %>
<%@ attribute name="label" required="false" %>

<c:set var="backUrl" value="${not empty href ? href : pageContext.request.contextPath.concat('/AnnonceList')}" />
<c:set var="backLabel" value="${not empty label ? label : 'Retour'}" />

<a class="inline-flex h-9 items-center justify-center rounded-md border border-zinc-200 bg-white px-4 text-sm font-medium text-zinc-700 shadow-sm transition-colors hover:bg-zinc-50"
   href="${backUrl}">
    <i data-lucide="arrow-left" class="mr-2 h-4 w-4"></i>
    <c:out value="${backLabel}"/>
</a>
