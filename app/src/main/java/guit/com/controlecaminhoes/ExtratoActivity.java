package guit.com.controlecaminhoes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import guit.com.controlecaminhoes.config.ConfiguracaoFirebase;
import guit.com.controlecaminhoes.helper.CustomAdapterExtrato;
import guit.com.controlecaminhoes.helper.CustomAdapterHistorico;
import guit.com.controlecaminhoes.helper.DataFormat;
import guit.com.controlecaminhoes.helper.DinheiroFormat;
import guit.com.controlecaminhoes.helper.UserFirebase;
import guit.com.controlecaminhoes.model.Entrega;
import guit.com.controlecaminhoes.model.LinhaExtrato;
import guit.com.controlecaminhoes.model.Usuario;

public class ExtratoActivity extends AppCompatActivity {

    private ImageView voltar;
    private TextView filtro_tv;
    private TextView mesano_tv;
    private TextView saldo_tv;
    private String filtro;
    private String mes;
    private int ano;
    private RecyclerView rc;
    private CustomAdapterExtrato customAdapterExtrato;
    private ArrayList<LinhaExtrato> extrato_array;
    private double total;
    private ImageView pdf_button;
    private Bitmap bmp, scaledBmp;
    private DatabaseReference db;
    private String nomeUsuario;
    private int pageWidth = 595;
    private int pageHeight = 840;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extrato);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mes = extras.getString("mes");
            ano = extras.getInt("ano");
            filtro = extras.getString("filtro");
            extrato_array = (ArrayList<LinhaExtrato>) getIntent().getSerializableExtra("array");
            db = ConfiguracaoFirebase.getFirebase();
            getUsuario();

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
            scaledBmp = Bitmap.createScaledBitmap(bmp, 60, 60, false);

            voltar = (ImageView) findViewById(R.id.voltar_button_ext);
            voltar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    paginaPrincipal();
                }
            });

            filtro_tv = (TextView) findViewById(R.id.filtro_titulo_ext);
            filtro_tv.setText(filtro);

            mesano_tv = (TextView) findViewById(R.id.mes_ano_titulo_ext);
            mesano_tv.setText(mes + "/" + ano);

            saldo_tv = (TextView) findViewById(R.id.saldo_ext);
            total = somarPesquisa();
            saldo_tv.setText(DinheiroFormat.converter(total));


            customAdapterExtrato = new CustomAdapterExtrato(ExtratoActivity.this, extrato_array);
            rc = (RecyclerView) findViewById(R.id.extrato_show);
            rc.setAdapter(customAdapterExtrato);
            rc.setLayoutManager(new LinearLayoutManager(ExtratoActivity.this));

            pdf_button = (ImageView) findViewById(R.id.gerador_pdf_ext);
            pdf_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    geradorPDF();
                }
            });
        }
    }

    public void paginaPrincipal() {
        finish();
    }

    public double somarPesquisa(){
        double soma = 0;
        for(LinhaExtrato e: extrato_array){
            soma += e.getValor();
        }
        return soma;
    }
    public void geradorPDF(){
        PdfDocument doc = new PdfDocument();
        PdfDocument.PageInfo detalhesPagina = new PdfDocument.PageInfo.Builder(pageWidth,pageHeight,1).create();
        PdfDocument.Page pagina = doc.startPage(detalhesPagina);
        Canvas canvas = pagina.getCanvas();
        //titulos iniciais
        Paint titulos_pnt = new Paint();
        titulos_pnt.setColor(Color.BLACK);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.bold);
        titulos_pnt.setTypeface(typeface);
        titulos_pnt.setTextSize(15);
        canvas.drawBitmap(scaledBmp, 20, 10, titulos_pnt);
        canvas.drawText("Controle Caminhões - Despesa e Receita ( D | R )",100,25,titulos_pnt);
        Paint titulos_pnt2 = new Paint();
        typeface = ResourcesCompat.getFont(this, R.font.regular);
        titulos_pnt2.setTypeface(typeface);
        titulos_pnt2.setTextSize(10);
        canvas.drawText(nomeUsuario,100,40,titulos_pnt2);
        canvas.drawText(filtro + " • " + mes + "/" + ano,100,55,titulos_pnt2);
        //tabela descrição
        Paint table_pnt = titulos_pnt2;
        table_pnt.setColor(Color.BLACK);
        table_pnt.setStyle(Paint.Style.STROKE);
        table_pnt.setStrokeWidth(2);
        canvas.drawRect(20,80,pageWidth-20,100,table_pnt);
        Paint descr_pnt = table_pnt;
        descr_pnt.setTextAlign(Paint.Align.LEFT);
        descr_pnt.setStyle(Paint.Style.FILL);
        typeface = ResourcesCompat.getFont(this, R.font.bold);
        descr_pnt.setTypeface(typeface);
        canvas.drawText("Data",30,95,descr_pnt);
        canvas.drawText("Categoria",80,95,descr_pnt);
        canvas.drawText("Placa",180,95,descr_pnt);
        canvas.drawText("Descrição",230,95,descr_pnt);
        canvas.drawText("D | R",450,95,descr_pnt);
        canvas.drawText("Valor",500,95,descr_pnt);
        canvas.drawLine(75,80,75,100,descr_pnt);
        canvas.drawLine(175,80,175,100,descr_pnt);
        canvas.drawLine(225,80,225,100,descr_pnt);
        canvas.drawLine(445,80,445,100,descr_pnt);
        canvas.drawLine(495,80,495,100,descr_pnt);
        //valores
        int i = 1;
        int top = 100;
        typeface = ResourcesCompat.getFont(this, R.font.regular);
        descr_pnt.setTypeface(typeface);
        descr_pnt.setTextSize(7);
        for (LinhaExtrato e: extrato_array){

            canvas.drawText(DataFormat.formatar(e.getDia(),e.getMes(),e.getAno()),30,top+9,descr_pnt);
            canvas.drawText(e.getCategoria(),80,top+9,descr_pnt);
            canvas.drawText(e.getPlaca(),180,top+9,descr_pnt);
            canvas.drawText(e.getDescricao(),230,top+9,descr_pnt);
            if(e.getValor() < 0.0)
                canvas.drawText("Despesa",450,top+9,descr_pnt);
            else
                canvas.drawText("Receita",450,top+9,descr_pnt);
            canvas.drawText(DinheiroFormat.converter(e.getValor()),500,top+9,descr_pnt);
            canvas.drawLine(20,top,20,top+10,descr_pnt);
            canvas.drawLine(75,top,75,top+10,descr_pnt);
            canvas.drawLine(175,top,175,top+10,descr_pnt);
            canvas.drawLine(225,top,225,top+10,descr_pnt);
            canvas.drawLine(445,top,445,top+10,descr_pnt);
            canvas.drawLine(495,top,495,top+10,descr_pnt);
            canvas.drawLine(pageWidth-20,top,pageWidth-20,top+10,descr_pnt);
            i++;
            top+=10;
        }
        canvas.drawLine(20,top,20,top+5,descr_pnt);
        canvas.drawLine(75,top,75,top+5,descr_pnt);
        canvas.drawLine(175,top,175,top+5,descr_pnt);
        canvas.drawLine(225,top,225,top+5,descr_pnt);
        canvas.drawLine(445,top,445,top+5,descr_pnt);
        canvas.drawLine(495,top,495,top+5,descr_pnt);
        canvas.drawLine(pageWidth-20,top,pageWidth-20,top+5,descr_pnt);
        canvas.drawLine(20,top+5,pageWidth-20,top+5,descr_pnt);

        top +=5;

        descr_pnt.setTextSize(10);
        typeface = ResourcesCompat.getFont(this, R.font.bold);
        descr_pnt.setTypeface(typeface);

        canvas.drawText(DinheiroFormat.converter(total),500,top+15,descr_pnt);
        canvas.drawLine(495,top,495,top+20,descr_pnt);
        canvas.drawLine(pageWidth-20,top,pageWidth-20,top+20,descr_pnt);
        canvas.drawLine(495,top+20,pageWidth-20,top+20,descr_pnt);






        doc.finishPage(pagina);
        String target = "/Extrato_"+filtro+"_"+mes+"_"+ano+".pdf";

        File filePath = new File(Environment.getExternalStorageDirectory(),target);
        try {
            doc.writeTo(new FileOutputStream(filePath));
            Toast.makeText(this,"PDF gerado com sucesso!", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Toast.makeText(this,"ERRO: Permissão negada.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(this,"Erro ao gerar PDF!", Toast.LENGTH_SHORT).show();
        }




    }

    private void getUsuario(){
        String user = UserFirebase.getCurrentUserEmail();

        db = db.child("Usuarios").child(user);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                nomeUsuario = usuario.getNome();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
