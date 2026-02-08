<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>

        <layout:page title="MasterAnnonce">
            <div class="flex min-h-[calc(100vh-12rem)] flex-col items-center justify-center">
                <div class="w-full max-w-2xl space-y-8 text-center">
                    <div class="space-y-4">
                        <div
                            class="inline-flex items-center rounded-full border border-zinc-200 bg-white px-3 py-1 text-xs font-medium text-zinc-600 shadow-sm">
                            Jakarta Servlet / JSP / JPA
                        </div>
                        <h1 class="text-4xl font-bold tracking-tight sm:text-5xl">
                            Gérez vos annonces<br>simplement
                        </h1>
                        <p class="mx-auto max-w-md text-lg text-zinc-600">
                            Application CRUD moderne pour la gestion des annonces avec validation serveur et base de
                            données PostgreSQL.
                        </p>
                    </div>
                    <div class="flex flex-wrap items-center justify-center gap-3">
                        <a class="inline-flex h-11 items-center justify-center rounded-md bg-zinc-900 px-6 text-sm font-medium text-white shadow transition-colors hover:bg-zinc-800"
                            href="${pageContext.request.contextPath}/AnnonceList">Voir les annonces</a>
                        <a class="inline-flex h-11 items-center justify-center rounded-md border border-zinc-200 bg-white px-6 text-sm font-medium text-zinc-700 shadow-sm transition-colors hover:bg-zinc-50"
                            href="${pageContext.request.contextPath}/AnnonceAdd">Créer une annonce</a>
                    </div>
                </div>

                <div class="mt-16 grid w-full max-w-2xl gap-4 sm:grid-cols-2">
                    <div class="rounded-lg border border-zinc-200 bg-white p-6 shadow-sm">
                        <h2 class="font-semibold">Pile technique</h2>
                        <ul class="mt-3 space-y-2 text-sm text-zinc-600">
                            <li class="flex items-center gap-2">
                                <span class="h-1.5 w-1.5 rounded-full bg-zinc-400"></span>
                                Java 25 + Tomcat 10.1.x
                            </li>
                            <li class="flex items-center gap-2">
                                <span class="h-1.5 w-1.5 rounded-full bg-zinc-400"></span>
                                Jakarta Servlet 6 / JSP / JSTL
                            </li>
                            <li class="flex items-center gap-2">
                                <span class="h-1.5 w-1.5 rounded-full bg-zinc-400"></span>
                                PostgreSQL + Hibernate
                            </li>
                        </ul>
                    </div>
                    <div class="rounded-lg border border-zinc-200 bg-white p-6 shadow-sm">
                        <h2 class="font-semibold">Fonctionnalités</h2>
                        <ul class="mt-3 space-y-2 text-sm text-zinc-600">
                            <li class="flex items-center gap-2">
                                <span class="h-1.5 w-1.5 rounded-full bg-zinc-400"></span>
                                CRUD complet des annonces
                            </li>
                            <li class="flex items-center gap-2">
                                <span class="h-1.5 w-1.5 rounded-full bg-zinc-400"></span>
                                Validation côté serveur
                            </li>
                            <li class="flex items-center gap-2">
                                <span class="h-1.5 w-1.5 rounded-full bg-zinc-400"></span>
                                Gestion des erreurs robuste
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </layout:page>