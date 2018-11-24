package ar.uba.fi.mercadolibre.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.adapter.QuestionAdapter;
import ar.uba.fi.mercadolibre.controller.APIResponse;
import ar.uba.fi.mercadolibre.controller.ControllerFactory;
import ar.uba.fi.mercadolibre.model.Account;
import ar.uba.fi.mercadolibre.model.Article;
import ar.uba.fi.mercadolibre.model.Question;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleQuestionsActivity extends BaseActivity {
    private Article article;
    private Account currentAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        Bundle extras = i.getExtras();

        if (extras == null) {
            return;
        }
        article = (Article) extras.get("article");
        if (article == null) {
            return;
        }
        setContentView(R.layout.activity_article_questions);
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
        ControllerFactory.getQuestionController().list(article.getID()).enqueue(new Callback<APIResponse<List<Question>>>() {
            @Override
            public void onResponse(Call<APIResponse<List<Question>>> call, Response<APIResponse<List<Question>>> response) {
                List<Question> data = getData(response);
                if (data == null) {
                    Log.e("Purchases GET", "data was null");
                    return;
                }
                findViewById(R.id.questionSend).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendMessage();
                    }
                });
                if (data.size() == 0) {
                    showEmptyMessage();
                    return;
                }
                ((ListView) findViewById(R.id.articleQuestions)).setAdapter(
                        new QuestionAdapter(ArticleQuestionsActivity.this, data)
                );
            }

            @Override
            public void onFailure(Call<APIResponse<List<Question>>> call, Throwable t) {
                Log.e("Purchases GET", t.toString());
            }
        });

    }
    private void showEmptyMessage() {
        TextView message = findViewById(R.id.noQuestions);
        message.setVisibility(View.VISIBLE);
        findViewById(R.id.articleQuestions).setVisibility(View.GONE);
    }

    public void sendMessage() {
        EditText editText = findViewById(R.id.editText);
        String message = editText.getText().toString();
        if (message.length() == 0) return;
        editText.getText().clear();
        saveQuestion(new Question(
                article,
                currentAccount,
                message
        ));
    }

    private void saveQuestion(Question question) {
        ControllerFactory.getQuestionController().create(article.getID(), question).enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Call<Question> call,
                                   Response<Question> response) {
                if (response.isSuccessful()) return;
                onFailure(call, new Exception("Unsuccessful response"));
            }

            @Override
            public void onFailure(Call<Question> call,
                                  Throwable t) {
                Log.e("ChatActivity", "Create question", t);
            }
        });
    }
}
