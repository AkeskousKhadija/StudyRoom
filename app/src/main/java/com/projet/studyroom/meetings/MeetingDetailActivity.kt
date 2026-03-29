package com.projet.studyroom.meetings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.projet.studyroom.databinding.ActivityMeetingDetailBinding

class MeetingDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMeetingDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMeetingDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle back button
        binding.ivBack.setOnClickListener {
            finish()
        }

        // Handle join meeting button
        binding.btnJoinMeeting.setOnClickListener {
            // TODO: Implement join meeting logic
        }

        // Handle add to calendar button
        binding.btnAddToCalendar.setOnClickListener {
            // TODO: Implement add to calendar logic
        }
    }
}
