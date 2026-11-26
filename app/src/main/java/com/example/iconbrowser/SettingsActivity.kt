package com.example.iconbrowser

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.iconbrowser.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener { finish() }

        val options = IconSwitcher.options(this)
        val adapter = IconOptionAdapter(options, IconSwitcher.getSelectedId(this)) { option ->
            IconSwitcher.setActiveIcon(this, option.id)
            Toast.makeText(this, getString(R.string.icon_changed_toast, option.label), Toast.LENGTH_SHORT).show()
        }

        binding.optionsList.layoutManager = LinearLayoutManager(this)
        binding.optionsList.adapter = adapter

        binding.autoClearSwitch.isChecked = HistoryPrivacy.isAutoClearEnabled(this)
        binding.autoClearSwitch.setOnCheckedChangeListener { _, isChecked ->
            HistoryPrivacy.setAutoClearEnabled(this, isChecked)
        }

        binding.clearCookiesSwitch.isChecked = HistoryPrivacy.isClearCookiesEnabled(this)
        binding.clearCookiesSwitch.setOnCheckedChangeListener { _, isChecked ->
            HistoryPrivacy.setClearCookiesEnabled(this, isChecked)
        }
    }
}
