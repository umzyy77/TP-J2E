package org.example.tpj2eannonces.servlet.annonce;

import org.example.tpj2eannonces.service.AnnonceService;
import org.example.tpj2eannonces.service.CategoryService;
import org.example.tpj2eannonces.servlet.BaseServlet;

public abstract class AbstractAnnonceFormServlet extends BaseServlet {
    protected static final String ATTR_ANNONCE = "annonce";
    protected static final String ATTR_CATEGORIES = "categories";
    protected static final String ATTR_SELECTED_CATEGORY = "selectedCategoryId";

    protected final transient AnnonceService annonceService = new AnnonceService();
    protected final transient CategoryService categoryService = new CategoryService();
}
