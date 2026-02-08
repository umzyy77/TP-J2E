<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
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
                    <c:if test="${not empty sessionScope.loggedUser}">
                        <a class="inline-flex h-9 items-center justify-center rounded-md bg-zinc-900 px-4 text-sm font-medium text-white transition-colors hover:bg-zinc-800"
                            href="${pageContext.request.contextPath}/AnnonceAdd">Ajouter</a>

                        <%-- Avatar dropdown --%>
                            <div class="relative ml-2" id="userMenu">
                                <button type="button" onclick="toggleUserMenu()"
                                    class="flex h-9 w-9 items-center justify-center rounded-full bg-zinc-200 text-sm font-medium text-zinc-700 hover:bg-zinc-300 transition-colors focus:outline-none focus:ring-2 focus:ring-zinc-400 focus:ring-offset-2">
                                    <c:out value="${sessionScope.loggedUser.username.substring(0,1).toUpperCase()}" />
                                </button>
                                <div id="userMenuDropdown"
                                    class="hidden absolute right-0 mt-2 w-48 origin-top-right rounded-md border border-zinc-200 bg-white py-1 shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none">
                                    <div class="border-b border-zinc-100 px-4 py-2">
                                        <p class="text-sm font-medium text-zinc-900">
                                            <c:out value="${sessionScope.loggedUser.username}" />
                                        </p>
                                        <p class="text-xs text-zinc-500">
                                            <c:out value="${sessionScope.loggedUser.email}" />
                                        </p>
                                    </div>
                                    <a href="${pageContext.request.contextPath}/AnnonceList?author=${sessionScope.loggedUser.id}"
                                        class="flex items-center gap-2 px-4 py-2 text-sm text-zinc-700 hover:bg-zinc-100">
                                        <i data-lucide="file-text" class="h-4 w-4"></i>
                                        Mes annonces
                                    </a>
                                    <a href="${pageContext.request.contextPath}/logout"
                                        class="flex items-center gap-2 px-4 py-2 text-sm text-red-600 hover:bg-zinc-100">
                                        <i data-lucide="log-out" class="h-4 w-4"></i>
                                        Déconnexion
                                    </a>
                                </div>
                            </div>
                    </c:if>
                    <c:if test="${empty sessionScope.loggedUser}">
                        <a class="inline-flex h-9 items-center justify-center rounded-md bg-zinc-900 px-4 text-sm font-medium text-white transition-colors hover:bg-zinc-800"
                            href="${pageContext.request.contextPath}/login">Connexion</a>
                    </c:if>
                </nav>
            </div>
        </header>

        <script>
            function toggleUserMenu() {
                const dropdown = document.getElementById('userMenuDropdown');
                dropdown.classList.toggle('hidden');
            }

            document.addEventListener('click', function (event) {
                const userMenu = document.getElementById('userMenu');
                const dropdown = document.getElementById('userMenuDropdown');
                if (userMenu && dropdown && !userMenu.contains(event.target)) {
                    dropdown.classList.add('hidden');
                }
            });
        </script>