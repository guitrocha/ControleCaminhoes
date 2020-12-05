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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import guit.com.controlecaminhoes.config.ConfiguracaoFirebase;
import guit.com.controlecaminhoes.helper.Base64Custom;
import guit.com.controlecaminhoes.model.Usuario;

public class CriarContaActivity extends AppCompatActivity {

    private ImageView voltar;
    private Button cadastrar;
    private EditText email_box;
    private EditText senha_box;
    private EditText confirm_box;
    private EditText nome_box;
    private Usuario usuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_conta);

        voltar = (ImageView) findViewById(R.id.voltar_button);
        cadastrar = (Button) findViewById(R.id.cadastrar_button);
        email_box = (EditText) findViewById(R.id.email_box_cad);
        senha_box = (EditText) findViewById(R.id.senha_box_cad);
        confirm_box = (EditText) findViewById(R.id.senhaconfirm_box_cad);
        nome_box = (EditText) findViewById(R.id.nome_box_cad);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paginaLogin();
            }
        });
        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome = nome_box.getText().toString();
                String email = email_box.getText().toString();
                String senha = senha_box.getText().toString();
                String confirm = confirm_box.getText().toString();
                if(nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirm.isEmpty())
                    Toast.makeText(CriarContaActivity.this,"Preencha todos os campos!",Toast.LENGTH_LONG).show();
                else {
                    if(!senha.equals(confirm))
                        Toast.makeText(CriarContaActivity.this,"Senhas não correspondem!",Toast.LENGTH_LONG).show();
                    else {
                        usuario = new Usuario();
                        usuario.setNome(nome);
                        usuario.setEmail(email);
                        usuario.setSenha(senha);
                        cadastrarUsuario();
                    }
                }
            }
        });
    }
    private void paginaLogin(){
        Intent intent = new Intent(CriarContaActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void cadastrarUsuario(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(usuario.getEmail(),usuario.getSenha())
                .addOnCompleteListener(CriarContaActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(CriarContaActivity.this,"Usuário cadastrado!",Toast.LENGTH_SHORT).show();
                    String identificador = Base64Custom.codificarBase64(usuario.getEmail());
                    usuario.setId(identificador);
                    usuario.salvar();
                    abrirTelaPrincipal();
                } else {
                    String erroexcecao = "";
                    try{
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e){
                        erroexcecao = "Digite uma senha mais forte!";
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        erroexcecao = "O email digitado é inválido!";
                    } catch (FirebaseAuthUserCollisionException e){
                        erroexcecao = "Email já cadastrado anteriormente!";
                    } catch (Exception e){
                        erroexcecao = "ERRO!";
                        e.printStackTrace();
                    }

                    Toast.makeText(CriarContaActivity.this,erroexcecao,Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void abrirTelaPrincipal(){
        Intent intent = new Intent(CriarContaActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
