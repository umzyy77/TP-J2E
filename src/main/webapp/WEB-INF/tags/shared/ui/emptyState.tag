<%@ tag pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="title" required="true" %>
<%@ attribute name="message" required="true" %>
<%@ attribute name="icon" required="false" %>
<%@ attribute name="actionHref" required="false" %>
<%@ attribute name="actionLabel" required="false" %>

<div class="flex flex-col items-center justify-center rounded-lg border border-dashed border-zinc-300 bg-white py-12">
    <div class="flex h-12 w-12 items-center justify-center rounded-full bg-zinc-100">
        <i data-lucide="${not empty icon ? icon : 'file-text'}" class="h-6 w-6 text-zinc-400"></i>
    </div>
    <h3 class="mt-4 text-sm font-medium text-zinc-900"><c:out value="${title}" /></h3>
    <p class="mt-1 text-sm text-zinc-500"><c:out value="${message}" /></p>
    <c:if test="${not empty actionHref}">
        <a class="mt-4 inline-flex h-9 items-center justify-center rounded-md bg-zinc-900 px-4 text-sm font-medium text-white shadow transition-colors hover:bg-zinc-800"
            href="${actionHref}"><c:out value="${actionLabel}" /></a>
    </c:if>
</div>
