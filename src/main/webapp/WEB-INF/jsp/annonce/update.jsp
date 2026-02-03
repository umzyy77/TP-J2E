<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="annonce" tagdir="/WEB-INF/tags/annonce" %>
        <%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>

            <layout:page title="Modifier une annonce">
                <annonce:form title="Modifier l'annonce" subtitle="Mettez à jour les informations."
                    actionUrl="${pageContext.request.contextPath}/AnnonceUpdate" submitLabel="Mettre à jour"
                    annonceObj="${requestScope.annonce}" annonceId="${requestScope.annonce.id}"
                    errorMessage="${requestScope.message}" />
            </layout:page>