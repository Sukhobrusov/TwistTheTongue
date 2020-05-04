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
import kotlinx.android.synthetic.main.fragmnet_patter_list.view.*
import ru.alexander.twistthetongue.R
import ru.alexander.twistthetongue.adapters.PatterAdapter
import ru.alexander.twistthetongue.model.Patter
import ru.alexander.twistthetongue.viewmodels.PatterListViewModel

class PatterListFragment : Fragment() {

    val listViewModel : PatterListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragmnet_patter_list, container, false)

        val recyclerView = v.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        val adapter = PatterAdapter(object:PatterAdapter.OnClickListener{
            override fun onClick(patter: Patter) {
                v.findNavController().navigate(R.id.action_patterListFragment_to_patterFragment, bundleOf("patter" to patter))
            }

            override fun onFavorite(patter: Patter) {
                listViewModel.update(patter)
            }
        })
        recyclerView.adapter = adapter
        listViewModel.allPatters.observe(viewLifecycleOwner, Observer {
            adapter.patters = it
        })



        return v
    }
}