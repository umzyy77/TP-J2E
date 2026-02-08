<%@ tag pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="annonce" tagdir="/WEB-INF/tags/features/annonce/components" %>
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
                <i data-lucide="square-pen" class="mr-1.5 h-3.5 w-3.5"></i>
                Modifier
            </a>
            <annonce:deleteButton annonceId="${annonce.id}" />
        </div>
    </div>
</div>
