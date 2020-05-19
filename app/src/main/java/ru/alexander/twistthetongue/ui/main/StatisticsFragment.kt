package ru.alexander.twistthetongue.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_statistics.view.*
import ru.alexander.twistthetongue.R
import ru.alexander.twistthetongue.viewmodels.PatterListViewModel

class StatisticsFragment : Fragment() {

    val patterViewModel : PatterListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_statistics, container, false)
        patterViewModel.allPatters.observe(viewLifecycleOwner, Observer {
            val list = it.map { item -> item.visits != 0 }
            var totalVisits = 0
            var averageMark = 0
            var favoritePatter: String
            val visitedPatters = list.size

            it.sortedWith(Comparator{ patter1, patter2 ->
                patter2.visits - patter1.visits
            }).also { patters -> favoritePatter = patters.first().title }.forEach { item ->
                totalVisits += item.visits
                averageMark += item.mark
            }

            v.totalVisitsTextView.text = totalVisits.toString()
            v.averageMarkTextView.text = averageMark.toString()
            v.favoritePatterTextView.text = favoritePatter
            v.visitedPattersTextView.text = visitedPatters.toString()
        })
        return v
    }
}