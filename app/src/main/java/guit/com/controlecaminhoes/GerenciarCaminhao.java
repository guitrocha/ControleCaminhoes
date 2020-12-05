package guit.com.controlecaminhoes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import guit.com.controlecaminhoes.config.ConfiguracaoFirebase;
import guit.com.controlecaminhoes.helper.CustomAdapterGerencia;
import guit.com.controlecaminhoes.helper.UserFirebase;
import guit.com.controlecaminhoes.model.Usuario;

public class GerenciarCaminhao extends AppCompatActivity {

    private DatabaseReference db;
    private LinearLayout add;
    private ImageView voltar;
    private ArrayList<String> placas_array;
    private CustomAdapterGerencia customAdapterGerencia;
    private RecyclerView rc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerenciar_caminhao);

        placas_array = new ArrayList<>();
        armazenarArray();
        customAdapterGerencia = new CustomAdapterGerencia(GerenciarCaminhao.this,placas_array);
        rc = (RecyclerView) findViewById(R.id.ger_recycler_view);
        rc.setAdapter(customAdapterGerencia);
        rc.setLayoutManager(new LinearLayoutManager(GerenciarCaminhao.this));


        voltar = (ImageView) findViewById(R.id.voltar_button_ger);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paginaPrincipal();
            }
        });

        add = (LinearLayout) findViewById(R.id.bt1);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telaAdicionar();
            }
        });

    }

    private void paginaPrincipal(){
        //Intent intent = new Intent(GerenciarCaminhao.this, MainActivity.class);
        //startActivity(intent);
        finish();
    }

    private void telaAdicionar(){
        Intent intent = new Intent(GerenciarCaminhao.this, AdicionarCaminhao.class);
        startActivity(intent);
        finish();
    }

    private void armazenarArray(){
        db = ConfiguracaoFirebase.getFirebase();
        String user = UserFirebase.getCurrentUserEmail();
        db = db.child("Usuarios").child(user).child("caminhao");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for(DataSnapshot ds: dataSnapshot.getChildren()) {
                        String placa = ds.child("placa").getValue().toString();
                        placas_array.add(placa);
                    }
                } else {
                    Toast.makeText(GerenciarCaminhao.this,"Nenhum caminhão cadastrado!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    String placa;

    private void metodoAdicionar(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(GerenciarCaminhao.this);
        dialog.setTitle("Novo caminhão");
        dialog.setMessage("Placa:");
        final EditText placa_box = new EditText(GerenciarCaminhao.this);
        dialog.setView(placa_box);
        dialog.setCancelable(false);
        dialog.setPositiveButton("Adicionar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                placa = placa_box.getText().toString();
                if(placa.isEmpty()){
                    Toast.makeText(GerenciarCaminhao.this,"Informe uma placa!",Toast.LENGTH_SHORT).show();
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
                            db.child("caminhao").child(String.valueOf(qt)).child("placa").setValue(placa);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
            }
        });
        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.create();
        dialog.show();
    }
}
