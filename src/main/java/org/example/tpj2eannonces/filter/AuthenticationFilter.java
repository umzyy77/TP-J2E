package org.example.tpj2eannonces.filter;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@WebFilter(filterName = "authFilter", urlPatterns = "/*")
public class AuthenticationFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    private static final String SESSION_USER = "loggedUser";

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/login",
            "/logout",
            "/register",
            "/AnnonceList",
            "/AnnonceDetail"
    );

    private static final Set<String> STATIC_EXTENSIONS = Set.of(
            ".css", ".js", ".png", ".jpg", ".jpeg", ".gif", ".ico", ".woff", ".woff2"
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("Filtre d'authentification initialisé");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getServletPath();
        String contextPath = httpRequest.getContextPath();

        if (isStaticResource(path)) {
            chain.doFilter(request, response);
            return;
        }
    
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        boolean isLoggedIn = session != null && session.getAttribute(SESSION_USER) != null;

        if (isLoggedIn) {
            chain.doFilter(request, response);
        } else {
            String requestedUrl = httpRequest.getRequestURL().toString();
            String queryString = httpRequest.getQueryString();
            if (queryString != null) {
                requestedUrl += "?" + queryString;
            }

            HttpSession newSession = httpRequest.getSession(true);
            newSession.setAttribute("redirectAfterLogin", requestedUrl);

            logger.debug("Accès refusé à {} - redirection vers login", path);
            httpResponse.sendRedirect(contextPath + "/login");
        }
    }

    private boolean isPublicPath(String path) {
        for (String publicPath : PUBLIC_PATHS) {
            if (path.startsWith(publicPath)) {
                return true;
            }
        }
        return path.equals("/") || path.isEmpty();
    }

    private boolean isStaticResource(String path) {
        for (String ext : STATIC_EXTENSIONS) {
            if (path.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void destroy() {
        logger.info("Filtre d'authentification détruit");
    }
}
