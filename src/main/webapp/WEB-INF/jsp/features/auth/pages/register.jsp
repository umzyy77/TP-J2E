<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags/shared/ui" %>

<layout:page title="Inscription - MasterAnnonce">
    <div class="flex min-h-[calc(100vh-16rem)] items-center justify-center">
        <div class="w-full max-w-md">
            <div class="rounded-lg border border-zinc-200 bg-white p-8 shadow-sm">
                <div class="mb-6 text-center">
                    <h1 class="text-2xl font-bold tracking-tight">Créer un compte</h1>
                    <p class="mt-2 text-sm text-zinc-600">Remplissez le formulaire pour vous inscrire</p>
                </div>

                <c:if test="${not empty message}">
                    <div class="mb-4">
                        <ui:alert variant="error" message="${message}" />
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/register" method="post" class="space-y-4">
                    <div class="space-y-2">
                        <label for="username" class="text-sm font-medium text-zinc-700">Nom d'utilisateur</label>
                        <input type="text" id="username" name="username" value="${username}" minlength="3"
                            maxlength="50"
                            class="w-full rounded-md border border-zinc-300 bg-white px-3 py-2 text-sm placeholder:text-zinc-400 focus:border-zinc-500 focus:outline-none focus:ring-2 focus:ring-zinc-500/20"
                            placeholder="jean_dupont" required autofocus>
                    </div>

                    <div class="space-y-2">
                        <label for="email" class="text-sm font-medium text-zinc-700">Email</label>
                        <input type="email" id="email" name="email" value="${email}"
                            class="w-full rounded-md border border-zinc-300 bg-white px-3 py-2 text-sm placeholder:text-zinc-400 focus:border-zinc-500 focus:outline-none focus:ring-2 focus:ring-zinc-500/20"
                            placeholder="jean@exemple.com" required>
                    </div>

                    <div class="space-y-2">
                        <label for="password" class="text-sm font-medium text-zinc-700">Mot de passe</label>
                        <input type="password" id="password" name="password" minlength="6"
                            class="w-full rounded-md border border-zinc-300 bg-white px-3 py-2 text-sm placeholder:text-zinc-400 focus:border-zinc-500 focus:outline-none focus:ring-2 focus:ring-zinc-500/20"
                            placeholder="••••••••" required>
                        <p class="text-xs text-zinc-500">Minimum 6 caractères</p>
                    </div>

                    <div class="space-y-2">
                        <label for="confirmPassword" class="text-sm font-medium text-zinc-700">Confirmer le mot de passe</label>
                        <input type="password" id="confirmPassword" name="confirmPassword" minlength="6"
                            class="w-full rounded-md border border-zinc-300 bg-white px-3 py-2 text-sm placeholder:text-zinc-400 focus:border-zinc-500 focus:outline-none focus:ring-2 focus:ring-zinc-500/20"
                            placeholder="••••••••" required>
                    </div>

                    <button type="submit"
                        class="w-full rounded-md bg-zinc-900 px-4 py-2 text-sm font-medium text-white shadow transition-colors hover:bg-zinc-800 focus:outline-none focus:ring-2 focus:ring-zinc-500 focus:ring-offset-2">
                        S'inscrire
                    </button>
                </form>

                <div class="mt-6 text-center text-sm text-zinc-600">
                    Déjà un compte ?
                    <a href="${pageContext.request.contextPath}/login"
                        class="font-medium text-zinc-900 hover:underline">
                        Se connecter
                    </a>
                </div>
            </div>
        </div>
    </div>
</layout:page>
