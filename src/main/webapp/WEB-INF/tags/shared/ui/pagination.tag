<%@ tag pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="currentPage" required="true" type="java.lang.Integer" %>
<%@ attribute name="totalPages" required="true" type="java.lang.Integer" %>
<%@ attribute name="baseUrl" required="true" %>

<%-- Styles communs --%>
<c:set var="btnBase" value="inline-flex h-9 w-9 items-center justify-center rounded-md border text-sm font-medium transition-colors" />
<c:set var="btnActive" value="${btnBase} border-zinc-900 bg-zinc-900 text-white" />
<c:set var="btnDefault" value="${btnBase} border-zinc-200 bg-white text-zinc-700 shadow-sm hover:bg-zinc-50" />
<c:set var="btnDisabled" value="${btnBase} border-zinc-200 bg-zinc-50 text-zinc-300 cursor-not-allowed" />
<c:set var="navBtn" value="inline-flex h-9 items-center justify-center rounded-md border text-sm font-medium transition-colors px-3" />
<c:set var="navBtnEnabled" value="${navBtn} border-zinc-200 bg-white text-zinc-700 shadow-sm hover:bg-zinc-50" />
<c:set var="navBtnDisabled" value="${navBtn} border-zinc-200 bg-zinc-50 text-zinc-300 cursor-not-allowed" />
<c:set var="ellipsis" value="${btnBase} border-transparent bg-transparent text-zinc-400 cursor-default" />

<c:if test="${totalPages > 1}">
    <nav class="flex items-center justify-center gap-1 border-t border-zinc-200 pt-4">

        <c:choose>
            <c:when test="${currentPage > 0}">
                <a href="${baseUrl}&page=${currentPage - 1}" class="${navBtnEnabled}">
                    <i data-lucide="chevron-left" class="h-4 w-4"></i>
                </a>
            </c:when>
            <c:otherwise>
                <span class="${navBtnDisabled}">
                    <i data-lucide="chevron-left" class="h-4 w-4"></i>
                </span>
            </c:otherwise>
        </c:choose>

        <c:choose>
            <c:when test="${currentPage == 0}">
                <span class="${btnActive}">1</span>
            </c:when>
            <c:otherwise>
                <a href="${baseUrl}&page=0" class="${btnDefault}">1</a>
            </c:otherwise>
        </c:choose>

        <c:if test="${currentPage > 2}">
            <span class="${ellipsis}">&hellip;</span>
        </c:if>

        <c:forEach var="i" begin="${currentPage > 1 ? (currentPage < totalPages - 2 ? currentPage - 1 : (totalPages > 3 ? totalPages - 3 : 1)) : 1}"
                   end="${currentPage < totalPages - 2 ? (currentPage > 0 ? currentPage + 1 : (totalPages > 3 ? 2 : totalPages - 2)) : totalPages - 2}">
            <c:if test="${i > 0 && i < totalPages - 1}">
                <c:choose>
                    <c:when test="${i == currentPage}">
                        <span class="${btnActive}"><c:out value="${i + 1}" /></span>
                    </c:when>
                    <c:otherwise>
                        <a href="${baseUrl}&page=${i}" class="${btnDefault}"><c:out value="${i + 1}" /></a>
                    </c:otherwise>
                </c:choose>
            </c:if>
        </c:forEach>

        <c:if test="${currentPage < totalPages - 3}">
            <span class="${ellipsis}">&hellip;</span>
        </c:if>

        <c:if test="${totalPages > 1}">
            <c:choose>
                <c:when test="${currentPage == totalPages - 1}">
                    <span class="${btnActive}"><c:out value="${totalPages}" /></span>
                </c:when>
                <c:otherwise>
                    <a href="${baseUrl}&page=${totalPages - 1}" class="${btnDefault}"><c:out value="${totalPages}" /></a>
                </c:otherwise>
            </c:choose>
        </c:if>

        <c:choose>
            <c:when test="${currentPage + 1 < totalPages}">
                <a href="${baseUrl}&page=${currentPage + 1}" class="${navBtnEnabled}">
                    <i data-lucide="chevron-right" class="h-4 w-4"></i>
                </a>
            </c:when>
            <c:otherwise>
                <span class="${navBtnDisabled}">
                    <i data-lucide="chevron-right" class="h-4 w-4"></i>
                </span>
            </c:otherwise>
        </c:choose>

    </nav>
</c:if>
