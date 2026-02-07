<%@ tag pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="title" required="true" %>
<%@ attribute name="message" required="true" %>
<%@ attribute name="iconPath" required="false" %>
<%@ attribute name="actionHref" required="false" %>
<%@ attribute name="actionLabel" required="false" %>

<div class="flex flex-col items-center justify-center rounded-lg border border-dashed border-zinc-300 bg-white py-12">
    <div class="flex h-12 w-12 items-center justify-center rounded-full bg-zinc-100">
        <svg class="h-6 w-6 text-zinc-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
            <path stroke-linecap="round" stroke-linejoin="round"
                d="${not empty iconPath ? iconPath : 'M19.5 14.25v-2.625a3.375 3.375 0 00-3.375-3.375h-1.5A1.125 1.125 0 0113.5 7.125v-1.5a3.375 3.375 0 00-3.375-3.375H8.25m2.25 0H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 00-9-9z'}" />
        </svg>
    </div>
    <h3 class="mt-4 text-sm font-medium text-zinc-900"><c:out value="${title}" /></h3>
    <p class="mt-1 text-sm text-zinc-500"><c:out value="${message}" /></p>
    <c:if test="${not empty actionHref}">
        <a class="mt-4 inline-flex h-9 items-center justify-center rounded-md bg-zinc-900 px-4 text-sm font-medium text-white shadow transition-colors hover:bg-zinc-800"
            href="${actionHref}"><c:out value="${actionLabel}" /></a>
    </c:if>
</div>
