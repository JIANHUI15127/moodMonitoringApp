package com.example.moodmonitoringapp.fragments.loginSignUp

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.moodmonitoringapp.R
import com.example.moodmonitoringapp.databinding.ActivityOtpBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*



class OtpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpBinding
    private lateinit var picker: MaterialTimePicker
    private lateinit var calendar: Calendar
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding = ActivityOtpBinding.inflate(layoutInflater)


        createNotificationChannel()

        binding.selectedTimeBtn.setOnClickListener{
            showTimePicker()
        }

        binding.notificationOn.setOnClickListener {
            setNotification()
        }

        binding.switch1.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                Toast.makeText(this,"Notification On", Toast.LENGTH_SHORT).show()


            }
            else{
                CancelNotification()
                Toast.makeText(this,"Notification On", Toast.LENGTH_SHORT).show()

            }
        }

    }

    private fun CancelNotification() {
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, Notification::class.java)

        pendingIntent = PendingIntent.getBroadcast(this,0,intent,0)



        alarmManager.cancel(pendingIntent)
        Toast.makeText(this,"Notification Cancel", Toast.LENGTH_SHORT).show()


    }

    private fun setNotification() {

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, Notification::class.java)
        intent.action = "MyBroadcastReceiverAction"


        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,pendingIntent
        )

        Toast.makeText(this,"Notification Set Successfully", Toast.LENGTH_SHORT).show()
    }

    private fun createNotificationChannel(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val name: CharSequence = "MOODIEReminderChannel"
            val description = "Channel for Alarm Manager"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("MOODIE", name, importance)
            channel.description = description
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }


    }

    private fun showTimePicker(){

        picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Select Notify Time")
            .build()

        picker.show(supportFragmentManager,"MOODIE")

        picker.addOnPositiveButtonClickListener{

            if(picker.hour > 12){
                binding.selectedTime.text =
                    String.format("%02d",picker.hour - 12) + ":" + String.format(
                        "%02d",
                        picker.minute
                    )+ "PM"
            }else{
                String.format("%02d",picker.hour) + ":" + String.format(
                    "%02d",
                    picker.minute
                )+ "AM"
            }

            calendar = Calendar.getInstance()
            calendar[Calendar.HOUR_OF_DAY] = picker.hour
            calendar[Calendar.MINUTE] = picker.minute
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0
        }

    }
}