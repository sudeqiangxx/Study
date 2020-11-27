package com.zzw.hiltdemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.new_activity.*

/**
 *
 * @author Created by lenna on 2020/9/8
 */
class NewWordActivity : AppCompatActivity() {
    private var editWordView: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_activity)
        editWordView = findViewById(R.id.edit_word)

        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(editWordView?.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val word = editWordView?.text.toString()
                replyIntent.putExtra(EXTRA_REPLY, word)
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }

        view_motion_dispatch.addView(cycleTextList)
        view_motion_dispatch.addView(oval_scale_view)
        val titles = arrayListOf<String>()
        titles.add("聆听自然")
        titles.add("就想听歌")
        titles.add("随便听听")
        titles.add("甜蜜陪伴")
        titles.add("宝宝睡觉")

        cycleTextList.setTitles(titles)
        cycleTextList.setCurrentTitle("随便听听")

    }

    companion object {
        const val EXTRA_REPLY = "com.example.android.wordlistsql.REPLY"
    }
}