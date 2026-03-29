# StudyRoom - Répartition des Tâches Développement

## 🎯 Structure Backend + Frontend

Pour une équipe de 4 développeurs, séparés en **Backend** et **Frontend**:

---

## 👥 Équipe Backend (2 développeurs)

### **Backend 1: Chat & Conversations**

**Responsable:** Fonctionnalités temps réel - Chat et Messagerie

#### 🔥 Tâches à implémenter:

##### 1. Modèle de données Firestore (Chat)
```kotlin
// Fichier: chat/ChatMessage.kt - À AMÉLIORER
- Ajouter champs: timestamp, read, attachments
- Ajouter type: text, image, file
```

##### 2. Opérations Firestore - ChatActivity.kt
```kotlin
// Dans: chat/ChatActivity.kt (lignes 12-14, 23, 39)

1. Envoyer message:
   db.collection("groups").document(groupId)
      .collection("messages")
      .add(messageData)

2. Écouter messages en temps réel:
   db.collection("groups").document(groupId)
      .collection("messages")
      .orderBy("timestamp", Query.Direction.ASCENDING)
      .addSnapshotListener { ... }

3. Marquer comme lu:
   - Update read: true quand message affiché

4. Supprimer message:
   db.collection("groups").document(groupId)
      .collection("messages").document(messageId)
      .delete()
```

##### 3. ChatListActivity.kt - Charger liste conversations
```kotlin
// Dans: chat/ChatListActivity.kt

1. Charger groupes de l'utilisateur:
   db.collection("groups")
      .whereArrayContains("members", currentUserId)
      .get()

2. Afficher dernier message:
   - Requête pour dernier message par groupe

3. Compteur messages non lus:
   - Query where read == false
```

##### 4. ChatGroupsAdapter.kt - Gestion groupes
```kotlin
// Dans: chat/ChatGroupsAdapter.kt

1. Créer groupe:
   db.collection("groups").add(groupData)

2. Ajouter membre:
   - Update array members

3. Quitter groupe:
   - Remove from members array
```

**Fichiers à modifier:**
- [`chat/ChatActivity.kt`](app/src/main/java/com/projet/studyroom/chat/ChatActivity.kt) - 15KB
- [`chat/ChatMessage.kt`](app/src/main/java/com/projet/studyroom/chat/ChatMessage.kt)
- [`chat/ChatListActivity.kt`](app/src/main/java/com/projet/studyroom/chat/ChatListActivity.kt)
- [`chat/ChatGroupsAdapter.kt`](app/src/main/java/com/projet/studyroom/chat/ChatGroupsAdapter.kt)
- [`chat/ChatMessageAdapter.kt`](app/src/main/java/com/projet/studyroom/chat/ChatMessageAdapter.kt)

**Tâches:**
- [ ] Créer collection Firestore: `groups`, `messages`
- [ ] Implémenter envoi/réception messages temps réel
- [ ] Ajouter support images/fichiers
- [ ] Implémenter indicateur "en train d'écrire"
- [ ] Notifications push pour nouveaux messages

---

### **Backend 2: Meetings & Calendar**

**Responsable:** Planification, Réunions et Calendrier Firebase

#### 🔥 Tâches à implémenter:

##### 1. Modèle de données Firestore (Meetings)
```kotlin
// Fichier: meetings/CalendarMeeting.kt - À AMÉLIORER
data class Meeting(
    val id: String,
    val title: String,
    val description: String,
    val date: Long,
    val startTime: String,
    val endTime: String,
    val location: String,
    val organizerId: String,
    val participants: List<String>,
    val groupId: String,
    val meetingLink: String?,  // Pour join meeting
    val reminder: Int          // Minutes avant
)
```

##### 2. Opérations Firestore - PlanifierMeetingActivity.kt
```kotlin
// Dans: meetings/PlanifierMeetingActivity.kt (lignes 65-68)

btnCreate.setOnClickListener {
    // 1. Créer document meeting dans Firestore:
    db.collection("meetings").add(meetingData)
        .addOnSuccessListener { docRef ->
            // 2. Ajouter aux calendriers des participants
            participants.forEach { userId ->
                addToUserCalendar(userId, docRef.id)
            }
            // 3. Envoyer notifications
            sendMeetingNotification(participants)
        }
    
    // TODO: Implémenter sauvegarde réelle (actuellement juste finish())
}
```

##### 3. CalendarMeetingActivity.kt - Vue calendrier
```kotlin
// Dans: meetings/CalendarMeetingActivity.kt

1. Charger réunions du mois:
   db.collection("meetings")
      .whereGreaterThan("date", startOfMonth)
      .whereLessThan("date", endOfMonth)
      .get()

2. Afficher indicateurs sur dates:
   - Vert: réunion demain
   - Orange: réunion aujourd'hui
   - Rouge: réunion en cours

3. Filtrer par groupe:
   db.collection("meetings")
      .whereEqualTo("groupId", selectedGroupId)
```

##### 4. MeetingDetailActivity.kt - ⚠️ URGENT
```kotlin
// Dans: meetings/MeetingDetailActivity.kt (lignes 23-28)

btnJoinMeeting.setOnClickListener {
    // TODO: Implémenter join meeting logic
    // 1. Générer/valider meeting link
    // 2. Démarrer activité vidéo
    // 3. Ajouter à l'historique
}

btnAddToCalendar.setOnClickListener {
    // TODO: Implémenter add to calendar logic
    // 1. Créer Intent pour calendrier système
    // 2. Pré-remplir données réunion
}
```

##### 5. MeetingCalendarActivity.kt - Calendrier réunions
```kotlin
// Dans: meetings/MeetingCalendarActivity.kt

1. Vue mensuelle avec réunions:
   - Afficher badges sur dates avec réunions

2. Navigation mois précédent/suivant:
   - Recharger réunions du nouveau mois

3. Clic sur date:
   - Afficher réunions du jour sélectionné
```

**Fichiers à modifier:**
- [`meetings/PlanifierMeetingActivity.kt`](app/src/main/java/com/projet/studyroom/meetings/PlanifierMeetingActivity.kt) - 3KB
- [`meetings/CalendarMeetingActivity.kt`](app/src/main/java/com/projet/studyroom/meetings/CalendarMeetingActivity.kt) - 7KB
- [`meetings/CalendarMeeting.kt`](app/src/main/java/com/projet/studyroom/meetings/CalendarMeeting.kt)
- [`meetings/MeetingDetailActivity.kt`](app/src/main/java/com/projet/studyroom/meetings/MeetingDetailActivity.kt) - ⚠️ TODO
- [`meetings/MeetingCalendarActivity.kt`](app/src/main/java/com/projet/studyroom/meetings/MeetingCalendarActivity.kt) - 4KB
- [`meetings/MeetingActivity.kt`](app/src/main/java/com/projet/studyroom/meetings/MeetingActivity.kt) - 7KB

**Tâches:**
- [ ] Créer collection Firestore: `meetings`
- [ ] Implémenter création/modification réunions
- [ ] Ajouter intégration calendrier système Android
- [ ] Implémenter liens de réunion (Zoom/Meet)
- [ ] Système de rappels/notifications

---

## 👥 Équipe Frontend (2 développeurs)

### **Frontend 1: Auth + Profile + Groups**

**Responsable:** Authentification, Profil utilisateur et Gestion des groupes

#### 🎨 Fichiers Layout à créer/modifier:

##### 1. Auth (layouts)
- [`activity_login.xml`](app/src/main/res/layout/activity_login.xml) - Déjà existe
- [`activity_register.xml`](app/src/main/res/layout/activity_register.xml) - Déjà existe

##### 2. Profile (layouts)
- [`activity_profile.xml`](app/src/main/res/layout/activity_profile.xml) - Déjà existe
- [`activity_edit_profile.xml`](app/src/main/res/layout/activity_edit_profile.xml) - Déjà existe
- [`activity_avatar.xml`](app/src/main/res/layout/activity_avatar.xml) - Déjà existe

##### 3. Groups (layouts)
- [`activity_groups.xml`](app/src/main/res/layout/activity_groups.xml) - Déjà existe
- [`dialog_create_group.xml`](app/src/main/res/layout/dialog_create_group.xml) - Déjà existe
- [`item_group.xml`](app/src/main/res/layout/item_group.xml) - Déjà existe

#### 🎨 Tâches UI/UX:

##### LoginActivity.kt - Améliorer design
```kotlin
// Ajouter:
- Animation d'entrée (fade in)
- Icônes dans les champs email/password
- Bouton "Mot de passe oublié"
- Indicateur chargement (progress bar)
- Validation en temps réel
```

##### RegisterActivity.kt - Layout existant (lignes 1-100)
```kotlin
// Améliorer UX:
- Vérification force mot de passe
- Confirmation email
- Animation de succès après inscription
```

##### ProfileActivity.kt
```kotlin
// Améliorer display:
- RecyclerView pour info profil
- Boutons Edit/Delete avatar
- Animation transition vers EditProfile
```

##### GroupsActivity.kt - 8KB
```kotlin
// Améliorer interface:
- FAB pour créer groupe (FloatingActionButton)
- SwipeRefreshLayout pour actualiser
- Dialog création groupe (dialog_create_group.xml)
- Animation ajout/suppression groupe
```

**Tâches UI:**
- [ ] Harmoniser les couleurs (suivre colors.xml)
- [ ] Ajouter icônes Material Design
- [ ] Animer les transitions entre écrans
- [ ] Responsive design (tous écrans)
- [ ] Feedback visuel (loading, errors)

---

### **Frontend 2: Search + Notifications + Home**

**Responsable:** Navigation principale, Recherche et Système de notifications

#### 🎨 Tâches UI/UX:

##### SearchActivity.kt - 5KB
```kotlin
// Améliorer recherche:
// 1. Auto-suggestions pendant frappe
// 2. Filtres (groupes, utilisateurs, réunions)
// 3. Historique des recherches
// 4. Animation résultats
// 5. Empty state design

// Layout: activity_search.xml
- SearchView avec icône
- Filtre chips (Tous, Groupes, Utilisateurs)
- RecyclerView résultats
```

##### NotificationActivity.kt
```kotlin
// Améliorer notifications:
// 1. Badge sur icône app
// 2. Pull-to-refresh
// 3. Swipe to delete
// 4. Mark all as read
// 5. Catégories (messages, réunions, système)

// Layout: activity_notifications.xml
- Toolbar avec "Tout marquer lu"
- RecyclerView notifications
- Empty state
```

##### HomeActivity.kt - Dashboard (9KB)
```kotlin
// Améliorer accueil:
// 1. Widgets rapides (prochaines réunions, messages)
// 2. Statistiques (groupes, réunions ce mois)
// 3. Actions rapides
// 4. Notifications importantes

// Layout: activity_home.xml
- Header avec avatar + nom
- Cartes statistiques
- Liste upcoming meetings
- Quick actions
```

##### Navigation - MainActivity.kt
```kotlin
// Améliorer navigation:
// 1. Bottom Navigation Bar
// 2. Navigation Drawer
// 3. Animation icônes
// 4. Badge notifications

// Layout: activity_main.xml
- FrameLayout pour fragments
- BottomNavigationView
```

**Tâches UI:**
- [ ] Créer design moderne et cohérent
- [ ] Ajouter animations fluides
- [ ] Optimiser performance滚动
- [ ] Gestion erreurs avec retry
- [ ] États vides élégants

---

## 📊 Tableau Récapitulatif des Tâches

### Backend 1 - Chat & Conversations

| Tâche | Fichier | Priorité |
|-------|---------|----------|
| Modèle messages | ChatMessage.kt | ⭐ Haute |
| Envoyer message | ChatActivity.kt | ⭐ Haute |
| Temps réel messages | ChatActivity.kt | ⭐ Haute |
| Liste conversations | ChatListActivity.kt | ⭐ Haute |
| Créer groupe | ChatGroupsAdapter.kt | 🔄 Moyen |
| Marquer lu | ChatActivity.kt | 🔄 Moyen |

### Backend 2 - Meetings & Calendar

| Tâche | Fichier | Priorité |
|-------|---------|----------|
| **JOIN MEETING** | MeetingDetailActivity.kt:23 | 🔴 URGENT |
| **ADD TO CALENDAR** | MeetingDetailActivity.kt:28 | 🔴 URGENT |
| Créer réunion | PlanifierMeetingActivity.kt:66 | ⭐ Haute |
| Charger réunions | CalendarMeetingActivity.kt | ⭐ Haute |
| Calendrier mensuel | MeetingCalendarActivity.kt | 🔄 Moyen |
| Détails réunion | MeetingDetailsActivity.kt | 🔄 Moyen |

### Frontend 1 - Auth + Profile + Groups

| Tâche | Fichier | Priorité |
|-------|---------|----------|
| Design connexion | LoginActivity.kt | ⭐ Haute |
| Animation inscription | RegisterActivity.kt | ⭐ Haute |
| Interface profil | ProfileActivity.kt | ⭐ Haute |
| Édition profil | EditProfileActivity.kt | 🔄 Moyen |
| Liste groupes | GroupsActivity.kt | ⭐ Haute |
| Dialog créer groupe | GroupsActivity.kt | 🔄 Moyen |

### Frontend 2 - Search + Notifications + Home

| Tâche | Fichier | Priorité |
|-------|---------|----------|
| Recherche temps réel | SearchActivity.kt | ⭐ Haute |
| Notifications push | NotificationActivity.kt | ⭐ Haute |
| Dashboard | HomeActivity.kt | ⭐ Haute |
| Navigation | MainActivity.kt | 🔄 Moyen |
| États vides | Tous layouts | 🔄 Moyen |

---

## 🚀 Instructions de Travail

### Pour Backend Developers:

```bash
# 1. Cloner le repo
git clone https://github.com/AkeskousKhadija/StudyRoom.git

# 2. Créer branche backend-chat OU backend-meetings
git checkout -b backend-chat

# 3. Travailler sur les fichiers Firestore
# 4. Tester avec Firebase local emulator OU cloud

# 5. Commiter
git add .
git commit -m "feat(chat): Add Firestore real-time messages"
git push origin backend-chat

# 6. Créer Pull Request
```

### Pour Frontend Developers:

```bash
# 1. Cloner le repo
git clone https://github.com/AkeskousKhadija/StudyRoom.git

# 2. Créer branche frontend-auth OU frontend-ui
git checkout -b frontend-auth

# 3. Travailler sur layouts et animations
# 4. Tester sur émulateur

# 5. Commiter
git add .
git commit -m "feat(ui): Improve login screen design"
git push origin frontend-auth

# 6. Créer Pull Request
```

---

## ⚠️ Règles Importantes

1. **Pas de code en dur** - Utiliser Firebase pour toutes les données
2. **Valider entrée** - Vérifier données côté client ET serveur
3. **Gestion erreurs** - Afficher messages utilisateur clairs
4. **Synchronisation** - Merger régulièrement avec main
5. **Code review** - Au moins 1 approval avant merge

---

## 🎯 Objectifs Sprint

### Première semaine:
- [ ] Backend 1: Chat temps réel fonctionnel
- [ ] Backend 2: JOIN MEETING + ADD TO CALENDAR
- [ ] Frontend 1: Design auth + profile
- [ ] Frontend 2: Dashboard + Navigation

### Deuxième semaine:
- [ ] Intégration complete
- [ ] Tests utilisateurs
- [ ] Corrections bugs
- [ ] Préparation présentation