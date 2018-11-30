package ar.uba.fi.mercadolibre.controller;

import java.util.List;

import ar.uba.fi.mercadolibre.model.ChatMessage;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ChatMessageController {
    @GET("chat_message/{room}/")
    Call<APIResponse<List<ChatMessage>>> list(@Path("room") String room);

    @POST("chat_message/{room}/")
    Call<Object> create(@Path("room") String room, @Body ChatMessage message);
}
