package ru.alexander.twistthetongue.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.favorite_patter_item.view.*
import kotlinx.android.synthetic.main.patter_item.view.*
import kotlinx.android.synthetic.main.patter_item.view.favoriteCheck
import kotlinx.android.synthetic.main.patter_item.view.titleTextView
import ru.alexander.twistthetongue.R
import ru.alexander.twistthetongue.listeners.OnPatterClickListener
import ru.alexander.twistthetongue.model.Patter

class FavoritePattersAdapter(val onClickListener: OnPatterClickListener) :
    RecyclerView.Adapter<FavoritePattersAdapter.FavoritePatterHolder>() {


    var patters: List<Patter> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritePatterHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.favorite_patter_item, parent, false)

        return FavoritePatterHolder(v, onClickListener)
    }

    override fun getItemCount(): Int = patters.size

    override fun onBindViewHolder(holder: FavoritePatterHolder, position: Int) {
        val patter = patters[position]
        holder.setPatter(patter)
    }

    class FavoritePatterHolder(itemView: View, val lisetner: OnPatterClickListener) :
        RecyclerView.ViewHolder(itemView) {


        fun setPatter(patter: Patter) {
            itemView.titleTextView.text = patter.title
            itemView.favoriteCheck.isChecked = patter.favorite
            itemView.visitCounterTextView.text = patter.visits.toString()
            itemView.visitCounterLayout.visibility = if (patter.visits != 0) View.VISIBLE else View.INVISIBLE
            itemView.setOnClickListener {
                lisetner.onClick(patter)
            }
            itemView.favoriteCheck.setOnCheckedChangeListener { compoundButton, b ->
                patter.favorite = false
                lisetner.onFavorite(patter)
            }
        }
    }

}