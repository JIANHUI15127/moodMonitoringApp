package com.example.moodmonitoringapp.fragments.moodCheckIn

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.room.Database
import com.example.moodmonitoringapp.R
import com.example.moodmonitoringapp.data.*
import com.example.moodmonitoringapp.fragments.loginSignUp.HistoryCheckInFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class CheckInRatingFragment : DialogFragment() {
    lateinit var currentMood: Mood
    lateinit var dismissListener: Updatable
    var pastimeEntries = ArrayList<String>()
    var weather = Weather.UNKNOWN
    lateinit var database: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    var veryHappy = 0
    var happy = 0
    var normal = 0
    var sad = 0
    var verySad = 0

    //val mood = EmojiData(veryHappy, happy, normal, sad, verySad)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_check_in_rating, container, false)
    }

    override fun onResume() {
        super.onResume()

        assembleMoodButtons()
        val checkBoxList = assemblePastimeCheckBoxes()

        val notesEditText = view?.findViewById<EditText>(R.id.entry_notes)
        val logMoodButton = view?.findViewById<Button>(R.id.log_mood_button)


        logMoodButton?.setOnClickListener { view ->
            if (!this::currentMood.isInitialized) {
                instructGuestToChooseAMood(view)
            } else {
                submitMoodEntry(checkBoxList, notesEditText)
                replaceFragment(HistoryCheckInFragment())
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (dismissListener != null) {
            dismissListener.onDismissal()
        }
    }

/*    private fun submitPastimeEntry(uniquePastime: EditText) {
        val pastimeDatabaseHelper = MoodEntrySQLiteDBHelper(activity)
        pastimeDatabaseHelper.saveMood(uniquePastime.text.toString().toUpperCase())
    }*/

    private fun submitMoodEntry(
        checkBoxList: ArrayList<CheckBox>,
        notesEditText: EditText?
    ) {
        var moodEntry = MoodEntry(currentMood)
        moodEntry.recentPastimes = ArrayList(
            checkBoxList
                .filter { checkBox -> checkBox.isChecked }
                .map { checkBox -> checkBox.text.toString() })
        moodEntry.notes = notesEditText?.text.toString()
        moodEntry.weather = weather
        Log.i("SUBMITTED MOOD ENTRY", moodEntry.toString())

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Users")

        val userUId = FirebaseAuth.getInstance().currentUser!!.uid

        //val userUId = "eEnewVtfJXfmjAMvkr5ESfJzjUo2"


        FirebaseDatabase.getInstance().getReference("Check-In").child(userUId)
            .get().addOnSuccessListener {
                val number = it.child("checkIn").value.toString().toInt()

                FirebaseDatabase.getInstance().getReference("Check-In")
                    .child(userUId).child("checkIn").setValue(number + 1)

            }

        database = FirebaseDatabase.getInstance().getReference("Stats")
        database.child(userUId).child("TotalMoods")


        database.child(userUId).child("mood").setValue(moodEntry.mood).addOnSuccessListener {

        }.addOnFailureListener {

        }



        database.child(userUId).child("TotalMoods").get().addOnSuccessListener {
            val verySad = it.child("verySad").value.toString().toInt()
            val sad = it.child("sad").value.toString().toInt()
            val normal = it.child("normal").value.toString().toInt()
            val happy = it.child("happy").value.toString().toInt()
            val veryHappy = it.child("veryHappy").value.toString().toInt()

            if (moodEntry.mood == Mood.OneStar) {
                database.child(userUId).child("TotalMoods").child("verySad").setValue(verySad + 1)
            }
            if (moodEntry.mood == Mood.TwoStar) {
                database.child(userUId).child("TotalMoods").child("sad").setValue(sad + 1)
            }
            if (moodEntry.mood == Mood.ThreeStar) {
                database.child(userUId).child("TotalMoods").child("normal").setValue(normal + 1)
            }
            if (moodEntry.mood == Mood.FourStar) {
                database.child(userUId).child("TotalMoods").child("happy").setValue(happy + 1)
            }
            if (moodEntry.mood == Mood.FiveStar) {
                database.child(userUId).child("TotalMoods").child("veryHappy")
                    .setValue(veryHappy + 1)
            }
        }

        val moodDatabaseHelper = MoodEntrySQLiteDBHelper(activity)
        moodDatabaseHelper.saveMood(moodEntry)
    }

    private fun updateCompletedGoalCount(userUId: String, moodCompletedCount: Int) {
        database.child(userUId).child("TotalMoods").child("Upset").setValue(moodCompletedCount + 1)
    }


    private fun fetchPastimeData(): ArrayList<String> {
        val databaseHelper = MoodEntrySQLiteDBHelper(activity)
        val cursor = databaseHelper.listPastimeEntries()

        val fromPastimeColumn =
            cursor.getColumnIndex(MoodEntrySQLiteDBHelper.PASTIME_ENTRY_COLUMN)

        if (cursor.count == 0) {
            Log.i("NO PASTIME ENTRIES", "Fetched data and found none.")
        } else {
            Log.i("PASTIME ENTRIES FETCHED", "Fetched data and found pastime entries.")
            pastimeEntries.clear()

            while (cursor.moveToNext()) {
                val nextPastime = cursor.getString(fromPastimeColumn)
                pastimeEntries.add(nextPastime.toString())
            }
        }
        return pastimeEntries
    }

    private fun instructGuestToChooseAMood(view: View) {
        val formValidationMessage = Snackbar.make(
            view,
            "Please select a mood icon to record your mood!",
            Snackbar.LENGTH_LONG
        )
        formValidationMessage.show()
    }

    private fun informGuestOfMoodEntryCreation(view: View) {
        val confirmationMessage = Snackbar.make(
            view,
            "We've logged your mood!",
            Snackbar.LENGTH_LONG
        )
        confirmationMessage.show()
    }

    private fun assemblePastimeCheckBoxes(): ArrayList<CheckBox> {
        val checkBoxLayout = view?.findViewById<LinearLayout>(R.id.pastimes_checkbox_list)
        val checkBoxList = arrayListOf<CheckBox>()
        val dbPastimes = fetchPastimeData()
        dbPastimes.forEach {
            val pastimeCheckBox = CheckBox(activity)
            pastimeCheckBox.text = it
            checkBoxLayout?.addView(pastimeCheckBox)
            checkBoxList.add(pastimeCheckBox)
        }
        return checkBoxList
    }

    private fun assembleMoodButtons() {

        val ratingBar = view?.findViewById<RatingBar>(R.id.rating_bar)

        /*val oneStar = view?.findViewById<ImageView>(R.id.ic_upset)
        val twoStar = view?.findViewById<ImageView>(R.id.ic_down)
        val threeStar = view?.findViewById<ImageView>(R.id.ic_neutral)
        val fourStar = view?.findViewById<ImageView>(R.id.ic_coping)
        val fiveStar = view?.findViewById<ImageView>(R.id.ic_elated)
        val moodButtonCollection = listOf(oneStar, twoStar, threeStar, fourStar, fiveStar)*/


        ratingBar?.setOnRatingBarChangeListener { rBar, rating, fromUser ->
            when(rBar.rating.toInt()){
                1 -> currentMood = Mood.OneStar
                2 -> currentMood = Mood.TwoStar
                3 -> currentMood = Mood.ThreeStar
                4 -> currentMood = Mood.FourStar
                5 -> currentMood = Mood.FiveStar
            }





        }

    }

    private fun replaceFragment(fragment: Fragment){
        if(fragment!=null ){

            val fragmentTransaction  = this.parentFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container,fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }
}