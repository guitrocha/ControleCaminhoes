package guit.com.controlecaminhoes.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import guit.com.controlecaminhoes.EditarRemoverCaminhao;
import guit.com.controlecaminhoes.HistoricoActivity;
import guit.com.controlecaminhoes.HistoricoActivity2;
import guit.com.controlecaminhoes.MainActivity;
import guit.com.controlecaminhoes.NovaEntregaActivity;
import guit.com.controlecaminhoes.R;
import guit.com.controlecaminhoes.config.ConfiguracaoFirebase;
import guit.com.controlecaminhoes.model.Entrega;

public class CustomAdapterHistorico extends RecyclerView.Adapter<CustomAdapterHistorico.MyViewHolder> {
    private Context context;
    private ArrayList<Entrega> hist_line;
    private TextView total_tv;

    public CustomAdapterHistorico(Context context, ArrayList<Entrega> hist_line, TextView total_tv){
        this.context = context;
        this.hist_line = hist_line;
        this.total_tv = total_tv;
    }
    @NonNull
    @Override
    public CustomAdapterHistorico.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =  LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.historico_row, parent, false);
        return new MyViewHolder(view);
    }

    public void insertData(ArrayList<Entrega> insertArray){
        MyDiffUtilCallback diffUtilCallback =  new MyDiffUtilCallback(hist_line,insertArray);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtilCallback);
        hist_line.addAll(insertArray);
        diffResult.dispatchUpdatesTo(this);

    }

    public void updateData(ArrayList<Entrega> newArray){
        MyDiffUtilCallback diffUtilCallback =  new MyDiffUtilCallback(hist_line,newArray);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtilCallback);
        hist_line.clear();
        hist_line.addAll(newArray);
        diffResult.dispatchUpdatesTo(this);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapterHistorico.MyViewHolder holder, final int position) {

        holder.placa.setText(String.valueOf(hist_line.get(position).getPlaca()));
        if(hist_line.get(position).getDia() < 10)
            holder.dia.setText("0" + String.valueOf(hist_line.get(position).getDia()));
        else
            holder.dia.setText(String.valueOf(hist_line.get(position).getDia()));
        holder.mes.setText(DataFormat.mesCut(hist_line.get(position).getMes()));
        holder.local.setText(String.valueOf(hist_line.get(position).getLocal()));
        holder.valor.setText(DinheiroFormat.converter(hist_line.get(position).getValor()));

        holder.show_hist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                //scrollView
                ScrollView resumo_scroll = new ScrollView(context);
                //layout
                LinearLayout resumo_layout = new LinearLayout(context);
                resumo_layout.setOrientation(LinearLayout.VERTICAL);
                resumo_layout.setPadding(20,20,20,20);
                resumo_scroll.addView(resumo_layout);
                //titulo
                TextView title = new TextView(context);
                title.setText("\nResumo da Entrega\n");
                title.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                title.setTextSize(22);
                title.setTextColor(Color.WHITE);
                title.setGravity(Gravity.CENTER);
                Typeface typeface = ResourcesCompat.getFont(context, R.font.bold);
                title.setTypeface(typeface);
                title.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 300));
                builder.setCustomTitle(title);
                //mapa
                TextView mapa_confirm = new TextView(context);
                mapa_confirm.setText("Nº Mapa:");
                mapa_confirm.setTextSize(10);
                mapa_confirm.setTextColor(Color.GRAY);
                mapa_confirm.setGravity(Gravity.CENTER);
                typeface = ResourcesCompat.getFont(context, R.font.regular);
                mapa_confirm.setTypeface(typeface);
                mapa_confirm.setPadding(0,50,0,0);
                resumo_layout.addView(mapa_confirm);
                //mapa
                TextView mapa_confirm2 = new TextView(context);
                mapa_confirm2.setText(String.valueOf(hist_line.get(position).getMapa()));
                mapa_confirm2.setTextSize(15);
                mapa_confirm2.setTextColor(Color.BLACK);
                mapa_confirm2.setGravity(Gravity.CENTER);
                typeface = ResourcesCompat.getFont(context, R.font.regular);
                mapa_confirm2.setTypeface(typeface);
                resumo_layout.addView(mapa_confirm2);
                //data
                TextView data_confirm = new TextView(context);
                data_confirm.setText("Data:");
                data_confirm.setTextSize(10);
                data_confirm.setTextColor(Color.GRAY);
                data_confirm.setGravity(Gravity.CENTER);
                typeface = ResourcesCompat.getFont(context, R.font.regular);
                data_confirm.setTypeface(typeface);
                data_confirm.setPadding(0,50,0,0);
                resumo_layout.addView(data_confirm);
                //data
                TextView data_confirm2 = new TextView(context);
                String data = DataFormat.formatar(hist_line.get(position).getDia(),hist_line.get(position).getMes(),hist_line.get(position).getAno());
                data_confirm2.setText(data);
                data_confirm2.setTextSize(15);
                data_confirm2.setTextColor(Color.BLACK);
                data_confirm2.setGravity(Gravity.CENTER);
                typeface = ResourcesCompat.getFont(context, R.font.regular);
                data_confirm2.setTypeface(typeface);
                resumo_layout.addView(data_confirm2);
                //placa
                TextView placa_confirm = new TextView(context);
                placa_confirm.setText("Placa:");
                placa_confirm.setTextSize(10);
                placa_confirm.setTextColor(Color.GRAY);
                placa_confirm.setGravity(Gravity.CENTER);
                typeface = ResourcesCompat.getFont(context, R.font.regular);
                placa_confirm.setTypeface(typeface);
                placa_confirm.setPadding(0,50,0,0);
                resumo_layout.addView(placa_confirm);
                //placa
                TextView placa_confirm2 = new TextView(context);
                placa_confirm2.setText(String.valueOf(hist_line.get(position).getPlaca()));
                placa_confirm2.setTextSize(15);
                placa_confirm2.setTextColor(Color.BLACK);
                placa_confirm2.setGravity(Gravity.CENTER);
                typeface = ResourcesCompat.getFont(context, R.font.regular);
                placa_confirm2.setTypeface(typeface);
                resumo_layout.addView(placa_confirm2);
                //local
                TextView local_confirm = new TextView(context);
                local_confirm.setText("Local:");
                local_confirm.setTextSize(10);
                local_confirm.setTextColor(Color.GRAY);
                local_confirm.setGravity(Gravity.CENTER);
                typeface = ResourcesCompat.getFont(context, R.font.regular);
                local_confirm.setTypeface(typeface);
                local_confirm.setPadding(0,50,0,0);
                resumo_layout.addView(local_confirm);
                //local
                TextView local_confirm2 = new TextView(context);
                local_confirm2.setText(String.valueOf(hist_line.get(position).getLocal()));
                local_confirm2.setTextSize(15);
                local_confirm2.setTextColor(Color.BLACK);
                local_confirm2.setGravity(Gravity.CENTER);
                typeface = ResourcesCompat.getFont(context, R.font.regular);
                local_confirm2.setTypeface(typeface);
                resumo_layout.addView(local_confirm2);
                //regiao
                TextView regiao_confirm = new TextView(context);
                regiao_confirm.setText("Região de cobrança:");
                regiao_confirm.setTextSize(10);
                regiao_confirm.setTextColor(Color.GRAY);
                regiao_confirm.setGravity(Gravity.CENTER);
                typeface = ResourcesCompat.getFont(context, R.font.regular);
                regiao_confirm.setTypeface(typeface);
                regiao_confirm.setPadding(0,50,0,0);
                resumo_layout.addView(regiao_confirm);
                //regiao
                TextView regiao_confirm2 = new TextView(context);
                regiao_confirm2.setText(String.valueOf(hist_line.get(position).getRegiao()));
                regiao_confirm2.setTextSize(15);
                regiao_confirm2.setTextColor(Color.BLACK);
                regiao_confirm2.setGravity(Gravity.CENTER);
                typeface = ResourcesCompat.getFont(context, R.font.regular);
                regiao_confirm2.setTypeface(typeface);
                resumo_layout.addView(regiao_confirm2);
                //valor
                TextView valor_confirm = new TextView(context);
                valor_confirm.setText("Valor:");
                valor_confirm.setTextSize(10);
                valor_confirm.setTextColor(Color.GRAY);
                valor_confirm.setGravity(Gravity.CENTER);
                typeface = ResourcesCompat.getFont(context, R.font.regular);
                valor_confirm.setTypeface(typeface);
                valor_confirm.setPadding(0,50,0,0);
                resumo_layout.addView(valor_confirm);
                //valor
                TextView valor_confirm2 = new TextView(context);
                valor_confirm2.setText(DinheiroFormat.converter(hist_line.get(position).getValor()));
                valor_confirm2.setTextSize(15);
                valor_confirm2.setTextColor(Color.BLACK);
                valor_confirm2.setGravity(Gravity.CENTER);
                typeface = ResourcesCompat.getFont(context, R.font.regular);
                valor_confirm2.setTypeface(typeface);
                resumo_layout.addView(valor_confirm2);
                //
                builder.setView(resumo_scroll);
                //botoes
                builder.setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                });

                builder.setNeutralButton("Excluir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatabaseReference db = ConfiguracaoFirebase.getFirebase();
                        String user = UserFirebase.getCurrentUserEmail();
                        db = db.getRoot().child("Usuarios").child(user).child("historico").child(hist_line.get(position).getMapa());
                        db.removeValue();
                        PagamentoTotal.atualizar();
                        Toast.makeText(context,"Mapa " + hist_line.get(position).getMapa() + " excluído!",Toast.LENGTH_LONG).show();
                        hist_line.remove(position);
                        notifyDataSetChanged();
                        total_tv.setText(DinheiroFormat.converter(somarPesquisa()));
                        dialogInterface.dismiss();
                    }
                });


                AlertDialog mDialog = builder.create();
                mDialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return hist_line.size();
    }

    public double somarPesquisa(){
        double soma = 0;
        for(Entrega e: hist_line){
            soma += e.getValor();
        }
        return soma;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView placa;
        private TextView dia;
        private TextView mes;
        private TextView local;
        private TextView valor;
        private LinearLayout show_hist;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            placa = itemView.findViewById(R.id.placa_hist_line);
            dia = itemView.findViewById(R.id.dia_hist_line);
            mes = itemView.findViewById(R.id.mes_hist_line);
            local = itemView.findViewById(R.id.local_hist_line);
            valor = itemView.findViewById(R.id.valor_hist_line);
            show_hist = itemView.findViewById(R.id.show_hist);
        }
    }
}
