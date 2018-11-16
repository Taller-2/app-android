package ar.uba.fi.mercadolibre.views;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.model.Article;
import ir.apend.slider.model.Slide;
import ir.apend.slider.ui.Slider;

public class ArticleSlider extends Slider {

    public ArticleSlider(@NonNull Context context) {
        super(context);
    }

    public ArticleSlider(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ArticleSlider(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(Article article, int corner) {
        removeAllViews();
        ArrayList<String> pictures = article.getPictureURLs();
        if (pictures == null || pictures.size() == 0) {
            ImageView blank = new ImageView(getContext());
            blank.setImageResource(R.drawable.ic_menu_camera);
            addView(blank);
            return;
        }

        List<Slide> slideList = new ArrayList<>();
        for (int i = 0; i < article.getPictureURLs().size(); i++) {
            String pictureURL = article.getPictureURLs().get(i);
            slideList.add(new Slide(i, pictureURL, corner));

        }
        addSlides(slideList);
    }

}
