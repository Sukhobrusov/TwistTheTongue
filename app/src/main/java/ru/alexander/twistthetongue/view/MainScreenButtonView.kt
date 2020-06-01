package ru.alexander.twistthetongue.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.view.children
import kotlinx.android.synthetic.main.main_screen_button_view.view.*
import ru.alexander.twistthetongue.R
import java.util.jar.Attributes

class MainScreenButtonView(context : Context, attributes: AttributeSet?) : ConstraintLayout(context, attributes) {

    private val imageView : ImageView
    private val textView : TextView


    companion object {
        const val TAG = "MainScreenButtonView"
    }
    init {


        val inflater = LayoutInflater.from(context).inflate(R.layout.main_screen_button_view, this, true)
        imageView = inflater.mainScreenViewImageView
        textView = inflater.mainScreenViewTextView

        context.theme.obtainStyledAttributes(
            attributes,
            R.styleable.MainScreenButtonView,
            0, 0
        ).apply {
            try {
                //Log.d(TAG, "${getResourceIdOrThrow(R.styleable.MainScreenButtonView_android_src)}")
                //Log.d(TAG, "${getString(R.styleable.MainScreenButtonView_android_text)}")

                textView.text = getString(R.styleable.MainScreenButtonView_android_text)
                imageView.setImageResource(getResourceIdOrThrow(R.styleable.MainScreenButtonView_android_src))
            } finally {
                recycle()
            }
        }
    }

//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        val width = MeasureSpec.getSize(widthMeasureSpec)
//        val height = MeasureSpec.getSize(heightMeasureSpec)
//
//        //Log.d(TAG, "$width")
//        //og.d(TAG, "$height")
//
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//
//
//
//
//        //super.onMeasure(widthMeasureSpec, widthMeasureSpec)
////        (layoutParams as LayoutParams).dimensionRatio = "1:1"
//
//    }
    override fun onViewAdded(view: View?) {
        super.onViewAdded(view)
        //Log.d(TAG, "onViewAdded + ${view!!::class.java.name}")
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }


    constructor(context: Context) : this(context, null)
}