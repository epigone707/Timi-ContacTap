package edu.umich.yanfuguo.contactap.ui

import android.R
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import edu.umich.yanfuguo.contactap.databinding.ActivityShareBinding
import edu.umich.yanfuguo.contactap.R.color.share_active
import edu.umich.yanfuguo.contactap.R.color.share_inactive
import android.widget.ArrayAdapter
import edu.umich.yanfuguo.contactap.toast

class ShareActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private var isSharing = false
    private lateinit var share_view: ActivityShareBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        share_view = ActivityShareBinding.inflate(layoutInflater)
        setContentView(share_view.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        val items = arrayOf("Personal", "Business", "EECS 441")
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_dropdown_item, items)
        share_view.profileSelector.adapter = adapter
        share_view.profileSelector.onItemSelectedListener = this;
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        toast("Selected profile $pos")
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }

    fun toggleShare(view: View?){
        isSharing = !isSharing;
        share_view.shareButton.backgroundTintList = ColorStateList.valueOf(
            getColor(if(isSharing) share_active else share_inactive));
        share_view.shareButton.text = if (isSharing) "SHAREING" else "SHARE";
    }
}
