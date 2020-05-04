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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.twister_view.view.*
import ru.alexander.twistthetongue.R
import ru.alexander.twistthetongue.model.Patter
import ru.alexander.twistthetongue.viewmodels.MarkViewModel
import ru.alexander.twistthetongue.viewmodels.MediaPlayerViewModel
import ru.alexander.twistthetongue.viewmodels.PatterListViewModel
import java.io.File


class PatterFragment : Fragment() {

    private lateinit var patter: Patter


    private val markViewModel: MarkViewModel by activityViewModels()
    private val mediaPlayerViewModel: MediaPlayerViewModel by activityViewModels()
    private val patterListViewModel : PatterListViewModel by activityViewModels()

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
        v.tongueTwisterTextView.text = patter.text
        v.currentMarkTextView.text = "${patter.mark}"
        v.favoriteButton.isChecked = patter.favorite

        markViewModel.mark.observe(viewLifecycleOwner, Observer {
            Log.d("Observing", " setting mark $it")
            v.currentMarkTextView.text = "$it"
        })

        val animation = AnimationUtils.loadAnimation(activity, R.anim.rotation);
        animation.repeatCount = Animation.INFINITE
        animation.repeatMode = Animation.RESTART

        val fileName = "${requireActivity().externalCacheDir!!.absolutePath}${File.separator}$FILE_NAME"

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
                    mediaPlayerViewModel.stopRecording()
                    v.spinningLogo.clearAnimation()
                    animation.cancel()
                    animation.reset()
                    view.isPressed = false
                    true
                }
                MotionEvent.ACTION_CANCEL -> {
                    mediaPlayerViewModel.stopRecording()
                    v.spinningLogo.clearAnimation()
                    animation.cancel()
                    animation.reset()
                    view.isPressed = false
                    true
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

        return v
    }

    override fun onStop() {
        mediaPlayerViewModel.stopPlaying()
        mediaPlayerViewModel.stopRecording()
        super.onStop()
    }

    override fun onDestroy() {
        mediaPlayerViewModel.stopPlaying()
        mediaPlayerViewModel.stopRecording()
        super.onDestroy()
    }

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