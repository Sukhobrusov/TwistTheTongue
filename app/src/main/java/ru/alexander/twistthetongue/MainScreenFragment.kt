package ru.alexander.twistthetongue


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.fragment_main_screen.view.*

/**
 * A simple [Fragment] subclass.
 */
class MainScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_main_screen, container, false)
        v.patterListButton.setOnClickListener {
            v.findNavController().navigate(R.id.action_mainScreenFragment_to_patterListFragment)
        }
        v.favoritePattersButton.setOnClickListener {
            v.findNavController().navigate(R.id.action_mainScreenFragment_to_favoriteListFragment)
        }

        v.statisticsButton.setOnClickListener {
            v.findNavController().navigate(R.id.action_mainScreenFragment_to_statisticsFragment)
        }
        return v
    }

}
