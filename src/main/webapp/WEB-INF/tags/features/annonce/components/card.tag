<%@ tag pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ attribute name="annonce" required="true" type="org.example.tpj2eannonces.model.Annonce" %>

<div class="rounded-lg border border-zinc-200 bg-white p-4 shadow-sm">
    <div class="flex items-start justify-between gap-4">
        <div class="min-w-0 flex-1">
            <h3 class="font-semibold text-zinc-900 truncate"><c:out value="${annonce.title}"/></h3>
            <p class="mt-1 text-sm text-zinc-600 line-clamp-2"><c:out value="${annonce.description}"/></p>
        </div>
    </div>
    
    <div class="mt-4 grid grid-cols-2 gap-3 text-sm">
        <div>
            <span class="text-zinc-500">Adresse</span>
            <p class="font-medium text-zinc-700 truncate"><c:out value="${annonce.adress}"/></p>
        </div>
        <div>
            <span class="text-zinc-500">Email</span>
            <p class="font-medium text-zinc-700 truncate"><c:out value="${annonce.mail}"/></p>
        </div>
    </div>
    
    <div class="mt-3 flex items-center justify-between border-t border-zinc-100 pt-3">
        <span class="text-xs text-zinc-500">
            <fmt:formatDate value="${annonce.dateAsDate}" pattern="dd/MM/yyyy HH:mm"/>
        </span>
        <div class="flex items-center gap-2">
            <a class="inline-flex h-8 items-center justify-center rounded-md border border-zinc-200 bg-white px-3 text-xs font-medium text-zinc-700 shadow-sm transition-colors hover:bg-zinc-50"
               href="${pageContext.request.contextPath}/AnnonceUpdate?id=${annonce.id}">
                <svg class="mr-1.5 h-3.5 w-3.5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/>
                </svg>
                Modifier
            </a>
            <a class="inline-flex h-8 items-center justify-center rounded-md border border-red-200 bg-white px-3 text-xs font-medium text-red-600 shadow-sm transition-colors hover:bg-red-50"
               href="${pageContext.request.contextPath}/AnnonceDelete?id=${annonce.id}"
               onclick="return confirm('Êtes-vous sûr de vouloir supprimer cette annonce ?');">
                <svg class="mr-1.5 h-3.5 w-3.5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/>
                </svg>
                Supprimer
            </a>
        </div>
    </div>
</div>
