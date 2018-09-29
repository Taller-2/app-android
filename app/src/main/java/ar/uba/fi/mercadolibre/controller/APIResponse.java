package ar.uba.fi.mercadolibre.controller;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Represents wrapped responses of the form:
 * { "ok": true, "data": ... }
 */
public class APIResponse<Data> {
    @SerializedName("ok")
    @Expose
    private Boolean ok = false;

    @SerializedName("data")
    @Expose
    private Data data = null;

    @NonNull
    public Data getData() throws InvalidResponseException {
        if (!ok || data == null) {
            throw new InvalidResponseException();
        }
        return data;
    }
}
