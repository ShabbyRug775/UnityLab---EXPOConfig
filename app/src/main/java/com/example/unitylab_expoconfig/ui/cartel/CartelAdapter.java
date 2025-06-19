package com.example.unitylab_expoconfig.ui.cartel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unitylab_expoconfig.R;
import com.example.unitylab_expoconfig.ui.cartel.Cartel;

import java.util.List;

public class CartelAdapter extends RecyclerView.Adapter<CartelAdapter.CartelViewHolder> {

    private Context context;
    private List<Cartel> listaCarteles;
    private OnCartelClickListener listener;

    public interface OnCartelClickListener {
        void onVistaPreviaClick(Cartel cartel);
        void onEstadoChanged(Cartel cartel, boolean isImpreso);
    }

    public CartelAdapter(Context context, List<Cartel> listaCarteles, OnCartelClickListener listener) {
        this.context = context;
        this.listaCarteles = listaCarteles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cartel_cola, parent, false);
        return new CartelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartelViewHolder holder, int position) {
        Cartel cartel = listaCarteles.get(position);
        holder.bind(cartel, listener);
    }

    @Override
    public int getItemCount() {
        return listaCarteles.size();
    }

    public void updateData(List<Cartel> newList) {
        listaCarteles = newList;
        notifyDataSetChanged();
    }

    public static class CartelViewHolder extends RecyclerView.ViewHolder {
        private CardView cvCartel;
        private ImageButton btnVistaPrevia;
        private SwitchCompat switchImpreso;
        private ProgressBar progressImpresion;
        private TextView tvNombreProyecto, tvNombreEquipo, tvMateria, tvGrupo, tvFechaSolicitud, tvEstadoCartel;
        private ImageView ivEstadoCartel;

        public CartelViewHolder(@NonNull View itemView) {
            super(itemView);
            //cvCartel = itemView.findViewById(R.id.cvCartel);
            btnVistaPrevia = itemView.findViewById(R.id.btnVistaPrevia);
            switchImpreso = itemView.findViewById(R.id.switchImpreso);
            progressImpresion = itemView.findViewById(R.id.progressImpresion);
            tvNombreProyecto = itemView.findViewById(R.id.tvNombreProyecto);
            tvNombreEquipo = itemView.findViewById(R.id.tvNombreEquipo);
            tvMateria = itemView.findViewById(R.id.tvMateria);
            tvGrupo = itemView.findViewById(R.id.tvGrupo);
            tvFechaSolicitud = itemView.findViewById(R.id.tvFechaSolicitud);
            tvEstadoCartel = itemView.findViewById(R.id.tvEstadoCartel);
            ivEstadoCartel = itemView.findViewById(R.id.ivEstadoCartel);
        }

        public void bind(Cartel cartel, OnCartelClickListener listener) {
            // Configurar datos
            tvNombreProyecto.setText(cartel.getNombreProyecto());
            tvNombreEquipo.setText("Equipo: " + cartel.getEquipo());
            tvMateria.setText(cartel.getMateria());
            tvGrupo.setText(cartel.getGrupo());
            tvFechaSolicitud.setText("Solicitado: " + cartel.getFechaSolicitud());

            // Configurar estado
            configurarEstado(cartel.getEstado(), cartel.getProgresoImpresion());

            // Configurar switch
            switchImpreso.setChecked(cartel.getEstado().equals("impreso"));

            // Listeners
            btnVistaPrevia.setOnClickListener(v -> listener.onVistaPreviaClick(cartel));
            switchImpreso.setOnCheckedChangeListener((buttonView, isChecked) -> {
                listener.onEstadoChanged(cartel, isChecked);
            });
        }

        private void configurarEstado(String estado, int progreso) {
            switch (estado) {
                case "imprimiendo":
                    ivEstadoCartel.setImageResource(R.drawable.ic_print);
                    ivEstadoCartel.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.accent_color));
                    tvEstadoCartel.setText("Imprimiendo");
                    tvEstadoCartel.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.accent_color));
                    progressImpresion.setVisibility(View.VISIBLE);
                    progressImpresion.setProgress(progreso);
                    switchImpreso.setVisibility(View.GONE);
                    break;

                case "impreso":
                    ivEstadoCartel.setImageResource(R.drawable.ic_check_circle);
                    ivEstadoCartel.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.success));
                    tvEstadoCartel.setText("Impreso");
                    tvEstadoCartel.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.success));
                    progressImpresion.setVisibility(View.GONE);
                    switchImpreso.setVisibility(View.VISIBLE);
                    break;

                case "error":
                    //ivEstadoCartel.setImageResource(R.drawable.ic_error);
                    ivEstadoCartel.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.error));
                    tvEstadoCartel.setText("Error");
                    tvEstadoCartel.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.error));
                    progressImpresion.setVisibility(View.GONE);
                    switchImpreso.setVisibility(View.VISIBLE);
                    break;

                default: // pendiente
                    ivEstadoCartel.setImageResource(R.drawable.ic_print);
                    ivEstadoCartel.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.warning));
                    tvEstadoCartel.setText("Pendiente");
                    tvEstadoCartel.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.warning));
                    progressImpresion.setVisibility(View.GONE);
                    switchImpreso.setVisibility(View.VISIBLE);
            }
        }
    }
}