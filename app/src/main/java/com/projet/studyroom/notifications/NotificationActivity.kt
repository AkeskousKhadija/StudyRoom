package com.projet.studyroom.notifications

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.projet.studyroom.databinding.ActivityNotificationsBinding

class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationsBinding
    private lateinit var notificationAdapter: NotificationAdapter

    // Sample notification data - empty list
    private val sampleNotifications = emptyList<Notification>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        // Back button click listener
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        notificationAdapter = NotificationAdapter(sampleNotifications)
        
        binding.rvNotifications.apply {
            layoutManager = LinearLayoutManager(this@NotificationActivity)
            adapter = notificationAdapter
        }

        // Show/hide empty state
        if (sampleNotifications.isEmpty()) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.rvNotifications.visibility = View.GONE
        } else {
            binding.tvEmpty.visibility = View.GONE
            binding.rvNotifications.visibility = View.VISIBLE
        }
    }
}
