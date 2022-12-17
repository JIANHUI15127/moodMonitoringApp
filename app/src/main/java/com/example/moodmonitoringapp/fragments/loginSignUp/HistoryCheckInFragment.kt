package com.example.moodmonitoringapp.fragments.loginSignUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.moodmonitoringapp.R
import com.example.moodmonitoringapp.data.MoodEntry
import com.example.moodmonitoringapp.data.MoodEntryAdapter
import com.example.moodmonitoringapp.data.MoodEntrySQLiteDBHelper
import com.google.firebase.auth.FirebaseAuth


class HistoryCheckInFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var recyclerViewAdapter: MoodEntryAdapter

    lateinit var databaseHelper: MoodEntrySQLiteDBHelper
    lateinit var moodEntries: ArrayList<MoodEntry>

    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_history_check_in, container, false)
        return root
    }

    override fun onResume() {
        super.onResume()


        var textview = view?.findViewById<Button>(R.id.tv_checkIn)


        // Because of the !! non-null assertion, the app is going to crash if it can't find this id.
        // I am OK with this because if that happens, the developer will catch that when they run
        // the app to look at the list (so the likelihood that this would go uncaught is very low).
        recyclerView = view?.findViewById(R.id.recyclerview)!!

        //textview?.setText(recyclerView.getAdapter()?.getItemCount()!!)

        databaseHelper = MoodEntrySQLiteDBHelper(activity)

        this.moodEntries = databaseHelper.fetchMoodData()

        recyclerViewAdapter = MoodEntryAdapter(activity?.applicationContext!!, this.moodEntries)

        recyclerView.adapter = recyclerViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity?.applicationContext!!)

        swipeRefreshLayout = view?.findViewById(R.id.swipe_refresh_layout)!!
        swipeRefreshLayout.setOnRefreshListener {

            this.moodEntries.clear()

            this.moodEntries.addAll(databaseHelper.fetchMoodData())

            recyclerViewAdapter.notifyDataSetChanged()
            swipeRefreshLayout.isRefreshing = false
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(sectionNumber: Int): HistoryCheckInFragment {
            return HistoryCheckInFragment()
        }
    }
}