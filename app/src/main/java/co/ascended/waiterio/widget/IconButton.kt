package co.ascended.waiterio.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.GradientDrawable
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.percent.PercentRelativeLayout
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import co.ascended.waiterio.R

/**
 * A custom button that allows icons to be used instead of text.
 * The icon uses a ripple as a background on post-lollipop devices
 * and smoothly changes the icon color when touch events occur.
 * The button's icon size can be adjusted using XML attributes.
 */

/**
 * Standard View entry point.

 * @param context Context the View is inflated in.
 * *
 * @param attrs XML attributes for the View.
 */
class IconButton(context: Context, attrs: AttributeSet) : PercentRelativeLayout(context, attrs) {

    /**
     * Drawable that is used for the button's icon.
     */
    private var drawableIcon0: VectorDrawableCompat? = null

    /**
     * Drawable that is used for the button's icon.
     */
    private var drawableIcon1: VectorDrawableCompat? = null

    /**
     * Drawable that is used as the background of layerDrawable.
     */
    private var drawableBackground: GradientDrawable? = null

    /**
     * Drawable that is used as the foreground of layerDrawable.
     */
    private var drawableForeground: GradientDrawable? = null

    /**
     * The icon's width as a percentage of the button's width.
     */
    private var iconWidth: Float = 0.toFloat()

    /**
     * The icon's left as a percentage of the button's width.
     */
    private var iconLeft: Float = 0.toFloat()

    /**
     * The icon's top as a percentage of the button's height.
     */
    private var iconTop: Float = 0.toFloat()

    /**
     * The foreground's width as a percentage of the button's width.
     */
    private var foregroundWidth: Float = 0.toFloat()

    /**
     * The color the icon will transition to when activated, pressed, or toggled.
     */
    private var colorActive: Int = 0

    /**
     * The icon's normal color.
     */
    private var colorInactive: Int = 0

    /**
     * Evaluator used for animating smoothly between colorActive and colorInactive.
     */
    private val argbEvaluator: ArgbEvaluator

    /**
     * ImageView that is used to display the button's background.
     */
    internal var imageViewBackground: ImageView? = null

    /**
     * ImageView that is used to display the button's foreground.
     */
    internal var imageViewForeground: ImageView? = null

    /**
     * ImageView that is used to display the button's icon.
     */
    internal var imageViewIcon0: ImageView

    /**
     * ImageView that is used to display the button's icon.
     */
    internal var imageViewIcon1: ImageView

    init {
        parseAttributes(context.obtainStyledAttributes(attrs, R.styleable.icon_button))

        // Inflate the icon's layout.
        //inflate(getContext(), R.layout.layout_icon_button, this);

        // Let Butterknife take care of locating views by ID.
        //ButterKnife.bind(this);

        val layoutParams = PercentRelativeLayout.LayoutParams(0, 0)
        if (iconLeft == 0f) layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
        if (iconTop == 0f) layoutParams.addRule(RelativeLayout.CENTER_VERTICAL)

        if (drawableBackground != null && drawableForeground != null) {
            val lp = PercentRelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            lp.addRule(RelativeLayout.CENTER_IN_PARENT)

            imageViewBackground = ImageView(getContext())
            imageViewForeground = ImageView(getContext())

            addView(imageViewBackground, LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
            addView(imageViewForeground, lp)
        }

        imageViewIcon0 = ImageView(getContext())
        addView(imageViewIcon0, layoutParams)

        imageViewIcon1 = ImageView(getContext())
        addView(imageViewIcon1, layoutParams)

        // Make sure the view can be used as a button.
        isClickable = true

        // Create an instance of the evaluator.
        argbEvaluator = ArgbEvaluator()

        // Set up the icon.
        drawButton()
    }

    /**
     * Standard parsing of XML attributes, to apply button options.
     * @param attributeArray XML attributes for the View.
     */
    private fun parseAttributes(attributeArray: TypedArray) {
        foregroundWidth = attributeArray.getFloat(R.styleable.icon_button_foreground_width, 0.9f)
        iconWidth = attributeArray.getFloat(R.styleable.icon_button_icon_width, 0.3f)
        iconLeft = attributeArray.getFloat(R.styleable.icon_button_icon_left, 0f)
        iconTop = attributeArray.getFloat(R.styleable.icon_button_icon_top, 0f)
        colorActive = attributeArray.getColor(R.styleable.icon_button_icon_color_active, 0)
        colorInactive = attributeArray.getColor(R.styleable.icon_button_icon_color_inactive, 0)

        val icon0ID = attributeArray.getResourceId(R.styleable.icon_button_icon_drawable_0, 0)
        val icon1ID = attributeArray.getResourceId(R.styleable.icon_button_icon_drawable_1, 0)
        val backgroundID = attributeArray.getResourceId(R.styleable.icon_button_background_drawable, 0)
        val foregroundID = attributeArray.getResourceId(R.styleable.icon_button_foreground_drawable, 0)

        // Drawables are created and mutated, so other instances in the application aren't affected when this button changes the drawable colors.
        if (icon0ID != 0) drawableIcon0 = VectorDrawableCompat.create(resources, icon0ID, null)!!.mutate() as VectorDrawableCompat
        if (icon1ID != 0) drawableIcon1 = VectorDrawableCompat.create(resources, icon1ID, null)!!.mutate() as VectorDrawableCompat
        if (backgroundID != 0) drawableBackground = ContextCompat.getDrawable(context, backgroundID)!!.mutate() as GradientDrawable
        if (foregroundID != 0) drawableForeground = ContextCompat.getDrawable(context, foregroundID)!!.mutate() as GradientDrawable
    }

    /**
     * Sets the drawable's color to colorInactive, adds the drawable to the ImageView,
     * and ensures the ImageView's width is as specified by the XML attribute.
     */
    private fun drawButton() {
        if (colorInactive != 0 && drawableIcon0 != null) {
            DrawableCompat.setTint(drawableIcon0!!, colorInactive)
            imageViewIcon0.setImageDrawable(drawableIcon0)

            if (drawableIcon1 != null) {
                DrawableCompat.setTint(drawableIcon1!!, colorInactive)
                imageViewIcon1.setImageDrawable(drawableIcon1)
                imageViewIcon1.alpha = 0.0f
            }
        } else {
            imageViewIcon0.setImageDrawable(null)
        }

        var p = imageViewIcon0.layoutParams as PercentRelativeLayout.LayoutParams
        var i = p.percentLayoutInfo
        i.widthPercent = iconWidth
        i.aspectRatio = 1f
        i.leftMarginPercent = iconLeft
        i.topMarginPercent = iconTop

        p = imageViewIcon1.layoutParams as PercentRelativeLayout.LayoutParams
        i = p.percentLayoutInfo
        i.widthPercent = iconWidth
        i.aspectRatio = 1f
        i.leftMarginPercent = iconLeft
        i.topMarginPercent = iconTop

        if (drawableBackground != null && drawableForeground != null) {
            imageViewBackground!!.setImageDrawable(drawableBackground)
            imageViewForeground!!.setImageDrawable(drawableForeground)

            p = imageViewForeground!!.layoutParams as PercentRelativeLayout.LayoutParams
            i = p.percentLayoutInfo
            i.widthPercent = foregroundWidth
            i.aspectRatio = 1f
        }
    }

    /**
     * Changes the icon's color according to the given colors and offset. This method would
     * be used in cases where a ViewPager's scroll events cause color changes, for example.

     * @param offset Fraction passed to the evaluator.
     * *
     * @param startColor Starting color passed to the evaluator.
     * *
     * @param endColor End color passed to the evaluator.
     */
    fun switchColor(offset: Float, startColor: Int, endColor: Int) {
        DrawableCompat.setTint(drawableIcon0!!, argbEvaluator.evaluate(offset, startColor, endColor) as Int)
        imageViewIcon0.setImageDrawable(drawableIcon0)
        imageViewIcon0.invalidate()
    }

    /**
     * Animates the icon's color between colorActive and colorInactive, using the duration
     * given. Allows for a userListener to fire when the animation completes.

     * @param active Flag to indicate if the animation should be from inactive to active.
     * *
     * @param duration Duration that the animation should run for.
     * *
     * @param onSwitchListener Interface that fires when the animation completes.
     */
    fun switchColor(active: Boolean, duration: Int, onSwitchListener: OnSwitchListener?) {
        val animator = ValueAnimator()
        if (!active) {
            animator.setIntValues(colorActive, colorInactive)
        } else {
            animator.setIntValues(colorInactive, colorActive)
        }

        animator.setEvaluator(ArgbEvaluator())
        animator.addUpdateListener { animation ->
            DrawableCompat.setTint(drawableIcon0!!, animation.animatedValue as Int)
            imageViewIcon0.setImageDrawable(drawableIcon0)
            imageViewIcon0.invalidate()
        }
        if (onSwitchListener != null) {
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    onSwitchListener.onSwitch()
                }
            })
        }
        animator.duration = duration.toLong()
        animator.start()
    }

    /**
     * Interface used to indicate when the icon's color switching animation completes.
     * The color switching animation would be in vain if the button's normal onTouchEvent
     * method is used.
     */
    interface OnSwitchListener {
        fun onSwitch()
    }

    /**
     * Changes the button icon by cross-fading the separate icon image views.

     * @param duration Duration the animation should take.
     */
    fun switchIcons(duration: Int) {
        if (imageViewIcon0.alpha > 0.0f) {
            imageViewIcon0.animate().withLayer().alpha(0.0f).duration = duration.toLong()
            imageViewIcon1.animate().withLayer().alpha(1.0f).duration = duration.toLong()
        } else {
            imageViewIcon0.animate().withLayer().alpha(1.0f).duration = duration.toLong()
            imageViewIcon1.animate().withLayer().alpha(0.0f).duration = duration.toLong()
        }
    }

    /**
     * Changes the button's icon's alpha value.

     * @param alpha Alpha value the icon should be set to.
     */
    fun setIconAlpha(alpha: Float) {
        imageViewIcon0.alpha = alpha
    }

    /**
     * Animates the button's icon's alpha value.

     * @param alpha Alpha value the icon should be animated to.
     * *
     * @param duration Duration of the animation.
     */
    fun animateIconAlpha(alpha: Float, duration: Int) {
        imageViewIcon0.animate().withLayer().alpha(alpha).duration = duration.toLong()
    }

    /**
     * Changes the background/foreground drawable colors according to the given colors and
     * offset. This method would be used in cases where a ViewPager's scroll events cause
     * color changes, for example.

     * @param offset Fraction passed to the evaluator.
     * *
     * @param startTopColor Starting top color of drawableForeground passed to the evaluator.
     * *
     * @param endTopColor End top color of drawableForeground passed to the evaluator.
     * *
     * @param startBottomColor Starting bottom color of drawableForeground passed to the evaluator.
     * *
     * @param endBottomColor End bottom color of drawableForeground passed to the evaluator.
     * *
     * @param startBackColor Starting color of drawableBackground passed to the evaluator.
     * *
     * @param endBackColor End color of drawableBackground passed to the evaluator.
     */
    fun switchColors(offset: Float, startTopColor: Int, endTopColor: Int, startBottomColor: Int, endBottomColor: Int, startBackColor: Int, endBackColor: Int) {
        if (drawableBackground != null && drawableForeground != null) {
            val f = Math.round(width * (1 - foregroundWidth))

            val colorTop = argbEvaluator.evaluate(offset, startTopColor, endTopColor) as Int
            val colorBottom = argbEvaluator.evaluate(offset, startBottomColor, endBottomColor) as Int
            val colorBack = argbEvaluator.evaluate(offset, startBackColor, endBackColor) as Int

            drawableForeground!!.colors = intArrayOf(colorTop, colorBottom)
            DrawableCompat.setTint(drawableBackground!!, colorBack)

            //imageViewBackground.setImageDrawable(drawableBackground);
            //imageViewForeground.setImageDrawable(drawableForeground);
        }
    }

    /**
     * Animates the background/foreground drawable colors according to the given colors and
     * duration.

     * @param startTopColor Starting top color of drawableForeground passed to the evaluator.
     * *
     * @param endTopColor End top color of drawableForeground passed to the evaluator.
     * *
     * @param startBottomColor Starting bottom color of drawableForeground passed to the evaluator.
     * *
     * @param endBottomColor End bottom color of drawableForeground passed to the evaluator.
     * *
     * @param duration Duration that the animation should run for.
     */
    fun switchColors(startTopColor: Int, endTopColor: Int, startBottomColor: Int, endBottomColor: Int, duration: Int) {
        val animator = ValueAnimator()
        animator.setIntValues(startTopColor, endTopColor)
        animator.setEvaluator(argbEvaluator)
        animator.addUpdateListener { animation ->
            val colorTop = argbEvaluator.evaluate(animation.animatedFraction, startTopColor, endTopColor) as Int
            val colorBottom = argbEvaluator.evaluate(animation.animatedFraction, startBottomColor, endBottomColor) as Int

            drawableForeground!!.colors = intArrayOf(colorTop, colorBottom)
        }
        animator.duration = duration.toLong()
        animator.start()
    }

    /**
     * Flag to indicate if the button should scale on touch and fire callbacks
     * for touch releases.
     */
    private val shouldScale: Boolean = false

    /**
     * Interface used to communicate the button's touch/hold state.
     */
    private val onHoldListener: OnHoldListener? = null

    /**
     * Flag to indicate if the timer is still running or not.
     */
    private var timerRunning = false

    /**
     * Flag to indicate if the timer should cancel itself.
     */
    private var shouldCancel = false

    /**
     * Flag to indicate if the button's scaling animation has completed.
     */
    private var animationDone = false

    /**
     * Time used to calculate how long the button is held down for.
     */
    private var touchStartTime: Long = 0

    /**
     * Flag to indicate if the start of a hold event has triggered a callback.
     */
    private var holdStartTriggered = false

    /**
     * Interface used to indicate when the button has been clicked, and when it is being held down.
     */
    interface OnHoldListener {
        fun onClick()
        fun onHoldStarted()
        fun onHoldEnded()
    }

    /**
     * Standard onTouchEvent, overridden to allow for some complex animating behaviour.
     * Simply put, this method scales the button to 1.2 times its normal size when it is touched.
     * A timer then runs every 50 milliseconds and checks if the button is still being held down.
     * If the button is held down for shorter than 350 milliseconds, the timer will reanimate the
     * button down to its original scale as soon as the initial animation has completed, and will
     * fire a callback to notify any listeners that the button was clicked, not held down. If the
     * button is held down for longer, the timer immediately animates it back to its original scale
     * and fires a callback indicating it was held down.

     * TODO: Try and simplify this.

     * @param event Standard motion event.
     * *
     * @return Flag indicating if the view was touched.
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (shouldScale) {
            when (event.actionMasked) {
                ACTION_DOWN -> {
                    touchStartTime = System.currentTimeMillis()
                    animate().withLayer().scaleX(1.2f).scaleY(1.2f).setDuration(150).withEndAction { animationDone = true }
                    post(object : Runnable {
                        override fun run() {
                            val duration = System.currentTimeMillis() - touchStartTime
                            if (animationDone && shouldCancel) {
                                animate().withLayer().scaleX(1.0f).scaleY(1.0f).duration = 150
                                resetRunnable()
                            }
                            if (duration >= 350 && timerRunning) {
                                if (onHoldListener != null && !holdStartTriggered) {
                                    holdStartTriggered = true
                                    onHoldListener.onHoldStarted()
                                }
                            }
                            if (duration >= 31500) {
                                resetButton()
                            }
                            if (timerRunning) postDelayed(this, 50)
                        }
                    })
                    timerRunning = true
                }
                ACTION_UP -> {
                    val duration = System.currentTimeMillis() - touchStartTime
                    if (duration < 350) {
                        onHoldListener?.onClick()
                        shouldCancel = true
                    } else {
                        resetButton()
                    }
                }
            }
            return true
        }
        return super.onTouchEvent(event)
    }

    /**
     * Method used to cancel running timer and task and reset
     * all flags involved in the timer animation.
     */
    private fun resetRunnable() {
        timerRunning = false
        animationDone = false
        shouldCancel = false
    }

    /**
     * Method used to reset the button's state.
     */
    private fun resetButton() {
        resetRunnable()
        animate().withLayer().scaleX(1.0f).scaleY(1.0f).duration = 150
        if (onHoldListener != null) {
            holdStartTriggered = false
            onHoldListener.onHoldEnded()
        }
    }
}