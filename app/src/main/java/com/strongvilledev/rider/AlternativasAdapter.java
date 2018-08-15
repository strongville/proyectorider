package com.strongvilledev.rider;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class AlternativasAdapter extends RecyclerView.Adapter<AlternativasAdapter.ViewHolder> {
    private ArrayList<String> mDatos;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView texto;

        public ViewHolder(TextView texto) {
            super(texto);
            this.texto = texto;
        }
    }

    public AlternativasAdapter(ArrayList<String> datos) {
        mDatos = datos;
    }

    @Override
    public AlternativasAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView listitem = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alternativa, parent, false);

        AlternativasAdapter.ViewHolder vh = new AlternativasAdapter.ViewHolder(listitem);
        return vh;
    }

    @Override
    public void onBindViewHolder(AlternativasAdapter.ViewHolder holder, int position) {
        holder.texto.setText(mDatos.get(position));
    }

    @Override
    public int getItemCount() {
        return mDatos.size();
    }
}