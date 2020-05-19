package ru.alexander.twistthetongue.ui.main

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.twister_view.view.*
import ru.alexander.twistthetongue.R
import ru.alexander.twistthetongue.model.Patter
import ru.alexander.twistthetongue.network.MarkEvaluator
import ru.alexander.twistthetongue.viewmodels.MediaPlayerViewModel
import ru.alexander.twistthetongue.viewmodels.PatterListViewModel
import java.io.File


class PatterFragment : Fragment() {

    private lateinit var patter: Patter

    private var dialog : DialogFragment? = null

    private lateinit var markEvaluator: MarkEvaluator
    private val mediaPlayerViewModel: MediaPlayerViewModel by activityViewModels()
    private val patterListViewModel : PatterListViewModel by activityViewModels()

    private var isCached = false
    private var isMarkReceived = false

    companion object {
        private const val PATTER_KEY = "patter"
        private const val FILE_NAME = "patter.3gp"
        private const val LOG_TAG = "PatterFragment"
        fun newInstance(patter: Patter): PatterFragment {
            val fragmnet = PatterFragment()
            val args = Bundle()
            args.putSerializable(PATTER_KEY, patter)
            fragmnet.arguments = args
            return fragmnet
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.twister_view, container, false)

        patter = arguments?.get(PATTER_KEY) as? Patter ?: return v
        markEvaluator = MarkEvaluator()
        v.tongueTwisterTextView.text = patter.text
        v.currentMarkTextView.text = "${patter.mark}"
        v.favoriteButton.isChecked = patter.favorite

        // when we receive the mark that has been calculated by markViewModel
        markEvaluator.markReturn.observe(viewLifecycleOwner, Observer {
            if (isAdded) {
                Log.d("Observing", " setting mark $it")
                isMarkReceived = true

                if (it.mark != -1) {
                    isCached = true

                    patter.mark = it.mark
                    patterListViewModel.update(patter)
                    v.currentMarkTextView.text = "${it.mark}"

                    dialog = MarkDialogFragment.newInstance(
                        mark = it.mark,
                        spannableStringBuilder = it.spannable
                    )
                    dialog?.show(parentFragmentManager, "show")
                } else {
                    Toast.makeText(activity, "Error occurred while evaluating your mark", Toast.LENGTH_SHORT).show()
                    dialog = null
                }
            }
        })

        val animation = AnimationUtils.loadAnimation(activity, R.anim.rotation);
        animation.repeatCount = Animation.INFINITE
        animation.repeatMode = Animation.RESTART

        val fileName = "${requireActivity().externalCacheDir!!.absolutePath}${File.separator}$FILE_NAME"

        // Determine what happens what happens when users lifts his finger from the playButton
        val onStopRecording = { view : View ->
            mediaPlayerViewModel.stopRecording()
            v.spinningLogo.clearAnimation()
            animation.cancel()
            animation.reset()

            view.isPressed = false
            isMarkReceived = true
            isCached = false
            true
        }
        // Start recording voice for future analysis
        v.recordButton.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.d(LOG_TAG, "Holding Record Button")
                    if (checkRecordPermission()) {
                        mediaPlayerViewModel.startRecording(fileName)
                        v.spinningLogo.startAnimation(animation)
                        view.isPressed = true
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    onStopRecording(view)
                }
                MotionEvent.ACTION_CANCEL -> {
                    onStopRecording(view)
                }
                else -> {
                    Log.d(LOG_TAG, "Action - ${motionEvent.action}")
                    false
                    //mediaPlayerViewModel.stopRecording()
                }
            }

        }

        //Play recorded voice (if there is any)
        v.playButton.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    mediaPlayerViewModel.startPlaying(fileName)
                    view.isPressed = true
                    true
                }
                MotionEvent.ACTION_UP -> {
                    mediaPlayerViewModel.stopPlaying()
                    view.isPressed = false
                    true
                }
                MotionEvent.ACTION_CANCEL -> {
                    mediaPlayerViewModel.stopPlaying()
                    view.isPressed = false
                    true
                }
                else -> {
                    Log.d(LOG_TAG, "Action - ${motionEvent.action}")
                    //mediaPlayerViewModel.stopPlaying()
                    false
                }
            }
        }

        v.favoriteButton.setOnCheckedChangeListener { _, b ->
                patter.favorite = b
                patterListViewModel.update(patter)
        }

        v.sendSpeechToRecognitionButton.setOnClickListener {
            if(isMarkReceived && !isCached)
                markEvaluator.recognize(byteArrayOf(0), patter.text)
            else {
                dialog?.show(parentFragmentManager, "show")
            }
        }

        return v
    }

    override fun onStop() {
        markEvaluator.cancelRequest()
        mediaPlayerViewModel.stopPlaying()
        mediaPlayerViewModel.stopRecording()
        super.onStop()
    }

    override fun onDestroy() {
        markEvaluator.cancelRequest()
        mediaPlayerViewModel.stopPlaying()
        mediaPlayerViewModel.stopRecording()
        super.onDestroy()
    }

    /**
     * Util functions for accessing permissions
     */
    private fun checkRecordPermission() : Boolean =
        if (ContextCompat.checkSelfPermission(requireActivity(),
                android.Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    android.Manifest.permission.RECORD_AUDIO)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(android.Manifest.permission.RECORD_AUDIO),5)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

            false
        } else {
            // Permission has already been granted
            true
        }
    private fun checkWritePermission() : Boolean =
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(requireActivity(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),2)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

            false
        } else {
            // Permission has already been granted
            true
        }
}