package com.uniguard.trackable.utils


import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.uniguard.trackable.R

object SoundUtils {
    private var soundPool: SoundPool? = null
    private var soundId: Int? = null

    fun initSound(context: Context) {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(10)
            .setAudioAttributes(audioAttributes)
            .build()

        soundId = soundPool?.load(context, R.raw.barcodebeep, 1)
    }

    fun setupReaderSound() {
        if (soundId != null && soundPool != null) {
            Reader.rrlib.SetSoundID(soundId!!, soundPool!!)
        }
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        soundId = null
    }
}
