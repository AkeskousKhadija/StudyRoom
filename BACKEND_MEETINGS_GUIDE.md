# 🎯 Guide Backend - Partie Meetings

## 📋 Votre Mission: Backend Meetings

Vous êtes responsable du backend pour la partie **Réunions (Meetings)** de l'application StudyRoom.

---

## 📁 Fichiers à modifier

### Dossier: `app/src/main/java/com/projet/studyroom/meetings/`

| Fichier | Description | Priorité | Status |
|---------|-------------|----------|--------|
| `MeetingDetailActivity.kt` | Détails réunion | 🔴 HAUTE | ❌ À implémenter |
| `PlanifierMeetingActivity.kt` | Créer réunion | 🔴 HAUTE | ❌ À implémenter |
| `MeetingActivity.kt` | Réunion en cours | ⚠️ MOYENNE | ⚠️ Partiel |
| `MeetingCalendarActivity.kt` | Calendrier | ⚠️ MOYENNE | ⚠️ Partiel |
| `CalendarMeetingActivity.kt` | Liste réunions | ⚠️ MOYENNE | ⚠️ Partiel |
| `MeetingDetailsActivity.kt` | Voir détails | 🔄 FAIBLE | ⚠️ Partiel |

---

## 🔴 Tâche 1: MeetingDetailActivity (URGENT)

**Fichier:** [`MeetingDetailActivity.kt`](app/src/main/java/com/projet/studyroom/meetings/MeetingDetailActivity.kt)

**Layout:** `activity_meeting_detail.xml` (11.6KB)

### Code actuel (lignes 22-29):
```kotlin
binding.btnJoinMeeting.setOnClickListener {
    // TODO: Implement join meeting logic
}

binding.btnAddToCalendar.setOnClickListener {
    // TODO: Implement add to calendar logic
}
```

### Code à implémenter:

```kotlin
// Ajouter ces imports
import android.content.Intent
import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class MeetingDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMeetingDetailBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    
    private var meetingId: String? = null
    private var meetingTitle: String? = null
    private var meetingDate: Long = 0
    private var meetingStartTime: String? = null
    private var meetingEndTime: String? = null
    private var meetingDescription: String? = null
    private var meetingLocation: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMeetingDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Initialize Firebase
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        
        // Get meeting ID from intent
        meetingId = intent.getStringExtra("meeting_id")
        
        if (meetingId == null) {
            Toast.makeText(this, "Erreur: Réunion non trouvée", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        // Load meeting details
        loadMeetingDetails()
        
        // Setup click listeners
        setupListeners()
    }
    
    private fun loadMeetingDetails() {
        db.collection("meetings").document(meetingId!!)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    meetingTitle = document.getString("title")
                    meetingDate = document.getLong("date") ?: 0
                    meetingStartTime = document.getString("startTime")
                    meetingEndTime = document.getString("endTime")
                    meetingDescription = document.getString("description")
                    meetingLocation = document.getString("location")
                    
                    // Update UI
                    binding.tvMeetingTitle.text = meetingTitle
                    binding.tvMeetingDate.text = formatDate(meetingDate)
                    binding.tvMeetingTime.text = "${meetingStartTime} - ${meetingEndTime}"
                    binding.tvMeetingDescription.text = meetingDescription ?: "Pas de description"
                    binding.tvMeetingLocation.text = meetingLocation ?: "Non spécifié"
                    
                    // Update meeting code if available
                    val meetingLink = document.getString("meetingLink")
                    if (meetingLink != null) {
                        binding.tvMeetingCode.text = extractCodeFromLink(meetingLink)
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    
    private fun setupListeners() {
        // Bouton retour
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        // ⚠️ JOIN MEETING - À IMPLÉMENTER
        binding.btnJoinMeeting.setOnClickListener {
            joinMeeting()
        }
        
        // ⚠️ ADD TO CALENDAR - À IMPLÉMENTER
        binding.btnAddToCalendar.setOnClickListener {
            addToCalendar()
        }
        
        // Copier code réunion
        binding.btnCopyCode.setOnClickListener {
            val code = binding.tvMeetingCode.text.toString()
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Meeting Code", code)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Code copié!", Toast.LENGTH_SHORT).show()
        }
    }
    
    // ✅ JOIN MEETING - IMPLÉMENTÉ
    private fun joinMeeting() {
        db.collection("meetings").document(meetingId!!)
            .get()
            .addOnSuccessListener { document ->
                val meetingLink = document.getString("meetingLink")
                
                if (!meetingLink.isNullOrEmpty()) {
                    // Ouvrir le lien de réunion
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(meetingLink))
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(this, "Impossible d'ouvrir le lien", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Générer un code alternatif
                    val generatedCode = generateMeetingCode()
                    
                    // Option 1: Afficher le code pour une réunion locale
                    AlertDialog.Builder(this)
                        .setTitle("Code de réunion")
                        .setMessage("Partagez ce code avec les participants:\n\n$generatedCode")
                        .setPositiveButton("OK", null)
                        .show()
                    
                    // Option 2: Intégration avec Google Meet (si disponible)
                    openGoogleMeet()
                }
                
                // Mettre à jour le statut de la réunion
                db.collection("meetings").document(meetingId!!)
                    .update(
                        mapOf(
                            "status" to "in_progress",
                            "startedAt" to System.currentTimeMillis(),
                            "startedBy" to auth.currentUser?.uid
                        )
                    )
                    
                Toast.makeText(this, "Réunion démarrée", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    
    // ✅ ADD TO CALENDAR - IMPLÉMENTÉ
    private fun addToCalendar() {
        // Créer un Intent pour ajouter au calendrier système
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = Uri.parse("content://com.android.calendar/events")
            
            putExtra("title", meetingTitle ?: "Réunion StudyRoom")
            putExtra("description", meetingDescription ?: "Réunion planifiée via StudyRoom")
            putExtra("eventLocation", meetingLocation ?: "En ligne")
            
            // Convertir date et heure en timestamp
            val startMillis = convertToMillis(meetingDate, meetingStartTime)
            val endMillis = convertToMillis(meetingDate, meetingEndTime)
            
            putExtra("beginTime", startMillis)
            putExtra("endTime", endMillis)
            putExtra("allDay", false)
        }
        
        try {
            startActivity(intent)
            Toast.makeText(this, "Calendrier ouvert", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            // Fallback: Afficher les détails à copier manuellement
            AlertDialog.Builder(this)
                .setTitle("Ajouter au calendrier")
                .setMessage("Détails de la réunion:\n\n" +
                        "Titre: $meetingTitle\n" +
                        "Date: ${formatDate(meetingDate)}\n" +
                        "Heure: $meetingStartTime - $meetingEndTime\n" +
                        "Lieu: $meetingLocation")
                .setPositiveButton("OK", null)
                .show()
        }
    }
    
    // Fonctions utilitaires
    
    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    private fun extractCodeFromLink(link: String): String {
        // Extraire le code depuis un lien Google Meet par exemple
        return link.substringAfterLast("/")
    }
    
    private fun generateMeetingCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..3).map { chars.random() }.joinToString("-") +
               (1..4).map { chars.random() }.joinToString("")
    }
    
    private fun openGoogleMeet() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://meet.google.com/"))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Ouvrez Google Meet manuellement", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun convertToMillis(date: Long, time: String?): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date
        
        // Parser le temps (format HH:mm)
        time?.let {
            val parts = it.split(":")
            if (parts.size == 2) {
                calendar.set(Calendar.HOUR_OF_DAY, parts[0].toIntOrNull() ?: 0)
                calendar.set(Calendar.MINUTE, parts[1].toIntOrNull() ?: 0)
            }
        }
        
        return calendar.timeInMillis
    }
}
```

---

## 🔴 Tâche 2: PlanifierMeetingActivity (URGENT)

**Fichier:** [`PlanifierMeetingActivity.kt`](app/src/main/java/com/projet/studyroom/meetings/PlanifierMeetingActivity.kt)

**Layout:** `activity_planifier_meeting.xml` (14.8KB)

### Code actuel (lignes 65-68):
```kotlin
btnCreate.setOnClickListener {
    finish()  // ❌ Juste fermeture, pas de sauvegarde
}
```

### Code à implémenter:

```kotlin
// Ajouter ces imports
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class PlanifierMeetingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlanifierMeetingBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    
    private var groupId: String? = null
    private var selectedDate: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlanifierMeetingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Initialize Firebase
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        
        // Get group ID if available
        groupId = intent.getStringExtra("group_id")
        
        setupUI()
        setupListeners()
    }
    
    private fun setupUI() {
        // Initialiser les champs date/heure avec aujourd'hui
        val today = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        
        binding.etDate.setText(dateFormat.format(today.time))
        binding.etHeureDebut.setText("09:00")
        binding.etHeureFin.setText("10:00")
    }
    
    private fun setupListeners() {
        // Bouton retour
        binding.btnBack.setOnClickListener {
            showCancelDialog()
        }
        
        // 📅 Sélecteur de date
        binding.etDate.setOnClickListener {
            showDatePicker()
        }
        
        // ⏰ Sélecteur heure début
        binding.etHeureDebut.setOnClickListener {
            showTimePicker(true)
        }
        
        // ⏰ Sélecteur heure fin
        binding.etHeureFin.setOnClickListener {
            showTimePicker(false)
        }
        
        // ✅ Créer la réunion - CODE À IMPLÉMENTER
        binding.btnCreate.setOnClickListener {
            createMeeting()
        }
        
        // ❌ Annuler
        binding.btnCancel.setOnClickListener {
            showCancelDialog()
        }
    }
    
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        
        DatePickerDialog(this,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                selectedDate = selectedCalendar.timeInMillis
                
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.etDate.setText(dateFormat.format(selectedCalendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
    
    private fun showTimePicker(isStartTime: Boolean) {
        val calendar = Calendar.getInstance()
        
        TimePickerDialog(this,
            { _, hourOfDay, minute ->
                val time = String.format("%02d:%02d", hourOfDay, minute)
                
                if (isStartTime) {
                    binding.etHeureDebut.setText(time)
                } else {
                    binding.etHeureFin.setText(time)
                }
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }
    
    // ✅ CREATE MEETING - IMPLÉMENTÉ
    private fun createMeeting() {
        // 1. Récupérer les valeurs des champs
        val sujet = binding.etSujet.text.toString().trim()
        val date = binding.etDate.text.toString().trim()
        val heureDebut = binding.etHeureDebut.text.toString().trim()
        val heureFin = binding.etHeureFin.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        
        // 2. Validation des champs obligatoires
        if (sujet.isEmpty()) {
            binding.tilSujet.error = "Le sujet est obligatoire"
            return
        }
        binding.tilSujet.error = null
        
        if (date.isEmpty()) {
            binding.tilDate.error = "La date est obligatoire"
            return
        }
        binding.tilDate.error = null
        
        if (heureDebut.isEmpty()) {
            binding.tilHeureDebut.error = "L'heure de début est obligatoire"
            return
        }
        binding.tilHeureDebut.error = null
        
        if (heureFin.isEmpty()) {
            binding.tilHeureFin.error = "L'heure de fin est obligatoire"
            return
        }
        binding.tilHeureFin.error = null
        
        // Vérifier que l'heure de fin est après l'heure de début
        if (heureDebut >= heureFin) {
            binding.tilHeureFin.error = "L'heure de fin doit être après l'heure de début"
            return
        }
        
        // 3. Afficher indicateur de chargement
        binding.btnCreate.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE
        
        // 4. Convertir la date en timestamp
        val dateTimestamp = parseDateToTimestamp(date)
        
        // 5. Générer un lien de réunion (ou laisser vide)
        val meetingLink = generateMeetingLink()
        
        // 6. Préparer les données
        val meetingData = hashMapOf(
            "title" to sujet,
            "description" to description,
            "date" to dateTimestamp,
            "startTime" to heureDebut,
            "endTime" to heureFin,
            "location" to location,
            "organizerId" to auth.currentUser?.uid,
            "groupId" to groupId,
            "meetingLink" to meetingLink,
            "status" to "scheduled",
            "createdAt" to System.currentTimeMillis(),
            "reminderMinutes" to 15
        )
        
        // 7. Sauvegarder dans Firestore
        db.collection("meetings")
            .add(meetingData)
            .addOnSuccessListener { documentReference ->
                // Success - notification
                Toast.makeText(
                    this,
                    "Réunion créée avec succès!",
                    Toast.LENGTH_SHORT
                ).show()
                
                // Planifier une notification de rappel
                scheduleReminder(documentReference.id, sujet, dateTimestamp)
                
                // Retourner à l'écran précédent
                finish()
            }
            .addOnFailureListener { e ->
                // Erreur
                binding.btnCreate.isEnabled = true
                binding.progressBar.visibility = View.GONE
                
                Toast.makeText(
                    this,
                    "Erreur lors de la création: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
    
    // Fonctions utilitaires
    
    private fun parseDateToTimestamp(dateString: String): Long {
        return try {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            dateFormat.parse(dateString)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }
    
    private fun generateMeetingLink(): String {
        // Générer un lien Google Meet aléatoire
        val chars = "abcdefghijklmnopqrstuvwxyz"
        val part1 = (1..3).map { chars.random() }.joinToString("")
        val part2 = (1..4).map { chars.random() }.joinToString("")
        val part3 = (1..3).map { chars.random() }.joinToString("")
        
        return "https://meet.google.com/$part1-$part2-$part3"
    }
    
    private fun scheduleReminder(meetingId: String, title: String, date: Long) {
        // Implémenter la planification du rappel (WorkManager ou AlarmManager)
        // Pour l'instant, juste un log
        Log.d("PlanifierMeeting", "Rappel programmé pour: $title à $date")
    }
    
    private fun showCancelDialog() {
        AlertDialog.Builder(this)
            .setTitle("Annulation")
            .setMessage("Voulez-vous vraiment annuler ce meeting ?")
            .setPositiveButton("Oui") { _, _ ->
                finish()
            }
            .setNegativeButton("Non", null)
            .show()
    }
}
```

---

## ⚠️ Tâche 3: MeetingActivity - Compléter

**Fichier:** [`MeetingActivity.kt`](app/src/main/java/com/projet/studyroom/meetings/MeetingActivity.kt)

**Charger les participants depuis Firestore:**
```kotlin
private fun loadParticipants(participantIds: List<String>) {
    participantIds.forEach { userId ->
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { doc ->
                val name = doc.getString("full_name") ?: "Unknown"
                val avatar = doc.getString("avatar_url")
                // Ajouter au layout
            }
    }
}
```

---

## ⚠️ Tâche 4: CalendarMeetingActivity

**Charger les réunions depuis Firestore:**
```kotlin
private fun loadMeetings() {
    val startOfMonth = /* calcul date début */
    val endOfMonth = /* calcul date fin */
    
    db.collection("meetings")
        .whereGreaterThan("date", startOfMonth)
        .whereLessThan("date", endOfMonth)
        .get()
        .addOnSuccessListener { documents ->
            val meetings = documents.map { doc ->
                CalendarMeeting(
                    doc.id,
                    doc.getString("title") ?: "",
                    doc.getLong("date") ?: 0,
                    doc.getString("startTime") ?: ""
                )
            }
            adapter.submitList(meetings)
        }
}
```

---

## 📝 Instructions de travail

### 1. Créer une branche
```bash
git checkout -b backend-meetings
```

### 2. Modifier les fichiers dans l'ordre:
1. `MeetingDetailActivity.kt` - JOIN + ADD TO CALENDAR
2. `PlanifierMeetingActivity.kt` - Créer réunion
3. `MeetingActivity.kt` - Charger participants
4. `CalendarMeetingActivity.kt` - Charger réunions

### 3. Tester avec Firebase
Assurez-vous d'avoir votre `google-services.json` dans `app/`

### 4. Commiter
```bash
git add .
git commit -m "feat(meetings): Implement backend for meeting features"
git push origin backend-meetings
```

### 5. Créer Pull Request
Sur GitHub, créer une PR vers `main`

---

## ✅ Checklist avant de finir

- [ ] MeetingDetailActivity: joinMeeting() fonctionne
- [ ] MeetingDetailActivity: addToCalendar() fonctionne
- [ ] PlanifierMeetingActivity: createMeeting() sauvegarde dans Firestore
- [ ] Validation des champs (sujet, date, heures)
- [ ] Messages d'erreur appropriés
- [ ] Indicateur de chargement pendant l'enregistrement
- [ ] Tests avec données réelles Firestore