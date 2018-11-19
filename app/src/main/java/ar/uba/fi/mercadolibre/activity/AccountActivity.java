package ar.uba.fi.mercadolibre.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.UploadTask;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.controller.APIResponse;
import ar.uba.fi.mercadolibre.controller.ControllerFactory;
import ar.uba.fi.mercadolibre.model.Account;
import ar.uba.fi.mercadolibre.utils.FirebaseImageManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountActivity extends BaseActivity {
    private static final int GET_FROM_GALLERY = 3;
    Account account = null;

    @Override
    public int identifierForDrawer() {
        return MY_ACCOUNT_IDENTIFIER;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_detail);

        ControllerFactory.getAccountController().currentAccount().enqueue(new Callback<APIResponse<Account>>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<Account>> call, @NonNull Response<APIResponse<Account>> response) {
                account = getData(response);
                if (account == null) return;
                updateShownAccount(account);
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse<Account>> call, @NonNull Throwable t) {
                onGetDataFailure(t);
            }
        });
    }

    private void updateShownAccount(Account account) {
        fillTextView(R.id.email, account.getEmail());
        fillTextView(R.id.name, account.getName());
        fillTextView(R.id.score, Integer.toString(account.getScore()));
        String pictureURL = account.getProfilePictureURL();
        if (pictureURL == null) {
            return;
        }
        new FirebaseImageManager().loadImageInto(
                account.getProfilePictureURL(),
                (ImageView) findViewById(R.id.picture)
        );
    }

    public void onChangeName(View view) {
        onChange("name", R.string.name);
    }

    public void onChangeEmail(View view) {
        onChange("email", R.string.email);
    }

    private void onChange(final String attributeName, int alertTitle) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);

        alert.setView(input);
        alert.setTitle(alertTitle);
        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int button) {
                String inputText = input.getText().toString();
                if (attributeName.equals("name")) {
                    account.setName(inputText);
                } else if (attributeName.equals("email")) {
                    account.setEmail(inputText);
                }
                updateCurrentAccount();
            }
        });
        alert.show();
    }

    private void updateCurrentAccount() {
        ControllerFactory.getAccountController().updateCurrentAccount(account).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (!response.isSuccessful()) {
                    onChangeError(response.toString());
                    return;
                }
                updateShownAccount(account);
                toast(R.string.generic_success);
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                onChangeError(t.getMessage());
            }
        });
    }

    private void onChangeError(String message) {
        Log.e("Account PATCH", message);
        toast(R.string.generic_error);
        finish();
    }

    public void onChangePicture(View view) {
        startActivityForResult(
                new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI
                ),
                GET_FROM_GALLERY
        );
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != GET_FROM_GALLERY) return;
        if (resultCode != Activity.RESULT_OK) return;

        final Uri fileUri = data.getData();
        if (fileUri == null) return;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        final String destinationPath = user.getUid() + "/account/profilePicture";
        new FirebaseImageManager().upload(fileUri, destinationPath, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                account.setProfilePictureURL(destinationPath);
                updateCurrentAccount();
                updateShownAccount(account);
            }
        });
    }
}
