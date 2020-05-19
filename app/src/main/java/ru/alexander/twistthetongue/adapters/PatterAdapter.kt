package ru.alexander.twistthetongue.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.patter_item.view.*
import ru.alexander.twistthetongue.R
import ru.alexander.twistthetongue.listeners.OnPatterClickListener
import ru.alexander.twistthetongue.model.Patter

class PatterAdapter(val onClickListener: OnPatterClickListener ) :
    RecyclerView.Adapter<PatterAdapter.PatterHolder>() {

    var patters: List<Patter> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatterHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.patter_item, parent, false)

        return PatterHolder(v, onClickListener)
    }

    override fun getItemCount(): Int = patters.size

    override fun onBindViewHolder(holder: PatterHolder, position: Int) {
        val patter = patters[position]
        holder.setPatter(patter)

    }

    class PatterHolder(itemView: View, val lisetner: OnPatterClickListener) :
        RecyclerView.ViewHolder(itemView) {


        fun setPatter(patter: Patter) {
            itemView.titleTextView.text = patter.title
            itemView.favoriteCheck.isChecked = patter.favorite
            itemView.markTextView.text = patter.mark.toString()
            itemView.markTextView.visibility = if (patter.mark != 0) View.VISIBLE else View.INVISIBLE
            itemView.setOnClickListener {
                lisetner.onClick(patter)
            }
            itemView.favoriteCheck.setOnCheckedChangeListener { compoundButton, b ->
                patter.favorite = b
                lisetner.onFavorite(patter)
            }
        }
    }

}