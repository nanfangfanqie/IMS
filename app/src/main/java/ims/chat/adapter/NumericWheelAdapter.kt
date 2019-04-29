package ims.chat.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import ims.chat.utils.citychoose.view.adapter.AbstractWheelTextAdapter


class NumericWheelAdapter : AbstractWheelTextAdapter {

    // Values
    private var minValue: Int = 0
    private var maxValue: Int = 0

    // format
    private var format: String? = null

    private var label: String? = null

    private var multiple: Int = 0

    /**
     * Constructor
     * @param context the current context
     * @param minValue the wheel min value
     * @param maxValue the wheel max value
     * @param format the format string
     */
    @JvmOverloads
    constructor(
        context: Context,
        minValue: Int = DEFAULT_MIN_VALUE,
        maxValue: Int = DEFAULT_MAX_VALUE,
        format: String? = null
    ) : super(context) {

        this.minValue = minValue
        this.maxValue = maxValue
        this.format = format
    }

    constructor(context: Context, minValue: Int, maxValue: Int, format: String, multiple: Int) : super(context) {

        this.minValue = minValue
        this.maxValue = maxValue
        this.format = format
        this.multiple = multiple
    }

    public override fun getItemText(index: Int): CharSequence? {
        if (index >= 0 && index < itemsCount) {

            var value = 0
            if (multiple != 0) {
                value = minValue + index * multiple
            } else {
                value = minValue + index
            }
            return if (format != null) String.format(format!!, value) else Integer.toString(value)
        }
        return null
    }

    override fun getItemsCount(): Int {
        return maxValue - minValue + 1
    }

    override fun getItem(index: Int, convertView: View?, parent: ViewGroup): View? {
        var convertView = convertView
        if (index in 0 until itemsCount) {
            if (convertView == null) {
                convertView = getView(itemResourceId, parent)
            }
            val textView = getTextView(convertView, itemTextResourceId)
            if (textView != null) {
                var text = getItemText(index)
                if (text == null) {
                    text = ""
                }
                textView.text = text.toString() + label!!
                textView.setPadding(0, 3, 0, 3)
                if (itemResourceId == AbstractWheelTextAdapter.TEXT_VIEW_ITEM_RESOURCE) {
                    configureTextView(textView)
                }
            }
            return convertView
        }
        return null
    }

    fun setLabel(label: String) {
        this.label = label
    }

    companion object {

        /** The default min value  */
        val DEFAULT_MAX_VALUE = 9

        /** The default max value  */
        private val DEFAULT_MIN_VALUE = 0
    }
}
