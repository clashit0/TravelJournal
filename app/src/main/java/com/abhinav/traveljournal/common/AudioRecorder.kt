package com.abhinav.traveljournal.common

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.File

class AudioRecorder(private val context: Context) {
    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null

    @RequiresApi(Build.VERSION_CODES.S)
    fun startRecording(): String? {
        return try {
            val file = File(
                context.cacheDir,
                "audio_${System.currentTimeMillis()}.m4a"
            )

            recorder = MediaRecorder(context).apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }

            outputFile = file
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    fun stopRecording(): String? {
        return try {
            recorder?.apply {
                stop()
                release()
            }
            recorder = null
            outputFile?.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}