<%@ tag pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="statuses" required="true" type="java.lang.Object" %>
<%@ attribute name="selectedStatus" required="false" %>

<c:if test="${not empty statuses}">
    <div class="flex items-center gap-2">
        <label for="statusFilter" class="text-sm font-medium text-zinc-700">Statut :</label>
        <select id="statusFilter"
            class="h-9 rounded-md border border-zinc-200 bg-white px-3 text-sm text-zinc-700 shadow-sm">
            <option value="">Tous</option>
            <c:forEach items="${statuses}" var="s">
                <option value="${s}" <c:if test="${s == selectedStatus}">selected</c:if>>
                    <c:out value="${s.displayLabel}" />
                </option>
            </c:forEach>
        </select>
    </div>
</c:if>

<script>
    (() => {
        const statusFilter = document.getElementById('statusFilter');
        if (!statusFilter) {
            return;
        }

        statusFilter.addEventListener('change', () => {
            const url = new URL(window.location.href);
            url.searchParams.delete('page');
            if (statusFilter.value) {
                url.searchParams.set('status', statusFilter.value);
            } else {
                url.searchParams.delete('status');
            }
            window.location.assign(url.toString());
        });
    })();
</script>
