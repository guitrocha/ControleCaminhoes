package guit.com.controlecaminhoes.model;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

import guit.com.controlecaminhoes.config.ConfiguracaoFirebase;
import guit.com.controlecaminhoes.helper.UserFirebase;

public class LinhaExtrato implements Serializable, Comparable<LinhaExtrato>{
    private String categoria;
    private String descricao;
    private String placa;
    private int dia;
    private int mes;
    private int ano;
    private double valor;

    public LinhaExtrato() {
    }

    public void salvar(){
        DatabaseReference db = ConfiguracaoFirebase.getFirebase();
        String user = UserFirebase.getCurrentUserEmail();
        db = db.child("Usuarios").child(user).child("extrato").push();
        db.setValue(this);
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor, boolean despesa) {
        if (despesa)
            this.valor = valor * (-1);
        else
            this.valor = valor;
    }

    @Override
    public int compareTo(LinhaExtrato linhaExtrato) {
        return Integer.valueOf(this.dia).compareTo(linhaExtrato.getDia());
    }
}
