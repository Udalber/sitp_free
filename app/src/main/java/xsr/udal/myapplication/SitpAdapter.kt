package xsr.udal.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class SitpAdapter(private val sitp:List<Feature>, context: Context): RecyclerView.Adapter<SitpViewHolder>() {

    var context: Context = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SitpViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return SitpViewHolder(
            layoutInflater.inflate(
                R.layout.selection_rute,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SitpViewHolder, position: Int) {
        val item = sitp[position]
        holder.bind(item, context)
    }

    override fun getItemCount(): Int = sitp.size
}