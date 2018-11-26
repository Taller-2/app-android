package ar.uba.fi.mercadolibre.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.fasterxml.jackson.databind.JsonNode;
import com.scaledrone.lib.Listener;
import com.scaledrone.lib.Member;
import com.scaledrone.lib.Room;
import com.scaledrone.lib.RoomListener;
import com.scaledrone.lib.Scaledrone;

import java.util.List;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.adapter.ChatMessageAdapter;
import ar.uba.fi.mercadolibre.controller.APIResponse;
import ar.uba.fi.mercadolibre.controller.ControllerFactory;
import ar.uba.fi.mercadolibre.model.Account;
import ar.uba.fi.mercadolibre.model.ChatMessage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatActivity extends BaseActivity implements RoomListener {
    private final static String channelID = "xFKSnmBDfC3SyNTj";
    private String roomName;
    private Scaledrone client;
    private ChatMessageAdapter messageAdapter;
    private ListView messagesView;
    private boolean connected = false;
    private Account currentAccount;

    @Override
    public int identifierForDrawer() {
        return CHAT_IDENTIFIER;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        roomName = "observable-" + getIntent().getExtras().get("chat_room");
        setContentView(R.layout.activity_chat);
        ControllerFactory.getAccountController().currentAccount().enqueue(new Callback<APIResponse<Account>>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<Account>> call,
                                   @NonNull Response<APIResponse<Account>> response) {
                currentAccount = getData(response);
                if (currentAccount == null) {
                    finish();
                    return;
                }
                initMessages();
                connectToClient();
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse<Account>> call,
                                  @NonNull Throwable t) {
                onGetDataFailure(t);
            }
        });
    }

    private void initMessages() {
        ControllerFactory.getChatMessageController().list(roomName).enqueue(new Callback<APIResponse<List<ChatMessage>>>() {
            @Override
            public void onResponse(Call<APIResponse<List<ChatMessage>>> call,
                                   Response<APIResponse<List<ChatMessage>>> response) {
                List<ChatMessage> messages = getData(response);
                if (messages == null) return;
                messagesView = findViewById(R.id.messages_view);
                messageAdapter = new ChatMessageAdapter(
                        getApplicationContext(),
                        messages,
                        currentAccount
                );
                messagesView.setAdapter(messageAdapter);
                scrollToBottom();
            }

            @Override
            public void onFailure(Call<APIResponse<List<ChatMessage>>> call,
                                  Throwable t) {
                onGetDataFailure(t);
            }
        });
    }

    public void connectToClient() {
        client = new Scaledrone(channelID, currentAccount);
        client.connect(new Listener() {
            @Override
            public void onOpen() {
                client.subscribe(roomName, ChatActivity.this);
                connected = true;
            }

            @Override
            public void onOpenFailure(Exception ex) {
                Log.e("ChatActivity", "onOpenFailure (Listener)", ex);
                finish();
            }

            @Override
            public void onFailure(Exception ex) {
                Log.e("ChatActivity", "onFailure", ex);
                finish();
            }

            @Override
            public void onClosed(String reason) {
                Log.e("ChatActivity", "onClosed: " + reason);
                finish();
            }
        });
    }

    @Override
    public void onOpen(Room room) {
        Log.d("ChatActivity", "onOpen (activity), room " + room.getName());
    }

    @Override
    public void onOpenFailure(Room room, Exception ex) {
        Log.e("ChatActivity", "onOpenFailure (activity)", ex);
        finish();
    }

    @Override
    public void onMessage(Room room, JsonNode messageText, Member member) {
        JsonNode data = member.getClientData();
        String senderName = null;
        String senderUserID = null;
        if (data != null) {
            senderName = data.get("name").asText();
            senderUserID = data.get("userID").asText();
        }
        if (senderName == null || senderName.length() == 0) {
            senderName = "<unknown>";
        }
        if (shouldIgnore(senderUserID)) return;
        final ChatMessage message = new ChatMessage(
                messageText.asText(),
                senderName,
                senderUserID
        );
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (messageAdapter == null) {
                    Log.e("onMessage", "messageAdapter was null");
                    return;
                }
                messageAdapter.add(message);
                scrollToBottom();
            }
        });
    }

    private void scrollToBottom() {
        messagesView.setSelection(messagesView.getCount() - 1);
    }

    private boolean shouldIgnore(String senderUserID) {
        // We can use senderUserID to filter senders, and then by also
        // adding transactionID to the data that is sent alongside a message,
        // we can filter by purchase too
        return false;
    }

    public void sendMessage(View view) {
        if (!connected) return;
        EditText editText = findViewById(R.id.editText);
        String message = editText.getText().toString();
        if (message.length() == 0) return;
        client.publish(roomName, message);
        editText.getText().clear();
        saveMessage(new ChatMessage(
                message,
                currentAccount.getName(),
                currentAccount.getUserID()
        ));
    }

    private void saveMessage(ChatMessage message) {
        ControllerFactory.getChatMessageController().create(roomName, message).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call,
                                   Response<Object> response) {
                if (response.isSuccessful()) return;
                onFailure(call, new Exception("Unsuccessful response"));
            }

            @Override
            public void onFailure(Call<Object> call,
                                  Throwable t) {
                Log.e("ChatActivity", "Create Chat Message", t);
            }
        });
    }
}
