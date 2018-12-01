package ar.uba.fi.mercadolibre.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.util.Date;

public class AccountEvent extends BaseModel {
    public enum Type {
        PUBLICATION, PURCHASE, SALE
    }

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("article_name")
    @Expose
    private String articleName;

    @SerializedName("score_increment")
    @Expose
    private int scoreIncrement;

    @SerializedName("timestamp")
    @Expose
    private String timestamp;

    public Type getType() {
        switch (type) {
            case "publication":
                return Type.PUBLICATION;
            case "purchase":
                return Type.PURCHASE;
            case "sale":
                return Type.SALE;
        }
        throw new RuntimeException("Invalid type");
    }

    public String getArticleName() {
        return articleName;
    }

    public int getScoreIncrement() {
        return scoreIncrement;
    }

    public Date getTimestamp() throws ParseException {
        return parseTimestamp(timestamp);
    }
}
