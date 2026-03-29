package com.projet.studyroom.meetings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.projet.studyroom.R
import com.projet.studyroom.databinding.ItemCalendarMeetingBinding

class CalendarMeetingAdapter(
    private val onItemClick: (CalendarMeeting) -> Unit
) : ListAdapter<CalendarMeeting, CalendarMeetingAdapter.MeetingViewHolder>(MeetingDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeetingViewHolder {
        val binding = ItemCalendarMeetingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MeetingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MeetingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MeetingViewHolder(
        private val binding: ItemCalendarMeetingBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(meeting: CalendarMeeting) {
            binding.tvMeetingTitle.text = meeting.title
            binding.tvMeetingDescription.text = meeting.description
            binding.tvMeetingDate.text = meeting.date
            binding.tvMeetingTime.text = meeting.time
            binding.tvMeetingLocation.text = meeting.location

            // Set status indicator color based on meeting status
            val statusColor = when {
                meeting.isOngoing -> R.color.green
                meeting.isPast -> R.color.text_secondary
                else -> R.color.primary
            }
            binding.viewStatusIndicator.setBackgroundColor(
                ContextCompat.getColor(binding.root.context, statusColor)
            )

            // Set text color for past meetings
            val textColor = if (meeting.isPast) {
                R.color.text_secondary
            } else {
                R.color.text_primary
            }
            binding.tvMeetingTitle.setTextColor(
                ContextCompat.getColor(binding.root.context, textColor)
            )

            // Show status badge
            when {
                meeting.isOngoing -> {
                    binding.tvStatusBadge.text = "NOW"
                    binding.tvStatusBadge.visibility = android.view.View.VISIBLE
                    binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_meeting_indicator_green)
                }
                meeting.isPast -> {
                    binding.tvStatusBadge.text = "PAST"
                    binding.tvStatusBadge.visibility = android.view.View.VISIBLE
                    binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_meeting_indicator)
                }
                else -> {
                    binding.tvStatusBadge.visibility = android.view.View.GONE
                }
            }
        }
    }

    class MeetingDiffCallback : DiffUtil.ItemCallback<CalendarMeeting>() {
        override fun areItemsTheSame(oldItem: CalendarMeeting, newItem: CalendarMeeting): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CalendarMeeting, newItem: CalendarMeeting): Boolean {
            return oldItem == newItem
        }
    }
}
