package com.example.music

import android.annotation.SuppressLint
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.RotateAnimation
import android.view.animation.ScaleAnimation
import androidx.appcompat.app.AppCompatActivity
import com.example.music.databinding.ActivityMainBinding
import com.google.android.material.slider.Slider
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var mediaPlayer: MediaPlayer
    var isUserChanging = false
    var isPlaying = true
    var mute = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prepareMusic()

        binding.sliderMusic.addOnChangeListener { slider, value, fromUser ->
            binding.txtTimePlay.text = changeMilitoSec(value.toLong())
            isUserChanging = fromUser
        }
        binding.sliderMusic.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {

            }

            override fun onStopTrackingTouch(slider: Slider) {
                mediaPlayer.seekTo(slider.value.toInt())
                isUserChanging = false
            }
        })
        binding.btnPlay.setOnClickListener {
            playPause()
        }
        binding.btnBefore.setOnClickListener {
            seekTo("before")
        }
        binding.btnNext.setOnClickListener {
            seekTo("next")
        }
        binding.btnVolume.setOnClickListener {

            val audioManger = getSystemService(AUDIO_SERVICE) as AudioManager
            if(mute) {
                audioManger.adjustVolume(AudioManager.ADJUST_UNMUTE , AudioManager.FLAG_SHOW_UI)
                binding.btnVolume.setImageResource(R.drawable.ic_volume_on)
                mute = false



            } else {
                audioManger.adjustVolume(AudioManager.ADJUST_MUTE , AudioManager.FLAG_SHOW_UI)
                binding.btnVolume.setImageResource(R.drawable.ic_volume_off)
                mute = true
            }

        }

    }

    private fun seekTo(btn: String) {

        val now = mediaPlayer.currentPosition
        if (btn == "before") {
            mediaPlayer.seekTo(now - 15000)

        } else if (btn == "next") {
            mediaPlayer.seekTo(now + 15000)
        }

    }
    private fun playPause() {

        if (isPlaying) {
            binding.imgCover.clearAnimation()

            val animS = ScaleAnimation(
                1f , 0.75f, 1f , 0.75f , Animation.RELATIVE_TO_SELF , 0.5f , Animation.RELATIVE_TO_SELF , 0.5f
            )
            animS.duration = 500
            animS.fillAfter = true
            binding.imgCover.startAnimation(animS)
            animS.duration = 500
            animS.fillAfter = true

            mediaPlayer.pause()
            isPlaying = false
            binding.btnPlay.setImageResource(R.drawable.ic_play)

        } else {
            val anmiR = RotateAnimation(0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f)
            anmiR.duration = 15000
            anmiR.repeatCount = 1000
            val animS = ScaleAnimation(
                0.75f , 1f, 0.75f , 1f , Animation.RELATIVE_TO_SELF , 0.5f , Animation.RELATIVE_TO_SELF , 0.5f
            )
            animS.duration = 500
            animS.fillAfter = true
            val animset = AnimationSet(false)
            animset.addAnimation(anmiR)
            animset.addAnimation(animS)
            binding.imgCover.startAnimation(animset)

            mediaPlayer.start()
            isPlaying = true
            binding.btnPlay.setImageResource(R.drawable.ic_pause)
        }


    }

    private fun prepareMusic() {

        mediaPlayer = MediaPlayer.create(this, R.raw.music_file)
        mediaPlayer.start()
        isPlaying = true
        binding.btnPlay.setImageResource(R.drawable.ic_pause)

        binding.sliderMusic.valueTo = mediaPlayer.duration.toFloat()
        binding.txtTimeMusic.text = changeMilitoSec(mediaPlayer.duration.toLong())

        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    if (!isUserChanging) {
                        binding.sliderMusic.value = mediaPlayer.currentPosition.toFloat()

                    }

                }
            }
        }, 1000, 1000)

        val anmiR = RotateAnimation(0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f)
        anmiR.duration = 15000
        anmiR.repeatCount = 1000
        binding.imgCover.startAnimation(anmiR)

    }

    @SuppressLint("DefaultLocale")
    private fun changeMilitoSec(duration: Long): String {

        val time = java.lang.String.format("%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(duration),
            TimeUnit.MILLISECONDS.toSeconds(duration) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        )
        return time

    }
}