package com.karansyd4.pinentrycustomview.view.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Editable
import android.util.AttributeSet
import android.util.Log
import android.view.ActionMode
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.karansyd4.pinentrycustomview.R

/**
 *
 * This class extends AppCompatEditText and draws boxes for user to enter pin code.
 * You can modify following attributes in the XML for this PinEntryEditText:
 * 1. mNumChars : The number of chars in the passcode
 * 2. mIsMask : true, if the mask is required; false otherwise
 * 3. mMaskChar: The mask character if, mask is required
 * 4. mBorderColor : The color of the rectangle border for each box
 * 5. mTextColor: The color of the text for pin
 *
 * Masking:-
 *      The characters are masked as follows: When you type and move to the next character, the previous
 *      character is masked. Eg.: 12345 will be displayed as ****5. In order to make changes to the
 *      mask pattern, make changes in getMaskText() method.
 *
 */
class PinEntryEditText : AppCompatEditText, View.OnKeyListener {

    private lateinit var mCanvas: Canvas
    private val TAG = "PinEntryEditText"

    /**
     * By default the attributes for this edit text will have below mentioned vales.
     */
    companion object {
        const val DEFAULT_PIN_SIZE = 6 //default and maximum length for PIN
        const val DEFAULT_IS_MASK = true // by default mask is true
        const val DEFAULT_MASK_CHAR = "*"
        const val DEFAULT_BORDER_COLOR = -16777216 // black color for border by default
        const val DEFAULT_TEXT_COLOR = -16777216 // black color for text by default
        const val DEFAULT_BORDER_WIDTH = 5f // 5dp id default border width
    }

    //Canvas details
    private var mCanvasWidth: Int = 0
    private var mCanvasHeight: Int = 0
    private var mCanvasLeft: Int = 0
    private var mCanvasTop: Int = 0
    private var mCanvasRight: Int = 0
    private var mCanvasBottom: Int = 0

    //custom attributes
    private var mNumChars =
        DEFAULT_PIN_SIZE // number of rectangles by default
    private var mBorderColor: Int =
        DEFAULT_BORDER_COLOR
    private var mTextColor: Int =
        DEFAULT_TEXT_COLOR

    private var mIsMask =
        DEFAULT_IS_MASK
    private var mMaskChar: String =
        DEFAULT_MASK_CHAR

    private lateinit var mStrokePaint: Paint

    private var mSpace = 10f  //space between each rectangle - 10dp
    private var mCharSize = 40f  // width of rectangle - 40dp

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    /**
     * The onDraw method will draw the rectangle boxes and the text entered by user.
     */
    override fun onDraw(canvas: Canvas) {

        mCanvas = canvas

        mCanvasWidth = width - paddingStart - paddingEnd
        mCanvasHeight = height - paddingTop - paddingBottom
        mCanvasLeft = paddingStart
        mCanvasTop = paddingTop
        mCanvasRight = width - paddingEnd
        mCanvasBottom = height - paddingBottom

        var startX = width / 2 - (((mNumChars - 1) * (mSpace) + (mNumChars * mCharSize)) / 2)

        //Text Paint
        paint.color = getCharColor()
        val text = text
        val textLength = text?.length ?: 0

        val textWidths = FloatArray(textLength)
        paint.getTextWidths(getText(), 0, textLength, textWidths)

        var i = 0
        while (i < mNumChars) {

            val lineSpacing = (mCanvasBottom - mCanvasTop) / 3.0f

            canvas.drawRect(
                startX, mCanvasTop.toFloat(), startX + mCharSize, mCanvasBottom.toFloat(), mStrokePaint
            )

            getText()?.let { it ->
                if (it.length > i) {
                    val middle = startX + mCharSize / 2

                    canvas.drawText(
                        getDrawText(text),
                        i,
                        i + 1,
                        middle - textWidths[0] / 2,
                        mCanvasBottom - lineSpacing,
                        paint
                    )
                }
            }

            startX += mCharSize + mSpace
            i++
        }
    }

    /**
     * Initialize the following custom attributes:
     * mNumChars, mIsMask, mMaskChar, mBorderColor, mTextColor
     */
    private fun init(context: Context, attrs: AttributeSet?) {

        setBackgroundResource(0)

        /* Convert DP to pixels */
        mSpace = dp2px(mSpace)
        mCharSize = dp2px(mCharSize)

        this.setOnKeyListener(this)

        if (null != attrs) {

            context.obtainStyledAttributes(attrs, R.styleable.PinEntryEditText).apply {
                try {
                    setBorderColor(getColor(R.styleable.PinEntryEditText_borderColor,
                        DEFAULT_BORDER_COLOR
                    ))
                    setCharColor(getColor(R.styleable.PinEntryEditText_textColor,
                        DEFAULT_TEXT_COLOR
                    ))
                    mNumChars = getInt(R.styleable.PinEntryEditText_numChars,
                        DEFAULT_PIN_SIZE
                    )
                    mIsMask = getBoolean(R.styleable.PinEntryEditText_isMask,
                        DEFAULT_IS_MASK
                    )
                    mMaskChar = getString(R.styleable.PinEntryEditText_maskChar).toString()
                } catch (e: Exception) {
                    Log.e(TAG, "${e.message}")
                } finally {
                    setStrokePaint()
                    recycle()
                }
            }
        }

        //disable copy and paste for Pin Entry Edit Text
        super.setCustomSelectionActionModeCallback(
            object : ActionMode.Callback {
                override fun onPrepareActionMode(
                    mode: ActionMode,
                    menu: Menu
                ): Boolean {
                    return false
                }

                override fun onDestroyActionMode(mode: ActionMode) {}

                override fun onCreateActionMode(
                    mode: ActionMode,
                    menu: Menu
                ): Boolean {
                    return false
                }

                override fun onActionItemClicked(
                    mode: ActionMode,
                    item: MenuItem
                ): Boolean {
                    return false
                }
            })
    }

    /**
     * Provide the text to be drawn by the canvas in OnDraw method.
     * If the masking is enabled, return the masked text to be drawn;
     * otherwise return the text as it is.
     */
    private fun getDrawText(text: Editable?): String {
        if (mIsMask)
            return getMaskText(text)
        else
            return text.toString()
    }

    /**
     * This method will calculate the mask text to display in the PinEntry Box.
     * All the characters except the last character will be masked with mMaskChar.
     *
     */
    private fun getMaskText(text: Editable?): String {
        var maskText = ""

        text?.let { it ->
            var i = 0
            while (i < it.length) {
                if (i == it.length - 1) {
                    maskText += it[i]
                } else {
                    maskText += mMaskChar
                }
                i++
            }
        }

        return maskText
    }

    /**
     * If the keycode is delete, empty the edit text
     */
    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (KeyEvent.KEYCODE_DEL == keyCode) {
            setText("")
        }
        return false
    }

    /**
     * Set stroke paint for border of rectangles in Pin Entry
     */
    private fun setStrokePaint() {
        mStrokePaint = Paint()
        mStrokePaint.style = Paint.Style.STROKE
        mStrokePaint.color = getBorderColor()
        mStrokePaint.strokeWidth =
            DEFAULT_BORDER_WIDTH
    }

    private fun getBorderColor(): Int {
        return mBorderColor
    }

    private fun setBorderColor(borderColor: Int) {
        mBorderColor = borderColor
    }

    private fun getCharColor(): Int {
        return mTextColor
    }

    private fun setCharColor(color: Int) {
        mTextColor = color
    }

    /**
     * Convert dp to pixel
     */
    private fun dp2px(dp: Float): Float {
        val density = resources.displayMetrics.density
        return dp * density
    }
}
