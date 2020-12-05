package guit.com.controlecaminhoes.helper;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import guit.com.controlecaminhoes.config.ConfiguracaoFirebase;
import guit.com.controlecaminhoes.model.Usuario;

public class UserFirebase {

    public static String getCurrentUserEmail(){
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        String email = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
        return email;
    }

}
