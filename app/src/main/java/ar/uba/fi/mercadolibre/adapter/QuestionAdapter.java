package ar.uba.fi.mercadolibre.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.model.Question;

public class QuestionAdapter extends ArrayAdapter<Question> {


    public QuestionAdapter(@NonNull Context context, List<Question> data) {
        super(context, 0, data);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Question question = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater
                    .from(getContext())
                    .inflate(R.layout.item_question, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.question)).setText(question.getQuestion());
        ((TextView) convertView.findViewById(R.id.answer)).setText(question.getAnswer());
        ((TextView) convertView.findViewById(R.id.questionDate)).setText(question.getCreatedAt().toString());
        return convertView;
    }

}
