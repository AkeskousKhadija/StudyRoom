package com.projet.studyroom.chat

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.projet.studyroom.R
import com.projet.studyroom.meetings.MeetingActivity
import com.projet.studyroom.profile.SettingsActivity

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: com.projet.studyroom.databinding.Chatgroupe1Binding
    
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var messageAdapter: ChatMessageAdapter
    
    private var groupId: String? = null
    private var groupName: String? = null
    private var currentUserId: String? = null
    
    private val messagesList = mutableListOf<ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = com.projet.studyroom.databinding.Chatgroupe1Binding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Initialize Firebase
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUserId = auth.currentUser?.uid

        // Get group info from intent
        groupId = intent.getStringExtra("group_id")
        groupName = intent.getStringExtra("group_name")

        if (groupId == null) {
            Toast.makeText(this, "Erreur: Groupe non trouvé", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Setup UI
        setupGroupInfo()
        setupRecyclerView()
        setupListeners()
        
        // Load messages
        loadMessages()
    }

    private fun setupGroupInfo() {
        binding.groupName.text = groupName ?: "Groupe"
    }

    private fun setupRecyclerView() {
        messageAdapter = ChatMessageAdapter(currentUserId ?: "")
        
        binding.messagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity).apply {
                stackFromEnd = true
            }
            adapter = messageAdapter
        }
    }

    private fun setupListeners() {
        // Back button
        binding.backButton.setOnClickListener {
            finish()
        }

        // ⚙️ Settings
        binding.settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("group_id", groupId)
            intent.putExtra("group_name", groupName)
            startActivity(intent)
        }

        // 📅 Calendar (button6)
        binding.button6.setOnClickListener {
            val intent = Intent(this, com.projet.studyroom.meetings.MeetingCalendarActivity::class.java)
            intent.putExtra("group_id", groupId)
            intent.putExtra("group_name", groupName)
            startActivity(intent)
        }

        // 📞 Meeting (button2)
        binding.button2.setOnClickListener {
            val intent = Intent(this, MeetingActivity::class.java)
            intent.putExtra("group_id", groupId)
            intent.putExtra("group_name", groupName)
            startActivity(intent)
        }

        // Send message
        binding.sendButton.setOnClickListener {
            sendMessage()
        }
    }

    private fun loadMessages() {
        groupId?.let { gid ->
            db.collection("conversations")
                .document(gid)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Toast.makeText(this, "Erreur: ${error.message}", Toast.LENGTH_SHORT).show()
                        return@addSnapshotListener
                    }
                    
                    messagesList.clear()
                    
                    val newMessages = mutableListOf<ChatMessage>()
                    
                    // First, collect all message documents
                    val messageDocs = mutableListOf<com.google.firebase.firestore.DocumentSnapshot>()
                    value?.forEach { document ->
                        messageDocs.add(document)
                    }
                    
                    // Then fetch user names for each unique sender
                    val senderIds = messageDocs.mapNotNull { it.getString("senderId") }.distinct()
                    val userNamesMap = mutableMapOf<String, String>()
                    
                    // Fetch all user names in parallel
                    val userNameFutures = senderIds.map { senderId ->
                        db.collection("users").document(senderId).get()
                    }
                    
                    // Use a counter to track when all names are fetched
                    var completedCount = 0
                    if (senderIds.isEmpty()) {
                        // No messages, just display them
                        messageDocs.forEach { document ->
                            val message = ChatMessage(
                                id = document.id,
                                senderId = document.getString("senderId") ?: "",
                                senderName = document.getString("senderName") ?: "Utilisateur",
                                senderAvatar = R.drawable.ic_profile,
                                content = document.getString("messageText") ?: "",
                                timestamp = document.getLong("timestamp") ?: System.currentTimeMillis(),
                                isFromCurrentUser = document.getString("senderId") == currentUserId
                            )
                            newMessages.add(message)
                        }
                        messageAdapter.submitList(newMessages.toList())
                        
                        if (newMessages.isNotEmpty()) {
                            binding.messagesRecyclerView.scrollToPosition(newMessages.size - 1)
                        }
                    } else {
                        // Fetch user names
                        senderIds.forEach { senderId ->
                            db.collection("users").document(senderId)
                                .get()
                                .addOnSuccessListener { userDoc ->
                                    val userName = userDoc.getString("full_name") 
                                        ?: userDoc.getString("name") 
                                        ?: userDoc.getString("displayName")
                                        ?: "Utilisateur"
                                    userNamesMap[senderId] = userName
                                    
                                    completedCount++
                                    if (completedCount == senderIds.size) {
                                        // All user names fetched, now create messages
                                        messageDocs.forEach { document ->
                                            val senderIdVal = document.getString("senderId") ?: ""
                                            val message = ChatMessage(
                                                id = document.id,
                                                senderId = senderIdVal,
                                                senderName = userNamesMap[senderIdVal] ?: "Utilisateur",
                                                senderAvatar = R.drawable.ic_profile,
                                                content = document.getString("messageText") ?: "",
                                                timestamp = document.getLong("timestamp") ?: System.currentTimeMillis(),
                                                isFromCurrentUser = document.getString("senderId") == currentUserId
                                            )
                                            newMessages.add(message)
                                        }
                                        
                                        messageAdapter.submitList(newMessages.toList())
                                        
                                        if (newMessages.isNotEmpty()) {
                                            binding.messagesRecyclerView.scrollToPosition(newMessages.size - 1)
                                        }
                                    }
                                }
                                .addOnFailureListener {
                                    userNamesMap[senderId] = "Utilisateur"
                                    completedCount++
                                    if (completedCount == senderIds.size) {
                                        messageDocs.forEach { document ->
                                            val senderIdVal = document.getString("senderId") ?: ""
                                            val message = ChatMessage(
                                                id = document.id,
                                                senderId = senderIdVal,
                                                senderName = userNamesMap[senderIdVal] ?: "Utilisateur",
                                                senderAvatar = R.drawable.ic_profile,
                                                content = document.getString("messageText") ?: "",
                                                timestamp = document.getLong("timestamp") ?: System.currentTimeMillis(),
                                                isFromCurrentUser = document.getString("senderId") == currentUserId
                                            )
                                            newMessages.add(message)
                                        }
                                        
                                        messageAdapter.submitList(newMessages.toList())
                                        
                                        if (newMessages.isNotEmpty()) {
                                            binding.messagesRecyclerView.scrollToPosition(newMessages.size - 1)
                                        }
                                    }
                                }
                        }
                    }
                }
        }
    }

    private fun sendMessage() {
        val messageText = binding.messageInput.text.toString().trim()
        
        if (messageText.isEmpty()) {
            Toast.makeText(this, "Entrez un message", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (currentUserId == null) {
            Toast.makeText(this, "Veuillez vous connecter", Toast.LENGTH_SHORT).show()
            return
        }
        
        groupId?.let { gid ->
            // Get sender name from current user
            db.collection("users").document(currentUserId!!)
                .get()
                .addOnSuccessListener { document ->
                    val senderName = document.getString("full_name") ?: document.getString("name") ?: "Utilisateur"
                    
                    val messageData = hashMapOf(
                        "senderId" to currentUserId,
                        "senderName" to senderName,
                        "messageText" to messageText,
                        "timestamp" to System.currentTimeMillis()
                    )
                    
                    // First, create/update conversation document in conversations collection
                    val conversationData = hashMapOf(
                        "groupId" to gid,
                        "groupName" to (groupName ?: "Groupe"),
                        "lastMessage" to messageText,
                        "lastMessageTime" to System.currentTimeMillis(),
                        "updatedAt" to System.currentTimeMillis()
                    )
                    
                    db.collection("conversations")
                        .document(gid)
                        .set(conversationData)
                        .addOnSuccessListener {
                            // Then save the message to messages subcollection
                            db.collection("conversations")
                                .document(gid)
                                .collection("messages")
                                .add(messageData)
                                .addOnSuccessListener {
                                    binding.messageInput.text?.clear()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    // Send anyway with default name
                    val messageData = hashMapOf(
                        "senderId" to currentUserId,
                        "senderName" to "Utilisateur",
                        "messageText" to messageText,
                        "timestamp" to System.currentTimeMillis()
                    )
                    
                    // First, create/update conversation document
                    val conversationData = hashMapOf(
                        "groupId" to gid,
                        "groupName" to (groupName ?: "Groupe"),
                        "lastMessage" to messageText,
                        "lastMessageTime" to System.currentTimeMillis(),
                        "updatedAt" to System.currentTimeMillis()
                    )
                    
                    db.collection("conversations")
                        .document(gid)
                        .set(conversationData)
                        .addOnSuccessListener {
                            // Then save the message
                            db.collection("conversations")
                                .document(gid)
                                .collection("messages")
                                .add(messageData)
                                .addOnSuccessListener {
                                    binding.messageInput.text?.clear()
                                }
                        }
                }
        }
    }
}
