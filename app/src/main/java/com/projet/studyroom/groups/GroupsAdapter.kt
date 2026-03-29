package com.projet.studyroom.groups

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.projet.studyroom.databinding.ItemGroupBinding

class GroupsAdapter(
    private val groups: List<Group>,
    private val onGroupClick: (Group) -> Unit,
    private val onJoinClick: (Group) -> Unit
) : RecyclerView.Adapter<GroupsAdapter.GroupViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val binding = ItemGroupBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(groups[position])
    }

    override fun getItemCount(): Int = groups.size

    inner class GroupViewHolder(
        private val binding: ItemGroupBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(group: Group) {
            binding.tvGroupName.text = group.name
            binding.tvGroupMembers.text = "${group.memberCount} members"
            
            // Clic sur toute la carte du groupe - Pour aller au chat
            binding.root.setOnClickListener {
                onGroupClick(group)
            }

            // Clic sur le bouton Join - Pour rejoindre le groupe
            binding.btnJoin.setOnClickListener {
                onJoinClick(group)
            }
        }
    }
}
