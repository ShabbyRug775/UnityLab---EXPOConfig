package com.example.unitylab_expoconfig.ui.proyectos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.unitylab_expoconfig.R;

import java.util.List;

public class ProyectosAdapter extends BaseAdapter {
    private Context context;
    private List<Proyecto> proyectos;
    private LayoutInflater inflater;

    public ProyectosAdapter(Context context, List<Proyecto> proyectos) {
        this.context = context;
        this.proyectos = proyectos;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return proyectos.size();
    }

    @Override
    public Object getItem(int position) {
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
            holder.tvNombreEquipo = convertView.findViewById(R.id.tvNombreEquipo);
            holder.tvMateria = convertView.findViewById(R.id.tvMateria);
            holder.tvGrupo = convertView.findViewById(R.id.tvGrupo);
            holder.tvEstado = convertView.findViewById(R.id.tvEstado);
            holder.tvProfesor = convertView.findViewById(R.id.tvProfesor);
            holder.tvEstudianteLider = convertView.findViewById(R.id.tvEstudianteLider);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Proyecto proyecto = proyectos.get(position);

        // Configurar los datos
        holder.tvNombreProyecto.setText(proyecto.getNombreProyecto());
        holder.tvNombreEquipo.setText("Equipo: " + proyecto.getNombreEquipo());
        holder.tvMateria.setText("Materia: " + proyecto.getMateria());
        holder.tvGrupo.setText("Grupo: " + proyecto.getGrupo());
        holder.tvEstado.setText(proyecto.getEstado());

        // Configurar el color del estado
        holder.tvEstado.setTextColor(proyecto.getColorEstado());

        // Configurar profesor (si está disponible)
        if (proyecto.getNombreProfesor() != null && !proyecto.getNombreProfesor().trim().isEmpty()) {
            holder.tvProfesor.setText("Profesor: " + proyecto.getNombreProfesor());
            holder.tvProfesor.setVisibility(View.VISIBLE);
        } else {
            holder.tvProfesor.setVisibility(View.GONE);
        }

        // Configurar estudiante líder (si está disponible)
        if (proyecto.getNombreEstudianteLider() != null && !proyecto.getNombreEstudianteLider().trim().isEmpty()) {
            holder.tvEstudianteLider.setText("Líder: " + proyecto.getNombreEstudianteLider());
            holder.tvEstudianteLider.setVisibility(View.VISIBLE);
        } else {
            holder.tvEstudianteLider.setVisibility(View.GONE);
        }

        return convertView;
    }

    // Metodo para actualizar la lista de proyectos
    public void actualizarProyectos(List<Proyecto> nuevosProyectos) {
        this.proyectos.clear();
        this.proyectos.addAll(nuevosProyectos);
        notifyDataSetChanged();
    }

    // Metodo para agregar un proyecto
    public void agregarProyecto(Proyecto proyecto) {
        this.proyectos.add(proyecto);
        notifyDataSetChanged();
    }

    // Metodo para eliminar un proyecto
    public void eliminarProyecto(int position) {
        if (position >= 0 && position < proyectos.size()) {
            this.proyectos.remove(position);
            notifyDataSetChanged();
        }
    }

    // Metodo para obtener un proyecto específico
    public Proyecto getProyecto(int position) {
        if (position >= 0 && position < proyectos.size()) {
            return proyectos.get(position);
        }
        return null;
    }

    // Metodo para filtrar proyectos por estado
    public void filtrarPorEstado(String estado) {
        // Este metodo podria implementarse para filtrar la lista actual
        // Por ahora, se deja como referencia para futuras implementaciones
    }

    // ViewHolder para optimizar el rendimiento
    private static class ViewHolder {
        TextView tvNombreProyecto;
        TextView tvNombreEquipo;
        TextView tvMateria;
        TextView tvGrupo;
        TextView tvEstado;
        TextView tvProfesor;
        TextView tvEstudianteLider;
    }
}