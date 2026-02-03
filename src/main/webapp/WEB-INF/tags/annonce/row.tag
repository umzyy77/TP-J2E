<%@ tag pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ attribute name="annonce" required="true" type="org.example.tpj2eannonces.model.Annonce" %>

<tr class="transition-colors hover:bg-zinc-50">
    <td class="whitespace-nowrap px-4 py-3 font-medium text-zinc-900"><c:out value="${annonce.title}"/></td>
    <td class="max-w-xs truncate px-4 py-3 text-zinc-600"><c:out value="${annonce.description}"/></td>
    <td class="hidden whitespace-nowrap px-4 py-3 text-zinc-600 md:table-cell"><c:out value="${annonce.adress}"/></td>
    <td class="hidden whitespace-nowrap px-4 py-3 text-zinc-600 lg:table-cell"><c:out value="${annonce.mail}"/></td>
    <td class="hidden whitespace-nowrap px-4 py-3 text-zinc-500 lg:table-cell">
        <fmt:formatDate value="${annonce.dateAsDate}" pattern="dd/MM/yyyy HH:mm"/>
    </td>
    <td class="whitespace-nowrap px-4 py-3 text-right">
        <div class="flex items-center justify-end gap-2">
            <a class="inline-flex h-8 items-center justify-center rounded-md border border-zinc-200 bg-white px-3 text-xs font-medium text-zinc-700 shadow-sm transition-colors hover:bg-zinc-50"
               href="${pageContext.request.contextPath}/AnnonceUpdate?id=${annonce.id}">Modifier</a>
            <a class="inline-flex h-8 items-center justify-center rounded-md border border-red-200 bg-white px-3 text-xs font-medium text-red-600 shadow-sm transition-colors hover:bg-red-50"
               href="${pageContext.request.contextPath}/AnnonceDelete?id=${annonce.id}"
               onclick="return confirm('Êtes-vous sûr de vouloir supprimer cette annonce ?');">Supprimer</a>
        </div>
    </td>
</tr>
