package pl.com.fireflies.quizapp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Klasa w której trzymane są zmienne wykorzystywane w wielu aktywnościach.
 * Taki pojemnik na zmienne globalne (Singleton).
 * */

public class DataHolder
{
    private static final DataHolder singleton = new DataHolder();
    public static final DataHolder getInstance()
    {
        return singleton;
    }

    // Autoryzacja logowania
    public FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    // Pobieranie informacji o zalogowanym uzytkowniku
    public FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    // Wskaznik do Storage w Firebase (informacje o uzytkownikach)
    public StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    // Wskaznik do Database w Firebase (baza z quizami)
    public DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
    // Wybór motywu aplikacji
    public boolean dark_theme = false;
    public boolean theme_changed = false;
    //

}
