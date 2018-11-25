package ar.uba.fi.mercadolibre.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.adapter.QuestionAdapter;
import ar.uba.fi.mercadolibre.adapter.UserQuestionAdapter;
import ar.uba.fi.mercadolibre.controller.APIResponse;
import ar.uba.fi.mercadolibre.controller.ControllerFactory;
import ar.uba.fi.mercadolibre.model.Account;
import ar.uba.fi.mercadolibre.model.Question;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserQuestionsActivity extends BaseActivity {
    private Account currentAccount;
    private List<Question> questions = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_questions);
        findViewById(R.id.questionForm).setVisibility(View.GONE);
        init();
    }

    private void init() {
        ControllerFactory.getAccountController().currentAccount().enqueue(new Callback<APIResponse<Account>>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<Account>> call,
                                   @NonNull Response<APIResponse<Account>> response) {
                currentAccount = getData(response);
                if (currentAccount == null) {
                    finish();
                    return;
                }
                initQuestions();
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse<Account>> call,
                                  @NonNull Throwable t) {
                onGetDataFailure(t);
            }
        });
    }
    private void initQuestions() {
        ControllerFactory.getQuestionController().listByArticleOwner(currentAccount.getID()).enqueue(new Callback<APIResponse<List<Question>>>() {
            @Override
            public void onResponse(Call<APIResponse<List<Question>>> call, Response<APIResponse<List<Question>>> response) {
                questions = getData(response);
                if (questions == null) {
                    Log.e("Questions GET", "data was null");
                    return;
                }
                if (questions.size() == 0) {
                    showEmptyMessage();
                    return;
                }
                ((ListView) findViewById(R.id.articleQuestions)).setAdapter(
                        new UserQuestionAdapter(UserQuestionsActivity.this, questions)
                );
            }

            @Override
            public void onFailure(Call<APIResponse<List<Question>>> call, Throwable t) {
                Log.e("Questions GET", t.toString());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentAccount == null) {
            return;
        }
        ListView view = findViewById(R.id.articleQuestions);
        view.setAdapter(null);
        initQuestions();
    }

    private void showEmptyMessage() {
        TextView message = findViewById(R.id.noQuestions);
        message.setVisibility(View.VISIBLE);
        findViewById(R.id.articleQuestions).setVisibility(View.GONE);
    }

}
