package com.strongvilledev.rider;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.strongvilledev.rider.pojo.Ruta;

import java.util.ArrayList;

public class RutasAdapter extends RecyclerView.Adapter<RutasAdapter.ViewHolder> {
    private ArrayList<Ruta> mDatos;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView desde, hasta, velocidad;

        public ViewHolder(View parent, TextView desde, TextView hasta, TextView velocidad) {
            super(parent);
            this.desde = desde;
            this.hasta = hasta;
            this.velocidad = velocidad;
        }
    }

    public RutasAdapter(ArrayList<Ruta> datos) {
        mDatos = datos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listitem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ruta, parent, false);

        ViewHolder vh = new ViewHolder(listitem, listitem.findViewById(R.id.origen),
                listitem.findViewById(R.id.destino), listitem.findViewById(R.id.velocidad));
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.desde.setText(mDatos.get(position).getOrigen());
        holder.hasta.setText(mDatos.get(position).getDestino());

        if (mDatos.get(position).getVelocidad() == 0) {
            holder.velocidad.setText("Lenta");
        }
        else if (mDatos.get(position).getVelocidad() == 1) {
            holder.velocidad.setText("Normal");
        }
        else if (mDatos.get(position).getVelocidad() == 2) {
            holder.velocidad.setText("Rápida");
        }
    }

    @Override
    public int getItemCount() {
        return mDatos.size();
    }
}
