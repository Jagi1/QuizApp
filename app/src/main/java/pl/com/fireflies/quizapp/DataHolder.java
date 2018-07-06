package pl.com.fireflies.quizapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Klasa w której trzymane są zmienne wykorzystywane w wielu aktywnościach.
 * Taki pojemnik na zmienne globalne (Singleton).
 */

public class DataHolder {
    public static final int PICK_IMAGE_REQUEST = 71, STORAGE_PERMISSION_CODE = 1;
    private static final DataHolder singleton = new DataHolder();

    // Autoryzacja logowania
    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    // Pobieranie informacji o zalogowanym uzytkowniku
    public static FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private static FirebaseStorage storage = FirebaseStorage.getInstance();
    // Wskaznik do Storage w Firebase (informacje o uzytkownikach)
//    public static StorageReference storageReference = storage.getReferenceFromUrl("gs://quiza2018.appspot.com");
    public static StorageReference storageReference = storage.getReference();
    // Wskaznik do Database w Firebase (baza z quizami)
    public static DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
    // Wybór motywu aplikacji
    public boolean dark_theme = false;
    public boolean theme_changed = false;
    // Czy chcemy ponownie zagrać
    public boolean play_again = false;
    // zmienna Bitmap przechowuje zdjecie zaladowane z serwera
    public Bitmap avatarBitmap;

    public static final DataHolder getInstance() {
        return singleton;
    }

    // ustawianie zmiennej Bitmap (pobieranie obrazka ze Storage) TODO (raz dziala, a raz nie)
    public static void setAvatarImage() {
        final long ONE_MEGABYTE = 1024 * 1024;
        final StorageReference refStoragePath = storageReference.child("user")
                .child(firebaseUser.getUid()).child("avatarImage.jpg");

        refStoragePath.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                DataHolder.getInstance().avatarBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                // imageView.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("ERROR_avatar", "blad");
            }
        });
    }

    public static void requestStoragePermission(final Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(activity)
                    .setTitle("Wymagane pozwolenie")
                    .setMessage("Pozwolenie jest potrzebne aby wybierac zdjecia.")
                    .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    }).setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

}
