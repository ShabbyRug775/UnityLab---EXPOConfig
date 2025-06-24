package com.example.unitylab_expoconfig.ui.equipos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitylab_expoconfig.R;

import java.util.List;

public class EquiposDisponiblesAdapter extends RecyclerView.Adapter<EquiposDisponiblesAdapter.EquipoViewHolder> {

    private List<UnirseAEquipoActivity.EquipoDisponible> equipos;
    private UnirseAEquipoActivity.OnEquipoSeleccionadoListener listener;

    public EquiposDisponiblesAdapter(List<UnirseAEquipoActivity.EquipoDisponible> equipos,
                                     UnirseAEquipoActivity.OnEquipoSeleccionadoListener listener) {
        this.equipos = equipos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EquipoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_equipo_disponible, parent, false);
        return new EquipoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EquipoViewHolder holder, int position) {
        UnirseAEquipoActivity.EquipoDisponible equipo = equipos.get(position);
        holder.bind(equipo, listener);
    }

    @Override
    public int getItemCount() {
        return equipos.size();
    }

    static class EquipoViewHolder extends RecyclerView.ViewHolder {
        private CardView cardEquipo;
        private TextView tvNombreEquipo;
        private TextView tvProyectoEquipo;
        private TextView tvDescripcionEquipo;
        private TextView tvLiderEquipo;
        private TextView tvIntegrantesEquipo;
        private Button btnUnirse;

        public EquipoViewHolder(@NonNull View itemView) {
            super(itemView);
            cardEquipo = itemView.findViewById(R.id.cardEquipo);
            tvNombreEquipo = itemView.findViewById(R.id.tvNombreEquipo);
            tvProyectoEquipo = itemView.findViewById(R.id.tvProyectoEquipo);
            tvDescripcionEquipo = itemView.findViewById(R.id.tvDescripcionEquipo);
            tvLiderEquipo = itemView.findViewById(R.id.tvLiderEquipo);
            tvIntegrantesEquipo = itemView.findViewById(R.id.tvIntegrantesEquipo);
            btnUnirse = itemView.findViewById(R.id.btnUnirse);
        }

        public void bind(UnirseAEquipoActivity.EquipoDisponible equipo,
                         UnirseAEquipoActivity.OnEquipoSeleccionadoListener listener) {
            tvNombreEquipo.setText(equipo.nombre);
            tvProyectoEquipo.setText("Proyecto: " + equipo.nombreProyecto);
            tvDescripcionEquipo.setText(equipo.descripcion);
            tvLiderEquipo.setText("Líder: " + equipo.nombreLider);

            String integrantesTexto = equipo.numeroAlumnos + " integrante" + (equipo.numeroAlumnos != 1 ? "s" : "");
            tvIntegrantesEquipo.setText(integrantesTexto);

            btnUnirse.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEquipoSeleccionado(equipo);
                }
            });

            cardEquipo.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEquipoSeleccionado(equipo);
                }
            });
        }
    }
}