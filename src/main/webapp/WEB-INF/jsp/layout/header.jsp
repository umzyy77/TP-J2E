<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <header
        class="sticky top-0 z-50 w-full border-b border-zinc-200 bg-white/95 backdrop-blur supports-[backdrop-filter]:bg-white/60">
        <div class="mx-auto flex h-14 w-full max-w-5xl items-center justify-between px-4 sm:px-6 lg:px-8">
            <a href="${pageContext.request.contextPath}/" class="flex items-center gap-2">
                <div
                    class="flex h-8 w-8 items-center justify-center rounded-lg bg-zinc-900 text-sm font-bold text-white">
                    M</div>
                <span class="text-lg font-semibold tracking-tight">MasterAnnonce</span>
            </a>
            <nav class="flex items-center gap-1">
                <a class="inline-flex h-9 items-center justify-center rounded-md px-4 text-sm font-medium text-zinc-600 transition-colors hover:bg-zinc-100 hover:text-zinc-900"
                    href="${pageContext.request.contextPath}/AnnonceList">Liste</a>
                <a class="inline-flex h-9 items-center justify-center rounded-md bg-zinc-900 px-4 text-sm font-medium text-white transition-colors hover:bg-zinc-800"
                    href="${pageContext.request.contextPath}/AnnonceAdd">Ajouter</a>
            </nav>
        </div>
    </header>