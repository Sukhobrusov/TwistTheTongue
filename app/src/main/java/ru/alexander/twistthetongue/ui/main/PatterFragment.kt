package ru.alexander.twistthetongue.ui.main

import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
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
import androidx.core.view.MotionEventCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.twister_view.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import ru.alexander.twistthetongue.R
import ru.alexander.twistthetongue.model.Patter
import ru.alexander.twistthetongue.network.MarkEvaluator
import ru.alexander.twistthetongue.viewmodels.MediaPlayerViewModel
import ru.alexander.twistthetongue.viewmodels.PatterListViewModel
import java.io.File


class PatterFragment : Fragment() {

    private lateinit var patter: Patter

    private var dialog: DialogFragment? = null

    private lateinit var markEvaluator: MarkEvaluator
    private val mediaPlayerViewModel: MediaPlayerViewModel by activityViewModels()
    private val patterListViewModel: PatterListViewModel by activityViewModels()

    private var speechRecognizer: SpeechRecognizer? = null

    private var isCached = false
    private var isMarkReceived = false

    private var cachedString = ""

    companion object {
        private const val PATTER_KEY = "patter"
        private const val FILE_NAME = "patter.flac"
        private const val LOG_TAG = "PatterFragment"
        private const val SPEECH_TO_TEXT_CODE = 528


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

//        v.setOnTouchListener { view, event ->
//            view.performClick()
//            true
//        }

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
                    Toast.makeText(
                        activity,
                        "Error occurred while evaluating your mark",
                        Toast.LENGTH_SHORT
                    ).show()
                    dialog = null
                }
            }
        })

        val animation = AnimationUtils.loadAnimation(activity, R.anim.rotation);
        animation.repeatCount = Animation.INFINITE
        animation.repeatMode = Animation.RESTART

        val fileName =
            "${requireActivity().externalCacheDir!!.absolutePath}${File.separator}$FILE_NAME"

        initRecognizer()
        // Determine what happens what happens when users lifts his finger from the playButton
        val onButtonReleased = { view: View ->

            v.spinningLogo.clearAnimation()
            animation.cancel()
            animation.reset()
            view.isPressed = false
            false
        }
        // Start recording voice for future analysis

        v.recordButton.setOnTouchListener { view, motionEvent ->
            Log.d(LOG_TAG, "CLICKED 2")

            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.d(LOG_TAG, "Holding Record Button")
                    startRecording {
                        if (!checkRecordPermission()) {
                            false
                        } else {
                            view.performClick()
                            true
                        }
                    }
                }
                MotionEvent.ACTION_UP -> {
                    stopRecording {
                        onButtonReleased(view)
                    }
                }
                MotionEvent.ACTION_CANCEL -> {
                    stopRecording {
                        onButtonReleased(view)
                    }
                }
                else -> {
                    Log.d(LOG_TAG, "Action - ${motionEvent.action}")
                    false
                }
            }

        }

        //Play recorded voice (if there is any)
        v.playButton.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    mediaPlayerViewModel.startPlaying(fileName) { onButtonReleased(view) }
                    v.spinningLogo.startAnimation(animation)
                    view.performClick()
                }
                MotionEvent.ACTION_UP -> {
                    mediaPlayerViewModel.stopPlaying()
                    onButtonReleased(view)
                }
                MotionEvent.ACTION_CANCEL -> {
                    mediaPlayerViewModel.stopPlaying()
                    onButtonReleased(view)
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
            if (isMarkReceived && !isCached) {
                // never happens in this version
                val recordData = MutableLiveData<MediaPlayerViewModel.Record>()
                recordData.observe(viewLifecycleOwner, Observer {
                    when (it.state) {
                        MediaPlayerViewModel.State.DONE -> {
                            markEvaluator.recognize(it.encodedString, patter.text)
                        }
                        MediaPlayerViewModel.State.ERROR_READING_FILE -> {
                            Toast.makeText(activity, "Error reading file", Toast.LENGTH_SHORT)
                                .show()
                            Log.d(LOG_TAG, "Error while reading file")
                            isMarkReceived = true
                        }
                        MediaPlayerViewModel.State.FILE_NOT_FOUND -> {
                            Toast.makeText(activity, "File not found", Toast.LENGTH_SHORT)
                                .show()
                            Log.d(LOG_TAG, "file not found")
                            isMarkReceived = true
                        }
                        MediaPlayerViewModel.State.CALCULATING -> {
                            Log.d(LOG_TAG, "calculating...")
                        }
                    }

                })
                mediaPlayerViewModel.getFileInfo(fileName, recordData)
                isMarkReceived = false
            } else if (isCached && !isMarkReceived) {
                markEvaluator.recognizePatter(cachedString, patter.text)
            } else {
                dialog?.show(parentFragmentManager, "show")
            }
        }

        return v
    }


    private inline fun startRecording(beforeStart: () -> Boolean): Boolean {

        val permittedToStart = beforeStart()

        if (!permittedToStart)
            return false
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru-RU")
        intent.putExtra("android.speech.extra.GET_AUDIO_FORMAT", "audio/AMR")
        intent.putExtra("android.speech.extra.GET_AUDIO", true)
        //speechRecognizer?.startListening(intent)

        Log.d(LOG_TAG, "starting activity for result")
        startActivityForResult(intent, SPEECH_TO_TEXT_CODE)

        isMarkReceived = false
        isCached = false
        return true
    }

    private inline fun stopRecording(afterStop: () -> Boolean): Boolean {
        speechRecognizer?.stopListening()
        //Log.d(LOG_TAG, "Stop listening ${speechRecognizer}")
        isCached = false
        return afterStop()
    }


    /**
     * Util functions for accessing permissions
     */
    private fun checkRecordPermission(): Boolean =
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.RECORD_AUDIO
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    android.Manifest.permission.RECORD_AUDIO
                )
            ) {
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.RECORD_AUDIO), 5
                )
            }
            false
        } else {
            true
        }

    private fun checkWritePermission(): Boolean =
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 2
                )

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

            false
        } else {
            // Permission has already been granted
            true
        }

    private fun initRecognizer() {
        if (checkRecordPermission()) {
            speechRecognizer =
                SpeechRecognizer.createSpeechRecognizer(requireContext())
            val recognitionListener = object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    Log.d(LOG_TAG, "onReadyForSpeech")

                }

                override fun onRmsChanged(rmsdB: Float) {
                    Log.d(LOG_TAG, "onRmsChanged $rmsdB")

                }

                override fun onBufferReceived(buffer: ByteArray?) {
                    Log.d(LOG_TAG, "onBufferedReceived")
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    Log.d(LOG_TAG, "onPartialResults")
                }

                override fun onEvent(eventType: Int, params: Bundle?) {
                    Log.d(LOG_TAG, "onEvent")

                }

                override fun onBeginningOfSpeech() {
                    Log.d(LOG_TAG, "beginning of the speech")

                }

                override fun onEndOfSpeech() {
                    Log.d(LOG_TAG, "onEndOfSpeech")

                }

                override fun onError(error: Int) {
                    Log.d(LOG_TAG, "onError $error")
                }

                override fun onResults(results: Bundle?) {
                    val res =
                        results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            ?: return
                    val str = res.first()
                    markEvaluator.recognizePatter(resolved = str, sourcePatter = patter.text)
                    Log.d(LOG_TAG, "Displaying array = $str")
                }

            }
            speechRecognizer?.setRecognitionListener(recognitionListener)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(LOG_TAG, "OnActivityResult $requestCode")
        when (requestCode) {
            SPEECH_TO_TEXT_CODE -> {
                Log.d(LOG_TAG, "Writing to file")
                if (checkWritePermission()) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val audioUri = data?.data ?: return@launch
                        val contentResolver: ContentResolver = requireActivity().contentResolver
                        val filestream = contentResolver.openInputStream(audioUri)
                        val file =
                            File("${requireActivity().externalCacheDir!!.absolutePath}${File.separator}$FILE_NAME")
                        file.outputStream().use {
                            filestream?.copyTo(it)
                        }
                        filestream?.close()
                    }
                }

                cachedString =
                    data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.first()
                        ?: return
                Log.d(LOG_TAG, cachedString)
                isCached = true
                isMarkReceived = false


            }
        }
    }

    override fun onStop() {
        markEvaluator.cancelRequest()
        mediaPlayerViewModel.stopPlaying()
        mediaPlayerViewModel.stopRecording()
        speechRecognizer?.destroy()
        lifecycleScope.cancel()
        super.onStop()
    }

    override fun onDestroy() {
        markEvaluator.cancelRequest()
        mediaPlayerViewModel.stopPlaying()
        mediaPlayerViewModel.stopRecording()
        speechRecognizer?.destroy()
        speechRecognizer = null
        lifecycleScope.cancel()
        super.onDestroy()
    }


}