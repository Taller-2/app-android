package ar.uba.fi.mercadolibre.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Purchase extends BaseModel {
    @SerializedName("user_id")
    @Expose
    private String userId;

    @SerializedName("article")
    @Expose
    private Article article;

    @SerializedName("units")
    @Expose
    private int units;

    public Article getArticle() {
        return article;
    }
}
