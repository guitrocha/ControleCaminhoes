package guit.com.controlecaminhoes.helper;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import guit.com.controlecaminhoes.config.ConfiguracaoFirebase;
import guit.com.controlecaminhoes.model.Entrega;

public class PagamentoTotal {
    public static void atualizar(){
        DatabaseReference db = ConfiguracaoFirebase.getFirebase();
        String user = UserFirebase.getCurrentUserEmail();
        db = db.child("Usuarios").child(user).child("historico");
        Query query = db.orderByChild("estaPago").equalTo(0);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double total = 0.0;
                for(DataSnapshot ds: dataSnapshot.getChildren()) {
                    Entrega linha = ds.getValue(Entrega.class);
                    total += linha.getValor();
                }
                dataSnapshot.getRef().getParent().child("pag_atual").setValue(total);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
