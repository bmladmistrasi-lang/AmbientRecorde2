package id.bml.ambientrecorder

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Visible, consent-based 30-minute test service.
 * Recording starts at t=0, 5, 10, 15, 20 and 25 minutes.
 * Each recording lasts 20 seconds. Files are stored in app-private external storage.
 *
 * This test build writes AAC audio in an M4A container. MP3 conversion and Drive sync
 * are not included in this build and must not be represented as available features.
 */
class RecorderService : Service() {
    private val handler = Handler(Looper.getMainLooper())
    private var recorder: MediaRecorder? = null
    private var running = false
    private val startedAt = System.currentTimeMillis()
    private val testDurationMs = 30 * 60 * 1000L
    private val intervalMs = 5 * 60 * 1000L
    private val durationMs = 20 * 1000L

    private val cycle = object : Runnable {
        override fun run() {
            if (!running) return
            val elapsed = System.currentTimeMillis() - startedAt
            if (elapsed >= testDurationMs) {
                stopSelf()
                return
            }
            startOneRecording()
        }
    }

    override fun onCreate() {
        super.onCreate()
        createChannel()
        startForeground(1001, notification("Tes 30 menit aktif; menunggu sesi pertama"))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!running) {
            running = true
            handler.post(cycle)
            handler.postDelayed({ if (running) stopSelf() }, testDurationMs)
        }
        return START_NOT_STICKY
    }

    private fun startOneRecording() {
        if (!running) return
        try {
            val dir = File(getExternalFilesDir(null), "AmbientRecorder").apply { mkdirs() }
            val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val out = File(dir, "$stamp.m4a")
            val r = if (Build.VERSION.SDK_INT >= 31) MediaRecorder(this) else MediaRecorder()
            recorder = r
            r.setAudioSource(MediaRecorder.AudioSource.MIC)
            r.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            r.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            r.setAudioSamplingRate(44100)
            r.setAudioEncodingBitRate(96000)
            r.setOutputFile(out.absolutePath)
            r.setOnErrorListener { _, _, _ -> safelyStopRecorder() }
            r.prepare()
            r.start()
            updateNotification("Merekam 20 detik; file disimpan di perangkat")
            handler.postDelayed({
                safelyStopRecorder()
                if (running) {
                    updateNotification("Menunggu sesi berikutnya; rekaman tersimpan lokal")
                    val elapsed = System.currentTimeMillis() - startedAt
                    val delay = (intervalMs - durationMs).coerceAtLeast(0)
                    if (elapsed < testDurationMs) handler.postDelayed(cycle, delay)
                }
            }, durationMs)
        } catch (e: Exception) {
            safelyStopRecorder()
            updateNotification("Perekaman gagal; periksa izin dan mikrofon")
            val elapsed = System.currentTimeMillis() - startedAt
            if (running && elapsed < testDurationMs) handler.postDelayed(cycle, intervalMs)
        }
    }

    private fun safelyStopRecorder() {
        try { recorder?.stop() } catch (_: Exception) { }
        try { recorder?.reset(); recorder?.release() } catch (_: Exception) { }
        recorder = null
    }

    private fun notification(message: String): Notification {
        val builder = if (Build.VERSION.SDK_INT >= 26)
            Notification.Builder(this, "ambient_recording")
        else Notification.Builder(this)
        return builder.setContentTitle("Ambient Recorder — Tes 30 Menit")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(message: String) {
        getSystemService(NotificationManager::class.java).notify(1001, notification(message))
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                "ambient_recording", "Status perekaman",
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = "Notifikasi status perekaman yang sedang aktif" }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        running = false
        handler.removeCallbacksAndMessages(null)
        safelyStopRecorder()
        if (Build.VERSION.SDK_INT >= 26) {
            getSystemService(NotificationManager::class.java).cancel(1001)
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
