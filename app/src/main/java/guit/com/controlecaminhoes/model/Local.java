package guit.com.controlecaminhoes.model;

public class Local {

    private String nome;
    private String regiao;
    private double valor;

    public Local(){}
    public Local(String nome, String regiao, double valor){ this.nome = nome; this.regiao = regiao; this.valor = valor;}

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getRegiao() {
        return regiao;
    }

    public void setRegiao(String regiao) {
        this.regiao = regiao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
