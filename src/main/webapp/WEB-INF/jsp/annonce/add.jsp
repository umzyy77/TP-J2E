<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="annonce" tagdir="/WEB-INF/tags/annonce" %>
        <%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>

            <layout:page title="Ajouter une annonce">
                <annonce:form title="Nouvelle annonce" subtitle="Remplissez les informations ci-dessous."
                    actionUrl="${pageContext.request.contextPath}/AnnonceAdd" submitLabel="Enregistrer"
                    annonceObj="${requestScope.annonce}" errorMessage="${requestScope.message}" />
            </layout:page>