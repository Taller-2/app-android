package ar.uba.fi.mercadolibre.controller;

import ar.uba.fi.mercadolibre.client.RetrofitClient;

public class ControllerFactory {

    private static final String API_URL = "https://taller2-app-server.herokuapp.com/";

    private ControllerFactory() {
    }

    public static ArticleController getArticleController() {
        return RetrofitClient.getClient(API_URL).create(ArticleController.class);
    }

    public static AccountController getAccountController() {
        return RetrofitClient.getClient(API_URL).create(AccountController.class);
    }

    public static ChatMessageController getChatMessageController() {
        return RetrofitClient.getClient(API_URL).create(ChatMessageController.class);
    }

    public static PurchaseController getPurchaseController() {
        return RetrofitClient.getClient(API_URL).create(PurchaseController.class);
    }

    public static QuestionController getQuestionController() {
        return RetrofitClient.getClient(API_URL).create(QuestionController.class);
    }
}
