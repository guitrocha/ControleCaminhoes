package guit.com.controlecaminhoes.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import guit.com.controlecaminhoes.R;
import guit.com.controlecaminhoes.model.Entrega;
import guit.com.controlecaminhoes.model.LinhaExtrato;

public class CustomAdapterExtrato extends RecyclerView.Adapter<CustomAdapterExtrato.MyViewHolder> {
    private Context context;
    private ArrayList<LinhaExtrato> ext_line;

    public CustomAdapterExtrato(Context context, ArrayList<LinhaExtrato> ext_line){
        this.context = context;
        this.ext_line = ext_line;
    }
    @NonNull
    @Override
    public CustomAdapterExtrato.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =  LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.extrato_row, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull CustomAdapterExtrato.MyViewHolder holder, final int position) {

        holder.placa.setText(String.valueOf(ext_line.get(position).getPlaca()));
        if(ext_line.get(position).getDia() < 10)
            holder.dia.setText("0" + String.valueOf(ext_line.get(position).getDia()));
        else
            holder.dia.setText(String.valueOf(ext_line.get(position).getDia()));
        holder.mes.setText(DataFormat.mesCut(ext_line.get(position).getMes()));
        holder.categoria.setText(String.valueOf(ext_line.get(position).getCategoria()));
        holder.descricao.setText(String.valueOf(ext_line.get(position).getDescricao()));
        if(ext_line.get(position).getValor() >= 0.0)
            holder.mm.setBackgroundResource(R.drawable.ic_add_circle_outline_black_24dp);
        else
            holder.mm.setBackgroundResource(R.drawable.ic_remove_circle_outline_black_24dp);
        holder.valor.setText(DinheiroFormat.converter(ext_line.get(position).getValor()));

        holder.show_ext.setOnClickListener(new View.OnClickListener() {
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
                title.setText("\nResumo\n");
                title.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                title.setTextSize(22);
                title.setTextColor(Color.WHITE);
                title.setGravity(Gravity.CENTER);
                Typeface typeface = ResourcesCompat.getFont(context, R.font.bold);
                title.setTypeface(typeface);
                title.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 300));
                builder.setCustomTitle(title);
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
                String data = DataFormat.formatar(ext_line.get(position).getDia(), ext_line.get(position).getMes(), ext_line.get(position).getAno());
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
                placa_confirm2.setText(String.valueOf(ext_line.get(position).getPlaca()));
                placa_confirm2.setTextSize(15);
                placa_confirm2.setTextColor(Color.BLACK);
                placa_confirm2.setGravity(Gravity.CENTER);
                typeface = ResourcesCompat.getFont(context, R.font.regular);
                placa_confirm2.setTypeface(typeface);
                resumo_layout.addView(placa_confirm2);
                //categoria
                TextView local_confirm = new TextView(context);
                local_confirm.setText("Categoria:");
                local_confirm.setTextSize(10);
                local_confirm.setTextColor(Color.GRAY);
                local_confirm.setGravity(Gravity.CENTER);
                typeface = ResourcesCompat.getFont(context, R.font.regular);
                local_confirm.setTypeface(typeface);
                local_confirm.setPadding(0,50,0,0);
                resumo_layout.addView(local_confirm);
                //local
                TextView local_confirm2 = new TextView(context);
                local_confirm2.setText(String.valueOf(ext_line.get(position).getCategoria()));
                local_confirm2.setTextSize(15);
                local_confirm2.setTextColor(Color.BLACK);
                local_confirm2.setGravity(Gravity.CENTER);
                typeface = ResourcesCompat.getFont(context, R.font.regular);
                local_confirm2.setTypeface(typeface);
                resumo_layout.addView(local_confirm2);
                //desc
                TextView regiao_confirm = new TextView(context);
                regiao_confirm.setText("Descrição:");
                regiao_confirm.setTextSize(10);
                regiao_confirm.setTextColor(Color.GRAY);
                regiao_confirm.setGravity(Gravity.CENTER);
                typeface = ResourcesCompat.getFont(context, R.font.regular);
                regiao_confirm.setTypeface(typeface);
                regiao_confirm.setPadding(0,50,0,0);
                resumo_layout.addView(regiao_confirm);
                //regiao
                TextView regiao_confirm2 = new TextView(context);
                regiao_confirm2.setText(String.valueOf(ext_line.get(position).getDescricao()));
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
                valor_confirm2.setText(DinheiroFormat.converter(Math.abs(ext_line.get(position).getValor())));
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
/*
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

 */
                AlertDialog mDialog = builder.create();
                mDialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return ext_line.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView placa;
        private TextView dia;
        private TextView mes;
        private TextView categoria;
        private TextView descricao;
        private TextView valor;
        private ImageView mm;
        private LinearLayout show_ext;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            placa = itemView.findViewById(R.id.placa_ext_line);
            dia = itemView.findViewById(R.id.dia_ext_line);
            mes = itemView.findViewById(R.id.mes_ext_line);
            categoria = itemView.findViewById(R.id.categoria_ext_line);
            descricao = itemView.findViewById(R.id.descricao_ext_line);
            valor = itemView.findViewById(R.id.valor_ext_txt);
            show_ext = itemView.findViewById(R.id.show_ext);
            mm = itemView.findViewById(R.id.mais_ou_menos);
        }
    }
}
