package ar.uba.fi.mercadolibre.utils;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class FirebaseImageManager {
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    public void upload(Uri fileUri, String destinationPath) {
        upload(fileUri, destinationPath, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d("Firebase upload",
                                "Image upload to firebase successful");
                    }
                }
        );
    }

    public void upload(Uri fileUri, String destinationPath, OnSuccessListener<UploadTask.TaskSnapshot> onSuccess) {
        StorageReference storageRef = storage.getReference();

        UploadTask task = storageRef.child(destinationPath).putFile(fileUri);
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("Firebase upload",
                        "Image upload to firebase failed: " + exception.getMessage());
            }
        }).addOnSuccessListener(onSuccess);
    }

    public void loadImageInto(final String path, final ImageView imageView) {
        getDownloadUrl(path, new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri)
                        .resize(imageView.getWidth(), imageView.getHeight())
                        .into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Firebase download",
                        "Error loading image: ref path " + path + ". Exception " + e.getMessage());
            }
        });
    }

    public Task<Uri> getDownloadUrl(String path, OnSuccessListener<Uri> callback) {
        StorageReference storageRef = storage.getReference();
        StorageReference pic = storageRef.child(path);
        return pic.getDownloadUrl().addOnSuccessListener(callback);
    }
}
