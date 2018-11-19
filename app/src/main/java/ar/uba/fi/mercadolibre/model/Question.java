package ar.uba.fi.mercadolibre.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Question implements Serializable {
    @SerializedName("user_id")
    @Expose
    private String userId;

    @SerializedName("article_id")
    @Expose
    private String articleId;

    @SerializedName("question")
    @Expose
    private String question;

    @SerializedName("answer")
    @Expose
    private String answer;

    @SerializedName("created_at")
    @Expose
    private Date createdAt;

    @SerializedName("answeredAt")
    @Expose
    private Date answeredAt;

    public String getUserId() {
        return userId;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getAnsweredAt() {
        return answeredAt;
    }

    public Question(Article article, Account user, String question) {
        this.articleId = article.getID();
        this.userId = user.getUserID();
        this.question = question;
        createdAt = Calendar.getInstance().getTime();
    }
}
