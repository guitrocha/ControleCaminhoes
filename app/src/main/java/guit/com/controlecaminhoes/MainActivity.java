package guit.com.controlecaminhoes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import guit.com.controlecaminhoes.config.ConfiguracaoFirebase;
import guit.com.controlecaminhoes.helper.Base64Custom;
import guit.com.controlecaminhoes.helper.DinheiroFormat;
import guit.com.controlecaminhoes.helper.UserFirebase;
import guit.com.controlecaminhoes.model.Entrega;
import guit.com.controlecaminhoes.model.Usuario;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private DatabaseReference db;
    private TextView nome_text;
    private TextView qt_text;
    private TextView valor_text;
    private ImageView sair_button;
    private LinearLayout gerencia;
    private LinearLayout ambev;
    private LinearLayout nova_entrega;
    private LinearLayout historico;
    private LinearLayout despesa;
    private LinearLayout extrato;
    private Button coletar;
    private double pag_atual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        db = ConfiguracaoFirebase.getFirebase();

        nome_text = (TextView) findViewById(R.id.nome_text);
        qt_text = (TextView) findViewById(R.id.qt_caminhao_text);
        valor_text = (TextView) findViewById(R.id.salario_valor_text);
        setPerfil();

        sair_button = (ImageView) findViewById(R.id.sair_button);
        sair_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deslogarUsuario();
            }
        });

        nova_entrega = (LinearLayout) findViewById(R.id.nova_entrega_button);
        nova_entrega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirNovaEntrega();
            }
        });

        historico = (LinearLayout) findViewById(R.id.historico_button);
        historico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirHistorico();
            }
        });

        gerencia = (LinearLayout) findViewById(R.id.gerencia_button);
        gerencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirGerenciamentoCaminhao();
            }
        });

        ambev = (LinearLayout) findViewById(R.id.ambev_button);
        ambev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirValoresAmbev();
            }
        });

        despesa = (LinearLayout) findViewById(R.id.despesa_button);
        despesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirDespesa();
            }
        });

        extrato = (LinearLayout) findViewById(R.id.extrato_button);
        extrato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirExtrato();
            }
        });

        coletar = (Button) findViewById(R.id.coletar_txt);
        coletar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                receberPagamento();
            }
        });
    }

    private void setPerfil(){
        String user = UserFirebase.getCurrentUserEmail();
        db = db.child("Usuarios").child(user);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                nome_text.setText(usuario.getNome());
                String qt = "" + usuario.getQt_caminhoes();
                qt_text.setText(qt);
                pag_atual = usuario.getPag_atual();
                String valor_helper = DinheiroFormat.converter(usuario.getPag_atual());
                valor_text.setText(valor_helper);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    public void abrirNovaEntrega(){
        Intent intent = new Intent(MainActivity.this,NovaEntregaActivity.class);
        startActivity(intent);
    }

    public void abrirHistorico(){
        Intent intent = new Intent(MainActivity.this,HistoricoActivity2.class);
        startActivity(intent);
    }

    public void abrirGerenciamentoCaminhao(){
        Intent intent = new Intent(MainActivity.this,GerenciarCaminhao.class);
        startActivity(intent);
    }

    public void abrirValoresAmbev(){
        Intent intent = new Intent(MainActivity.this,ValoresAmbevActivity.class);
        startActivity(intent);
    }

    public void abrirDespesa(){
        Intent intent = new Intent(MainActivity.this,DespesaActivity.class);
        startActivity(intent);
    }

    public void abrirExtrato(){
        Intent intent = new Intent(MainActivity.this,ExtratoActivity2.class);
        startActivity(intent);
    }

    public void deslogarUsuario(){
        autenticacao.signOut();
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void receberPagamento(){
        //String user = UserFirebase.getCurrentUserEmail();
        //db = db.child("Usuarios").child(user);
        Intent intent = new Intent(MainActivity.this,ReceberPagamentoActivity.class);
        intent.putExtra("pag_atual",pag_atual);
        startActivity(intent);
        /*
        db.child("pag_backup").setValue(pag_atual);
        db.child("pag_atual").setValue(0.0);
        Toast.makeText(MainActivity.this,"Pagamento recebido!",Toast.LENGTH_SHORT).show();

         */

    }
}
