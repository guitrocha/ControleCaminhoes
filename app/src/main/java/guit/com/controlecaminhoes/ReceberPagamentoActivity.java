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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import guit.com.controlecaminhoes.config.ConfiguracaoFirebase;
import guit.com.controlecaminhoes.helper.DataFormat;
import guit.com.controlecaminhoes.helper.DinheiroFormat;
import guit.com.controlecaminhoes.helper.PagamentoTotal;
import guit.com.controlecaminhoes.helper.UserFirebase;
import guit.com.controlecaminhoes.model.Entrega;
import guit.com.controlecaminhoes.model.LinhaExtrato;

public class ReceberPagamentoActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private int i_or_f = 1;
    private TextView data_inicio_tv, data_fim_tv, data_pag_tv;
    private TextView placa_tv;
    private Button adicionar;
    private ImageView voltar;
    private int dia_i, mes_i, ano_i, dia_f, mes_f, ano_f, dia_p, mes_p, ano_p;
    private String data_i, data_f, data_p;
    private LinhaExtrato pagamento;
    private DatabaseReference db;
    private double pag_atual;
    private ArrayList<String> placas_array;
    private String placa;
    private ArrayList<Entrega> entregas_pag = new ArrayList<>();
    private double valor_total = 0.0;
    private boolean done = false;

    public void HideKeyboard(View view){
        InputMethodManager ipm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        ipm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receber_pagamento);
        db = ConfiguracaoFirebase.getFirebase();
        placas_array = new ArrayList<>();
        armazenarArrayPlaca();
        Bundle extras = getIntent().getExtras();
        if(extras != null)
            pag_atual = extras.getDouble("pag_atual");

        voltar = (ImageView) findViewById(R.id.voltar_pagamento_button);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paginaPrincipal();
            }
        });

        data_inicio_tv = (TextView) findViewById(R.id.data_box_pagamento_inicio);
        data_inicio_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HideKeyboard(view);
                i_or_f = 1;
                showDataDialog();
            }
        });

        data_fim_tv = (TextView) findViewById(R.id.data_box_pagamento_fim);
        data_fim_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HideKeyboard(view);
                i_or_f = 2;
                showDataDialog();
            }
        });

        data_pag_tv = (TextView) findViewById(R.id.data_box_pagamento_dia);
        data_pag_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HideKeyboard(view);
                i_or_f = 3;
                showDataDialog();
            }
        });

        placa_tv = (TextView) findViewById(R.id.placa_caminhao_box_pagamento);
        placa_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HideKeyboard(view);
                showPlacaDialog();
            }
        });

        adicionar = (Button) findViewById(R.id.add_pagamento_button);
        adicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HideKeyboard(view);

                if(data_inicio_tv.getText().toString().isEmpty() || data_fim_tv.getText().toString().isEmpty()
                        || placa_tv.getText().toString().isEmpty() || data_pag_tv.getText().toString().isEmpty()){
                    Toast.makeText(ReceberPagamentoActivity.this,"Preencha todos os campos!",Toast.LENGTH_LONG).show();
                } else {
                    entregas_pag.clear();
                    filtro(placa_tv.getText().toString(),data_inicio_tv.getText().toString(),data_fim_tv.getText().toString());


                    //showResumo();
                }
            }
        });
    }



    private void paginaPrincipal() {
        finish();
    }

    public void showDataDialog(){
        DatePickerDialog date_dialog = new DatePickerDialog(
                ReceberPagamentoActivity.this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        date_dialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int ano, int mes, int dia) {
        if(i_or_f == 1) {
            this.ano_i = ano;
            this.mes_i = mes + 1;
            this.dia_i = dia;
            data_i = DataFormat.formatar(dia, mes + 1, ano);
            data_inicio_tv.setText(data_i);
        } else if(i_or_f == 2){
            this.ano_f = ano;
            this.mes_f = mes + 1;
            this.dia_f = dia;
            data_f = DataFormat.formatar(dia, mes + 1, ano);
            data_fim_tv.setText(data_f);
        } else {
            this.ano_p = ano;
            this.mes_p = mes + 1;
            this.dia_p = dia;
            data_p = DataFormat.formatar(dia, mes + 1, ano);
            data_pag_tv.setText(data_p);
        }
    }

    public void showPlacaDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(ReceberPagamentoActivity.this);
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
                    Toast.makeText(ReceberPagamentoActivity.this,"Nenhum caminhão cadastrado!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showResumo() {
        if(data_inicio_tv.getText().toString().isEmpty() || data_fim_tv.getText().toString().isEmpty()
                || placa_tv.getText().toString().isEmpty() || data_pag_tv.getText().toString().isEmpty()){
            Toast.makeText(ReceberPagamentoActivity.this,"Preencha todos os campos!",Toast.LENGTH_LONG).show();
        } else {

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
            title.setText("\nResumo do Pagamento\n");
            title.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            title.setTextSize(22);
            title.setTextColor(Color.WHITE);
            title.setGravity(Gravity.CENTER);
            Typeface typeface = ResourcesCompat.getFont(this, R.font.bold);
            title.setTypeface(typeface);
            title.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 300));
            builder.setCustomTitle(title);
            //data
            TextView datai_confirm = new TextView(this);
            datai_confirm.setText("Data Início:");
            datai_confirm.setTextSize(10);
            datai_confirm.setTextColor(Color.GRAY);
            datai_confirm.setGravity(Gravity.CENTER);
            typeface = ResourcesCompat.getFont(this, R.font.regular);
            datai_confirm.setTypeface(typeface);
            datai_confirm.setPadding(0,50,0,0);
            resumo_layout.addView(datai_confirm);
            //data
            TextView datai_confirm2 = new TextView(this);
            datai_confirm2.setText(data_inicio_tv.getText().toString());
            datai_confirm2.setTextSize(15);
            datai_confirm2.setTextColor(Color.BLACK);
            datai_confirm2.setGravity(Gravity.CENTER);
            typeface = ResourcesCompat.getFont(this, R.font.regular);
            datai_confirm2.setTypeface(typeface);
            resumo_layout.addView(datai_confirm2);
            //data
            TextView dataf_confirm = new TextView(this);
            dataf_confirm.setText("Data Fim:");
            dataf_confirm.setTextSize(10);
            dataf_confirm.setTextColor(Color.GRAY);
            dataf_confirm.setGravity(Gravity.CENTER);
            typeface = ResourcesCompat.getFont(this, R.font.regular);
            dataf_confirm.setTypeface(typeface);
            dataf_confirm.setPadding(0,50,0,0);
            resumo_layout.addView(dataf_confirm);
            //data
            TextView dataf_confirm2 = new TextView(this);
            dataf_confirm2.setText(data_fim_tv.getText().toString());
            dataf_confirm2.setTextSize(15);
            dataf_confirm2.setTextColor(Color.BLACK);
            dataf_confirm2.setGravity(Gravity.CENTER);
            typeface = ResourcesCompat.getFont(this, R.font.regular);
            dataf_confirm2.setTypeface(typeface);
            resumo_layout.addView(dataf_confirm2);
            //placa
            TextView placares = new TextView(this);
            placares.setText("Placa:");
            placares.setTextSize(10);
            placares.setTextColor(Color.GRAY);
            placares.setGravity(Gravity.CENTER);
            typeface = ResourcesCompat.getFont(this, R.font.regular);
            placares.setTypeface(typeface);
            placares.setPadding(0,50,0,0);
            resumo_layout.addView(placares);
            //placa
            TextView placares2 = new TextView(this);
            placares2.setText(placa_tv.getText().toString());
            placares2.setTextSize(15);
            placares2.setTextColor(Color.BLACK);
            placares2.setGravity(Gravity.CENTER);
            typeface = ResourcesCompat.getFont(this, R.font.regular);
            placares2.setTypeface(typeface);
            resumo_layout.addView(placares2);
            //data
            TextView datap_confirm = new TextView(this);
            datap_confirm.setText("Dia do Pagamento:");
            datap_confirm.setTextSize(10);
            datap_confirm.setTextColor(Color.GRAY);
            datap_confirm.setGravity(Gravity.CENTER);
            typeface = ResourcesCompat.getFont(this, R.font.regular);
            datap_confirm.setTypeface(typeface);
            datap_confirm.setPadding(0,50,0,0);
            resumo_layout.addView(datap_confirm);
            //data
            TextView datap_confirm2 = new TextView(this);
            datap_confirm2.setText(data_pag_tv.getText().toString());
            datap_confirm2.setTextSize(15);
            datap_confirm2.setTextColor(Color.BLACK);
            datap_confirm2.setGravity(Gravity.CENTER);
            typeface = ResourcesCompat.getFont(this, R.font.regular);
            datap_confirm2.setTypeface(typeface);
            resumo_layout.addView(datap_confirm2);
            //qt
            TextView qt_confirm = new TextView(this);
            qt_confirm.setText("Quantidade de Entregas:");
            qt_confirm.setTextSize(10);
            qt_confirm.setTextColor(Color.GRAY);
            qt_confirm.setGravity(Gravity.CENTER);
            typeface = ResourcesCompat.getFont(this, R.font.regular);
            qt_confirm.setTypeface(typeface);
            qt_confirm.setPadding(0,50,0,0);
            resumo_layout.addView(qt_confirm);
            //qt
            TextView qt_confirm2 = new TextView(this);
            qt_confirm2.setText(String.valueOf(entregas_pag.size()));
            qt_confirm2.setTextSize(15);
            qt_confirm2.setTextColor(Color.BLACK);
            qt_confirm2.setGravity(Gravity.CENTER);
            typeface = ResourcesCompat.getFont(this, R.font.regular);
            qt_confirm2.setTypeface(typeface);
            resumo_layout.addView(qt_confirm2);
            //valor
            TextView valor_confirm = new TextView(this);
            valor_confirm.setText("Valor Total:");
            valor_confirm.setTextSize(10);
            valor_confirm.setTextColor(Color.GRAY);
            valor_confirm.setGravity(Gravity.CENTER);
            typeface = ResourcesCompat.getFont(this, R.font.regular);
            valor_confirm.setTypeface(typeface);
            valor_confirm.setPadding(0,50,0,0);
            resumo_layout.addView(valor_confirm);
            //valor

            TextView valor_confirm2 = new TextView(this);
            valor_confirm2.setText(DinheiroFormat.converter(valor_total));
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
                    pagamento = new LinhaExtrato();
                    pagamento.setCategoria("Recebimento de salário");
                    pagamento.setDescricao("Salário pago pela AMBEV");
                    pagamento.setPlaca(placa);
                    pagamento.setDia(dia_p);
                    pagamento.setMes(mes_p);
                    pagamento.setAno(ano_p);
                    pagamento.setValor(valor_total,false);
                    pagamento.salvar();
                    Toast.makeText(ReceberPagamentoActivity.this,"Pagamento Recebido!",Toast.LENGTH_SHORT).show();
                    for(Entrega e: entregas_pag){
                        e.salvar();
                    }
                    String user = UserFirebase.getCurrentUserEmail();
                    db = db.getRoot();
                    db = db.child("Usuarios").child(user);
                    db.child("pag_backup").setValue(pag_atual);
                    PagamentoTotal.atualizar();
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
    public void filtro(final String placa, final String data_i, final String data_f){
        DatabaseReference db = ConfiguracaoFirebase.getFirebase();
        String user = UserFirebase.getCurrentUserEmail();
        db = db.child("Usuarios").child(user).child("historico");
        Query query = db.orderByChild("estaPago").equalTo(0);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds: dataSnapshot.getChildren()) {
                    Entrega linha = ds.getValue(Entrega.class);
                    if(linha.getPlaca().equals(placa)) {
                        String data_check = DataFormat.formatar(linha.getDia(), linha.getMes(), linha.getAno());
                        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                        try {
                            Date date1 = format.parse(data_i);
                            Date date2 = format.parse(data_check);
                            Date date3 = format.parse(data_f);
                            if ((date1.before(date2) && date3.after(date2)) || date1.equals(date2) || date3.equals(date2)) {
                                linha.setEstaPago(1);
                                entregas_pag.add(linha);
                                System.out.println("e: " + linha.getMapa());
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                for(Entrega e: entregas_pag){
                    valor_total += e.getValor();
                }
                showResumo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
