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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import guit.com.controlecaminhoes.config.ConfiguracaoFirebase;
import guit.com.controlecaminhoes.helper.DataFormat;
import guit.com.controlecaminhoes.helper.DinheiroFormat;
import guit.com.controlecaminhoes.helper.UserFirebase;
import guit.com.controlecaminhoes.model.Entrega;
import guit.com.controlecaminhoes.model.LinhaExtrato;

public class DespesaActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private TextView categoria_tv;
    private TextView placa_tv;
    private TextView data_tv;
    private EditText descricao_et;
    private EditText valor_et;
    private Button adicionar;
    private ImageView voltar;

    private int dia, mes, ano;
    private String data, categoria, descricao, placa, user;
    private double valor;
    private ArrayList<String> placas_array, categoria_array;
    private DatabaseReference db;
    private LinhaExtrato despesa;

    public void HideKeyboard(View view){
        InputMethodManager ipm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        ipm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesa);
        user = UserFirebase.getCurrentUserEmail();
        db = ConfiguracaoFirebase.getFirebase();
        placas_array = new ArrayList<>();
        armazenarArrayPlaca();
        categoria_array = new ArrayList<>();
        armazenarArrayCategoria();

        voltar = (ImageView) findViewById(R.id.voltar_despesa_button);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paginaPrincipal();
            }
        });

        categoria_tv = (TextView) findViewById(R.id.categoria_box);
        categoria_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HideKeyboard(view);
                showCategoriaDialog();
            }
        });

        placa_tv = (TextView) findViewById(R.id.placa_caminhao_box_despesa);
        placa_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HideKeyboard(view);
                showPlacaDialog();
            }
        });

        data_tv = (TextView) findViewById(R.id.data_box_despesa);
        data_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HideKeyboard(view);
                showDataDialog();
            }
        });

        descricao_et = (EditText) findViewById(R.id.descricao_box);
        valor_et = (EditText) findViewById(R.id.valor_box_despesa);

        adicionar = (Button) findViewById(R.id.add_despesa_button);
        adicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HideKeyboard(view);
                showResumoDespesa();
            }
        });
    }

    private void showResumoDespesa() {
        if(categoria_tv.getText().toString().isEmpty() || descricao_et.getText().toString().isEmpty()
                || placa_tv.getText().toString().isEmpty() || data_tv.getText().toString().isEmpty()
                || valor_et.getText().toString().isEmpty()){
            Toast.makeText(DespesaActivity.this,"Preencha todos os campos!",Toast.LENGTH_LONG).show();
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
            title.setText("\nResumo da Despesa\n");
            title.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            title.setTextSize(22);
            title.setTextColor(Color.WHITE);
            title.setGravity(Gravity.CENTER);
            Typeface typeface = ResourcesCompat.getFont(this, R.font.bold);
            title.setTypeface(typeface);
            title.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 300));
            builder.setCustomTitle(title);
            //categoria
            TextView mapa_confirm = new TextView(this);
            mapa_confirm.setText("Categoria:");
            mapa_confirm.setTextSize(10);
            mapa_confirm.setTextColor(Color.GRAY);
            mapa_confirm.setGravity(Gravity.CENTER);
            typeface = ResourcesCompat.getFont(this, R.font.regular);
            mapa_confirm.setTypeface(typeface);
            mapa_confirm.setPadding(0,50,0,0);
            resumo_layout.addView(mapa_confirm);
            //categoria
            TextView mapa_confirm2 = new TextView(this);
            mapa_confirm2.setText(categoria);
            mapa_confirm2.setTextSize(15);
            mapa_confirm2.setTextColor(Color.BLACK);
            mapa_confirm2.setGravity(Gravity.CENTER);
            typeface = ResourcesCompat.getFont(this, R.font.regular);
            mapa_confirm2.setTypeface(typeface);
            resumo_layout.addView(mapa_confirm2);
            //descricao
            TextView data_confirm = new TextView(this);
            data_confirm.setText("Descrição:");
            data_confirm.setTextSize(10);
            data_confirm.setTextColor(Color.GRAY);
            data_confirm.setGravity(Gravity.CENTER);
            typeface = ResourcesCompat.getFont(this, R.font.regular);
            data_confirm.setTypeface(typeface);
            data_confirm.setPadding(0,50,0,0);
            resumo_layout.addView(data_confirm);
            //descricao
            TextView data_confirm2 = new TextView(this);
            data_confirm2.setText(descricao_et.getText().toString());
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
            //data
            TextView local_confirm = new TextView(this);
            local_confirm.setText("Data:");
            local_confirm.setTextSize(10);
            local_confirm.setTextColor(Color.GRAY);
            local_confirm.setGravity(Gravity.CENTER);
            typeface = ResourcesCompat.getFont(this, R.font.regular);
            local_confirm.setTypeface(typeface);
            local_confirm.setPadding(0,50,0,0);
            resumo_layout.addView(local_confirm);
            //data
            TextView local_confirm2 = new TextView(this);
            local_confirm2.setText(data_tv.getText().toString());
            local_confirm2.setTextSize(15);
            local_confirm2.setTextColor(Color.BLACK);
            local_confirm2.setGravity(Gravity.CENTER);
            typeface = ResourcesCompat.getFont(this, R.font.regular);
            local_confirm2.setTypeface(typeface);
            resumo_layout.addView(local_confirm2);
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
            valor_confirm2.setText(DinheiroFormat.converter(Double.valueOf(valor_et.getText().toString())));
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
                    despesa = new LinhaExtrato();
                    despesa.setCategoria(categoria);
                    despesa.setDescricao(descricao_et.getText().toString());
                    despesa.setPlaca(placa);
                    despesa.setDia(dia);
                    despesa.setMes(mes);
                    despesa.setAno(ano);
                    despesa.setValor(Double.valueOf(valor_et.getText().toString()),true);
                    despesa.salvar();
                    Toast.makeText(DespesaActivity.this,"Despesa Adicionada!",Toast.LENGTH_SHORT).show();
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

    public void paginaPrincipal(){
        finish();
    }

    public void showCategoriaDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(DespesaActivity.this);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, categoria_array);
        TextView title = new TextView(this);
        title.setText("\nSelecione a \nCategoria\n");
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
                categoria = categoria_array.get(which);
                categoria_tv.setText(categoria);
            }
        });
        AlertDialog alert = dialog.create();
        alert.show();
        alert.getWindow().setLayout(800,ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void showPlacaDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(DespesaActivity.this);
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

    public void showDataDialog(){
        DatePickerDialog date_dialog = new DatePickerDialog(
                DespesaActivity.this,
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

    public void armazenarArrayPlaca(){
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
                    Toast.makeText(DespesaActivity.this,"Nenhum caminhão cadastrado!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void armazenarArrayCategoria(){
        categoria_array.add("Pagamento de Funcionário");
        categoria_array.add("Mecânico (Mão de Obra)");
        categoria_array.add("Eletricista (Mão de Obra)");
        categoria_array.add("Borracheiro (Mão de Obra)");
        categoria_array.add("Peça");
        categoria_array.add("Pneu");
        categoria_array.add("Imposto MEI");
        categoria_array.add("Outros");
    }
}
