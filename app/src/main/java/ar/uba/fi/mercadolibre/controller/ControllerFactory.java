package ar.uba.fi.mercadolibre.controller;

import ar.uba.fi.mercadolibre.client.RetrofitClient;

public class ControllerFactory {

    private ControllerFactory() {
    }

    private static final String API_URL = "https://taller2-app-server.herokuapp.com/";

    public static ArticleController getArticleController() {
        return RetrofitClient.getClient(API_URL).create(ArticleController.class);
    }
}
