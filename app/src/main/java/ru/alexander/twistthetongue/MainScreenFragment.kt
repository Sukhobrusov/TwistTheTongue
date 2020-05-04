package ru.alexander.twistthetongue


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.fragment_main_screen.view.*
import ru.alexander.twistthetongue.model.Patter

/**
 * A simple [Fragment] subclass.
 */
class MainScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_main_screen, container, false)
        v.materialButton.setOnClickListener {
            v.findNavController().navigate(R.id.action_mainScreenFragment_to_patterListFragment)
        }
        return v
    }




}
