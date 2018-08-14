package ch.deletescape.lawnchair.settings.ui

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AlertDialog
import android.support.v7.preference.PreferenceDialogFragmentCompat
import android.util.Log
import android.view.View
import android.widget.NumberPicker
import ch.deletescape.lawnchair.colors.ColorEngine
import com.android.launcher3.R

class GridSizeDialogFragmentCompat : PreferenceDialogFragmentCompat(), ColorEngine.OnAccentChangeListener {

    private val gridSizePreference get() = preference as GridSizePreference

    private var numRows = 0
    private var numColumns = 0

    private lateinit var numRowsPicker: NumberPicker
    private lateinit var numColumnsPicker: NumberPicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val size = gridSizePreference.getSize()
        numRows = savedInstanceState?.getInt(SAVE_STATE_ROWS) ?: size.first
        numColumns = savedInstanceState?.getInt(SAVE_STATE_COLUMNS) ?: size.second
    }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)

        numRowsPicker = view.findViewById(R.id.rowsPicker)
        numColumnsPicker = view.findViewById(R.id.columnsPicker)

        numRowsPicker.minValue = 3
        numRowsPicker.maxValue = 9
        numColumnsPicker.minValue = 3
        numColumnsPicker.maxValue = 9

        numRowsPicker.value = numRows
        numColumnsPicker.value = numColumns

        ColorEngine.getInstance(context!!).addAccentChangeListener(this)
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            gridSizePreference.setSize(numRowsPicker.value, numColumnsPicker.value)
        }
    }

    override fun onPrepareDialogBuilder(builder: AlertDialog.Builder) {
        super.onPrepareDialogBuilder(builder)

        builder.setNeutralButton(R.string.grid_size_default, {_, _ ->
            gridSizePreference.setSize(0, 0)
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(SAVE_STATE_ROWS, numRowsPicker.value)
        outState.putInt(SAVE_STATE_COLUMNS, numColumnsPicker.value)
    }

    override fun onAccentChange(color: Int, foregroundColor: Int) {
        try {
            val mSelectionDivider = NumberPicker::class.java.getDeclaredField("mSelectionDivider")
            mSelectionDivider.isAccessible = true
            val drawable = mSelectionDivider.get(numColumnsPicker) as Drawable
            drawable.setTint(color)
            mSelectionDivider.set(numColumnsPicker, drawable)
            mSelectionDivider.set(numRowsPicker, drawable)
        } catch (e: Exception) {
            Log.e("GridSizeDialog","Failed to set mSelectionDivider", e)
        }
    }

    override fun onDetach() {
        super.onDetach()
        ColorEngine.getInstance(context!!).removeAccentChangeListener(this)
    }

    companion object {
        const val SAVE_STATE_ROWS = "rows"
        const val SAVE_STATE_COLUMNS = "columns"

        fun newInstance(key: String?) = GridSizeDialogFragmentCompat().apply {
            arguments = Bundle(1).apply {
                putString(ARG_KEY, key)
            }
        }
    }
}