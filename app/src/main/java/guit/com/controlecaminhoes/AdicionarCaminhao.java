package guit.com.controlecaminhoes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import guit.com.controlecaminhoes.config.ConfiguracaoFirebase;
import guit.com.controlecaminhoes.helper.UserFirebase;
import guit.com.controlecaminhoes.model.Usuario;

public class AdicionarCaminhao extends AppCompatActivity {

    private ImageView voltar;
    private EditText placa_box;
    private Button add;
    private String placa;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_caminhao);
        voltar = (ImageView) findViewById(R.id.voltar_add_button);
        placa_box = (EditText) findViewById(R.id.placa_box_add);
        add = (Button) findViewById(R.id.cadastrar_add_button);

        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telaGerenciar();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                metodoAdicionar();
            }
        });
    }

    public void metodoAdicionar(){
        placa = placa_box.getText().toString();
        if(placa.isEmpty()){
            Toast.makeText(AdicionarCaminhao.this,"Informe uma placa!",Toast.LENGTH_SHORT).show();
        } else {
            db = ConfiguracaoFirebase.getFirebase();
            String user = UserFirebase.getCurrentUserEmail();
            db = db.child("Usuarios").child(user);
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Usuario usuario = dataSnapshot.getValue(Usuario.class);
                    int qt = usuario.getQt_caminhoes() + 1;
                    db.child("qt_caminhoes").setValue(qt);
                    db.child("caminhao").child(placa).child("placa").setValue(placa);
                    telaPrincipal();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }
    }

    public void telaPrincipal(){
        //Intent intent = new Intent(AdicionarCaminhao.this, MainActivity.class);
        //startActivity(intent);
        finish();
    }

    public void telaGerenciar(){
        Intent intent = new Intent(AdicionarCaminhao.this, GerenciarCaminhao.class);
        startActivity(intent);
        finish();
    }
}
