package com.example.cap_9

import android.os.Bundle
import android.content.res.ColorStateList
import android.util.Log
import android.widget.Button
import android.widget.CompoundButton
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import com.example.cap_9.databinding.ActivityButtonsBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.color.MaterialColors

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityButtonsBinding
    private val removedChipIds = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityButtonsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        configureVideoExamples(savedInstanceState)

        findViewById<Button>(R.id.btn_login).setOnClickListener {
            showEvent(getString(R.string.btn_login))
        }
        findViewById<ImageButton>(R.id.btn_email).setOnClickListener {
            showEvent(getString(R.string.btn_email))
        }
        findViewById<FloatingActionButton>(R.id.fab_viaje).setOnClickListener {
            showEvent(getString(R.string.fab_clicked))
        }

        removedChipIds.addAll(savedInstanceState?.getIntegerArrayList("removed_chips").orEmpty())
        configureChips()
        observeRadioGroup(R.id.rg_destino, R.string.destination_changed)
        observeRadioGroup(R.id.rg_transporte, R.string.transport_changed)
        observeToggle(R.id.cb_seguro, R.string.cb_seguro)
        observeToggle(R.id.cb_cancelable, R.string.cb_cancelable)
        observeToggle(R.id.tg_normal, R.string.tg_normal)
        observeToggle(R.id.tg_custom, R.string.tg_custom)
        observeToggle(R.id.sw_normal, R.string.sw_normal)
        observeToggle(R.id.sw_custom, R.string.sw_custom)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("material_button_enabled", binding.btnMaterial.isEnabled)
        outState.putIntegerArrayList("removed_chips", ArrayList(removedChipIds))
    }

    private fun configureVideoExamples(savedInstanceState: Bundle?) {
        // Botón clásico con findViewById.
        findViewById<Button>(R.id.btn_enviar).setOnClickListener {
            showEvent(getString(R.string.classic_click))
        }

        // Botón Material con View Binding. Una pulsación larga no dispara el clic.
        binding.btnMaterial.apply {
            setText(R.string.material_binding)
            isEnabled = savedInstanceState?.getBoolean("material_button_enabled", true) ?: true
            setOnClickListener {
                showEvent(getString(R.string.material_click))
                isEnabled = false
            }
            setOnLongClickListener {
                showEvent(getString(R.string.material_long_click))
                true
            }
        }

        binding.btnVideoEmail.setOnClickListener {
            showEvent(getString(R.string.btn_email))
        }
        binding.btnVideoEmail.setOnLongClickListener {
            showEvent(getString(R.string.image_long_click))
            true
        }
    }

    private fun configureChips() {
        val group = findViewById<ChipGroup>(R.id.cg_condiciones)
        val extraChip = Chip(this).apply {
            id = R.id.chip_extra
            setText(R.string.chip_extra)
            isCloseIconVisible = true
        }
        group.addView(extraChip)

        group.children.filterIsInstance<Chip>().toList().forEach { chip ->
            if (chip.id in removedChipIds) {
                group.removeView(chip)
            } else {
                chip.setCloseIconResource(R.drawable.ic_close)
                chip.closeIconTint = ColorStateList.valueOf(
                    MaterialColors.getColor(chip, com.google.android.material.R.attr.colorOnSurface)
                )
                chip.setOnClickListener {
                    showEvent(getString(R.string.chip_clicked, chip.text))
                }
                chip.setOnCloseIconClickListener {
                    removedChipIds.add(chip.id)
                    group.removeView(chip)
                    showEvent(getString(R.string.chip_removed, chip.text))
                }
            }
        }
    }

    private fun observeRadioGroup(viewId: Int, messageId: Int) {
        findViewById<RadioGroup>(viewId).setOnCheckedChangeListener { group, checkedId ->
            val selection = group.findViewById<RadioButton>(checkedId)
            if (selection != null) {
                showEvent(getString(messageId, selection.text))
            }
        }
    }

    private fun observeToggle(viewId: Int, labelId: Int) {
        findViewById<CompoundButton>(viewId).setOnCheckedChangeListener { _, checked ->
            val messageId = if (checked) R.string.control_enabled else R.string.control_disabled
            showEvent(getString(messageId, getString(labelId)))
        }
    }

    private fun showEvent(message: String) {
        Log.d("Capitulo9", message)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
