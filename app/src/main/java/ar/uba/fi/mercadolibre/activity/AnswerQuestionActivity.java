package ar.uba.fi.mercadolibre.activity;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.controller.APIResponse;
import ar.uba.fi.mercadolibre.controller.ControllerFactory;
import ar.uba.fi.mercadolibre.model.Question;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnswerQuestionActivity extends BaseActivity {
    private Question question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras == null) return;
        question = (Question) extras.get("question");
        setContentView(R.layout.activity_answer_question);
        init();
    }

    private void init() {
        ((TextView) findViewById(R.id.question)).setText(question.getQuestion());
        ((TextInputEditText) findViewById(R.id.answerInput)).setText(question.getAnswer());
        findViewById(R.id.submitAnswer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                patch();
            }
        });
    }

    private void patch() {
        String answer = ((TextInputEditText) findViewById(R.id.answerInput)).getText().toString();
        if (answer.length() == 0) {
            return;
        }
        question.answer(answer);
        ControllerFactory.getQuestionController().patch(question).enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Call<Question> call, Response<Question> response) {
                if (!response.isSuccessful()) {
                    Log.e("Questions PATCH", response.errorBody().toString());
                    return;
                }
                finish();
            }

            @Override
            public void onFailure(Call<Question> call, Throwable t) {
                Log.e("Questions PATCH", t.toString());
            }
        });

    }
}
