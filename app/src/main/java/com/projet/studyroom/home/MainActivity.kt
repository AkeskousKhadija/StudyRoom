package com.projet.studyroom.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Cette activité n'est plus utilisée
        finish()  // Se ferme immédiatement
    }
}
