package com.projet.studyroom.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.projet.studyroom.R
import com.projet.studyroom.databinding.ItemChatMessageBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Adapter for displaying chat messages in a group conversation
 * Utilise ListAdapter pour une mise à jour efficace des données
 */
class ChatMessageAdapter(
    private val currentUserId: String
) : ListAdapter<ChatMessage, ChatMessageAdapter.MessageViewHolder>(MessageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemChatMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MessageViewHolder(
        private val binding: ItemChatMessageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: ChatMessage) {
            // Configurer le nom de l'expéditeur
            binding.tvSenderName.text = message.senderName
            
            // Configurer le contenu du message
            binding.tvMessageContent.text = message.content
            
            // Configurer l'avatar de l'expéditeur
            binding.ivSenderAvatar.setImageResource(message.senderAvatar)
            
            // Formater et afficher l'horodatage
            binding.tvTimestamp.text = formatTimestamp(message.timestamp)
            
            // Forcer la mise à jour du binding
            binding.executePendingBindings()
        }

        private fun formatTimestamp(timestamp: Long): String {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }

    /**
     * DiffUtil callback pour une mise à jour efficace de la liste
     */
    class MessageDiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem == newItem
        }
    }
}
