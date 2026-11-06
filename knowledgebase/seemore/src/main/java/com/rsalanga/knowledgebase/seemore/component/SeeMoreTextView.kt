package com.rsalanga.knowledgebase.seemore.component

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import com.google.android.material.textview.MaterialTextView
import com.rsalanga.knowledgebase.seemore.R

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan

class SeeMoreTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private var initialMaxLines: Int = Int.MAX_VALUE

    val contentTextView = MaterialTextView(context).apply {
        layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        ellipsize = TextUtils.TruncateAt.END
    }

    val seeMoreTextView = MaterialTextView(context).apply {
        layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
            gravity = Gravity.BOTTOM or Gravity.END
        }
        val seeMoreText = SpannableStringBuilder().apply {
            append("... ")
            val seeMoreStart = length
            append("See more")
            setSpan(
                ForegroundColorSpan(context.getColor(android.R.color.holo_blue_light)),
                seeMoreStart,
                length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        text = seeMoreText
        visibility = INVISIBLE

        val typedValue = TypedValue()
        val backgroundColor = if (context.theme.resolveAttribute(android.R.attr.colorBackground, typedValue, true)) {
            typedValue.data
        } else {
            context.getColor(android.R.color.white)
        }
        setBackgroundColor(backgroundColor)
        val paddingPx = (8 * context.resources.displayMetrics.density).toInt()
        setPadding(paddingPx, 0, 0, 0)
    }

    init {
        context.obtainStyledAttributes(attrs, R.styleable.SeeMoreTextView).apply {
            initialMaxLines = getInt(R.styleable.SeeMoreTextView_android_maxLines, Int.MAX_VALUE)
            contentTextView.maxLines = initialMaxLines
            val textAttr = getString(R.styleable.SeeMoreTextView_android_text)
            if (textAttr != null) {
                setText(textAttr)
            }
            recycle()
        }

        addView(contentTextView)
        addView(seeMoreTextView)

        val expandAction = OnClickListener {
            if (seeMoreTextView.isVisible) {
                contentTextView.maxLines = Int.MAX_VALUE
                seeMoreTextView.isVisible = false
            }
        }
        seeMoreTextView.setOnClickListener(expandAction)
        setOnClickListener(expandAction)
    }

    fun setText(text: CharSequence) {
        contentTextView.maxLines = initialMaxLines
        contentTextView.text = text
        seeMoreTextView.visibility = INVISIBLE
        contentTextView.doOnLayout {
            seeMoreTextView.isVisible = contentTextView.isEllipsized()
        }
    }

    private fun TextView.isEllipsized(): Boolean {
        val layout = layout ?: return false

        for (i in 0 until layout.lineCount) {
            if (layout.getEllipsisCount(i) > 0) {
                return true
            }
        }

        return false
    }
}
