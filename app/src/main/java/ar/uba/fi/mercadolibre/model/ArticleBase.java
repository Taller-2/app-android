package ar.uba.fi.mercadolibre.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ArticleBase {

    @SerializedName("data")
    @Expose
    private List<Article> data = null;
    @SerializedName("ok")
    @Expose
    private Boolean ok;

    public List<Article> getData() {
        return data;
    }

    public void setData(List<Article> data) {
        this.data = data;
    }

    public Boolean getOk() {
        return ok;
    }

    public void setOk(Boolean ok) {
        this.ok = ok;
    }

}