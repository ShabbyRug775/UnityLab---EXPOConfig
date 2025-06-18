package com.example.unitylab_expoconfig.ui.proyectos;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitylab_expoconfig.R;

import java.util.List;

public class ProyectosAdapter extends RecyclerView.Adapter<ProyectosAdapter.ProyectoViewHolder> {

    private List<Proyecto> proyectos;
    private Context context;
    private LayoutInflater inflater;

    // Constructor mejorado (con parámetro para destacados)
    public ProyectosAdapter(List<Proyecto> proyectos, Context context) {
        this.proyectos = proyectos;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ProyectoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_proyecto, parent, false);
        return new ProyectoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProyectoViewHolder holder, int position) {
        Proyecto proyecto = proyectos.get(position);
        holder.bind(proyecto, false);

        // Opcional: Manejar clicks en items
        holder.itemView.setOnClickListener(v -> {
            // Aquí puedes lanzar una Activity de detalle si lo necesitas
        });
    }

    @Override
    public int getItemCount() {
        return proyectos.size();
    }

    // Métodos adicionales para manipular datos
    public void actualizarProyectos(List<Proyecto> nuevosProyectos) {
        proyectos.clear();
        proyectos.addAll(nuevosProyectos);
        notifyDataSetChanged();
    }

    public void agregarProyecto(Proyecto proyecto) {
        proyectos.add(proyecto);
        notifyItemInserted(proyectos.size() - 1);
    }

    public void eliminarProyecto(int position) {
        if (position >= 0 && position < proyectos.size()) {
            proyectos.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Proyecto getProyecto(int position) {
        if (position >= 0 && position < proyectos.size()) {
            return proyectos.get(position);
        }
        return null;
    }

    // ViewHolder optimizado
    public static class ProyectoViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNombreProyecto;
        private final TextView tvNombreEquipo;
        private final TextView tvMateria;
        private final TextView tvGrupo;
        private final TextView tvEstado;
        private final TextView tvProfesor;
        private final TextView tvEstudianteLider;

        public ProyectoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreProyecto = itemView.findViewById(R.id.tvNombreProyecto);
            tvNombreEquipo = itemView.findViewById(R.id.tvNombreEquipo);
            tvMateria = itemView.findViewById(R.id.tvMateria);
            tvGrupo = itemView.findViewById(R.id.tvGrupo);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            tvProfesor = itemView.findViewById(R.id.tvProfesor);
            tvEstudianteLider = itemView.findViewById(R.id.tvEstudianteLider);
        }

        public void bind(Proyecto proyecto, boolean esDestacado) {
            // Configurar datos básicos
            tvNombreProyecto.setText(proyecto.getNombreProyecto());
            tvNombreEquipo.setText("Equipo: " + proyecto.getNombreEquipo());
            tvMateria.setText("Materia: " + proyecto.getMateria());
            tvGrupo.setText("Grupo: " + proyecto.getGrupo());
            tvEstado.setText(proyecto.getEstado());
            tvEstado.setTextColor(proyecto.getColorEstado());

            // Manejar campos opcionales
            if (proyecto.getNombreProfesor() != null && !proyecto.getNombreProfesor().isEmpty()) {
                tvProfesor.setText("Profesor: " + proyecto.getNombreProfesor());
                tvProfesor.setVisibility(View.VISIBLE);
            } else {
                tvProfesor.setVisibility(View.GONE);
            }

            if (proyecto.getNombreEstudianteLider() != null && !proyecto.getNombreEstudianteLider().isEmpty()) {
                tvEstudianteLider.setText("Líder: " + proyecto.getNombreEstudianteLider());
                tvEstudianteLider.setVisibility(View.VISIBLE);
            } else {
                tvEstudianteLider.setVisibility(View.GONE);
            }

            // Estilo para destacados (opcional)
            if (esDestacado) {
                itemView.setBackgroundColor(Color.parseColor("#FFFDE7")); // Amarillo claro
            } else {
                itemView.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }
}