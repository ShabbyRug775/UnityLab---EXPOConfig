package com.example.unitylab_expoconfig.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitylab_expoconfig.R;
import com.example.unitylab_expoconfig.ui.inicio.ProfesorActivity.ProyectoReciente;

import java.util.List;

public class ProyectosRecientesAdapter extends RecyclerView.Adapter<ProyectosRecientesAdapter.ProyectoViewHolder> {

    private List<ProyectoReciente> proyectos;
    private OnProyectoClickListener listener;

    public interface OnProyectoClickListener {
        void onProyectoClick(ProyectoReciente proyecto);
    }

    public ProyectosRecientesAdapter(List<ProyectoReciente> proyectos, OnProyectoClickListener listener) {
        this.proyectos = proyectos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProyectoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_proyecto_reciente, parent, false);
        return new ProyectoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProyectoViewHolder holder, int position) {
        ProyectoReciente proyecto = proyectos.get(position);
        holder.bind(proyecto);
    }

    @Override
    public int getItemCount() {
        return proyectos.size();
    }

    public void actualizarProyectos(List<ProyectoReciente> nuevosProyectos) {
        this.proyectos.clear();
        this.proyectos.addAll(nuevosProyectos);
        notifyDataSetChanged();
    }

    class ProyectoViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView tvNombreProyecto;
        private TextView tvDescripcionProyecto;
        private TextView tvClaveProyecto;
        private TextView tvNumEquipos;

        public ProyectoViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardProyecto);
            tvNombreProyecto = itemView.findViewById(R.id.tvNombreProyecto);
            tvDescripcionProyecto = itemView.findViewById(R.id.tvDescripcionProyecto);
            tvClaveProyecto = itemView.findViewById(R.id.tvClaveProyecto);
            tvNumEquipos = itemView.findViewById(R.id.tvNumEquipos);
        }

        public void bind(ProyectoReciente proyecto) {
            tvNombreProyecto.setText(proyecto.getNombre());
            tvDescripcionProyecto.setText(proyecto.getDescripcion());
            tvClaveProyecto.setText("Clave: " + proyecto.getClave());

            String equiposText = proyecto.getNumEquipos() == 1 ?
                    proyecto.getNumEquipos() + " equipo inscrito" :
                    proyecto.getNumEquipos() + " equipos inscritos";
            tvNumEquipos.setText(equiposText);

            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProyectoClick(proyecto);
                }
            });
        }
    }
}