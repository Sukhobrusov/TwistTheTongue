package ru.alexander.twistthetongue.ui.main

import android.app.Dialog
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.mark_correction_dialogfragment.view.*
import ru.alexander.twistthetongue.R

class MarkDialogFragment : DialogFragment() {

    private var mark : Int = 0
    private lateinit var spannableStringBuilder: SpannableStringBuilder

    companion object {

        private const val MARK = "mark"
        private const val SPANNABLE = "spannable"

        fun newInstance(
            mark : Int,
            spannableStringBuilder: SpannableStringBuilder
        ): MarkDialogFragment {
            val fragment = MarkDialogFragment()
            val bundle = bundleOf(
                MARK to mark,
                SPANNABLE to spannableStringBuilder

            )
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.mark_correction_dialogfragment, container, false)

        mark = arguments?.get(MARK) as? Int ?: return v
        spannableStringBuilder = arguments?.get(SPANNABLE) as? SpannableStringBuilder ?: return v

        v.newMarkTextView.text = mark.toString()
        v.difTextView.text = spannableStringBuilder

        return v
    }


}