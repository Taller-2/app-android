package ar.uba.fi.mercadolibre.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.activity.AnswerQuestionActivity;
import ar.uba.fi.mercadolibre.model.Article;
import ar.uba.fi.mercadolibre.model.Question;

public class UserQuestionAdapter extends ArrayAdapter<Question> {


    public UserQuestionAdapter(@NonNull Context context, List<Question> data) {
        super(context, 0, data);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final Question question = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater
                    .from(getContext())
                    .inflate(R.layout.item_user_question, parent, false);
        }
        Article article = question.getArticle();
        assert article != null;
        ((TextView) convertView.findViewById(R.id.item_name)).setText(article.getName());

        ImageView image = convertView.findViewById(R.id.list_article_image);
        if (!article.getPictureURLs().isEmpty()) {
            loadImage(image, article);
        } else {
            image.setImageResource(R.drawable.ic_camera_alt_black_24dp);
        }

        ((TextView) convertView.findViewById(R.id.question)).setText(question.getQuestion());
        ((TextView) convertView.findViewById(R.id.answer)).setText(question.getAnswer());
        ((TextView) convertView.findViewById(R.id.questionDate)).setText(question.getCreatedAt().toString());

        final Context context = getContext();
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (question.getAnswer() != null) return;
                Intent i = new Intent(context, AnswerQuestionActivity.class);
                i.putExtra("question", question);
                context.startActivity(i);
            }
        });
        return convertView;
    }

    private void loadImage(final ImageView imageView, Article a) {
        final String path = a.getPictureURLs().get(0);
        Picasso
                .get()
                .load(path)
                .resize(125, 125)
                .centerCrop()
                .into(imageView);
    }

}
