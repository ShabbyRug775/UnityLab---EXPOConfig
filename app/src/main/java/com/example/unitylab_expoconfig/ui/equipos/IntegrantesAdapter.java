package com.example.unitylab_expoconfig.ui.equipos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitylab_expoconfig.R;

import java.util.List;

public class IntegrantesAdapter extends RecyclerView.Adapter<IntegrantesAdapter.IntegranteViewHolder> {

    private List<RegistrarEquipoActivity.IntegranteEquipo> integrantes;
    private OnIntegranteActionListener listener;

    public interface OnIntegranteActionListener {
        void onEliminarIntegrante(int position);
    }

    public IntegrantesAdapter(List<RegistrarEquipoActivity.IntegranteEquipo> integrantes,
                              OnIntegranteActionListener listener) {
        this.integrantes = integrantes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public IntegranteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_integrante_equipo, parent, false);
        return new IntegranteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IntegranteViewHolder holder, int position) {
        RegistrarEquipoActivity.IntegranteEquipo integrante = integrantes.get(position);
        holder.bind(integrante, position, listener);
    }

    @Override
    public int getItemCount() {
        return integrantes.size();
    }

    static class IntegranteViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNombreIntegrante;
        private TextView tvBoletaIntegrante;
        private TextView tvRolIntegrante;
        private ImageView ivEstadoIntegrante;
        private ImageButton btnEliminarIntegrante;

        public IntegranteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreIntegrante = itemView.findViewById(R.id.tvNombreIntegrante);
            tvBoletaIntegrante = itemView.findViewById(R.id.tvBoletaIntegrante);
            tvRolIntegrante = itemView.findViewById(R.id.tvRolIntegrante);
            ivEstadoIntegrante = itemView.findViewById(R.id.ivEstadoIntegrante);
            btnEliminarIntegrante = itemView.findViewById(R.id.btnEliminarIntegrante);
        }

        public void bind(RegistrarEquipoActivity.IntegranteEquipo integrante, int position,
                         OnIntegranteActionListener listener) {
            tvNombreIntegrante.setText(integrante.nombre);
            tvBoletaIntegrante.setText("Boleta: " + integrante.boleta);

            if (integrante.esLider) {
                tvRolIntegrante.setText("Líder del equipo");
                tvRolIntegrante.setTextColor(itemView.getContext().getResources().getColor(R.color.primary_color));
                btnEliminarIntegrante.setVisibility(View.GONE);
                ivEstadoIntegrante.setImageResource(R.drawable.ic_class);
            } else {
                tvRolIntegrante.setText("Integrante");
                tvRolIntegrante.setTextColor(itemView.getContext().getResources().getColor(R.color.text_secondary));
                btnEliminarIntegrante.setVisibility(View.VISIBLE);
                ivEstadoIntegrante.setImageResource(R.drawable.ic_person);

                btnEliminarIntegrante.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onEliminarIntegrante(position);
                    }
                });
            }

            // Cambiar ícono según si ya existe en la BD
            if (integrante.yaExiste) {
                ivEstadoIntegrante.setColorFilter(itemView.getContext().getResources().getColor(R.color.success));
            } else {
                ivEstadoIntegrante.setColorFilter(itemView.getContext().getResources().getColor(R.color.warning));
            }
        }
    }
}