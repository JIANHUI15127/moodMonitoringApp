package com.example.moodmonitoringapp.fragments.loginSignUp

import android.app.Activity
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import com.example.moodmonitoringapp.MainActivity

import com.example.moodmonitoringapp.R
import com.example.moodmonitoringapp.data.UserData
import com.example.moodmonitoringapp.databinding.FragmentEditProfileBinding
import com.google.android.gms.tasks.Task
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class EditProfileFragment : Fragment() {
    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var database : DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var picker: MaterialTimePicker
    private lateinit var calendar: Calendar
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent

    private var username=""
    private var email=""
    private var phone = ""
    private var password = ""
    private var imageUrl: Uri? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Users")

        val userUId = FirebaseAuth.getInstance().currentUser!!.uid


        //fetch data
        /*database.child(userUId).child("username").setValue().toString()
        database.child(userUId).child("email").setValue(binding.txtEmail).toString()
        database.child(userUId).child("phoneNumber").setValue(binding.txtPhone).toString()
        database.child(userUId).child("password").setValue(binding.txtPassword).toString()*/

        /*binding.txtName.setText(inputUsername)
        binding.txtEmail.setText(inputEmail)
        binding.txtPassword.setText(inputPassword)
        binding.txtPhone.setText(inputPhoneNumber)*/

        readData(userUId)


        /*binding.notificationOn.setOnClickListener {
            val intent = Intent(this@EditProfileFragment.requireContext(), testing1::class.java)
            startActivity(intent)
        }*/

        binding.userImage.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 1)
            imageResultLauncher.launch(intent)
        }

        binding.switch1.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                Log.d(javaClass.name, "Subscribing to discount-offers topic")

                FirebaseMessaging.getInstance().subscribeToTopic("discount-offers")
                    .addOnCompleteListener { task ->
                        showToast("Notification ON!! You will receive mood check in notifications")
                        if (!task.isSuccessful) {
                            showToast("Failed! Try again.")
                        }
                    }

            }
            else{
                showToast("Notification OFF.")

            }
        }

        binding.btnUpdate.setOnClickListener {

            username = binding.txtName.text.toString().trim()
            email = binding.txtEmail.text.toString().trim()
            phone = binding.txtPhone.text.toString().trim()
            password = binding.txtPassword.text.toString().trim()

            val userID = FirebaseAuth.getInstance().currentUser!!.uid

            val filePathAndName = "userProfile/$userID"

            val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
            storageReference.putFile(imageUrl!!)
                .addOnSuccessListener {taskSnapshot ->
                    val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                    while (!uriTask.isSuccessful);
                    val uploadedPostImageUrl = "${uriTask.result}"
                    updateUser(username, phone,email,password,uploadedPostImageUrl)   //Upload to real time database
                    //uploadCampaignInfoToDb(uploadedCampaignUrl, postID.toString())
                    //Toast.makeText(context,"Successful",Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{ e ->
                    Toast.makeText(context,"Failed to upload ${e.message}",Toast.LENGTH_SHORT).show()
                }


        }

        return binding.root
    }

    //Display selected image
    private val imageResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                Log.d(TAG, "Picture Picked")
                imageUrl = result.data!!.data
                binding.userImage.setImageURI(imageUrl)

            } else {
                Log.d(
                    TAG, "Pick Cancelled"
                )
            }
        }
    )

    private fun CancelNotification() {
        alarmManager = requireContext().getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireActivity(),Notification::class.java)

        pendingIntent = PendingIntent.getBroadcast(requireActivity(),0,intent,0)



        alarmManager.cancel(pendingIntent)
        Toast.makeText(context,"Notification Cancel", Toast.LENGTH_SHORT).show()


    }

    private fun setNotification() {

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context,Notification::class.java)
        intent.action = "MyBroadcastReceiverAction"


        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,pendingIntent
        )

        /*val msUntilTriggerHour: Long = 30000
        val alarmTimeAtUTC: Long = System.currentTimeMillis() + msUntilTriggerHour

// Depending on the version of Android use different function for setting an
// Alarm.
// setAlarmClock() - used for everything lower than Android M
// setExactAndAllowWhileIdle() - used for everything on Android M and higher
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(alarmTimeAtUTC, pendingIntent),
                pendingIntent
            )
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alarmTimeAtUTC,
                pendingIntent
            )
        }*/

        Toast.makeText(context,"Notification Set Successfully", Toast.LENGTH_SHORT).show()
    }

    private fun updateUser(username:String, phone:String, email:String, password:String, imageUrl:String){

        val user = UserData(username, phone, email, password, imageUrl)

        val userUId = FirebaseAuth.getInstance().currentUser!!.uid

        database.child(userUId)
            .setValue(user).addOnSuccessListener {
                binding.txtName.text.clear()
                binding.txtEmail.text.clear()
                binding.txtPhone.text.clear()
                binding.txtPassword.text.clear()
                Toast.makeText(context, "Edit Successfully!", Toast.LENGTH_SHORT).show()

                replaceFragment(UserProfileFragment())   // Need to change replace dashboard fragment

            }.addOnFailureListener{
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
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

    private fun readData(userUId: String){
        database = FirebaseDatabase.getInstance().getReference("Users")
        database.child(userUId).get().addOnSuccessListener {
            val username = it.child("username").value.toString()
            val email = it.child("email").value.toString()
            val password = it.child("password").value.toString()
            val phone = it.child("phoneNumber").value.toString()

            binding.txtName.setText(username)
            binding.txtEmail.setText(email)
            binding.txtPassword.setText(password)
            binding.txtPhone.setText(phone)

        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

    }




}




