package ar.uba.fi.mercadolibre.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Arrays;
import java.util.List;

import ar.uba.fi.mercadolibre.client.RetrofitClient;
import ar.uba.fi.mercadolibre.controller.APIResponse;
import ar.uba.fi.mercadolibre.controller.ControllerFactory;
import ar.uba.fi.mercadolibre.controller.InvalidResponseException;
import ar.uba.fi.mercadolibre.model.Account;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {
    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    void setFirebaseLoginActivity() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Esto se ejecuta luego del (intento de) sign in del usuario
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                processSuccessSignin();

            } else {
                Log.e("Firebase login API call", response.getError().getMessage());
            }
        }
    }

    private void processSuccessSignin() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        user.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            processInstanceId();

                        } else {
                            String msg = task.getException().getLocalizedMessage();
                            if (msg != null) {
                                Log.e("Firebase Signin", msg);

                            }
                        }
                    }
                });
    }

    private void processInstanceId() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                postInstanceIdToAccount(instanceIdResult.getToken());

            }
        });
    }

    private void postInstanceIdToAccount(final String id) {
        ControllerFactory.getAccountController().currentAccount().enqueue(new Callback<APIResponse<Account>>() {
            @Override
            public void onResponse(Call<APIResponse<Account>> call, Response<APIResponse<Account>> response) {
                if (response.isSuccessful()) {
                    try {
                        doAccountUpdateAndShowMainMenu(response.body().getData(), id);
                    } catch (InvalidResponseException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<APIResponse<Account>> call, Throwable t) {

            }
        });
    }

    private void doAccountUpdateAndShowMainMenu(final Account account, final String id) {
        account.setInstanceId(id);
        ControllerFactory.getAccountController().updateCurrentAccount(account).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    // String idToken = task.getResult().getToken();
                    startActivity(new Intent(getApplicationContext(), MainMenuActivity.class));
                    // ...

                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {

            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.getIdToken(true).addOnSuccessListener(
                    new OnSuccessListener<GetTokenResult>() {
                        @Override
                        public void onSuccess(GetTokenResult result) {
                            RetrofitClient.firebaseToken = result.getToken();
                        }
                    });
            startActivity(new Intent(getApplicationContext(), MainMenuActivity.class));
            finish();
        } else {  // Firebase login view
            this.setFirebaseLoginActivity();
        }
    }
}
