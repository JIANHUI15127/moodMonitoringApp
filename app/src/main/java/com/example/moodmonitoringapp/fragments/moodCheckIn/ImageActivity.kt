package com.example.moodmonitoringapp.fragments.moodCheckIn

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.moodmonitoringapp.R
import com.example.moodmonitoringapp.data.ScreenState
import com.example.moodmonitoringapp.databinding.ActivityImageBinding
import com.example.moodmonitoringapp.fragments.InsetsHelper
import com.example.moodmonitoringapp.model.Recognized
import com.example.moodmonitoringapp.util.extensions.toTitleCase
import com.example.moodmonitoringapp.util.extensions.useViewModel
import com.example.moodmonitoringapp.util.values.IMAGE_PATH
import com.example.moodmonitoringapp.util.values.RECOGNIZED_KEY
import com.example.moodmonitoringapp.viewModel.ImageViewModel

import kotlinx.android.synthetic.main.activity_image.*
import org.ocpsoft.prettytime.PrettyTime

class ImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageBinding
    private lateinit var viewModel: ImageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rootViewImage.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        viewModel = useViewModel(this, ImageViewModel::class.java)
        InsetsHelper.handleBottom(true, otherEmotionsTextView)

        val path = intent.getParcelableExtra<Uri>(IMAGE_PATH)
        val recognized = intent.getParcelableExtra<Recognized>(RECOGNIZED_KEY)

        if (path != null) {
            showImage(path)
            viewModel.recognize(path)
        } else if (recognized != null) {
            viewModel.updateRecognized(recognized)
            showImage(Uri.parse(recognized.image))
            showRecognized(recognized)
        }

        initObservers()
    }

    private fun initObservers() {
        viewModel.state.observe(this, Observer {
            setState(it)
        })

        viewModel.recognized.observe(this, Observer {
            showRecognized(it)
        })
    }

    private fun showImage(uri: Uri) {
        Glide
            .with(this)
            .load(uri)
            .centerCrop()
            .into(imageView)
    }

    private fun setState(state: ScreenState) {
        when (state) {
            ScreenState.ERROR -> {
                errorLayout.isVisible = true
                progressLayout.isVisible = false
                infoLayout.isVisible = false

                errorLayout.text = "${getString(R.string.recognizing_error)}\n ${viewModel.errorMessage}"
            }
            ScreenState.SUCCESS -> {
                errorLayout.isVisible = false
                progressLayout.isVisible = false
                infoLayout.isVisible = true
            }
            ScreenState.LOADING -> {
                errorLayout.isVisible = false
                progressLayout.isVisible = true
                infoLayout.isVisible = false
            }
            ScreenState.DEFAULT, ScreenState.EMPTY  -> {
                errorLayout.isVisible = false
                progressLayout.isVisible = false
                infoLayout.isVisible = false
            }
        }
    }

    private fun showRecognized(recognized: Recognized) {
        dateTextView.text = PrettyTime().format(recognized.date)

        val mainEmotion = recognized.emotions.maxBy { it.value }?.key
        if (mainEmotion != null) {
            mainEmotionTextView.text = "${mainEmotion.toTitleCase()} - ${recognized.emotions[mainEmotion]}%"
            otherEmotionsTextView.text = composeOtherEmotions(recognized.emotions, mainEmotion)
        }
    }

    private fun composeOtherEmotions(emotions: Map<String, Int>, main: String): String {
        val sb = StringBuilder()

        emotions.toSortedMap().forEach {
            if (it.key != main) {
                sb.append("${it.key.toTitleCase()} - ${it.value}%\n")
            }
        }

        return sb.toString()
    }
}