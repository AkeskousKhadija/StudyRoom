package com.projet.studyroom.search

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.projet.studyroom.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchAdapter: SearchResultAdapter

    // Sample search data
    private val allResults = listOf(
        SearchResult(1, "Math Study Group", "Mathematics study group for high school students", "group"),
        SearchResult(2, "Physics Workshop", "Weekly physics experiments and discussions", "group"),
        SearchResult(3, "Chemistry Lab", "Chemistry practical sessions every Saturday", "meeting"),
        SearchResult(4, "Ahmed", "Member - Math Study Group", "member"),
        SearchResult(5, "Sarah", "Member - Physics Workshop", "member"),
        SearchResult(6, "English Conversation", "English language practice group", "group"),
        SearchResult(7, "Biology Study", "Biology preparation for exams", "group"),
        SearchResult(8, "History Club", "World history discussions", "group")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSearch()
        
        // Show keyboard automatically when activity opens
        binding.etSearch.requestFocus()
        binding.etSearch.postDelayed({
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT)
        }, 100)
        
        // Back button
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun setupSearch() {
        searchAdapter = SearchResultAdapter { result ->
            // Handle item click - navigate to detail or show message
        }
        
        binding.rvSearchResults.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = searchAdapter
        }

        // Set initial empty state
        searchAdapter.submitList(emptyList())
        binding.tvNoResults.visibility = View.VISIBLE
        binding.rvSearchResults.visibility = View.GONE

        // Search text listener
        binding.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                filterResults(s?.toString() ?: "")
            }
        })

        // Handle search action (Enter key)
        binding.etSearch.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                performSearch(binding.etSearch.text.toString())
                true
            } else {
                false
            }
        }
    }

    private fun filterResults(query: String) {
        if (query.isEmpty()) {
            searchAdapter.submitList(emptyList())
            binding.tvNoResults.visibility = View.VISIBLE
            binding.rvSearchResults.visibility = View.GONE
        } else {
            val filtered = allResults.filter { 
                it.title.contains(query, ignoreCase = true) || 
                it.description.contains(query, ignoreCase = true)
            }
            searchAdapter.submitList(filtered)
            
            if (filtered.isEmpty()) {
                binding.tvNoResults.visibility = View.VISIBLE
                binding.rvSearchResults.visibility = View.GONE
            } else {
                binding.tvNoResults.visibility = View.GONE
                binding.rvSearchResults.visibility = View.VISIBLE
            }
        }
    }

    private fun performSearch(query: String) {
        if (query.isNotEmpty()) {
            val results = allResults.filter { 
                it.title.contains(query, ignoreCase = true) || 
                it.description.contains(query, ignoreCase = true)
            }
            searchAdapter.submitList(results)
            
            if (results.isEmpty()) {
                binding.tvNoResults.visibility = View.VISIBLE
                binding.rvSearchResults.visibility = View.GONE
            } else {
                binding.tvNoResults.visibility = View.GONE
                binding.rvSearchResults.visibility = View.VISIBLE
            }
        }
    }
}
