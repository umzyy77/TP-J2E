<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>

<layout:page title="Page introuvable">
    <div class="flex min-h-[calc(100vh-16rem)] flex-col items-center justify-center text-center">
        <div class="flex h-16 w-16 items-center justify-center rounded-full bg-zinc-100">
            <span class="text-3xl font-bold text-zinc-400">404</span>
        </div>
        <h1 class="mt-6 text-2xl font-bold tracking-tight">Page introuvable</h1>
        <p class="mt-2 text-sm text-zinc-500">L'annonce demandée n'existe pas ou a été supprimée.</p>
        <a class="mt-6 inline-flex h-10 items-center justify-center rounded-md bg-zinc-900 px-6 text-sm font-medium text-white shadow transition-colors hover:bg-zinc-800"
            href="${pageContext.request.contextPath}/AnnonceList">Retour à la liste</a>
    </div>
</layout:page>
