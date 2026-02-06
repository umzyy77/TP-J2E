<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>

            <layout:page title="Connexion - MasterAnnonce">
                <div class="flex min-h-[calc(100vh-16rem)] items-center justify-center">
                    <div class="w-full max-w-md">
                        <div class="rounded-lg border border-zinc-200 bg-white p-8 shadow-sm">
                            <div class="mb-6 text-center">
                                <h1 class="text-2xl font-bold tracking-tight">Connexion</h1>
                                <p class="mt-2 text-sm text-zinc-600">Entrez vos identifiants pour accéder à votre
                                    compte</p>
                            </div>

                            <c:if test="${not empty param.logout}">
                                <div
                                    class="mb-4 rounded-md bg-green-50 border border-green-200 p-3 text-sm text-green-800">
                                    Vous êtes déconnecté.
                                </div>
                            </c:if>

                            <c:if test="${not empty param.registered}">
                                <div
                                    class="mb-4 rounded-md bg-green-50 border border-green-200 p-3 text-sm text-green-800">
                                    Inscription réussie ! Vous pouvez maintenant vous connecter.
                                </div>
                            </c:if>

                            <c:if test="${not empty message}">
                                <div class="mb-4 rounded-md bg-red-50 border border-red-200 p-3 text-sm text-red-800">
                                    ${message}
                                </div>
                            </c:if>

                            <form action="${pageContext.request.contextPath}/login" method="post" class="space-y-4">
                                <div class="space-y-2">
                                    <label for="username" class="text-sm font-medium text-zinc-700">Nom
                                        d'utilisateur</label>
                                    <input type="text" id="username" name="username" value="${username}"
                                        class="w-full rounded-md border border-zinc-300 bg-white px-3 py-2 text-sm placeholder:text-zinc-400 focus:border-zinc-500 focus:outline-none focus:ring-2 focus:ring-zinc-500/20"
                                        placeholder="admin" required autofocus>
                                </div>

                                <div class="space-y-2">
                                    <label for="password" class="text-sm font-medium text-zinc-700">Mot de passe</label>
                                    <input type="password" id="password" name="password"
                                        class="w-full rounded-md border border-zinc-300 bg-white px-3 py-2 text-sm placeholder:text-zinc-400 focus:border-zinc-500 focus:outline-none focus:ring-2 focus:ring-zinc-500/20"
                                        placeholder="••••••••" required>
                                </div>

                                <button type="submit"
                                    class="w-full rounded-md bg-zinc-900 px-4 py-2 text-sm font-medium text-white shadow transition-colors hover:bg-zinc-800 focus:outline-none focus:ring-2 focus:ring-zinc-500 focus:ring-offset-2">
                                    Se connecter
                                </button>
                            </form>

                            <div class="mt-6 text-center text-sm text-zinc-600">
                                Pas encore de compte ?
                                <a href="${pageContext.request.contextPath}/register"
                                    class="font-medium text-zinc-900 hover:underline">
                                    S'inscrire
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </layout:page>