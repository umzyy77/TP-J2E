<%@ tag pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="action" required="true" description="URL du formulaire" %>
<%@ attribute name="name" required="false" description="Nom du paramètre (défaut: q)" %>
<%@ attribute name="value" required="false" description="Valeur courante de la recherche" %>
<%@ attribute name="placeholder" required="false" description="Placeholder du champ" %>
<%@ attribute name="debounce" required="false" description="Délai debounce en ms (défaut: 400)" %>
<%@ attribute name="clearUrl" required="false" description="URL pour effacer la recherche" %>

<c:set var="paramName" value="${empty name ? 'q' : name}" />
<c:set var="placeholderText" value="${empty placeholder ? 'Rechercher...' : placeholder}" />
<c:set var="debounceMs" value="${empty debounce ? '400' : debounce}" />
<c:set var="uid" value="search_${paramName}" />

<form id="${uid}_form" method="get" action="${action}" class="flex items-center gap-2">
    <label for="${uid}_input"></label><input type="text" id="${uid}_input" name="${paramName}" value="${value}"
                                             placeholder="${placeholderText}"
                                             class="h-9 w-64 rounded-md border border-zinc-200 bg-white px-3 text-sm text-zinc-700 shadow-sm placeholder:text-zinc-400" />
    <button type="submit"
        class="inline-flex h-9 items-center justify-center rounded-md bg-zinc-900 px-3 text-sm font-medium text-white shadow transition-colors hover:bg-zinc-800">
        <i data-lucide="search" class="mr-1.5 h-3.5 w-3.5"></i>
        Rechercher
    </button>
    <c:if test="${not empty value}">
        <a href="${empty clearUrl ? action : clearUrl}" class="text-sm text-zinc-500 hover:underline">Effacer</a>
    </c:if>
</form>

<script>
    (() => {
        const input = document.getElementById('${uid}_input');
        const form = document.getElementById('${uid}_form');
        let timer;

        input.addEventListener('input', () => {
            clearTimeout(timer);
            timer = setTimeout(() => form.submit(), ${debounceMs});
        });

        if (input.value.length > 0) {
            input.focus();
            input.setSelectionRange(input.value.length, input.value.length);
        }
    })();
</script>
