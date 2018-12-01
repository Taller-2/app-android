package ar.uba.fi.mercadolibre.model;

import android.annotation.SuppressLint;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseModel implements Serializable {
    @SerializedName("_id")
    @Expose
    private String id;

    BaseModel() {
    }

    public String getID() {
        return id;
    }

    Date parseTimestamp(String timestamp) throws ParseException {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat mongoFormat = new SimpleDateFormat(
                "yyyy-MM-dd' 'HH:mm:ss.SSSSSSZ"
        );
        return mongoFormat.parse(timestamp + "+00:00");
    }
}
