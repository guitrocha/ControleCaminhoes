package guit.com.controlecaminhoes.helper;

import java.text.DecimalFormat;
import java.util.Formatter;
import java.util.Locale;

public class DinheiroFormat {
    public static double casasDecimais(double original, int quant)
    {   double valor = original;
        String formato = "%." + quant + "f";
        valor = Double.valueOf(String.format(Locale.US, formato, valor));
        return valor;
    }
    public static String converter(double precoDouble) {
        /*Transformando um double em 2 casas decimais*/
        boolean negativo = false;
        if(precoDouble < 0) {
            precoDouble = precoDouble * (-1);
            negativo = true;
        }
        DecimalFormat fmt = new DecimalFormat("0.00");   //limita o número de casas decimais
        String string = fmt.format(precoDouble);
        String[] part = string.split("[,]");
        String preco;
        if(negativo)
            preco = "- R$ " + brasilFormat(Integer.valueOf(part[0]))+","+part[1];
        else
            preco = "R$ " + brasilFormat(Integer.valueOf(part[0]))+","+part[1];
        return preco;
    }
    public static String brasilFormat(double precoDouble){
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, new Locale("pt","BR"));
        formatter.format("%(,.0f", precoDouble);
        return sb.toString();
    }
}
