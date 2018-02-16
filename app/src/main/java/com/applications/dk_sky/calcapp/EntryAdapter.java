package com.applications.dk_sky.calcapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.ViewHolder> {

    private List<Entry> entries;


    EntryAdapter(List<Entry> entries) {

        this.entries = entries;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.userName.setText(entries.get(position).getUserName());
        holder.buttonsPressed.setText(String.valueOf(entries.get(position).getButtonsPressed()));
        holder.expression.setText(entries.get(position).getExpression());
        holder.result.setText(String.valueOf(entries.get(position).getResult()));
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView buttonsPressed;
        TextView expression;
        TextView result;

        ViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            buttonsPressed = itemView.findViewById(R.id.buttons_pressed);
            expression = itemView.findViewById(R.id.expression);
            result = itemView.findViewById(R.id.result);
        }
    }
}
