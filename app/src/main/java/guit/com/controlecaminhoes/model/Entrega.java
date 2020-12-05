package guit.com.controlecaminhoes.model;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;

import guit.com.controlecaminhoes.config.ConfiguracaoFirebase;
import guit.com.controlecaminhoes.helper.UserFirebase;

public class Entrega implements Serializable, Comparable<Entrega> {

    private String mapa;
    private int ano;
    private int mes;
    private int dia;
    private String placa;
    private String local;
    private int regiao;
    private double valor;
    private int estaPago;

    public Entrega() {
    }

    public void salvar(){
        DatabaseReference db = ConfiguracaoFirebase.getFirebase();
        String user = UserFirebase.getCurrentUserEmail();
        db = db.child("Usuarios").child(user).child("historico").child(mapa);
        db.setValue(this);
    }

    public String getMapa() {
        return mapa;
    }

    public void setMapa(String mapa) {
        this.mapa = mapa;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public int getRegiao() {
        return regiao;
    }

    public void setRegiao(int regiao) {
        this.regiao = regiao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public int getEstaPago() {
        return estaPago;
    }

    public void setEstaPago(int estaPago) {
        this.estaPago = estaPago;
    }

    @Override
    public int compareTo(Entrega entrega) {
        return Integer.valueOf(this.dia).compareTo(entrega.getDia());
    }
}
