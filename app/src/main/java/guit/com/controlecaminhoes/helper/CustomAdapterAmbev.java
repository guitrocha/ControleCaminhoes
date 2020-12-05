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

public class CustomAdapterAmbev extends RecyclerView.Adapter<CustomAdapterAmbev.MyViewHolder> {
    private Context context;
    private ArrayList<String> locais;

    public CustomAdapterAmbev(Context context, ArrayList<String> locais){
        this.context = context;
        this.locais = locais;
    }
    @NonNull
    @Override
    public CustomAdapterAmbev.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =  LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.ambev_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapterAmbev.MyViewHolder holder, final int position) {

        holder.local.setText(String.valueOf(locais.get(position)));

    }

    @Override
    public int getItemCount() {
        return locais.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView local;
        private LinearLayout show_locais_abv;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            local = itemView.findViewById(R.id.local_abv_txt);
            show_locais_abv = itemView.findViewById(R.id.show_locais_abv);
        }
    }
}
