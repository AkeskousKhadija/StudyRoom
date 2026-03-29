package com.projet.studyroom.chat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.projet.studyroom.databinding.ActivityChatListBinding
import com.projet.studyroom.groups.Group
import com.projet.studyroom.meetings.CalendarMeetingActivity

class ChatListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatListBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var chatGroupsAdapter: ChatGroupsAdapter
    private val userGroupsList = mutableListOf<Group>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Setup RecyclerView with new adapter
        chatGroupsAdapter = ChatGroupsAdapter(
            userGroupsList,
            onGroupClick = { group ->
                // Navigate to ChatActivity with group info
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra("group_id", group.id)
                intent.putExtra("group_name", group.name)
                startActivity(intent)
            },
            onDetailsClick = { group ->
                // Debug: Show toast to verify click is working
                Toast.makeText(this, "Details clicked for: ${group.name}", Toast.LENGTH_SHORT).show()
                
                // Navigate to CalendarMeetingActivity for group details
                val intent = Intent(this, CalendarMeetingActivity::class.java)
                intent.putExtra("group_id", group.id)
                intent.putExtra("group_name", group.name)
                startActivity(intent)
            }
        )

        binding.rvUserGroups.apply {
            layoutManager = LinearLayoutManager(this@ChatListActivity)
            adapter = chatGroupsAdapter
        }

        // Setup back button
        binding.ivBack.setOnClickListener {
            finish()
        }

        // Load user's groups (groups user is member of)
        loadUserGroups()
    }

    private fun loadUserGroups() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Veuillez vous connecter", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = currentUser.uid

        binding.progressBar.visibility = android.view.View.VISIBLE

        // Get groups where user is a member
        db.collection("groups")
            .whereArrayContains("members", userId)
            .get()
            .addOnSuccessListener { result ->
                binding.progressBar.visibility = android.view.View.GONE
                userGroupsList.clear()

                for (document in result) {
                    val group = Group(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        description = document.getString("description") ?: "",
                        memberCount = document.getLong("memberCount")?.toInt() ?: 0,
                        imageUrl = document.getString("imageUrl") ?: ""
                    )
                    userGroupsList.add(group)
                }

                chatGroupsAdapter.notifyDataSetChanged()

                if (userGroupsList.isEmpty()) {
                    Toast.makeText(this, "Vous n'avez encore aucune conversation", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                binding.progressBar.visibility = android.view.View.GONE
                Toast.makeText(this, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
