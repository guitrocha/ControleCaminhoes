package guit.com.controlecaminhoes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import guit.com.controlecaminhoes.config.ConfiguracaoFirebase;
import guit.com.controlecaminhoes.helper.DataFormat;
import guit.com.controlecaminhoes.helper.DinheiroFormat;
import guit.com.controlecaminhoes.helper.PagamentoTotal;
import guit.com.controlecaminhoes.helper.UserFirebase;
import guit.com.controlecaminhoes.model.Entrega;
import guit.com.controlecaminhoes.model.Local;

public class NovaEntregaActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private ImageView voltar;
    private EditText mapa_et;
    private TextView data_tv;
    private TextView placa_tv;
    private TextView local_tv;
    private Button adicionar;
    private String data;
    private String placa;
    private ArrayList<Integer> local;
    private ArrayList<String> placas_array;
    private String[] locais_list;
    private ArrayList<String> regiao;
    private String regiao_final;
    private double valor_aux;
    private ArrayList<Double> valores;
    private double valor_final = 0;
    private ArrayList<Local> locais_array;
    private DatabaseReference db;
    private boolean[] checkedItems;
    private String auxiliar_i;
    private double update_pagamento;
    private String user;

    private int ano, mes, dia;
    private Entrega entrega;

    public void HideKeyboard(View view){
        InputMethodManager ipm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        ipm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_entrega);

        voltar = (ImageView) findViewById(R.id.voltar_entrega_button);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paginaPrincipal();
            }
        });

        mapa_et = (EditText) findViewById(R.id.mapa_id_box);
        data_tv = (TextView) findViewById(R.id.data_box);
        placa_tv = (TextView) findViewById(R.id.placa_caminhao_box);
        local_tv = (TextView) findViewById(R.id.local_box);
        adicionar = (Button) findViewById(R.id.add_entrega_button);

        user = UserFirebase.getCurrentUserEmail();
        db = ConfiguracaoFirebase.getFirebase();
        //atualizando pag_atual
        db.getRoot().child("Usuarios").child(user).child("pag_atual").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double aux = Double.valueOf(dataSnapshot.getValue().toString());
                update_pagamento = aux;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        db.getRoot();

        placas_array = new ArrayList<>();
        armazenarArrayPlaca();
        locais_array = new ArrayList<>();
        regiao = new ArrayList<>();
        valores = new ArrayList<>();
        armazenarArrayLocal();
        locais_list = getResources().getStringArray(R.array.todos_locais);

        local = new ArrayList<>();
        checkedItems = new boolean[locais_list.length];



        data_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HideKeyboard(view);
                showDatePickerDialog();
            }
        });
        placa_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HideKeyboard(view);
                showPlacaPickerDialog();
            }
        });
        local_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HideKeyboard(view);
                showLocalPickerDialog();
            }
        });
        adicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HideKeyboard(view);
                showResumoEntregaDialog();
            }
        });
    }

    public void paginaPrincipal(){
        finish();
    }

    public void showDatePickerDialog(){
        DatePickerDialog date_dialog = new DatePickerDialog(
                NovaEntregaActivity.this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        date_dialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int ano, int mes, int dia) {
        this.ano = ano; this.mes = mes+1; this.dia = dia;
        data = DataFormat.formatar(dia,mes+1,ano);
        data_tv.setText(data);
    }

    public  void showPlacaPickerDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(NovaEntregaActivity.this);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, placas_array);
        TextView title = new TextView(this);
        title.setText("\nSelecione a Placa\n");
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
                placa = placas_array.get(which);
                placa_tv.setText(placa);
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
                    Toast.makeText(NovaEntregaActivity.this,"Nenhum caminhão cadastrado!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void showLocalPickerDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        TextView title = new TextView(this);
        title.setText("\nSelecione o local\n");
        title.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        title.setTextSize(25);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.bold);
        title.setTypeface(typeface);
        title.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 300));
        builder.setCustomTitle(title);
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected

        builder.setMultiChoiceItems(locais_list, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
//                        if (isChecked) {
//                            if (!mUserItems.contains(position)) {
//                                mUserItems.add(position);
//                            }
//                        } else if (mUserItems.contains(position)) {
//                            mUserItems.remove(position);
//                        }
                if(isChecked){
                    local.add(position);
                }else{
                    local.remove((Integer.valueOf(position)));
                }
            }
        });

        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String item = "";
                for (int i = 0; i < local.size(); i++) {
                    item = item + locais_list[local.get(i)];
                    if (i != local.size() - 1) {
                        item = item + ", ";
                    }
                }
                local_tv.setText(item);
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.setNeutralButton("Limpar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                for (int i = 0; i < checkedItems.length; i++) {
                    checkedItems[i] = false;
                    local.clear();
                    local_tv.setText("");
                }
            }
        });

        AlertDialog mDialog = builder.create();
        mDialog.show();

    }
/*
    public void showLocalPickerDialog(){
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, locais_array);
        TextView title = new TextView(this);
        title.setText("\nSelecione o local:\n");
        title.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        title.setTextSize(25);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.bold);
        title.setTypeface(typeface);
        title.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 300));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setCustomTitle(title)
                .setAdapter(dataAdapter, null)
                .setPositiveButton("Confirmar", null)
                .setNegativeButton("Cancelar", null)
                .create();

        dialog.getListView().setItemsCanFocus(false);
        dialog.getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // Manage selected items here
                System.out.println("clicked" + position);
                CheckedTextView textView = (CheckedTextView) view;
                if(textView.isChecked()) {
                    local.add(position);
                } else if(local.contains(position)){
                    local.remove(Integer.valueOf(position));
                }
            }
        });

        dialog.show();
    }
*/
    public void armazenarArrayLocal(){
        locais_array.clear();
        for(int i=1 ; i<=4; i++){
            db = db.getRoot();
            db.child("Tabela Abv").child("Regiao").child(String.valueOf(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null) {
                        auxiliar_i = dataSnapshot.getKey();
                        valor_aux = Double.valueOf(dataSnapshot.child("Valor").getValue().toString());
                        dataSnapshot = dataSnapshot.child("Local");
                        for(DataSnapshot ds: dataSnapshot.getChildren()) {
                            String nome = ds.getValue().toString();
                            Local lc = new Local(nome,String.valueOf(auxiliar_i),valor_aux);
                            locais_array.add(lc);
                            System.out.println(2);
                        }
                    } else {
                        Toast.makeText(NovaEntregaActivity.this,"Nenhum local cadastrado!",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    }

    public void showResumoEntregaDialog(){
        if(mapa_et.getText().toString().isEmpty() || data.isEmpty()
                || placa.isEmpty() || local_tv.getText().toString().isEmpty()){
            Toast.makeText(NovaEntregaActivity.this,"Preencha todos os campos!",Toast.LENGTH_LONG).show();
        }
        else {
            buscarRegiaoValor();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //scrollView
            ScrollView resumo_scroll = new ScrollView(this);
            //layout
            LinearLayout resumo_layout = new LinearLayout(this);
            resumo_layout.setOrientation(LinearLayout.VERTICAL);
            resumo_layout.setPadding(20,20,20,20);
            resumo_scroll.addView(resumo_layout);
            //titulo
            TextView title = new TextView(this);
            title.setText("\nResumo da Entrega\n");
            title.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            title.setTextSize(22);
            title.setTextColor(Color.WHITE);
            title.setGravity(Gravity.CENTER);
            Typeface typeface = ResourcesCompat.getFont(this, R.font.bold);
            title.setTypeface(typeface);
            title.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 300));
            builder.setCustomTitle(title);
            //mapa
            TextView mapa_confirm = new TextView(this);
            mapa_confirm.setText("Nº Mapa:");
            mapa_confirm.setTextSize(10);
            mapa_confirm.setTextColor(Color.GRAY);
            mapa_confirm.setGravity(Gravity.CENTER);
            typeface = ResourcesCompat.getFont(this, R.font.regular);
            mapa_confirm.setTypeface(typeface);
            mapa_confirm.setPadding(0,50,0,0);
            resumo_layout.addView(mapa_confirm);
            //mapa
            TextView mapa_confirm2 = new TextView(this);
            mapa_confirm2.setText(mapa_et.getText().toString());
            mapa_confirm2.setTextSize(15);
            mapa_confirm2.setTextColor(Color.BLACK);
            mapa_confirm2.setGravity(Gravity.CENTER);
            typeface = ResourcesCompat.getFont(this, R.font.regular);
            mapa_confirm2.setTypeface(typeface);
            resumo_layout.addView(mapa_confirm2);
            //data
            TextView data_confirm = new TextView(this);
            data_confirm.setText("Data:");
            data_confirm.setTextSize(10);
            data_confirm.setTextColor(Color.GRAY);
            data_confirm.setGravity(Gravity.CENTER);
            typeface = ResourcesCompat.getFont(this, R.font.regular);
            data_confirm.setTypeface(typeface);
            data_confirm.setPadding(0,50,0,0);
            resumo_layout.addView(data_confirm);
            //data
            TextView data_confirm2 = new TextView(this);
            data_confirm2.setText(data);
            data_confirm2.setTextSize(15);
            data_confirm2.setTextColor(Color.BLACK);
            data_confirm2.setGravity(Gravity.CENTER);
            typeface = ResourcesCompat.getFont(this, R.font.regular);
            data_confirm2.setTypeface(typeface);
            resumo_layout.addView(data_confirm2);
            //placa
            TextView placa_confirm = new TextView(this);
            placa_confirm.setText("Placa:");
            placa_confirm.setTextSize(10);
            placa_confirm.setTextColor(Color.GRAY);
            placa_confirm.setGravity(Gravity.CENTER);
            typeface = ResourcesCompat.getFont(this, R.font.regular);
            placa_confirm.setTypeface(typeface);
            placa_confirm.setPadding(0,50,0,0);
            resumo_layout.addView(placa_confirm);
            //placa
            TextView placa_confirm2 = new TextView(this);
            placa_confirm2.setText(placa);
            placa_confirm2.setTextSize(15);
            placa_confirm2.setTextColor(Color.BLACK);
            placa_confirm2.setGravity(Gravity.CENTER);
            typeface = ResourcesCompat.getFont(this, R.font.regular);
            placa_confirm2.setTypeface(typeface);
            resumo_layout.addView(placa_confirm2);
            //local
            TextView local_confirm = new TextView(this);
            local_confirm.setText("Local:");
            local_confirm.setTextSize(10);
            local_confirm.setTextColor(Color.GRAY);
            local_confirm.setGravity(Gravity.CENTER);
            typeface = ResourcesCompat.getFont(this, R.font.regular);
            local_confirm.setTypeface(typeface);
            local_confirm.setPadding(0,50,0,0);
            resumo_layout.addView(local_confirm);
            //local
            TextView local_confirm2 = new TextView(this);
            local_confirm2.setText(local_tv.getText().toString());
            local_confirm2.setTextSize(15);
            local_confirm2.setTextColor(Color.BLACK);
            local_confirm2.setGravity(Gravity.CENTER);
            typeface = ResourcesCompat.getFont(this, R.font.regular);
            local_confirm2.setTypeface(typeface);
            resumo_layout.addView(local_confirm2);
            //regiao
            TextView regiao_confirm = new TextView(this);
            regiao_confirm.setText("Região de cobrança:");
            regiao_confirm.setTextSize(10);
            regiao_confirm.setTextColor(Color.GRAY);
            regiao_confirm.setGravity(Gravity.CENTER);
            typeface = ResourcesCompat.getFont(this, R.font.regular);
            regiao_confirm.setTypeface(typeface);
            regiao_confirm.setPadding(0,50,0,0);
            resumo_layout.addView(regiao_confirm);
            //regiao
            TextView regiao_confirm2 = new TextView(this);
            regiao_confirm2.setText(regiao_final);
            regiao_confirm2.setTextSize(15);
            regiao_confirm2.setTextColor(Color.BLACK);
            regiao_confirm2.setGravity(Gravity.CENTER);
            typeface = ResourcesCompat.getFont(this, R.font.regular);
            regiao_confirm2.setTypeface(typeface);
            resumo_layout.addView(regiao_confirm2);
            //valor
            TextView valor_confirm = new TextView(this);
            valor_confirm.setText("Valor:");
            valor_confirm.setTextSize(10);
            valor_confirm.setTextColor(Color.GRAY);
            valor_confirm.setGravity(Gravity.CENTER);
            typeface = ResourcesCompat.getFont(this, R.font.regular);
            valor_confirm.setTypeface(typeface);
            valor_confirm.setPadding(0,50,0,0);
            resumo_layout.addView(valor_confirm);
            //valor
            TextView valor_confirm2 = new TextView(this);
            valor_confirm2.setText(DinheiroFormat.converter(valor_final));
            valor_confirm2.setTextSize(15);
            valor_confirm2.setTextColor(Color.BLACK);
            valor_confirm2.setGravity(Gravity.CENTER);
            typeface = ResourcesCompat.getFont(this, R.font.regular);
            valor_confirm2.setTypeface(typeface);
            resumo_layout.addView(valor_confirm2);
            //
            builder.setView(resumo_scroll);
            //botoes
            builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    update_pagamento += valor_final;
                    entrega = new Entrega();
                    entrega.setMapa(mapa_et.getText().toString());
                    entrega.setPlaca(placa);
                    entrega.setLocal(local_tv.getText().toString());
                    entrega.setRegiao(Integer.valueOf(regiao_final));
                    entrega.setValor(valor_final);
                    entrega.setAno(ano);
                    entrega.setMes(mes);
                    entrega.setDia(dia);
                    entrega.setEstaPago(0);
                    entrega.salvar();
                    PagamentoTotal.atualizar();
                    Toast.makeText(NovaEntregaActivity.this,"Entrega Adicionada!",Toast.LENGTH_SHORT).show();
                    paginaPrincipal();
                }
            });

            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog mDialog = builder.create();
            mDialog.show();
        }

    }

    public void buscarRegiaoValor(){
        valor_final = 0.0;
        regiao_final = "";
        for(Integer selec: local){
            System.out.println(1);
            for (Local l: locais_array){
                System.out.println(2);
                if(l.getNome().equals(locais_list[selec])){
                    System.out.println(3);
                    if(valor_final < l.getValor()) {
                        System.out.println(4);
                        valor_final = l.getValor();
                        regiao_final = l.getRegiao();
                    }
                }
            }
        }


    }

}
