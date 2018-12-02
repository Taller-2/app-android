package ar.uba.fi.mercadolibre.notifications;

import android.content.Intent;

import java.io.IOException;
import java.util.Map;

import ar.uba.fi.mercadolibre.activity.ArticleQuestionsActivity;
import ar.uba.fi.mercadolibre.controller.ControllerFactory;
import ar.uba.fi.mercadolibre.model.Article;

class NewAnswerMessage extends FirebaseMessage {
    NewAnswerMessage(Map<String, String> data) {
        super(data);
    }

    @Override
    Class<?> getActivityClass() {
        return ArticleQuestionsActivity.class;
    }

    @Override
    Intent setIntentExtras(Intent i) {
        String articleId = data.get("article_id");
        try {
            Article a = ControllerFactory.getArticleController().getByID(articleId).execute().body();
            i.putExtra("article", a);
            return i;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}


