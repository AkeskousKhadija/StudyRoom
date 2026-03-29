package com.projet.studyroom.groups

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.projet.studyroom.databinding.ActivityGroupsBinding
import com.projet.studyroom.chat.ChatActivity
import com.projet.studyroom.meetings.CalendarMeetingActivity

class GroupsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupsBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var groupsAdapter: GroupsAdapter
    private val groupsList = mutableListOf<Group>()
    private val filteredList = mutableListOf<Group>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firestore and Auth
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Setup RecyclerView
        groupsAdapter = GroupsAdapter(
            filteredList,
            onGroupClick = { group ->
                // Clic sur le groupe - aller au chat
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra("group_id", group.id)
                intent.putExtra("group_name", group.name)
                startActivity(intent)
            },
            onJoinClick = { group ->
                // Rejoindre le groupe et devenir membre
                joinGroup(group)
            }
        )

        binding.rvGroups.apply {
            layoutManager = LinearLayoutManager(this@GroupsActivity)
            adapter = groupsAdapter
        }

        // Setup back button
        binding.ivBack.setOnClickListener {
            finish()
        }

        // Setup FAB for creating new group
        binding.fabAddGroup.setOnClickListener {
            createNewGroup()
        }

        // Load groups from Firestore
        loadGroups()

        // Setup search
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                filterGroups(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterGroups(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(groupsList)
        } else {
            for (group in groupsList) {
                if (group.name.lowercase().contains(query.lowercase())) {
                    filteredList.add(group)
                }
            }
        }
        groupsAdapter.notifyDataSetChanged()
    }

    private fun loadGroups() {
        binding.progressBar.visibility = android.view.View.VISIBLE

        db.collection("groups")
            .get()
            .addOnSuccessListener { result ->
                binding.progressBar.visibility = android.view.View.GONE
                groupsList.clear()
                filteredList.clear()
                
                for (document in result) {
                    val group = Group(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        description = document.getString("description") ?: "",
                        memberCount = document.getLong("memberCount")?.toInt() ?: 0,
                        imageUrl = document.getString("imageUrl") ?: ""
                    )
                    groupsList.add(group)
                    filteredList.add(group)
                }
                
                groupsAdapter.notifyDataSetChanged()
                
                if (groupsList.isEmpty()) {
                    Toast.makeText(this, "Aucun groupe. Créez-en un!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                binding.progressBar.visibility = android.view.View.GONE
                Toast.makeText(this, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun joinGroup(group: Group) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Veuillez vous connecter", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = currentUser.uid
        val groupRef = db.collection("groups").document(group.id)

        // Ajouter l'utilisateur à la liste des membres
        groupRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    @Suppress("UNCHECKED_CAST")
                    val members = document.get("members") as? List<String> ?: emptyList()
                    
                    if (members.contains(userId)) {
                        // Utilisateur déjà membre, aller au chat
                        navigateToChat(group)
                    } else {
                        // Ajouter l'utilisateur aux membres
                        val newMembers = members + userId
                        val newMemberCount = (document.getLong("memberCount") ?: 0) + 1
                        
                        groupRef.update(
                            mapOf(
                                "members" to newMembers,
                                "memberCount" to newMemberCount
                            )
                        )
                        .addOnSuccessListener {
                            Toast.makeText(this, "Vous avez rejoint le groupe!", Toast.LENGTH_SHORT).show()
                            navigateToChat(group)
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToChat(group: Group) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("group_id", group.id)
        intent.putExtra("group_name", group.name)
        startActivity(intent)
    }

    private fun createNewGroup() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Veuillez vous connecter", Toast.LENGTH_SHORT).show()
            return
        }

        val newGroup = hashMapOf(
            "name" to "Nouveau groupe d'étude",
            "description" to "Un nouveau groupe pour étudier",
            "memberCount" to 1,
            "members" to listOf(currentUser.uid),
            "admins" to listOf(currentUser.uid),  // Le créateur devient admin
            "createdBy" to currentUser.uid,
            "imageUrl" to "",
            "createdAt" to System.currentTimeMillis()
        )

        db.collection("groups")
            .add(newGroup)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Groupe créé!", Toast.LENGTH_SHORT).show()
                loadGroups()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
