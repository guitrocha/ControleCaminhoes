package guit.com.controlecaminhoes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import guit.com.controlecaminhoes.config.ConfiguracaoFirebase;
import guit.com.controlecaminhoes.helper.CustomAdapterHistorico;
import guit.com.controlecaminhoes.helper.UserFirebase;
import guit.com.controlecaminhoes.model.Entrega;

public class HistoricoActivity2 extends AppCompatActivity {


    private Button buscar;
    private ImageView voltar;
    private TextView filtro_tv;
    private TextView mes_tv;
    private TextView ano_tv;
    private ArrayList<String> placas_array;
    private String filtro;
    private ArrayList<String> mes_array;
    private String mes;
    private int mes_id;
    private ArrayList<String> ano_array;
    private String ano;
    private int ano_id;
    private DatabaseReference db;
    private ArrayList<Entrega> entregas_array;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico2);



        db = ConfiguracaoFirebase.getFirebase();
        placas_array = new ArrayList<>();
        placas_array.add("Geral");
        armazenarArrayPlaca();
        mes_array = new ArrayList<>();
        armazenarArrayMes();
        ano_array = new ArrayList<>();
        armazenarArrayAno();


        entregas_array = new ArrayList<>();




        voltar = (ImageView) findViewById(R.id.voltar_button_hist2);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paginaPrincipal();
            }
        });

        filtro_tv = (TextView) findViewById(R.id.filtro_dropdown);
        filtro_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFiltroDialog();
            }
        });

        mes_tv = (TextView) findViewById(R.id.mes_dropdown);
        mes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMesDialog();
            }
        });

        ano_tv = (TextView) findViewById(R.id.ano_dropdown);
        ano_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAnoDialog();
            }
        });

        buscar = (Button) findViewById(R.id.buscar_hist_button);
        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscarHistorico();
            }
        });



        init();
        armazenarArrayEntregas();


    }

    public void paginaPrincipal(){
        finish();
    }

    public void buscarHistorico(){
        if(entregas_array.isEmpty())
            Toast.makeText(HistoricoActivity2.this,"Nenhuma entrega encontrada para esse período!",Toast.LENGTH_SHORT).show();
        else{
            Collections.sort(entregas_array);

            Intent intent = new Intent(HistoricoActivity2.this,HistoricoActivity.class);
            intent.putExtra("mes",mes);
            intent.putExtra("ano",ano_id);
            intent.putExtra("filtro",filtro);
            Bundle bundle = new Bundle();
            bundle.putSerializable("array",entregas_array);
            intent.putExtras(bundle);


            startActivity(intent);
            finish();
        }

    }

    public void showFiltroDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(HistoricoActivity2.this);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, placas_array);
        TextView title = new TextView(this);
        title.setText("\nSelecione o Filtro\n");
        title.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        title.setTextSize(25);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.bold);
        title.setTypeface(typeface);
        title.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 300));
        dialog.setCustomTitle(title);
        dialog.setAdapter(dataAdapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // The 'which' argument contains the index position
                // of the selected item
                filtro = placas_array.get(which);
                filtro_tv.setText(filtro);
                armazenarArrayEntregas();

            }
        });
        AlertDialog alert = dialog.create();
        alert.show();
        alert.getWindow().setLayout(800,ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void showMesDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(HistoricoActivity2.this);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, mes_array);
        TextView title = new TextView(this);
        title.setText("\nSelecione o Mês\n");
        title.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        title.setTextSize(25);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.bold);
        title.setTypeface(typeface);
        title.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 300));
        dialog.setCustomTitle(title);
        dialog.setAdapter(dataAdapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // The 'which' argument contains the index position
                // of the selected item
                mes = mes_array.get(which);
                mes_id = which;
                mes_tv.setText(mes);
                armazenarArrayEntregas();

            }
        });
        AlertDialog alert = dialog.create();
        alert.show();
        alert.getWindow().setLayout(800,ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void showAnoDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(HistoricoActivity2.this);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, ano_array);
        TextView title = new TextView(this);
        title.setText("\nSelecione o Ano\n");
        title.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        title.setTextSize(25);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.bold);
        title.setTypeface(typeface);
        title.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 300));
        dialog.setCustomTitle(title);
        dialog.setAdapter(dataAdapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // The 'which' argument contains the index position
                // of the selected item
                ano = ano_array.get(which);
                ano_id = Integer.valueOf(ano);
                ano_tv.setText(ano);
                armazenarArrayEntregas();

            }
        });
        AlertDialog alert = dialog.create();
        alert.show();
        alert.getWindow().setLayout(800,ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void armazenarArrayPlaca(){
        db = db.getRoot();
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
                    Toast.makeText(HistoricoActivity2.this,"Nenhum caminhão cadastrado!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void armazenarArrayMes(){
        mes_array.add("Janeiro");
        mes_array.add("Fevereiro");
        mes_array.add("Março");
        mes_array.add("Abril");
        mes_array.add("Maio");
        mes_array.add("Junho");
        mes_array.add("Julho");
        mes_array.add("Agosto");
        mes_array.add("Setembro");
        mes_array.add("Outubro");
        mes_array.add("Novembro");
        mes_array.add("Dezembro");
    }

    private void armazenarArrayAno(){
        db = db.getRoot();
        String user = UserFirebase.getCurrentUserEmail();
        db = db.child("Usuarios").child(user).child("historico");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for(DataSnapshot ds: dataSnapshot.getChildren()) {
                        String ano_fb = ds.child("ano").getValue().toString();
                        if(!ano_array.contains(ano_fb))
                            ano_array.add(ano_fb);
                    }
                } else {
                    Toast.makeText(HistoricoActivity2.this,"Nenhuma entrega cadastrada!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void armazenarArrayEntregas(){
        entregas_array.clear();
        if(filtro.equals("Geral")) {
            db = db.getRoot();
            String user = UserFirebase.getCurrentUserEmail();
            db = db.child("Usuarios").child(user).child("historico");
            Query query = db.orderByChild("mes").equalTo(mes_id+1);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds: dataSnapshot.getChildren()) {
                        Entrega linha = ds.getValue(Entrega.class);
                        if(linha.getAno() == ano_id)
                            entregas_array.add(linha);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(HistoricoActivity2.this,"Nenhuma entrega encontrada para esse período!",Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            db = db.getRoot();
            String user = UserFirebase.getCurrentUserEmail();
            db = db.child("Usuarios").child(user).child("historico");
            Query query = db.orderByChild("placa").equalTo(filtro);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds: dataSnapshot.getChildren()) {
                        Entrega linha = ds.getValue(Entrega.class);
                        if(linha.getAno() == ano_id)
                            if(linha.getMes() == mes_id+1)
                                entregas_array.add(linha);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(HistoricoActivity2.this,"Nenhuma entrega encontrada para esse período!",Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    private void init(){
        filtro = placas_array.get(0);
        filtro_tv.setText(filtro);

        mes_id = Calendar.getInstance().get(Calendar.MONTH);
        mes = mes_array.get(mes_id);
        mes_tv.setText(mes);

        ano_id = Calendar.getInstance().get(Calendar.YEAR);
        //ano = ano_array.get(ano_id);
        ano_tv.setText(String.valueOf(ano_id));
    }

}
