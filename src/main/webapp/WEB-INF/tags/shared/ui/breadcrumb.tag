<%@ tag pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="parentLabel" required="true" %>
<%@ attribute name="parentHref" required="true" %>
<%@ attribute name="currentLabel" required="true" %>

<div class="flex items-center justify-between">
    <nav class="flex items-center gap-2 text-sm text-zinc-600">
        <a href="${parentHref}" class="hover:text-zinc-900"><c:out value="${parentLabel}" /></a>
        <span>/</span>
        <span class="text-zinc-900"><c:out value="${currentLabel}" /></span>
    </nav>
    <jsp:doBody />
</div>
