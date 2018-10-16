package ar.uba.fi.mercadolibre.controller;

import ar.uba.fi.mercadolibre.client.RetrofitClient;

public class ControllerFactory {

    private ControllerFactory() {
    }

    private static final String API_URL = "https://11ebd7a5.ngrok.io/";

    public static ArticleController getArticleController() {
        return RetrofitClient.getClient(API_URL).create(ArticleController.class);
    }

    public static AccountController getAccountController() {
        return RetrofitClient.getClient(API_URL).create(AccountController.class);
    }
}
