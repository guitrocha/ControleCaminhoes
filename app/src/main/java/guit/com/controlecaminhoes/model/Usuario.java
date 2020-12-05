package guit.com.controlecaminhoes.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;

import guit.com.controlecaminhoes.config.ConfiguracaoFirebase;

public class Usuario {

    private String id;
    private String nome;
    private String email;
    private String senha;
    private double pag_atual;
    private double pag_backup;
    private int qt_caminhoes;

    public Usuario() {
        pag_atual = 0;
        pag_backup = 0;
        qt_caminhoes = 0;
    }

    public void salvar(){
        DatabaseReference db = ConfiguracaoFirebase.getFirebase();
        db.child("Usuarios").child(getId()).setValue(this);
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public double getPag_atual() {
        return pag_atual;
    }

    public void setPag_atual(double pag_atual) {
        this.pag_atual = pag_atual;
    }

    public double getPag_backup() {
        return pag_backup;
    }

    public void setPag_backup(double pag_backup) {
        this.pag_backup = pag_backup;
    }

    public int getQt_caminhoes() {
        return qt_caminhoes;
    }

    public void setQt_caminhoes(int qt_caminhoes) {
        this.qt_caminhoes = qt_caminhoes;
    }

}
