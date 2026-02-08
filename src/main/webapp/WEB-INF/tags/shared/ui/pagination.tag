<%@ tag pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="currentPage" required="true" type="java.lang.Integer" %>
<%@ attribute name="totalPages" required="true" type="java.lang.Integer" %>
<%@ attribute name="baseUrl" required="true" %>

<c:if test="${totalPages > 1}">
    <nav class="flex items-center justify-between border-t border-zinc-200 pt-4">
        <p class="text-sm text-zinc-500">
            Page <c:out value="${currentPage + 1}" /> sur <c:out value="${totalPages}" />
        </p>
        <div class="flex items-center gap-2">
            <c:choose>
                <c:when test="${currentPage > 0}">
                    <a href="${baseUrl}&page=${currentPage - 1}"
                       class="inline-flex h-9 items-center justify-center rounded-md border border-zinc-200 bg-white px-3 text-sm font-medium text-zinc-700 shadow-sm transition-colors hover:bg-zinc-50">
                        &larr; Précédent
                    </a>
                </c:when>
                <c:otherwise>
                    <span class="inline-flex h-9 items-center justify-center rounded-md border border-zinc-200 bg-zinc-50 px-3 text-sm font-medium text-zinc-400 cursor-not-allowed">
                        &larr; Précédent
                    </span>
                </c:otherwise>
            </c:choose>

            <c:choose>
                <c:when test="${currentPage + 1 < totalPages}">
                    <a href="${baseUrl}&page=${currentPage + 1}"
                       class="inline-flex h-9 items-center justify-center rounded-md border border-zinc-200 bg-white px-3 text-sm font-medium text-zinc-700 shadow-sm transition-colors hover:bg-zinc-50">
                        Suivant &rarr;
                    </a>
                </c:when>
                <c:otherwise>
                    <span class="inline-flex h-9 items-center justify-center rounded-md border border-zinc-200 bg-zinc-50 px-3 text-sm font-medium text-zinc-400 cursor-not-allowed">
                        Suivant &rarr;
                    </span>
                </c:otherwise>
            </c:choose>
        </div>
    </nav>
</c:if>
