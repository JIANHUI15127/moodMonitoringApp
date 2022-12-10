package com.example.moodmonitoringapp.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moodmonitoringapp.R

class MoodEntryAdapter(context: Context, moodEntries: ArrayList<MoodEntry>) :
    RecyclerView.Adapter<MoodEntryAdapter.MoodEntryViewHolder>() {
    var context: Context
    var moodEntries: ArrayList<MoodEntry>

    init {
        this.context = context
        this.moodEntries = moodEntries
    }

    inner class MoodEntryViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var moodImage: ImageView
        var loggedAtTextView: TextView
        var pastimesTextView: TextView
        var notesTextView: TextView
        //var weatherTextView: TextView

        init {
            moodImage = itemView.findViewById(R.id.entry_mood)
            loggedAtTextView = itemView.findViewById(R.id.entry_date_time)
            pastimesTextView = itemView.findViewById(R.id.entry_pastimes)
            notesTextView = itemView.findViewById(R.id.entry_notes)
            //weatherTextView = itemView.findViewById(R.id.entry_weather)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodEntryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val moodEntryListItem: View = inflater
            .inflate(R.layout.recyclerview_history_template, parent, false)
        return MoodEntryViewHolder(moodEntryListItem)
    }

    override fun getItemCount(): Int {
        return moodEntries.size
    }

    override fun onBindViewHolder(holder: MoodEntryViewHolder, position: Int) {
        holder.moodImage.setBackgroundResource(selectImageFor(moodEntries.get(position).mood))
        holder.loggedAtTextView.text = MoodEntry.getFormattedLogTime(moodEntries.get(position).loggedAt)
        holder.pastimesTextView.text = MoodEntry.formatPastimeForView(moodEntries.get(position).recentPastimes)
        holder.notesTextView.text = MoodEntry.formatNotesForView(moodEntries.get(position).notes)
        //holder.weatherTextView.text = moodEntries.get(position).weather.toString()
    }

    private fun selectImageFor(mood: Mood): Int {
        when (mood) {
            Mood.RED -> return R.drawable.ic_circle_red
            Mood.ORANGE -> return R.drawable.ic_circle_orange
            Mood.YELLOW -> return R.drawable.ic_circle_yellow
            Mood.GREEN -> return R.drawable.ic_circle_green
            Mood.DARKGREEN -> return R.drawable.ic_circle_dark_green
            Mood.UPSET -> return R.drawable.ic_awful
            Mood.DOWN -> return R.drawable.ic_bad
            Mood.NEUTRAL -> return R.drawable.ic_okay
            Mood.COPING -> return R.drawable.ic_good
            Mood.ELATED -> return R.drawable.ic_great
            Mood.OneStar -> return R.drawable.star_rating_1_of_5
            Mood.TwoStar -> return R.drawable.star_rating_2_of_5
            Mood.ThreeStar -> return R.drawable.star_rating_3_of_5
            Mood.FourStar -> return R.drawable.star_rating_4_of_5
            Mood.FiveStar -> return R.drawable.star_rating_5_of_5
        }
    }
}
