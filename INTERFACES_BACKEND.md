# StudyRoom - Analyse Complète: Interfaces XML ↔ Backend Firebase

## 📊 Vue d'ensemble des interfaces

| # | Interface XML | Fichier Kotlin | Collection Firebase | Status |
|---|---------------|----------------|---------------------|--------|
| 1 | activity_splash.xml | SplashActivity.kt | - | ✅ OK |
| 2 | activity_login.xml | LoginActivity.kt | users | ✅ OK |
| 3 | activity_register.xml | RegisterActivity.kt | users | ✅ OK |
| 4 | activity_onboarding1.xml | OnboardingActivity.kt | - | ✅ OK |
| 5 | activity_onboarding2.xml | OnboardingActivity.kt | - | ✅ OK |
| 6 | activity_home.xml | HomeActivity.kt | users, meetings, groups | ⚠️ INCOMPLET |
| 7 | activity_main.xml | MainActivity.kt | - | ✅ OK |
| 8 | activity_profile.xml | ProfileActivity.kt | users | ⚠️ INCOMPLET |
| 9 | activity_edit_profile.xml | EditProfileActivity.kt | users | ⚠️ INCOMPLET |
| 10 | activity_avatar.xml | AvatarActivity.kt | users, storage | ⚠️ INCOMPLET |
| 11 | activity_settings.xml | SettingsActivity.kt | users, groups | ⚠️ INCOMPLET |
| 12 | activity_appearance.xml | Appearance.kt | users | ⚠️ INCOMPLET |
| 13 | activity_language.xml | Language_activity.kt | users | ⚠️ INCOMPLET |
| 14 | activity_groups.xml | GroupsActivity.kt | groups | ⚠️ INCOMPLET |
| 15 | activity_chat_list.xml | ChatListActivity.kt | groups, messages | ⚠️ INCOMPLET |
| 16 | chatgroupe1.xml | ChatActivity.kt | messages | ⚠️ INCOMPLET |
| 17 | activity_meeting.xml | MeetingActivity.kt | meetings | ⚠️ INCOMPLET |
| 18 | activity_meeting_detail.xml | MeetingDetailActivity.kt | meetings | 🔴 TODO |
| 19 | activity_meeting_details.xml | MeetingDetailsActivity.kt | meetings | ⚠️ INCOMPLET |
| 20 | activity_calendar_meeting.xml | CalendarMeetingActivity.kt | meetings | ⚠️ INCOMPLET |
| 21 | activity_meeting_calendar.xml | MeetingCalendarActivity.kt | meetings | ⚠️ INCOMPLET |
| 22 | activity_planifier_meeting.xml | PlanifierMeetingActivity.kt | meetings | 🔴 TODO |
| 23 | activity_notifications.xml | NotificationActivity.kt | notifications | ⚠️ INCOMPLET |
| 24 | activity_search.xml | SearchActivity.kt | users, groups, meetings | ⚠️ INCOMPLET |

---

## 🔥 Interfaces avec BACKEND MANQUANT (à implémenter)

### 🔴 **PRIORITÉ 1: MeetingDetailActivity** 
**Fichier:** `meetings/MeetingDetailActivity.kt`
**Layout:** `activity_meeting_detail.xml`

```kotlin
// ❌ CODE MANQUANT - À IMPLÉMENTER:

btnJoinMeeting.setOnClickListener {
    // 1. Récupérer meetingLink de Firestore
    db.collection("meetings").document(meetingId)
        .get()
        .addOnSuccessListener { doc ->
            val meetingLink = doc.getString("meetingLink")
            
            // 2. Ouvrir lien ou générer code
            if (meetingLink != null) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(meetingLink)))
            } else {
                // Générer code de réunion
                generateMeetingCode()
            }
            
            // 3. Mettre à jour statut: "joined"
            db.collection("meetings").document(meetingId)
                .update("status", "in_progress")
        }
}

btnAddToCalendar.setOnClickListener {
    // 1. Récupérer données réunion
    val meeting = getMeetingFromFirestore(meetingId)
    
    // 2. Créer Intent calendrier
    val intent = Intent(Intent.ACTION_INSERT)
    intent.type = "vnd.android.cursor.item/event"
    intent.putExtra("title", meeting.title)
    intent.putExtra("beginTime", meeting.startTime)
    intent.putExtra("endTime", meeting.endTime)
    intent.putExtra("description", meeting.description)
    intent.putExtra("eventLocation", meeting.location)
    
    startActivity(intent)
}
```

**Collection Firestore: `meetings`**
```json
{
  "id": "meeting_123",
  "title": "Réunion équipe",
  "description": "Description",
  "date": 1711708800000,
  "startTime": "14:00",
  "endTime": "15:00",
  "location": "Salle A",
  "organizerId": "user_456",
  "participants": ["user_789", "user_012"],
  "groupId": "group_123",
  "meetingLink": "https://meet.google.com/abc-defg-hij",
  "status": "scheduled",
  "reminderMinutes": 15
}
```

---

### 🔴 **PRIORITÉ 2: PlanifierMeetingActivity**
**Fichier:** `meetings/PlanifierMeetingActivity.kt`
**Layout:** `activity_planifier_meeting.xml`

```kotlin
// ❌ CODE MANQUANT - À IMPLÉMENTER:

btnCreate.setOnClickListener {
    // 1. Récupérer données du formulaire
    val sujet = etSujet.text.toString()
    val date = etDate.text.toString()
    val heureDebut = etHeureDebut.text.toString()
    val heureFin = etHeureFin.text.toString()
    val description = etDescription.text.toString()
    val location = etLocation.text.toString()
    
    // 2. Validation
    if (sujet.isEmpty() || date.isEmpty() || heureDebut.isEmpty()) {
        Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
        return@setOnClickListener
    }
    
    // 3. Créer document dans Firestore
    val meetingData = hashMapOf(
        "title" to sujet,
        "date" to parseDate(date),
        "startTime" to heureDebut,
        "endTime" to heureFin,
        "description" to description,
        "location" to location,
        "organizerId" to auth.currentUser?.uid,
        "groupId" to currentGroupId,
        "status" to "scheduled",
        "createdAt" to System.currentTimeMillis()
    )
    
    // 4. Sauvegarder
    db.collection("meetings")
        .add(meetingData)
        .addOnSuccessListener { docRef ->
            // 5. Planifier notification
            scheduleMeetingReminder(docRef.id)
            
            Toast.makeText(this, "Meeting créé!", Toast.LENGTH_SHORT).show()
            finish()
        }
        .addOnFailureListener { e ->
            Toast.makeText(this, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}
```

---

## ⚠️ **PRIORITÉ 2: Interfaces nécessitant du backend**

### 1. **HomeActivity** - Dashboard
**Fichier:** `home/HomeActivity.kt`
**Layout:** `activity_home.xml` (12.5KB - Grand)

```kotlin
// ❌ FONCTIONNALITÉS MANQUANTES:

// 1. Charger statistiques
private fun loadStats() {
    // - Nombre de groupes
    // - Nombre de réunions ce mois
    // - Messages non lus
    db.collection("groups")
        .whereArrayContains("members", currentUserId)
        .get()
        .addOnSuccessListener { documents ->
            groupsCount = documents.size()
            updateStatsUI()
        }
}

// 2. Afficher prochaines réunions
private fun loadUpcomingMeetings() {
    db.collection("meetings")
        .whereEqualTo("participants", currentUserId)
        .whereGreaterThan("date", System.currentTimeMillis())
        .orderBy("date")
        .limit(3)
        .get()
        .addOnSuccessListener { ... }
}

// 3. Notifications récentes
private fun loadRecentNotifications() {
    db.collection("notifications")
        .whereEqualTo("userId", currentUserId)
        .orderBy("timestamp", Query.Direction.DESCENDING)
        .limit(5)
        .get()
}
```

**Collections Firestore utilisées:**
- `groups` - Liste groupes utilisateur
- `meetings` - Prochaines réunions
- `notifications` - Alertes récentes

---

### 2. **ProfileActivity** - Profil utilisateur
**Fichier:** `profile/ProfileActivity.kt`
**Layout:** `activity_profile.xml`

```kotlin
// ❌ FONCTIONNALITÉS MANQUANTES:

// 1. Charger profil depuis Firestore
private fun loadProfile() {
    val userId = auth.currentUser?.uid
    
    db.collection("users").document(userId!!)
        .get()
        .addOnSuccessListener { doc ->
            binding.tvName.text = doc.getString("full_name")
            binding.tvEmail.text = doc.getString("email")
            binding.ivAvatar.setImageURI(doc.getString("avatar_url"))
        }
}

// 2. Statistiques profil
private fun loadProfileStats() {
    // - Nombre de réunions organisées
    // - Groupes actifs
    // - Messages envoyés
}
```

---

### 3. **EditProfileActivity** - Modifier profil
**Fichier:** `profile/EditProfileActivity.kt`
**Layout:** `activity_edit_profile.xml`

```kotlin
// ❌ FONCTIONNALITÉS MANQUANTES:

// 1. Charger données existantes
override fun onCreate(...) {
    loadCurrentProfile()
}

// 2. Sauvegarder modifications
btnSave.setOnClickListener {
    val updates = hashMapOf(
        "full_name" to binding.etName.text.toString(),
        "bio" to binding.etBio.text.toString(),
        "phone" to binding.etPhone.text.toString()
    )
    
    db.collection("users").document(userId)
        .update(updates as Map<String, Any>)
        .addOnSuccessListener {
            Toast.makeText(this, "Profil mis à jour", Toast.LENGTH_SHORT).show()
        }
}
```

---

### 4. **AvatarActivity** - Photo de profil
**Fichier:** `profile/AvatarActivity.kt`
**Layout:** `activity_avatar.xml`

```kotlin
// ❌ FONCTIONNALITÉS MANQUANTES:

// 1. Sélectionner image
private fun pickImage() {
    val intent = Intent(Intent.ACTION_PICK)
    intent.type = "image/*"
    startActivityForResult(intent, REQUEST_IMAGE_PICK)
}

// 2. Upload vers Firebase Storage
private fun uploadAvatar(imageUri: Uri) {
    val ref = FirebaseStorage.getInstance()
        .getReference("avatars/${auth.currentUser?.uid}.jpg")
    
    ref.putFile(imageUri)
        .addOnSuccessListener { snapshot ->
            ref.downloadUrl.addOnSuccessListener { uri ->
                // 3. Mettre à jour Firestore
                db.collection("users").document(userId)
                    .update("avatar_url", uri.toString())
            }
        }
}
```

---

### 5. **GroupsActivity** - Liste des groupes
**Fichier:** `groups/GroupsActivity.kt`
**Layout:** `activity_groups.xml`

```kotlin
// ❌ FONCTIONNALITÉS MANQUANTES:

// 1. Charger groupes
private fun loadGroups() {
    db.collection("groups")
        .whereArrayContains("members", currentUserId)
        .get()
        .addOnSuccessListener { documents ->
            val groups = documents.map { doc -> 
                Group(doc.id, doc.getString("name"), ...)
            }
            groupsAdapter.submitList(groups)
        }
}

// 2. Créer groupe
btnCreateGroup.setOnClickListener {
    showCreateGroupDialog()
}

private fun createGroup(name: String, description: String) {
    val groupData = hashMapOf(
        "name" to name,
        "description" to description,
        "members" to arrayListOf(currentUserId),
        "createdAt" to System.currentTimeMillis()
    )
    
    db.collection("groups").add(groupData)
}
```

---

### 6. **ChatListActivity** - Liste conversations
**Fichier:** `chat/ChatListActivity.kt`
**Layout:** `activity_chat_list.xml`

```kotlin
// ❌ FONCTIONNALITÉS MANQUANTES:

// 1. Charger groupes avec derniers messages
private fun loadChatList() {
    // Requête复合: groups + derniers messages
    db.collection("groups")
        .whereArrayContains("members", currentUserId)
        .get()
        .addOnSuccessListener { groups ->
            for (group in groups) {
                loadLastMessage(group.id)
            }
        }
}

// 2. Charger dernier message par groupe
private fun loadLastMessage(groupId: String) {
    db.collection("groups").document(groupId)
        .collection("messages")
        .orderBy("timestamp", Query.Direction.DESCENDING)
        .limit(1)
        .get()
}
```

---

### 7. **ChatActivity** - Conversation
**Fichier:** `chat/ChatActivity.kt`
**Layout:** `chatgroupe1.xml` (8.5KB)

```kotlin
// ❌ FONCTIONNALITÉS PARTIELLES - À COMPLÉTER:

// 1. Envoyer message
binding.btnSend.setOnClickListener {
    val message = binding.etMessage.text.toString()
    
    val messageData = hashMapOf(
        "text" to message,
        "senderId" to currentUserId,
        "timestamp" to System.currentTimeMillis(),
        "read" to false
    )
    
    // Ajouter à la collection messages
    db.collection("groups").document(groupId!!)
        .collection("messages")
        .add(messageData)
        .addOnSuccessListener {
            binding.etMessage.text.clear()
        }
}

// 2. Écouter messages en temps réel (À AJOUTER)
private fun listenForMessages() {
    db.collection("groups").document(groupId!!)
        .collection("messages")
        .orderBy("timestamp")
        .addSnapshotListener { snapshots, error ->
            // Mettre à jour RecyclerView en temps réel
        }
}
```

---

### 8. **MeetingActivity** - Réunion en cours
**Fichier:** `meetings/MeetingActivity.kt`
**Layout:** `activity_meeting.xml` (23KB - Très grand)

```kotlin
// ❌ FONCTIONNALITÉS MANQUANTES:

// 1. Charger détails réunion
private fun loadMeetingDetails() {
    db.collection("meetings").document(meetingId)
        .get()
        .addOnSuccessListener { doc ->
            binding.meetingCode.text = doc.getString("meetingLink") ?: generateCode()
            loadParticipants(doc.get("participants"))
        }
}

// 2. Timer de durée (simulation)
private fun startTimer() {
    var seconds = 0
    timer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            seconds++
            val hours = seconds / 3600
            val minutes = (seconds % 3600) / 60
            val secs = seconds % 60
            binding.meetingTimer.text = String.format("%02d:%02d:%02d", hours, minutes, secs)
        }
        override fun onFinish() {}
    }.start()
}

// 3. Statistiques réunion (À implémenter)
private fun simulateSpeakingActivity() {
    // - Qui parle
    // - Durée发言
    // - Activité participants
}
```

---

### 9. **CalendarMeetingActivity** - Calendrier réunions
**Fichier:** `meetings/CalendarMeetingActivity.kt`
**Layout:** `activity_calendar_meeting.xml`

```kotlin
// ❌ FONCTIONNALITÉS MANQUANTES:

// 1. Charger réunions du mois
private fun loadMonthMeetings(year: Int, month: Int) {
    val startOfMonth = Calendar.getInstance().apply {
        set(year, month, 1, 0, 0, 0)
    }.timeInMillis
    
    val endOfMonth = Calendar.getInstance().apply {
        set(year, month, 31, 23, 59, 59)
    }.timeInMillis
    
    db.collection("meetings")
        .whereGreaterThan("date", startOfMonth)
        .whereLessThan("date", endOfMonth)
        .get()
        .addOnSuccessListener { documents ->
            // Afficher indicateurs sur dates
        }
}

// 2. Clic sur date
private fun onDateSelected(date: Long) {
    db.collection("meetings")
        .whereEqualTo("date", date)
        .get()
        .addOnSuccessListener { ... }
}
```

---

### 10. **MeetingCalendarActivity** - Vue calendrier
**Fichier:** `meetings/MeetingCalendarActivity.kt`
**Layout:** `activity_meeting_calendar.xml`

```kotlin
// ❌ FONCTIONNALITÉS MANQUANTES:

// 1. Afficher calendrier avec CompactCalendarView
private fun setupCalendar() {
    binding.calendarView.setListener(object : CompactCalendarView.CompactCalendarViewListener {
        override fun onDayClick(dateClicked: Date) {
            showMeetingsForDate(dateClicked)
        }
        
        override fun onMonthScroll(firstDayOfNewMonth: Date) {
            loadMeetingsForMonth(firstDayOfNewMonth)
        }
    })
}

// 2. Ajouter événements sur calendrier
private fun addEventsToCalendar(meetings: List<Meeting>) {
    meetings.forEach { meeting ->
        // Badge coloré selon type
    }
}
```

---

### 11. **NotificationActivity** - Notifications
**Fichier:** `notifications/NotificationActivity.kt`
**Layout:** `activity_notifications.xml`

```kotlin
// ❌ FONCTIONNALITÉS MANQUANTES:

// 1. Charger notifications
private fun loadNotifications() {
    db.collection("notifications")
        .whereEqualTo("userId", currentUserId)
        .orderBy("timestamp", Query.Direction.DESCENDING)
        .get()
        .addOnSuccessListener { documents ->
            val notifications = documents.map { ... }
            adapter.submitList(notifications)
        }
}

// 2. Marquer comme lu
fun markAsRead(notificationId: String) {
    db.collection("notifications").document(notificationId)
        .update("read", true)
}

// 3. Supprimer notification
fun deleteNotification(notificationId: String) {
    db.collection("notifications").document(notificationId)
        .delete()
}
```

---

### 12. **SearchActivity** - Recherche
**Fichier:** `search/SearchActivity.kt`
**Layout:** `activity_search.xml`

```kotlin
// ❌ FONCTIONNALITÉS MANQUANTES:

// 1. Recherche en temps réel
binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
    override fun onQueryTextChange(newText: String): Boolean {
        if (newText.length >= 2) {
            performSearch(newText)
        }
        return true
    }
})

// 2. Rechercher dans Firestore
private fun performSearch(query: String) {
    // Rechercher dans users
    db.collection("users")
        .whereGreaterThanOrEqualTo("full_name", query)
        .get()
    
    // Rechercher dans groups
    db.collection("groups")
        .whereGreaterThanOrEqualTo("name", query)
        .get()
    
    // Rechercher dans meetings
    db.collection("meetings")
        .whereGreaterThanOrEqualTo("title", query)
        .get()
}
```

---

## 📋 Collections Firestore à créer

```javascript
// Collection: users
{
  id: "user_123",
  email: "user@example.com",
  full_name: "John Doe",
  avatar_url: "https://...",
  bio: "Student",
  phone: "+212600000000",
  created_at: timestamp,
  settings: {
    language: "fr",
    theme: "light"
  }
}

// Collection: groups
{
  id: "group_123",
  name: "Study Group A",
  description: "Math study group",
  members: ["user_123", "user_456"],
  createdBy: "user_123",
  createdAt: timestamp,
  photo_url: "https://..."
}

// Collection: meetings
{
  id: "meeting_123",
  title: "Weekly Study",
  description: "Math revision",
  date: timestamp,
  startTime: "14:00",
  endTime: "16:00",
  location: "Library",
  organizerId: "user_123",
  participants: ["user_123", "user_456"],
  groupId: "group_123",
  meetingLink: "https://meet.google.com/...",
  status: "scheduled|completed|cancelled",
  reminderMinutes: 15
}

// Collection: messages (sous-groupe de groups)
{
  id: "message_123",
  text: "Hello!",
  senderId: "user_123",
  timestamp: timestamp,
  read: false,
  type: "text|image|file"
}

// Collection: notifications
{
  id: "notif_123",
  userId: "user_123",
  title: "New message",
  body: "You have a new message in Study Group",
  type: "message|meeting|system",
  read: false,
  timestamp: timestamp
}
```

---

## 🎯 Résumé des tâches par développeur

### **Backend 1: Chat & Realtime**
- [ ] ChatActivity - Messages temps réel
- [ ] ChatListActivity - Liste conversations
- [ ] Notifications - Système push

### **Backend 2: Meetings & Calendar**
- [ ] **MeetingDetailActivity** - JOIN + CALENDAR 🔴
- [ ] **PlanifierMeetingActivity** - Créer réunion 🔴
- [ ] CalendarMeetingActivity - Vue calendrier
- [ ] MeetingCalendarActivity - Calendrier

### **Frontend 1: Profile & Groups**
- [ ] ProfileActivity - Charger/modifier profil
- [ ] EditProfileActivity - Modification données
- [ ] AvatarActivity - Upload photo
- [ ] GroupsActivity - CRUD groupes

### **Frontend 2: Home & Search**
- [ ] HomeActivity - Dashboard + stats
- [ ] SearchActivity - Recherche Firestore
- [ ] SettingsActivity - Paramètres
- [ ] Appearance - Thème