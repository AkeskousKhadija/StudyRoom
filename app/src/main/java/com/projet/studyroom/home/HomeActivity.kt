package com.projet.studyroom.home

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.recyclerview.widget.LinearLayoutManager
import com.projet.studyroom.R
import com.projet.studyroom.databinding.ActivityHomeBinding
import com.projet.studyroom.groups.Group
import com.projet.studyroom.groups.GroupsAdapter
import com.projet.studyroom.meetings.MeetingCalendarActivity
import com.projet.studyroom.notifications.NotificationActivity
import com.projet.studyroom.profile.ProfileActivity
import com.projet.studyroom.search.SearchActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var groupsAdapter: GroupsAdapter
    private val groupsList = mutableListOf<Group>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupGroupsRecyclerView()
        setupSearch()

        setupCalendarClickListeners()

        setupBottomNavigation()
        
        loadGroups()
    }
    
    private fun setupGroupsRecyclerView() {
        groupsAdapter = GroupsAdapter(
            groups = groupsList,
            onGroupClick = { group ->
                // Navigate to chat
                val intent = Intent(this, com.projet.studyroom.chat.ChatActivity::class.java)
                intent.putExtra("group_id", group.id)
                intent.putExtra("group_name", group.name)
                startActivity(intent)
            },
            onJoinClick = { group ->
                joinGroup(group)
            }
        )
        
        binding.rvGroups.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = groupsAdapter
        }
    }
    
    private fun loadGroups() {
        val db = FirebaseFirestore.getInstance()
        db.collection("groups")
            .get()
            .addOnSuccessListener { documents ->
                groupsList.clear()
                for (document in documents) {
                    val group = Group(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        description = document.getString("description") ?: "",
                        memberCount = (document.getLong("memberCount") ?: 0).toInt(),
                        imageUrl = ""
                    )
                    groupsList.add(group)
                }
                groupsAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    
    private fun joinGroup(group: Group) {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        
        if (currentUser == null) {
            Toast.makeText(this, "Veuillez vous connecter", Toast.LENGTH_SHORT).show()
            return
        }
        
        val userId = currentUser.uid
        
        db.collection("groups").document(group.id)
            .get()
            .addOnSuccessListener { document ->
                val members = document.get("members") as? List<String> ?: emptyList()
                if (!members.contains(userId)) {
                    val updatedMembers = members + userId
                    db.collection("groups").document(group.id)
                        .update("members", updatedMembers, "memberCount", updatedMembers.size)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Vous avez rejoint le groupe!", Toast.LENGTH_SHORT).show()
                            loadGroups()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Vous êtes déjà membre", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun setupSearch() {
        // Click on search box to navigate to SearchActivity
        binding.tilSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
        
        // Also handle click on the EditText itself
        binding.etSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
    }

    private fun setupCalendarClickListeners() {
        // Click listeners for calendar card - redirect to calendar
        binding.cardCalendar.setOnClickListener {
            startActivity(Intent(this, MeetingCalendarActivity::class.java))
        }

        // Click listeners for individual days
        binding.dayMon.setOnClickListener {
            startActivity(Intent(this, MeetingCalendarActivity::class.java))
        }

        binding.dayTue.setOnClickListener {
            startActivity(Intent(this, MeetingCalendarActivity::class.java))
        }

        binding.dayWed.setOnClickListener {
            startActivity(Intent(this, MeetingCalendarActivity::class.java))
        }

        binding.dayThu.setOnClickListener {
            startActivity(Intent(this, MeetingCalendarActivity::class.java))
        }

        binding.dayFri.setOnClickListener {
            startActivity(Intent(this, MeetingCalendarActivity::class.java))
        }

        binding.daySat.setOnClickListener {
            startActivity(Intent(this, MeetingCalendarActivity::class.java))
        }

        binding.daySun.setOnClickListener {
            startActivity(Intent(this, MeetingCalendarActivity::class.java))
        }
    }

    private fun setupBottomNavigation() {
        // Click listener for notification icon
        binding.ivNotification.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        // Bottom navigation click listeners
        binding.ivHome.setOnClickListener {
            // Already on home
        }

        binding.ivGroups.setOnClickListener {
            startActivity(Intent(this, com.projet.studyroom.groups.GroupsActivity::class.java))
        }

        binding.fabAdd.setOnClickListener {
            showCreateGroupDialog()
        }

        binding.ivChat.setOnClickListener {
            startActivity(Intent(this, com.projet.studyroom.chat.ChatListActivity::class.java))
        }

        binding.ivProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun showCreateGroupDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_create_group, null)
        val etGroupName = dialogView.findViewById<TextInputEditText>(R.id.etGroupName)
        val etGroupDescription = dialogView.findViewById<TextInputEditText>(R.id.etGroupDescription)
        
        val dialog = AlertDialog.Builder(this)
            .setTitle("Créer un nouveau groupe")
            .setView(dialogView)
            .setPositiveButton("Créer") { _, _ ->
                val groupName = etGroupName?.text.toString().trim()
                val groupDescription = etGroupDescription?.text.toString().trim()
                
                if (groupName.isNotEmpty()) {
                    createGroup(groupName, groupDescription)
                }
            }
            .setNegativeButton("Annuler", null)
            .create()
        dialog.show()
    }

    private fun createGroup(groupName: String, groupDescription: String) {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        
        if (currentUser == null) {
            Toast.makeText(this, "Veuillez vous connecter", Toast.LENGTH_SHORT).show()
            return
        }
        
        val userId = currentUser.uid
        
        val groupData = hashMapOf(
            "name" to groupName,
            "description" to groupDescription,
            "createdBy" to userId,
            "memberCount" to 1,
            "members" to listOf(userId),
            "admins" to listOf(userId),  // Le créateur devient admin
            "createdAt" to System.currentTimeMillis()
        )
        
        db.collection("groups")
            .add(groupData)
            .addOnSuccessListener {
                Toast.makeText(this, "Groupe créé avec succès! Vous êtes l'admin du groupe.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
