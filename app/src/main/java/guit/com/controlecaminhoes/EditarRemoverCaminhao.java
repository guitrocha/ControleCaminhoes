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

public class EditarRemoverCaminhao extends AppCompatActivity {

    private ImageView voltar;
    private EditText placa_box;
    private Button update;
    private Button remove;
    private String placa;
    private String placa_nova;
    private DatabaseReference db;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_remover_caminhao);

        voltar = (ImageView) findViewById(R.id.voltar_up_button);
        placa_box = (EditText) findViewById(R.id.placa_box_up);
        update = (Button) findViewById(R.id.atualizar_up_button);
        remove = (Button) findViewById(R.id.rmv_up_button);
        getSetIntentData();
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                atualizarCaminhao();
            }
        });
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removerCaminhao();
            }
        });
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telaGerenciar();
            }
        });
    }
    public void telaGerenciar(){
        Intent intent = new Intent(EditarRemoverCaminhao.this, GerenciarCaminhao.class);
        startActivity(intent);
        finish();
    }
    public void telaPrincipal(){
        //Intent intent = new Intent(EditarRemoverCaminhao.this, MainActivity.class);
        //startActivity(intent);
        finish();
    }
    void getSetIntentData(){
        if(getIntent().hasExtra("placa")){
            position = Integer.valueOf(getIntent().getStringExtra("position"));
            placa = getIntent().getStringExtra("placa");
            placa_box.setText(placa);
        }
    }

    void atualizarCaminhao(){
        placa_nova = placa_box.getText().toString();
        if(placa_nova.isEmpty()){
            Toast.makeText(EditarRemoverCaminhao.this,"Informe uma placa!",Toast.LENGTH_SHORT).show();
        } else {
            db = ConfiguracaoFirebase.getFirebase();
            String user = UserFirebase.getCurrentUserEmail();
            db = db.child("Usuarios").child(user);
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    db.child("caminhao").child(placa).removeValue();
                    db.child("caminhao").child(placa_nova).child("placa").setValue(placa_nova);

                    telaPrincipal();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }
    }
    void removerCaminhao(){

        db = ConfiguracaoFirebase.getFirebase();
        String user = UserFirebase.getCurrentUserEmail();
        db = db.child("Usuarios").child(user);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                int qt = usuario.getQt_caminhoes() - 1;
                db.child("qt_caminhoes").setValue(qt);
                db.child("caminhao").child(placa).removeValue();
                telaPrincipal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
