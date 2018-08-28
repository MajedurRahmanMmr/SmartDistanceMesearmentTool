package com.mesearment.com;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryHolder> {

    LayoutInflater layoutInflater;
    Context context;
    List<RecordModel> list;


    HistoryAdapter(Context context, List<RecordModel> list) {
        this.context = context;
        this.list = list;
        layoutInflater = LayoutInflater.from(context);

    }

    @NonNull
    @Override
    public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HistoryHolder(layoutInflater.inflate(R.layout.item_history, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryHolder holder, int position) {

        try {
            holder.notes.setText(list.get(position).notes);
            holder.values.setText(list.get(position).measuredData);
            holder.unit.setText(list.get(position).Measuredunit);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    public class HistoryHolder extends RecyclerView.ViewHolder {

        TextView values;
        TextView unit;
        TextView notes;

        public HistoryHolder(@NonNull View itemView) {
            super(itemView);

            values = itemView.findViewById(R.id.valuesT);
            unit = itemView.findViewById(R.id.unitsT);
            notes = itemView.findViewById(R.id.NotesT);
        }
    }
}
