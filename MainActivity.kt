package id.bml.ambientrecorder

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private val requestCode = 41

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 60, 40, 40)
        }
        layout.addView(TextView(this).apply {
            text = "AMBIENT RECORDER\nUJI COBA 30 MENIT"
            textSize = 24f
        })
        layout.addView(TextView(this).apply {
            text = "\nJadwal: rekam 20 detik setiap 5 menit.\nTes berhenti otomatis setelah 30 menit.\nFormat prototipe: M4A/AAC.\n"
            textSize = 16f
        })
        layout.addView(Button(this).apply {
            text = "MULAI TES 30 MENIT"
            setOnClickListener { checkAndStart() }
        })
        layout.addView(Button(this).apply {
            text = "BERHENTI SEKARANG"
            setOnClickListener {
                stopService(Intent(this@MainActivity, RecorderService::class.java))
                Toast.makeText(this@MainActivity, "Layanan dihentikan", Toast.LENGTH_SHORT).show()
            }
        })
        setContentView(layout)
    }

    private fun checkAndStart() {
        val permissions = mutableListOf(Manifest.permission.RECORD_AUDIO)
        if (Build.VERSION.SDK_INT >= 33) permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        val missing = permissions.filter {
            checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
        }
        if (missing.isNotEmpty()) requestPermissions(missing.toTypedArray(), requestCode)
        else startRecorder()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == this.requestCode && grantResults.isNotEmpty() &&
            grantResults.all { it == PackageManager.PERMISSION_GRANTED }) startRecorder()
        else Toast.makeText(this, "Izin mikrofon dan notifikasi diperlukan", Toast.LENGTH_LONG).show()
    }

    private fun startRecorder() {
        val intent = Intent(this, RecorderService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(intent)
        else startService(intent)
        Toast.makeText(this, "Tes 30 menit dimulai", Toast.LENGTH_SHORT).show()
    }
}
