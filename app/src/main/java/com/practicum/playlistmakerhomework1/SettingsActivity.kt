package com.practicum.playlistmakerhomework1

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.ACTION_SENDTO
import android.content.Intent.EXTRA_SUBJECT
import android.content.Intent.EXTRA_TEXT
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.switchmaterial.SwitchMaterial

const val THEME_KEY = "key_for_theme"
const val THEME = "day_night_theme"

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "IntentReset")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val email = getString(R.string.emailMessage)
        val subject = getString(R.string.subjectMessage)
        val body = getString(R.string.bodyMessage)
        val switchTheme = findViewById<SwitchMaterial>(R.id.switchTheme)
        val sharedSwitch = getSharedPreferences(THEME, MODE_PRIVATE)
        val check = sharedSwitch.getBoolean(THEME_KEY, false)
        switchTheme.isChecked = check
        (applicationContext as App).switchTheme(check)

        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            (applicationContext as App).switchTheme(isChecked)
            sharedSwitch.edit().putBoolean(THEME_KEY, isChecked).apply()
        }

        val backButton = findViewById<Button>(R.id.backSettings)

        backButton.setOnClickListener {
            finish()
        }

        val textView = findViewById<TextView>(R.id.ShareButton)
        textView.setOnClickListener {
            val message = getString(R.string.messageLink)
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(EXTRA_TEXT, message)
            startActivity(Intent.createChooser(sharingIntent, "Share message"))
        }

        findViewById<TextView>(R.id.SupportService).setOnClickListener {

            val mailIntent = Intent(Intent.ACTION_SEND)
            mailIntent.data = Uri.parse("mailto:")
            mailIntent.type = "text/plan"
            mailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email) )
            mailIntent.putExtra(EXTRA_SUBJECT, subject)
            mailIntent.putExtra(EXTRA_TEXT, body)

try {
    startActivity(Intent.createChooser(mailIntent, "Send email"))
} catch (e: Exception){
    Toast.makeText(this,e.message, Toast.LENGTH_LONG).show()
}
        }

        val termsButton = findViewById<TextView>(R.id.terms_button)
        termsButton.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.offerLink)))
            startActivity(browserIntent)
        }

}
}