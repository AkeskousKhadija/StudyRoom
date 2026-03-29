# StudyRoom - Répartition des Tâches d'Équipe

## 📋 Structure du Projet

```
app/src/main/java/com/projet/studyroom/
├── auth/           # Authentification (5 fichiers)
├── chat/           # Chat (5 fichiers)
├── groups/         # Groupes (3 fichiers)
├── home/          # Accueil (3 fichiers)
├── meetings/      # Réunions (8 fichiers)
├── notifications/ # Notifications (3 fichiers)
├── profile/       # Profil (4 fichiers)
└── search/        # Recherche (3 fichiers)
```

---

## 👥 Répartition des Tâches pour 4 Membres

---

### **Membre 1: Auth + Profile**

**Responsable:** Authentification et Gestion du Profil

#### 🔐 Authentification (auth/)
| Fichier | Description | Priorité |
|---------|------------|----------|
| [`LoginActivity.kt`](app/src/main/java/com/projet/studyroom/auth/LoginActivity.kt) | Connexion Firebase | ⭐ Haute |
| [`RegisterActivity.kt`](app/src/main/java/com/projet/studyroom/auth/RegisterActivity.kt) | Inscription Firebase | ⭐ Haute |
| [`SplashActivity.kt`](app/src/main/java/com/projet/studyroom/auth/SplashActivity.kt) | Écran de chargement | 🔄 Moyen |
| [`OnboardingActivity.kt`](app/src/main/java/com/projet/studyroom/auth/OnboardingActivity.kt) | Tutoriel introductif | 🔄 Moyen |
| [`Language_activity.kt`](app/src/main/java/com/projet/studyroom/auth/Language_activity.kt) | Sélection langue | 🔄 Moyen |

#### 👤 Profil (profile/)
| Fichier | Description | Priorité |
|---------|------------|----------|
| [`ProfileActivity.kt`](app/src/main/java/com/projet/studyroom/profile/ProfileActivity.kt) | Affichage profil | ⭐ Haute |
| [`EditProfileActivity.kt`](app/src/main/java/com/projet/studyroom/profile/EditProfileActivity.kt) | Modification profil | ⭐ Haute |
| [`AvatarActivity.kt`](app/src/main/java/com/projet/studyroom/profile/AvatarActivity.kt) | Changement avatar | 🔄 Moyen |
| [`SettingsActivity.kt`](app/src/main/java/com/projet/studyroom/profile/SettingsActivity.kt) | Paramètres (22KB - كبير) | ⭐ Haute |

**Tâches:**
- [ ] Vérifier la connexion Firebase dans LoginActivity
- [ ] Améliorer la validation des formulaires
- [ ] Implémenter "mot de passe oublié"
- [ ] Ajouter gestion des erreurs Firebase
- [ ] Tester l'inscription et connexion

---

### **Membre 2: Chat + Groups**

**Responsable:** Chat en temps réel et Gestion des Groupes

#### 💬 Chat (chat/)
| Fichier | Description | Priorité |
|---------|------------|----------|
| [`ChatActivity.kt`](app/src/main/java/com/projet/studyroom/chat/ChatActivity.kt) | (15KB - أكبر ملف) | ⭐ Haute |
| [`ChatListActivity.kt`](app/src/main/java/com/projet/studyroom/chat/ChatListActivity.kt) | Liste des conversations | ⭐ Haute |
| [`ChatMessage.kt`](app/src/main/java/com/projet/studyroom/chat/ChatMessage.kt) | Modèle message | 🔄 Moyen |
| [`ChatMessageAdapter.kt`](app/src/main/java/com/projet/studyroom/chat/ChatMessageAdapter.kt) | Affichage messages | 🔄 Moyen |
| [`ChatGroupsAdapter.kt`](app/src/main/java/com/projet/studyroom/chat/ChatGroupsAdapter.kt) | Liste des groupes | 🔄 Moyen |

#### 👥 Groups (groups/)
| Fichier | Description | Priorité |
|---------|------------|----------|
| [`Group.kt`](app/src/main/java/com/projet/studyroom/groups/Group.kt) | Modèle groupe | 🔄 Moyen |
| [`GroupsActivity.kt`](app/src/main/java/com/projet/studyroom/groups/GroupsActivity.kt) | Liste/groupes (8KB) | ⭐ Haute |
| [`GroupsAdapter.kt`](app/src/main/java/com/projet/studyroom/groups/GroupsAdapter.kt) | Affichage groupes | 🔄 Moyen |

**Tâches:**
- [ ] Intégrer Firestore pour les messages en temps réel
- [ ] Créer/Delete/Edit groupes
- [ ] Ajouter/supprimer membres d'un groupe
- [ ] Implémenter l'envoi de messages
- [ ] Afficher les messages en temps réel

---

### **Membre 3: Meetings + Calendar**

**Responsable:** Planification et Gestion des Réunions

#### 📅 Meetings (meetings/)
| Fichier | Description | Priorité |
|---------|------------|----------|
| [`MeetingActivity.kt`](app/src/main/java/com/projet/studyroom/meetings/MeetingActivity.kt) | (7KB) | ⭐ Haute |
| [`MeetingDetailActivity.kt`](app/src/main/java/com/projet/studyroom/meetings/MeetingDetailActivity.kt) | ⚠️ TODO à implémenter | 🔴 Urgent |
| [`MeetingDetailsActivity.kt`](app/src/main/java/com/projet/studyroom/meetings/MeetingDetailsActivity.kt) | (2KB) | 🔄 Moyen |
| [`MeetingCalendarActivity.kt`](app/src/main/java/com/projet/studyroom/meetings/MeetingCalendarActivity.kt) | Vue calendrier | ⭐ Haute |
| [`PlanifierMeetingActivity.kt`](app/src/main/java/com/projet/studyroom/meetings/PlanifierMeetingActivity.kt) | Créer réunion | ⭐ Haute |
| [`CalendarMeeting.kt`](app/src/main/java/com/projet/studyroom/meetings/CalendarMeeting.kt) | Modèle réunion | 🔄 Moyen |
| [`CalendarMeetingActivity.kt`](app/src/main/java/com/projet/studyroom/meetings/CalendarMeetingActivity.kt) | (7KB) | ⭐ Haute |
| [`CalendarMeetingAdapter.kt`](app/src/main/java/com/projet/studyroom/meetings/CalendarMeetingAdapter.kt) | Affichage réunion | 🔄 Moyen |

#### ⚠️ Tâches URGENTES dans [`MeetingDetailActivity.kt`](app/src/main/java/com/projet/studyroom/meetings/MeetingDetailActivity.kt:23)
```kotlin
// TODO: Implement join meeting logic     ← À FAIRE
// TODO: Implement add to calendar logic  ← À FAIRE
```

**Tâches:**
- [ ] Implémenter "Rejoindre réunion" (btnJoinMeeting)
- [ ] Implémenter "Ajouter au calendrier" (btnAddToCalendar)
- [ ] Créer réunion avec date/heure/lieu
- [ ] Afficher les réunions à venir
- [ ] Envoyer notifications de rappel

---

### **Membre 4: Search + Notifications + Home**

**Responsable:** Navigation, Recherche et Notifications

#### 🔍 Search (search/)
| Fichier | Description | Priorité |
|---------|------------|----------|
| [`SearchActivity.kt`](app/src/main/java/com/projet/studyroom/search/SearchActivity.kt) | (5KB) | ⭐ Haute |
| [`SearchResult.kt`](app/src/main/java/com/projet/studyroom/search/SearchResult.kt) | Modèle résultat | 🔄 Moyen |
| [`SearchResultAdapter.kt`](app/src/main/java/com/projet/studyroom/search/SearchResultAdapter.kt) | Affichage résultats | 🔄 Moyen |

#### 🔔 Notifications (notifications/)
| Fichier | Description | Priorité |
|---------|------------|----------|
| [`Notification.kt`](app/src/main/java/com/projet/studyroom/notifications/Notification.kt) | Modèle notification | 🔄 Moyen |
| [`NotificationActivity.kt`](app/src/main/java/com/projet/studyroom/notifications/NotificationActivity.kt) | Liste notifications | ⭐ Haute |
| [`NotificationAdapter.kt`](app/src/main/java/com/projet/studyroom/notifications/NotificationAdapter.kt) | Affichage notification | 🔄 Moyen |

#### 🏠 Home (home/)
| Fichier | Description | Priorité |
|---------|------------|----------|
| [`HomeActivity.kt`](app/src/main/java/com/projet/studyroom/home/HomeActivity.kt) | Dashboard (9KB) | ⭐ Haute |
| [`MainActivity.kt`](app/src/main/java/com/projet/studyroom/home/MainActivity.kt) | Navigation principale | 🔄 Moyen |
| [`Appearance.kt`](app/src/main/java/com/projet/studyroom/home/Appearance.kt) | Thème/apparence | 🔄 Moyen |

**Tâches:**
- [ ] Implémenter recherche dans Firestore (groupes, utilisateurs)
- [ ] Afficher résultats de recherche en temps réel
- [ ] Créer système de notifications push
- [ ] Dashboard avec statistiques
- [ ] Navigation entre les écrans

---

## 📊 Tableau Récapitulatif

| Membre | Modules | Nombre fichiers |
|--------|---------|-----------------|
| **Membre 1** | auth/ + profile/ | 9 fichiers |
| **Membre 2** | chat/ + groups/ | 8 fichiers |
| **Membre 3** | meetings/ | 8 fichiers |
| **Membre 4** | search/ + notifications/ + home/ | 9 fichiers |

---

## 🚀 Instructions pour chaque membre

1. **Cloner le repo:**
   ```bash
   git clone https://github.com/AkeskousKhadija/StudyRoom.git
   ```

2. **Créer une branche:**
   ```bash
   git checkout -b feature/nom-du-membre
   ```

3. **Travailler sur les fichiers assignés**

4. **Tester avec Firebase:**
   - Chaque membre doit ajouter son `google-services.json` dans `app/`

5. **Commiter et pusher:**
   ```bash
   git add .
   git commit -m "feat: description de la tâche"
   git push origin feature/nom-du-membre
   ```

6. **Créer une Pull Request** pour merger dans `main`

---

## ⚠️ Points à внимание

1. **Fichiers exclus:** `hajar/` et `mariam/` ne sont pas dans le repo GitHub
2. **Firebase:** Chaque développeur doit avoir son propre `google-services.json`
3. **Communication:** Utilisez le chat de l'équipe pour coordonner
4. **Merge conflicts:** Synchronisez régulièrement avec `main`