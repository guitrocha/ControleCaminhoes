package guit.com.controlecaminhoes.helper;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import guit.com.controlecaminhoes.EditarRemoverCaminhao;
import guit.com.controlecaminhoes.R;

public class CustomAdapterGerencia extends RecyclerView.Adapter<CustomAdapterGerencia.MyViewHolder> {
    private Context context;
    private ArrayList<String> placas;

    public CustomAdapterGerencia(Context context, ArrayList<String> placas){
        this.context = context;
        this.placas = placas;
    }
    @NonNull
    @Override
    public CustomAdapterGerencia.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =  LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.gerencia_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapterGerencia.MyViewHolder holder, final int position) {

        holder.placa.setText(String.valueOf(placas.get(position)));
        holder.show_placas.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(context, EditarRemoverCaminhao.class);
                intent.putExtra("placa",String.valueOf(placas.get(position)));
                intent.putExtra("position",String.valueOf(position));
                context.startActivity(intent);
            }
        }));
    }

    @Override
    public int getItemCount() {
        return placas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView placa;
        private LinearLayout show_placas;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            placa = itemView.findViewById(R.id.placa_ger_txt);
            show_placas = itemView.findViewById(R.id.show_placas);
        }
    }
}
