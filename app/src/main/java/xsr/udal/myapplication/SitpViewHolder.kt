package xsr.udal.myapplication;

import android.widget.*
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView


class SitpViewHolder(view: View):RecyclerView.ViewHolder(view) {

    val nameRute = view.findViewById<TextView>(R.id.nameRute)
    val origenRute = view.findViewById<TextView>(R.id.origenRute)
    val tipoOperacion = view.findViewById<TextView>(R.id.tipoOperacion)
//    val tipoOperacion = view.findViewById<TextView>(R.id.tipoOperacion)

    fun bind(sitp: Feature, context: Context){
        nameRute.text = "Nombre: " + sitp.attributes.route_name_ruta_zonal
        origenRute.text = "Ruta: " + sitp.attributes.denominacion_ruta_zonal
        tipoOperacion.text = "Horario: " + sitp.attributes.tipo_operacion
//        _tipoOperacion.text = clean_nulls(sitp.attributes.tipo_operacion)

        nameRute.setOnClickListener {
            val intent = Intent(context, MapsSitp::class.java)
            intent.putExtra("usuario", sitp.attributes.route_name_ruta_zonal);
            context.startActivity(intent)
        }
    }

}