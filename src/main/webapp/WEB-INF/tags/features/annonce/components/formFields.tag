<%@ tag pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="annonce" required="true" type="org.example.tpj2eannonces.model.Annonce" %>
<%@ attribute name="categories" required="false" type="java.util.List" %>
<%@ attribute name="selectedCategoryId" required="false" %>

<div class="space-y-4">
    <div class="space-y-2">
        <label class="text-sm font-medium leading-none text-zinc-900" for="title">Titre</label>
        <input class="flex h-10 w-full rounded-md border border-zinc-200 bg-white px-3 py-2 text-sm text-zinc-900 ring-offset-white placeholder:text-zinc-400 focus:outline-none focus:ring-2 focus:ring-zinc-900 focus:ring-offset-2"
               id="title" name="title" type="text" required placeholder="Titre de l'annonce" value="<c:out value='${annonce.title}'/>">
    </div>
    <div class="space-y-2">
        <label class="text-sm font-medium leading-none text-zinc-900" for="description">Description</label>
        <textarea class="flex min-h-[120px] w-full rounded-md border border-zinc-200 bg-white px-3 py-2 text-sm text-zinc-900 ring-offset-white placeholder:text-zinc-400 focus:outline-none focus:ring-2 focus:ring-zinc-900 focus:ring-offset-2"
                  id="description" name="description" required placeholder="Description détaillée..."><c:out value="${annonce.description}"/></textarea>
    </div>
    <c:if test="${not empty categories}">
        <div class="space-y-2">
            <label class="text-sm font-medium leading-none text-zinc-900" for="categoryId">Catégorie</label>
            <select class="flex h-10 w-full rounded-md border border-zinc-200 bg-white px-3 py-2 text-sm text-zinc-900 ring-offset-white focus:outline-none focus:ring-2 focus:ring-zinc-900 focus:ring-offset-2"
                    id="categoryId" name="categoryId">
                <option value="">-- Sélectionner une catégorie --</option>
                <c:forEach items="${categories}" var="cat">
                    <option value="${cat.id}" ${cat.id.toString() == selectedCategoryId ? 'selected' : ''}>
                        <c:out value="${cat.label}" />
                    </option>
                </c:forEach>
            </select>
        </div>
    </c:if>
    <div class="grid gap-4 sm:grid-cols-2">
        <div class="space-y-2">
            <label class="text-sm font-medium leading-none text-zinc-900" for="adress">Adresse</label>
            <input class="flex h-10 w-full rounded-md border border-zinc-200 bg-white px-3 py-2 text-sm text-zinc-900 ring-offset-white placeholder:text-zinc-400 focus:outline-none focus:ring-2 focus:ring-zinc-900 focus:ring-offset-2"
                   id="adress" name="adress" type="text" required placeholder="Adresse" value="<c:out value='${annonce.adress}'/>">
        </div>
        <div class="space-y-2">
            <label class="text-sm font-medium leading-none text-zinc-900" for="mail">Email</label>
            <input class="flex h-10 w-full rounded-md border border-zinc-200 bg-white px-3 py-2 text-sm text-zinc-900 ring-offset-white placeholder:text-zinc-400 focus:outline-none focus:ring-2 focus:ring-zinc-900 focus:ring-offset-2"
                   id="mail" name="mail" type="email" required placeholder="email@exemple.com" value="<c:out value='${annonce.mail}'/>">
        </div>
    </div>
</div>
