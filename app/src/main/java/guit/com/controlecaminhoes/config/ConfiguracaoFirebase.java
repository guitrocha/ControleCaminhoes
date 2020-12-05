package guit.com.controlecaminhoes.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public final class ConfiguracaoFirebase {

    private static DatabaseReference referenciafirebase;
    private static FirebaseAuth autenticacao;

    public static DatabaseReference getFirebase(){
        if(referenciafirebase == null)
            referenciafirebase = FirebaseDatabase.getInstance().getReference();

        return referenciafirebase;
    }

    public static FirebaseAuth getFirebaseAutenticacao(){
        if(autenticacao == null)
            autenticacao = FirebaseAuth.getInstance();

        return autenticacao;
    }
}
