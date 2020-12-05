package guit.com.controlecaminhoes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import guit.com.controlecaminhoes.config.ConfiguracaoFirebase;
import guit.com.controlecaminhoes.model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private Button criar_conta;
    private Button entrar;
    private EditText email_box;
    private EditText senha_box;
    private Usuario usuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        verificarUsuarioLogado();

        criar_conta = (Button) findViewById(R.id.criar_button);
        criar_conta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                criarConta();
            }
        });

        entrar = (Button) findViewById(R.id.entrar_button);
        email_box = (EditText) findViewById(R.id.email_box_log);
        senha_box = (EditText) findViewById(R.id.senha_box_log);

        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usuario = new Usuario();
                usuario.setEmail(email_box.getText().toString());
                usuario.setSenha(senha_box.getText().toString());
                validarLogin();
            }
        });
    }

    private void validarLogin(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(usuario.getEmail(),usuario.getSenha())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this,"Login bem sucedido!",Toast.LENGTH_SHORT).show();
                            abrirTelaPrincipal();
                        } else {
                            String erroexcecao = "";
                            try{
                                throw task.getException();
                            } catch (FirebaseAuthInvalidCredentialsException e){
                                erroexcecao = "Senha inválida!";
                            } catch (FirebaseAuthInvalidUserException e){
                                erroexcecao = "Email não cadastrado!";
                            } catch (Exception e){
                                erroexcecao = "ERRO!";
                                e.printStackTrace();
                            }

                            Toast.makeText(LoginActivity.this,erroexcecao,Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void verificarUsuarioLogado(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        if(autenticacao.getCurrentUser()!=null){
            abrirTelaPrincipal();
        }
    }

    private void abrirTelaPrincipal(){
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void criarConta(){
        Intent intent = new Intent(LoginActivity.this,CriarContaActivity.class);
        startActivity(intent);
        finish();
    }
}
