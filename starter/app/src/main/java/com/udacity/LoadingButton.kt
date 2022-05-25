package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var progressBar = 0f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }


    private val valueAnimator = ValueAnimator.ofFloat(0f, 1f)

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Loading -> valueAnimator.start()
            ButtonState.Completed -> {
                valueAnimator.end()
            }
            ButtonState.Clicked -> buttonState = ButtonState.Loading
        }
    }


    init {
        setWillNotDraw(false)
        valueAnimator.duration = 3000
        valueAnimator.repeatCount = Animation.INFINITE
        valueAnimator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
            override fun onAnimationUpdate(animation: ValueAnimator?) {
                val value = valueAnimator.getAnimatedValue() as Float
                progressBar = value
                invalidate()

            }
        })

        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                progressBar = 0f
                invalidate()
            }
        })
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawColor(resources.getColor(R.color.colorPrimary, null))
        if (progressBar != 0f) {
            paint.color = resources.getColor(R.color.colorPrimaryDark, null)
            canvas?.drawRect(Rect(0, 0, (progressBar * width).toInt(), height), paint)
            paint.color = resources.getColor(R.color.white, null)
            canvas?.drawLabel("We are loading", progressBar)
        } else {
            paint.color = resources.getColor(R.color.white, null)
            canvas?.drawLabel("Download", 0f)
        }
    }

    fun Canvas.drawLabel(text: String, progress: Float) {
        val xPos = width / 2
        val yPos = height / 2 - (paint.descent() + paint.ascent()) / 2
        drawText(text, xPos.toFloat(), yPos, paint)

        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        val textEnd = width / 2 + bounds.width() / 2
        val top = height / 2 - bounds.height() / 2
        val bot = height / 2 + bounds.height() / 2
        val radius = bounds.height() / 2
        paint.color = resources.getColor(R.color.colorAccent, null)
        drawArc(
            textEnd.toFloat(),
            top.toFloat(), (textEnd + 2 * radius).toFloat(),
            bot.toFloat(), 0f, 360 * progress, true, paint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}