package com.example.unitylab_expoconfig.ui.proyectos;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.unitylab_expoconfig.R;

import java.util.List;

public class ProyectosAdapter extends BaseAdapter {
    private List<Proyecto> proyectos;
    private Context context;
    private int idUsuarioActual;
    private String tipoUsuario;
    private LayoutInflater inflater;

    public ProyectosAdapter(List<Proyecto> proyectos, Context context, int idUsuario, String tipoUsuario) {
        this.proyectos = proyectos;
        this.context = context;
        this.idUsuarioActual = idUsuario;
        this.tipoUsuario = tipoUsuario;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return proyectos != null ? proyectos.size() : 0;
    }

    @Override
    public Proyecto getItem(int position) {
        return proyectos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return proyectos.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_proyecto, parent, false);
            holder = new ViewHolder();
            holder.tvNombreProyecto = convertView.findViewById(R.id.tvNombreProyecto);
            holder.tvDescripcion = convertView.findViewById(R.id.tvDescripcion);
            holder.tvFechaCreacion = convertView.findViewById(R.id.tvFechaCreacion);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Proyecto proyecto = getItem(position);
        holder.tvNombreProyecto.setText(proyecto.getNombreProyecto());
        holder.tvDescripcion.setText(proyecto.getDescripcion());
        holder.tvFechaCreacion.setText(formatearFecha(proyecto.getFechaCreacion()));

        // Configurar el click listener
        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalleProyectoActivity.class);
            intent.putExtra("idProyecto", proyecto.getId());
            intent.putExtra("idUsuario", idUsuarioActual);
            intent.putExtra("tipoUsuario", tipoUsuario);
            context.startActivity(intent);
        });

        return convertView;
    }

    public void actualizarProyectos(List<Proyecto> nuevosProyectos) {
        this.proyectos = nuevosProyectos;
        notifyDataSetChanged();
    }

    public void agregarProyecto(Proyecto proyecto) {
        proyectos.add(proyecto);
        notifyDataSetChanged();
    }

    public void eliminarProyecto(int position) {
        if (position >= 0 && position < proyectos.size()) {
            proyectos.remove(position);
            notifyDataSetChanged();
        }
    }

    public Proyecto getProyecto(int position) {
        if (position >= 0 && position < proyectos.size()) {
            return proyectos.get(position);
        }
        return null;
    }

    private String formatearFecha(String fechaOriginal) {
        try {
            return "Creado: " + fechaOriginal.substring(0, 10);
        } catch (Exception e) {
            return "Creado: " + fechaOriginal;
        }
    }

    static class ViewHolder {
        TextView tvNombreProyecto;
        TextView tvDescripcion;
        TextView tvFechaCreacion;
    }
}