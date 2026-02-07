<%@ tag pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="categories" required="true" type="java.util.List" %>
<%@ attribute name="selectedCategory" required="false" %>

<c:if test="${not empty categories}">
    <div class="flex items-center gap-2">
        <label for="categoryFilter" class="text-sm font-medium text-zinc-700">Catégorie :</label>
        <select id="categoryFilter" onchange="if(this.value){location.href='${pageContext.request.contextPath}/AnnonceList?category='+this.value}else{location.href='${pageContext.request.contextPath}/AnnonceList'}"
            class="h-9 rounded-md border border-zinc-200 bg-white px-3 text-sm text-zinc-700 shadow-sm">
            <option value="">Toutes</option>
            <c:forEach items="${categories}" var="cat">
                <option value="${cat.id}" <c:if test="${cat.id == selectedCategory}">selected</c:if>>
                    <c:out value="${cat.label}" />
                </option>
            </c:forEach>
        </select>
    </div>
</c:if>
