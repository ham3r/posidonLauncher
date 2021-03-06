package posidon.launcher.customizations

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import posidon.launcher.Main
import posidon.launcher.R
import posidon.launcher.tools.ColorTools
import posidon.launcher.storage.Settings
import posidon.launcher.tools.Tools
import posidon.launcher.tools.applyFontSetting
import posidon.launcher.view.Spinner


class CustomDrawer : AppCompatActivity() {

    private var icsize: SeekBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyFontSetting()
        setContentView(R.layout.custom_drawer)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        findViewById<View>(R.id.settings).setPadding(0, 0, 0, Tools.navbarHeight)

        icsize = findViewById(R.id.iconsizeslider)
        icsize!!.progress = Settings["icsize", 1]

        val columnslider = findViewById<SeekBar>(R.id.columnslider)
        columnslider.progress = Settings["drawer:columns", 4] - 1
        val c = findViewById<TextView>(R.id.columnnum)
        c.text = Settings["drawer:columns", 4].toString()
        columnslider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                c.text = (progress + 1).toString()
                Settings["drawer:columns"] = progress + 1
                Main.customized = true
            }
        })

        val verticalspacingslider = findViewById<SeekBar>(R.id.verticalspacingslider)
        verticalspacingslider.progress = Settings["verticalspacing", 12]
        val verticalspacingnum = findViewById<TextView>(R.id.verticalspacingnum)
        verticalspacingnum.text = Settings["verticalspacing", 12].toString()
        verticalspacingslider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                verticalspacingnum.text = progress.toString()
                Settings["verticalspacing"] = progress
                Main.customized = true
            }
        })

        findViewById<Switch>(R.id.labelsenabled).isChecked = Settings["labelsenabled", false]
        findViewById<Switch>(R.id.scrollbarEnabled).isChecked = Settings["drawer:scrollbar_enabled", false]
        findViewById<View>(R.id.bgColorPrev).background = ColorTools.colorCircle(Settings["drawer:background_color", -0x78000000])
        findViewById<View>(R.id.labelColorPrev).background = ColorTools.colorCircle(Settings["labelColor", 0xeeeeeeee.toInt()])

        findViewById<Spinner>(R.id.sortingOptions).data = resources.getStringArray(R.array.sortingAlgorithms)
        findViewById<Spinner>(R.id.sortingOptions).selectionI = Settings["drawer:sorting", 0]

        findViewById<Switch>(R.id.blurswitch).isChecked = Settings["drawer:blur", true]
        val blurSlider = findViewById<SeekBar>(R.id.blurSlider)
        blurSlider.progress = Settings["drawer:blur:rad", 15f].toInt()
        val blurNum = findViewById<TextView>(R.id.blurNum)
        blurNum.text = Settings["drawer:blur:rad", 15f].toInt().toString()
        blurSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                window.setBackgroundDrawable(BitmapDrawable(resources, Tools.blurredWall(progress.toFloat())))
                Settings["drawer:blur:rad"] = progress.toFloat()
                blurNum.text = progress.toString()
            }
        })

        val blurLayerSlider = findViewById<SeekBar>(R.id.blurLayerSlider)
        blurLayerSlider.progress = Settings["blurLayers", 1] - 1
        val blurLayerNum = findViewById<TextView>(R.id.blurLayerNum)
        blurLayerNum.text = Settings["blurLayers", 1].toString()
        blurLayerSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                blurLayerNum.text = (progress + 1).toString()
                Settings["blurLayers"] = progress + 1
            }
        })

        findViewById<Spinner>(R.id.sectionLetter).data = resources.getStringArray(R.array.namePositions)
        findViewById<Spinner>(R.id.sectionLetter).selectionI = Settings["drawer:sec_name_pos", 0]

        findViewById<Switch>(R.id.sectionsEnabled).isChecked = Settings["drawer:sections_enabled", false]
    }

    fun pickBGColor(v: View) = ColorTools.pickColor(this, Settings["drawer:background_color", -0x78000000]) {
        v as ViewGroup
        v.getChildAt(1).background = ColorTools.colorCircle(it)
        Settings["drawer:background_color"] = it
        Main.shouldSetApps = true
    }

    fun pickLabelColor(v: View) = ColorTools.pickColor(this, Settings["labelColor", 0xeeeeeeee.toInt()]) {
        v as ViewGroup
        v.getChildAt(1).background = ColorTools.colorCircle(it)
        Settings["labelColor"] = it
        Main.shouldSetApps = true
    }

    override fun onPause() {
        Main.setDrawerScrollbarEnabled(findViewById<Switch>(R.id.scrollbarEnabled).isChecked)
        Settings.apply {
            putNotSave("icsize", icsize!!.progress)
            putNotSave("labelsenabled", findViewById<Switch>(R.id.labelsenabled).isChecked)
            if (get("drawer:sections_enabled", false) != findViewById<Switch>(R.id.sectionsEnabled).isChecked) {
                putNotSave("drawer:sections_enabled", findViewById<Switch>(R.id.sectionsEnabled).isChecked)
                Main.shouldSetApps = true
                Main.customized = true
            }
            if (get("drawer:sorting", 0) != findViewById<Spinner>(R.id.sortingOptions).selectionI) {
                putNotSave("drawer:sorting", findViewById<Spinner>(R.id.sortingOptions).selectionI)
                Main.shouldSetApps = true
            }
            if (get("drawer:sec_name_pos", 0) != findViewById<Spinner>(R.id.sectionLetter).selectionI) {
                putNotSave("drawer:sec_name_pos", findViewById<Spinner>(R.id.sectionLetter).selectionI)
                Main.shouldSetApps = true
            }
            putNotSave("drawer:blur", findViewById<Switch>(R.id.blurswitch).isChecked)
            apply()
        }
        super.onPause()
    }
}