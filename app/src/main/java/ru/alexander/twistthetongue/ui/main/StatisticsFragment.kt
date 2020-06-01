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
import kotlinx.android.synthetic.main.fragment_statistics.*
import kotlinx.android.synthetic.main.fragment_statistics.view.*
import kotlinx.android.synthetic.main.fragment_statistics.view.recyclerView
import ru.alexander.twistthetongue.R
import ru.alexander.twistthetongue.adapters.FavoritePattersAdapter
import ru.alexander.twistthetongue.adapters.PatterAdapter
import ru.alexander.twistthetongue.listeners.OnPatterClickListener
import ru.alexander.twistthetongue.model.Patter
import ru.alexander.twistthetongue.viewmodels.PatterListViewModel
import ru.alexander.twistthetongue.viewmodels.SortedPattersViewModel

class StatisticsFragment : Fragment() {

    val sortedPattersViewModel : SortedPattersViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_statistics, container, false)
        v.recyclerView.layoutManager = LinearLayoutManager(activity)
        val adapter = FavoritePattersAdapter(object : OnPatterClickListener {
            override fun onClick(patter: Patter) {
                // go into the patter action_statisticsFragment_to_patterFragment
                patter.visits++
                sortedPattersViewModel.update(patter)
                v.findNavController().navigate(R.id.action_statisticsFragment_to_patterFragment, bundleOf("patter" to patter))
            }

            override fun onFavorite(patter: Patter) {
                with(patter){
                    favorite = !favorite
                }
                sortedPattersViewModel.update(patter)
            }

        })
        v.recyclerView.adapter = adapter

        sortedPattersViewModel.sortedPatters.observe(viewLifecycleOwner, Observer {
            adapter.patters = it
            var totalVisits = 0
            var averageMark = 0
            var favoritePatter: String
            val visitedPatters = it.size

            if (visitedPatters == 0 ){
                v.totalVisitsTextView.text = "0"
                v.averageMarkTextView.text = "0"
                v.favoritePatterTextView.text = "-"
                v.visitedPattersTextView.text = "0"
                return@Observer
            }
            it.sortedWith(Comparator{ patter1, patter2 ->
                patter2.visits - patter1.visits
            }).also { patters -> favoritePatter = patters.first().title }.forEach { item ->
                totalVisits += item.visits
                averageMark += item.mark
            }

            v.totalVisitsTextView.text = totalVisits.toString()
            v.averageMarkTextView.text = (averageMark / visitedPatters).toString()
            v.favoritePatterTextView.text = favoritePatter
            v.visitedPattersTextView.text = visitedPatters.toString()
        })
        return v
    }

}