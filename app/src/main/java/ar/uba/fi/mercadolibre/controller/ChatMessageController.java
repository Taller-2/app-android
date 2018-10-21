package ar.uba.fi.mercadolibre.controller;

import java.util.List;

import ar.uba.fi.mercadolibre.model.ChatMessage;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ChatMessageController {
    @GET("chat_message/")
    Call<APIResponse<List<ChatMessage>>> list();

    @POST("chat_message/")
    Call<Object> create(@Body ChatMessage message);
}
