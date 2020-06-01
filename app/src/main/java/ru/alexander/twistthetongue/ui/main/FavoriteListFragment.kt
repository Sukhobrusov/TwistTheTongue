package ru.alexander.twistthetongue.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_favorites.view.*
import ru.alexander.twistthetongue.R
import ru.alexander.twistthetongue.adapters.FavoritePattersAdapter
import ru.alexander.twistthetongue.adapters.PatterAdapter
import ru.alexander.twistthetongue.listeners.OnPatterClickListener
import ru.alexander.twistthetongue.model.Patter
import ru.alexander.twistthetongue.viewmodels.FavouritesViewModel

class FavoriteListFragment : Fragment() {

    private val favoritesViewModel : FavouritesViewModel by activityViewModels()

    companion object {
        fun newInstance() : FavoriteListFragment {
            return FavoriteListFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val v = inflater.inflate(R.layout.fragment_favorites, container, false)
        // setting up the list
        // finding RecyclerView and setting adapters and managers to it
        val recyclerView = v.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        val adapter = FavoritePattersAdapter(object: OnPatterClickListener {
            override fun onClick(patter: Patter) {
                patter.visits++
                favoritesViewModel.update(patter)
                v.findNavController().navigate(R.id.action_favoriteListFragment_to_patterFragment, bundleOf("patter" to patter))
            }

            override fun onFavorite(patter: Patter) {
                patter.favorite = false
                favoritesViewModel.update(patter)
            }
        })
        recyclerView.adapter = adapter

        // setting up observers for list of favorite patters
        favoritesViewModel.favorites.observe(viewLifecycleOwner, Observer {
            adapter.patters = it
        })
        return v
    }


}