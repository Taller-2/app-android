package ar.uba.fi.mercadolibre.utils;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.model.Article;
import ir.apend.slider.model.Slide;
import ir.apend.slider.ui.Slider;

public class ArticleImageSliderManager {
    private Slider slider;
    private Article article;

    public ArticleImageSliderManager(Slider slider, Article article) {
        this.slider = slider;
        this.article = article;
    }

    public void init(Context context, int imageCorner) {
        slider.removeAllViews();
        ArrayList<String> pictures = article.getPictureURLs();
        if (pictures == null || pictures.size() == 0) {
            ImageView blank = new ImageView(context);
            blank.setImageResource(R.drawable.ic_menu_camera);
            slider.addView(blank);
            return;
        }

        List<Slide> slideList = new ArrayList<>();
        for (int i = 0; i < article.getPictureURLs().size(); i++) {
            String pictureURL = article.getPictureURLs().get(i);
            slideList.add(new Slide(i, pictureURL, imageCorner));

        }
        slider.addSlides(slideList);
    }

}
