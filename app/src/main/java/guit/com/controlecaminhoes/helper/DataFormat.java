package guit.com.controlecaminhoes.helper;

public class DataFormat {
    public static String formatar(int dia, int mes, int ano){
        String d, m;
        if(dia < 10)
            d = "0" + dia;
        else
            d = String.valueOf(dia);

        if(mes < 10)
            m = "0" + mes;
        else
            m = String.valueOf(mes);

        return d + "/" + m + "/" + ano;
    }

    public static String mesCut(int mes){
        String mescut = "";
        switch(mes){
            case 1:
                mescut = "JAN";
                break;
            case 2:
                mescut = "FEV";
                break;
            case 3:
                mescut = "MAR";
                break;
            case 4:
                mescut = "ABR";
                break;
            case 5:
                mescut = "MAI";
                break;
            case 6:
                mescut = "JUN";
                break;
            case 7:
                mescut = "JUL";
                break;
            case 8:
                mescut = "AGO";
                break;
            case 9:
                mescut = "SET";
                break;
            case 10:
                mescut = "OUT";
                break;
            case 11:
                mescut = "NOV";
                break;
            case 12:
                mescut = "DEZ";
                break;
        }

        return mescut;
    }
}
