package ar.uba.fi.mercadolibre.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;

import ar.uba.fi.mercadolibre.controller.ControllerFactory;
import ar.uba.fi.mercadolibre.model.Article;
import ar.uba.fi.mercadolibre.model.Categories;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TagsSpinner extends android.support.v7.widget.AppCompatSpinner {
    final static String NO_CATEGORY = "-";

    public TagsSpinner(Context context) {
        super(context);
    }

    public TagsSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TagsSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(final Context context) {
        ControllerFactory.getArticleController().listCategories().enqueue(new Callback<Categories>() {
            @Override
            public void onResponse(Call<Categories> call, Response<Categories> response) {
                fill(response, context);
            }

            @Override
            public void onFailure(Call<Categories> call, Throwable t) {

            }
        });
    }

    private void fill(Response<Categories> response, Context context) {
        Categories c = response.body();
        assert c != null;
        ArrayList<String> categories = c.getCategories();
        categories.add(0, NO_CATEGORY);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item,
                        categories);
        setAdapter(adapter);
    }

    public String getSelectedTag() {
        String item = (String) getSelectedItem();
        if (item.equals(NO_CATEGORY)) {
            return null;
        }
        return item;
    }

    public void init(final Context context, final Article article) {
        ControllerFactory.getArticleController().listCategories().enqueue(new Callback<Categories>() {
            @Override
            public void onResponse(Call<Categories> call, Response<Categories> response) {
                fill(response, context);
                setSelectedItemFromArticle(article);
            }

            @Override
            public void onFailure(Call<Categories> call, Throwable t) {

            }
        });
    }

    private void setSelectedItemFromArticle(Article article) {
        SpinnerAdapter adapter = getAdapter();
        if (adapter == null) {
            return;
        }

        ArrayList<String> tags = article.getTags();
        if (tags == null || tags.size() == 0) {
            return;
        }

        String tag = tags.get(0);
        for (int i = 0; i < getAdapter().getCount(); i++) {
            if(getAdapter().getItem(i).equals(tag)) {
                setSelection(i);
                return;
            }
        }
    }
}
