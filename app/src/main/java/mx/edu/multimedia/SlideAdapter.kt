package mx.edu.multimedia

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Slide(val drawableId: Int, val titulo: String)

class SlideAdapter(private val slides: List<Slide>) : RecyclerView.Adapter<SlideAdapter.VH>() {
    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val img: ImageView = v.findViewById(R.id.img)
        val caption: TextView = v.findViewById(R.id.caption)
    }
    override fun onCreateViewHolder(p: ViewGroup, t: Int) =
        VH(LayoutInflater.from(p.context).inflate(R.layout.item_slide, p, false))
    override fun onBindViewHolder(h: VH, i: Int) {
        h.img.setImageResource(slides[i].drawableId)
        h.caption.text = slides[i].titulo
    }
    override fun getItemCount() = slides.size
}
