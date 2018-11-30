package ar.uba.fi.mercadolibre.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChatMessage {
    @SerializedName("text")
    @Expose
    private String text;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("purchase_id")
    @Expose
    private String purchaseId;

    @SerializedName("sender_user_id")
    @Expose
    private String senderUserID;

    public ChatMessage(String text, String name, String senderUserID) {
        this.text = text;
        this.name = name;
        this.senderUserID = senderUserID;
    }

    public ChatMessage(String text, String purchaseId) {
        this.text = text;
        this.purchaseId = purchaseId;
    }

    public String getText() {
        return text;
    }

    public String getName() {
        return name;
    }

    public String senderUserID() {
        return senderUserID;
    }
}
