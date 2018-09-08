package ar.uba.fi.mercadolibre.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.util.Arrays;
import java.util.List;

import ar.uba.fi.mercadolibre.server_api.AppServer;
import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity {
    // No se como leer esto desde una env variable o lo que sea
    static final String APP_SERVER_URL = "http://0.0.0.0:8000";

    protected CompositeDisposable compositeDisposable;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    void setFirebaseLoginActivity() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
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
                            // String idToken = task.getResult().getToken();
                            startActivity(new Intent(getApplicationContext(), MainMenuActivity.class));
                            // ...
                        } else {
                            String msg = task.getException().getLocalizedMessage();
                            if (msg != null) {
                                Log.e("Firebase Signin", msg);

                            }
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(getApplicationContext(), MainMenuActivity.class));
        } else {  // Firebase login view
            this.setFirebaseLoginActivity();
        }

        // SAMPLE API CALL!
        AppServer as = new AppServer(APP_SERVER_URL);
        as.api();

    }

    @Override
    protected void onDestroy() {
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
        super.onDestroy();
    }
}
