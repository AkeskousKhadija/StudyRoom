package com.projet.studyroom.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.projet.studyroom.databinding.ItemChatGroupBinding

class ChatGroupsAdapter(
    private val groups: List<com.projet.studyroom.groups.Group>,
    private val onGroupClick: (com.projet.studyroom.groups.Group) -> Unit,
    private val onDetailsClick: (com.projet.studyroom.groups.Group) -> Unit
) : RecyclerView.Adapter<ChatGroupsAdapter.ChatGroupViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatGroupViewHolder {
        val binding = ItemChatGroupBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChatGroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatGroupViewHolder, position: Int) {
        holder.bind(groups[position])
    }

    override fun getItemCount(): Int = groups.size

    inner class ChatGroupViewHolder(
        private val binding: ItemChatGroupBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(group: com.projet.studyroom.groups.Group) {
            binding.tvGroupName.text = group.name
            binding.tvGroupMembers.text = "${group.memberCount} members"
            
            // Clic sur toute la carte du groupe - Pour aller au chat
            binding.root.setOnClickListener {
                android.util.Log.d("ChatGroupsAdapter", "Group clicked for: ${group.name}")
                onGroupClick(group)
            }

            // Clic sur le bouton Details - Pour voir les détails
            binding.btnDetails.setOnClickListener {
                android.util.Log.d("ChatGroupsAdapter", "Details button clicked for: ${group.name}")
                onDetailsClick(group)
            }
        }
    }
}
