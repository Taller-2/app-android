package ar.uba.fi.mercadolibre.notifications;

import android.content.Intent;

import java.io.IOException;
import java.util.Map;

import ar.uba.fi.mercadolibre.activity.AnswerQuestionActivity;
import ar.uba.fi.mercadolibre.controller.ControllerFactory;
import ar.uba.fi.mercadolibre.model.Question;


 class NewQuestionMessage extends FirebaseMessage {
    NewQuestionMessage(Map<String, String> data) {
        super(data);
    }

    @Override
    Class<?> getActivityClass() {
        return AnswerQuestionActivity.class;
    }

    @Override
    Intent setIntentExtras(Intent i) {
        String questionId = data.get("question");
        try {
            Question q = ControllerFactory.getQuestionController().get(questionId).execute().body();
            i.putExtra("question", q);
            return i;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return i;
    }
}
