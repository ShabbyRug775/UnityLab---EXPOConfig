package com.example.unitylab_expoconfig.ui.visitante;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unitylab_expoconfig.R;
import java.util.List;
import java.util.Locale;

public class ProyectosDestacadosAdapter extends RecyclerView.Adapter<ProyectosDestacadosAdapter.ProyectoViewHolder> {

    private List<VisitanteActivity.ProyectoDestacado> proyectos;
    private OnProyectoClickListener listener;

    public interface OnProyectoClickListener {
        void onProyectoClick(VisitanteActivity.ProyectoDestacado proyecto);
    }

    public ProyectosDestacadosAdapter(List<VisitanteActivity.ProyectoDestacado> proyectos,
                                      OnProyectoClickListener listener) {
        this.proyectos = proyectos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProyectoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_proyecto_destacado, parent, false);
        return new ProyectoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProyectoViewHolder holder, int position) {
        VisitanteActivity.ProyectoDestacado proyecto = proyectos.get(position);

        holder.tvNombreProyecto.setText(proyecto.getNombreProyecto());
        holder.tvNombreEquipo.setText(proyecto.getNombreEquipo());
        holder.tvDescripcion.setText(proyecto.getDescripcion());
        holder.tvLugar.setText("Ubicación: " + proyecto.getLugar());

        // Mostrar promedio si hay evaluaciones
        if (proyecto.getCantEvaluaciones() > 0) {
            holder.tvPromedio.setText(String.format(Locale.getDefault(), "%.1f ⭐", proyecto.getPromedio()));
            holder.tvCantEvaluaciones.setText(String.format(Locale.getDefault(),
                    "(%d evaluación%s)", proyecto.getCantEvaluaciones(),
                    proyecto.getCantEvaluaciones() != 1 ? "es" : ""));
            holder.tvPromedio.setVisibility(View.VISIBLE);
            holder.tvCantEvaluaciones.setVisibility(View.VISIBLE);
        } else {
            holder.tvPromedio.setText("Sin evaluar");
            holder.tvCantEvaluaciones.setVisibility(View.GONE);
            holder.tvPromedio.setVisibility(View.VISIBLE);
        }

        // Click listener
        holder.cardProyecto.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProyectoClick(proyecto);
            }
        });
    }

    @Override
    public int getItemCount() {
        return proyectos != null ? proyectos.size() : 0;
    }

    public void updateProyectos(List<VisitanteActivity.ProyectoDestacado> nuevosProyectos) {
        this.proyectos = nuevosProyectos;
        notifyDataSetChanged();
    }

    static class ProyectoViewHolder extends RecyclerView.ViewHolder {
        CardView cardProyecto;
        TextView tvNombreProyecto, tvNombreEquipo, tvDescripcion, tvLugar, tvPromedio, tvCantEvaluaciones;

        public ProyectoViewHolder(@NonNull View itemView) {
            super(itemView);
            cardProyecto = itemView.findViewById(R.id.cardProyecto);
            tvNombreProyecto = itemView.findViewById(R.id.tvNombreProyecto);
            tvNombreEquipo = itemView.findViewById(R.id.tvNombreEquipo);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvLugar = itemView.findViewById(R.id.tvLugar);
            tvPromedio = itemView.findViewById(R.id.tvPromedio);
            tvCantEvaluaciones = itemView.findViewById(R.id.tvCantEvaluaciones);
        }
    }
}