package com.bogdan.codeforceswatcher.components

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import android.widget.TextView
import com.bogdan.codeforceswatcher.R
import kotlinx.android.synthetic.main.input_field.view.*

class InputField(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    enum class Type {
        EMAIL,
        PASSWORD,
        TEXT,
        TEXT_MULTILINE;

        val textViewInputType: Int
            get() = when (this) {
                EMAIL -> InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                PASSWORD -> InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_PASSWORD
                TEXT -> InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                TEXT_MULTILINE -> InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_FLAG_MULTI_LINE or
                        InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            }

        val maxInputLines: Int
            get() = when (this) {
                TEXT_MULTILINE -> Int.MAX_VALUE
                else -> 1
            }
    }

    sealed class Action(
            val imeActionId: Int
    ) {

        open val onClick: (() -> Unit)? = null

        data class Go(
                override val onClick: (() -> Unit)
        ) : Action(EditorInfo.IME_ACTION_GO)

        object Next : Action(EditorInfo.IME_ACTION_NEXT)
    }

    init {
        View.inflate(context, R.layout.input_field, this)
    }

    fun configure(
            labelText: CharSequence? = null,
            labelTextResId: Int? = null,
            inputHint: CharSequence? = null,
            inputHintResId: Int? = null,
            inputText: CharSequence? = null,
            type: Type = Type.TEXT,
            minLines: Int = 1,
            action: Action? = null
    ) {
        editText.hint = when {
            inputHint != null -> inputHint
            inputHintResId != null && inputHintResId != 0 -> resources.getString(inputHintResId)
            else -> null
        }
        editText.setText(inputText ?: "", TextView.BufferType.EDITABLE)
        editText.setSelection(0, editText.text?.length ?: 0)
        editText.inputType = type.textViewInputType
        editText.maxLines = type.maxInputLines
        editText.minLines = minLines
        if (action != null) {
            editText.imeOptions = action.imeActionId
            editText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == action.imeActionId && action.onClick != null) {
                    action.onClick?.invoke()
                    true
                } else {
                    false
                }
            }
        } else {
            editText.imeOptions = EditorInfo.IME_ACTION_NONE
            editText.setOnEditorActionListener(null)
        }
        label.text = when {
            labelText != null -> labelText
            labelTextResId != null && labelTextResId != 0 -> resources.getString(labelTextResId)
            else -> ""
        }
        label.visibility = if (label.text?.any() == true) View.VISIBLE else View.GONE
    }
}
