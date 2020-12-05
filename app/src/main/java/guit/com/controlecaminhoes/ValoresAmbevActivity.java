package guit.com.controlecaminhoes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import guit.com.controlecaminhoes.config.ConfiguracaoFirebase;
import guit.com.controlecaminhoes.helper.CustomAdapterAmbev;
import guit.com.controlecaminhoes.helper.DinheiroFormat;
import guit.com.controlecaminhoes.helper.UserFirebase;

public class ValoresAmbevActivity extends AppCompatActivity {

    private ImageView voltar;
    private ImageView prev;
    private ImageView next;
    private TextSwitcher regiao;
    private TextSwitcher valor;
    private int regiao_atual;
    private String valor_atual;
    private DatabaseReference db;
    private RecyclerView rc;
    private ArrayList<String> locais_array;
    private CustomAdapterAmbev customAdapterAmbev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valores_ambev);

        voltar = (ImageView) findViewById(R.id.voltar_button_abv);
        prev = (ImageView) findViewById(R.id.regiao_prev_button);
        next = (ImageView) findViewById(R.id.regiao_next_button);
        regiao = (TextSwitcher) findViewById(R.id.regiao_page_switcher);
        valor = (TextSwitcher) findViewById(R.id.valor_switcher_abv);
        regiao.setInAnimation(ValoresAmbevActivity.this, android.R.anim.fade_in);
        regiao.setOutAnimation(ValoresAmbevActivity.this, android.R.anim.fade_out);
        valor.setInAnimation(ValoresAmbevActivity.this, android.R.anim.fade_in);
        valor.setOutAnimation(ValoresAmbevActivity.this, android.R.anim.fade_out);
        db = ConfiguracaoFirebase.getFirebase();
        db = db.child("Tabela Abv").child("Regiao");

        displayRegiao(1);

        locais_array = new ArrayList<>();
        armazenarArray();
        customAdapterAmbev = new CustomAdapterAmbev(ValoresAmbevActivity.this,locais_array);
        rc = (RecyclerView) findViewById(R.id.tabela_ambev);
        rc.setAdapter(customAdapterAmbev);
        rc.setLayoutManager(new LinearLayoutManager(ValoresAmbevActivity.this));


        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paginaPrincipal();
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regiaoPrev();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regiaoNext();
            }
        });
    }

    private void paginaPrincipal(){
        //Intent intent = new Intent(ValoresAmbevActivity.this, MainActivity.class);
        //startActivity(intent);
        finish();
    }

    private void armazenarArray(){
        locais_array.clear();
        db.child(String.valueOf(regiao_atual)).child("Local").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //int i = 0;
                if (dataSnapshot.getValue() != null) {
                    for(DataSnapshot ds: dataSnapshot.getChildren()) {
                        String local = ds.getValue().toString();
                        locais_array.add(local);
                        //i++;
                    }
                } else {
                    Toast.makeText(ValoresAmbevActivity.this,"Nenhum local cadastrado!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void displayRegiao(int page){
        regiao_atual = page;
        regiao.setText(String.valueOf(regiao_atual));
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String aux = dataSnapshot.child(String.valueOf(regiao_atual)).child("Valor").getValue().toString();
                double toConvert = Double.valueOf(aux);
                valor_atual = DinheiroFormat.converter(toConvert);
                valor.setText(valor_atual);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void regiaoPrev(){
        if(regiao_atual == 1){
            regiao_atual = 4;
        } else {
            regiao_atual = regiao_atual - 1;
        }
        displayRegiao(regiao_atual);
        armazenarArray();
        customAdapterAmbev.notifyDataSetChanged();
    }

    private void regiaoNext(){
        if(regiao_atual == 4) {
            regiao_atual = 1;
        } else {
            regiao_atual = regiao_atual + 1;
        }
        displayRegiao(regiao_atual);
        armazenarArray();
        customAdapterAmbev.notifyDataSetChanged();
    }
}
