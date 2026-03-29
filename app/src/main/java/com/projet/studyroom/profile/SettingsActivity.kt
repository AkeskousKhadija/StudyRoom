package com.projet.studyroom.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.projet.studyroom.R

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: com.projet.studyroom.databinding.ActivitySettingsBinding
    
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    
    private var groupId: String? = null
    private var groupName: String? = null
    private var currentUserId: String? = null
    private var isAdmin: Boolean = false
    
    private val membersList = mutableListOf<MemberInfo>()

    data class MemberInfo(
        val userId: String,
        val name: String,
        val email: String,
        val isAdmin: Boolean
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = com.projet.studyroom.databinding.ActivitySettingsBinding.inflate(layoutInflater)
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

        // Setup window insets
        setupWindowInsets()

        // Setup listeners
        setupListeners()

        // Load group data
        loadGroupData()
    }

    private fun setupWindowInsets() {
        val mainView = binding.settingsMain
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupListeners() {
        // Back button
        binding.backButton.setOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        // Edit group button (admin only)
        binding.editGroupButton.setOnClickListener {
            if (isAdmin) {
                showEditGroupDialog()
            } else {
                Toast.makeText(this, "Seuls les admins peuvent modifier le groupe", Toast.LENGTH_SHORT).show()
            }
        }

        // Search icon
        binding.searchIcon.setOnClickListener {
            val query = binding.searchMember.text.toString()
            if (query.isNotEmpty()) {
                searchMembers(query)
            } else {
                Toast.makeText(this, "Entrez un nom à rechercher", Toast.LENGTH_SHORT).show()
            }
        }

        // Add member button (admin only)
        binding.addMemberButton.setOnClickListener {
            if (isAdmin) {
                showAddMemberDialog()
            } else {
                Toast.makeText(this, "Seuls les admins peuvent ajouter des membres", Toast.LENGTH_SHORT).show()
            }
        }

        // Exit group
        binding.exitGroupOption.setOnClickListener {
            showExitGroupConfirmation()
        }

        // Delete group (admin only)
        binding.deleteGroupOption.setOnClickListener {
            if (isAdmin) {
                showDeleteGroupConfirmation()
            } else {
                Toast.makeText(this, "Seuls les admins peuvent supprimer le groupe", Toast.LENGTH_SHORT).show()
            }
        }

        // Archive group
        binding.archiveGroupOption.setOnClickListener {
            Toast.makeText(this, "Groupe archivé", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadGroupData() {
        groupId?.let { id ->
            db.collection("groups").document(id)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("name") ?: "Groupe"
                        val description = document.getString("description") ?: ""
                        val memberCount = document.getLong("memberCount")?.toInt() ?: 0
                        val admins = document.get("admins") as? List<*> ?: emptyList<String>()
                        
                        // Check if current user is admin
                        currentUserId?.let { uid ->
                            isAdmin = admins.contains(uid)
                        }

                        // Update UI
                        binding.groupName.text = name
                        binding.groupDescription.text = description
                        binding.memberCount.text = "$memberCount membres"

                        // Show/hide admin options
                        if (isAdmin) {
                            binding.addMemberButton.visibility = View.VISIBLE
                            binding.deleteGroupOption.visibility = View.VISIBLE
                            binding.editGroupButton.visibility = View.VISIBLE
                        } else {
                            binding.addMemberButton.visibility = View.GONE
                            binding.deleteGroupOption.visibility = View.GONE
                            binding.editGroupButton.visibility = View.GONE
                        }

                        // Load members
                        loadMembers()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun loadMembers() {
        membersList.clear()
        
        groupId?.let { id ->
            db.collection("groups").document(id)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val members = document.get("members") as? List<*> ?: emptyList<String>()
                        val admins = document.get("admins") as? List<*> ?: emptyList<String>()
                        
                        // Clear existing member views
                        binding.membersContainer.removeAllViews()
                        
                        // Load each member's info
                        if (members.isNotEmpty()) {
                            loadMemberDetails(members as List<String>, admins as List<String>)
                        } else {
                            showEmptyMembers()
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun loadMemberDetails(memberIds: List<String>, adminIds: List<String>) {
        var loadedCount = 0
        
        for (userId in memberIds) {
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("fullName") ?: document.getString("name") ?: "Utilisateur"
                        val email = document.getString("email") ?: ""
                        val isMemberAdmin = adminIds.contains(userId)
                        
                        val member = MemberInfo(userId, name, email, isMemberAdmin)
                        membersList.add(member)
                        
                        loadedCount++
                        if (loadedCount == memberIds.size) {
                            displayMembers()
                        }
                    }
                }
                .addOnFailureListener {
                    loadedCount++
                    if (loadedCount == memberIds.size) {
                        displayMembers()
                    }
                }
        }
    }

    private fun displayMembers() {
        binding.membersContainer.removeAllViews()
        
        // Sort: admins first, then members
        val sortedMembers = membersList.sortedByDescending { it.isAdmin }
        
        for (member in sortedMembers) {
            addMemberView(member)
        }
        
        if (membersList.isEmpty()) {
            showEmptyMembers()
        }
    }

    private fun addMemberView(member: MemberInfo) {
        val inflater = LayoutInflater.from(this)
        val memberView = inflater.inflate(R.layout.item_member, binding.membersContainer, false)
        
        val nameText = memberView.findViewById<TextView>(R.id.memberName)
        val roleText = memberView.findViewById<TextView>(R.id.memberRole)
        val roleBadge = memberView.findViewById<View>(R.id.adminBadge)
        
        nameText.text = member.name
        if (member.isAdmin) {
            roleText.text = "Admin"
            roleBadge.visibility = View.VISIBLE
        } else {
            roleText.text = "Membre"
            roleBadge.visibility = View.GONE
        }
        
        // Click listener for member options (admin only)
        memberView.setOnClickListener {
            if (isAdmin && member.userId != currentUserId) {
                showMemberOptionsDialog(member)
            }
        }
        
        binding.membersContainer.addView(memberView)
    }

    private fun showEmptyMembers() {
        val emptyText = TextView(this)
        emptyText.text = "Aucun membre"
        emptyText.setTextColor(resources.getColor(R.color.text_secondary, theme))
        emptyText.setPadding(32, 32, 32, 32)
        binding.membersContainer.addView(emptyText)
    }

    private fun showMemberOptionsDialog(member: MemberInfo) {
        val options = if (member.isAdmin) {
            arrayOf("Retirer le statut admin", "Retirer du groupe")
        } else {
            arrayOf("Promouvoir admin", "Retirer du groupe")
        }

        AlertDialog.Builder(this)
            .setTitle(member.name)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        if (member.isAdmin) {
                            removeAdminStatus(member)
                        } else {
                            promoteToAdmin(member)
                        }
                    }
                    1 -> removeMember(member)
                }
            }
            .show()
    }

    private fun promoteToAdmin(member: MemberInfo) {
        groupId?.let { id ->
            db.collection("groups").document(id)
                .update("admins", com.google.firebase.firestore.FieldValue.arrayUnion(member.userId))
                .addOnSuccessListener {
                    Toast.makeText(this, "${member.name} est maintenant admin", Toast.LENGTH_SHORT).show()
                    loadMembers()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun removeAdminStatus(member: MemberInfo) {
        groupId?.let { id ->
            db.collection("groups").document(id)
                .update("admins", com.google.firebase.firestore.FieldValue.arrayRemove(member.userId))
                .addOnSuccessListener {
                    Toast.makeText(this, "Statut admin retiré", Toast.LENGTH_SHORT).show()
                    loadMembers()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun removeMember(member: MemberInfo) {
        AlertDialog.Builder(this)
            .setTitle("Retirer du groupe")
            .setMessage("Voulez-vous retirer ${member.name} du groupe?")
            .setPositiveButton("Oui") { _, _ ->
                groupId?.let { id ->
                    db.collection("groups").document(id)
                        .update(
                            mapOf(
                                "members" to com.google.firebase.firestore.FieldValue.arrayRemove(member.userId),
                                "memberCount" to com.google.firebase.firestore.FieldValue.increment(-1)
                            )
                        )
                        .addOnSuccessListener {
                            // Also remove from admins if they were admin
                            if (member.isAdmin) {
                                db.collection("groups").document(id)
                                    .update("admins", com.google.firebase.firestore.FieldValue.arrayRemove(member.userId))
                            }
                            Toast.makeText(this, "${member.name} a été retiré", Toast.LENGTH_SHORT).show()
                            loadMembers()
                            loadGroupData()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .setNegativeButton("Non", null)
            .show()
    }

    private fun showAddMemberDialog() {
        val input = android.widget.EditText(this)
        input.hint = "Email de l'utilisateur"
        
        AlertDialog.Builder(this)
            .setTitle("Ajouter un membre")
            .setView(input)
            .setPositiveButton("Ajouter") { _, _ ->
                val email = input.text.toString().trim()
                if (email.isNotEmpty()) {
                    addMemberByEmail(email)
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun addMemberByEmail(email: String) {
        // Search for user by email
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val userDoc = documents.documents[0]
                    val userId = userDoc.id
                    
                    // Check if already a member
                    groupId?.let { id ->
                        db.collection("groups").document(id)
                            .get()
                            .addOnSuccessListener { groupDoc ->
                                val members = groupDoc.get("members") as? List<*> ?: emptyList<String>()
                                if (members.contains(userId)) {
                                    Toast.makeText(this, "Cet utilisateur est déjà membre", Toast.LENGTH_SHORT).show()
                                } else {
                                    // Add member
                                    db.collection("groups").document(id)
                                        .update(
                                            mapOf(
                                                "members" to com.google.firebase.firestore.FieldValue.arrayUnion(userId),
                                                "memberCount" to com.google.firebase.firestore.FieldValue.increment(1)
                                            )
                                        )
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "Membre ajouté!", Toast.LENGTH_SHORT).show()
                                            loadMembers()
                                            loadGroupData()
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(this, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            }
                    }
                } else {
                    Toast.makeText(this, "Utilisateur non trouvé", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showEditGroupDialog() {
        val inputName = android.widget.EditText(this)
        inputName.hint = "Nom du groupe"
        inputName.setText(binding.groupName.text)
        
        val inputDesc = android.widget.EditText(this)
        inputDesc.hint = "Description"
        inputDesc.setText(binding.groupDescription.text)
        
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 20, 50, 20)
        layout.addView(inputName)
        layout.addView(inputDesc)
        
        AlertDialog.Builder(this)
            .setTitle("Modifier le groupe")
            .setView(layout)
            .setPositiveButton("Enregistrer") { _, _ ->
                val newName = inputName.text.toString().trim()
                val newDesc = inputDesc.text.toString().trim()
                
                if (newName.isNotEmpty()) {
                    updateGroupInfo(newName, newDesc)
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun updateGroupInfo(name: String, description: String) {
        groupId?.let { id ->
            db.collection("groups").document(id)
                .update(
                    mapOf(
                        "name" to name,
                        "description" to description
                    )
                )
                .addOnSuccessListener {
                    binding.groupName.text = name
                    binding.groupDescription.text = description
                    Toast.makeText(this, "Groupe mis à jour", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun searchMembers(query: String) {
        val filteredMembers = membersList.filter { 
            it.name.contains(query, ignoreCase = true) || 
            it.email.contains(query, ignoreCase = true)
        }
        
        binding.membersContainer.removeAllViews()
        for (member in filteredMembers) {
            addMemberView(member)
        }
        
        if (filteredMembers.isEmpty()) {
            Toast.makeText(this, "Aucun résultat", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showExitGroupConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Quitter le groupe")
            .setMessage("Êtes-vous sûr de vouloir quitter ce groupe?")
            .setPositiveButton("Oui") { _, _ ->
                exitGroup()
            }
            .setNegativeButton("Non", null)
            .show()
    }

    private fun exitGroup() {
        currentUserId?.let { uid ->
            groupId?.let { id ->
                db.collection("groups").document(id)
                    .update(
                        mapOf(
                            "members" to com.google.firebase.firestore.FieldValue.arrayRemove(uid),
                            "memberCount" to com.google.firebase.firestore.FieldValue.increment(-1)
                        )
                    )
                    .addOnSuccessListener {
                        // Also remove from admins if was admin
                        db.collection("groups").document(id)
                            .update("admins", com.google.firebase.firestore.FieldValue.arrayRemove(uid))
                        
                        Toast.makeText(this, "Vous avez quitté le groupe", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun showDeleteGroupConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Supprimer le groupe")
            .setMessage("Êtes-vous sûr de vouloir supprimer définitivement ce groupe? Cette action est irréversible.")
            .setPositiveButton("Supprimer") { _, _ ->
                deleteGroup()
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun deleteGroup() {
        groupId?.let { id ->
            db.collection("groups").document(id)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Groupe supprimé", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
