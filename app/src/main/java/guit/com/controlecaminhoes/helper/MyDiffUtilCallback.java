package guit.com.controlecaminhoes.helper;

import androidx.recyclerview.widget.DiffUtil;

import java.util.ArrayList;

import guit.com.controlecaminhoes.model.Entrega;

public class MyDiffUtilCallback extends DiffUtil.Callback {
    private ArrayList<Entrega> old_list;
    private ArrayList<Entrega> new_list;

    public MyDiffUtilCallback(ArrayList<Entrega> old_list, ArrayList<Entrega> new_list) {
        this.old_list = old_list;
        this.new_list = new_list;
    }

    @Override
    public int getOldListSize() {
        return old_list.size();
    }

    @Override
    public int getNewListSize() {
        return new_list.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldItemPosition == newItemPosition;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return old_list.get(oldItemPosition) == new_list.get(newItemPosition);
    }
}
