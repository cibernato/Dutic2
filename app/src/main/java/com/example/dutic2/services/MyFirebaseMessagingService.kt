package com.example.dutic2.services

import android.app.NotificationManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.dutic2.R
import com.example.dutic2.utils.sendNotification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        remoteMessage.notification!!.let {
            notificationManager.sendNotification(it.body!!,"2",applicationContext)
        }
    }

    override fun onNewToken(p0: String) {
        guardarEnSharedPreferences(p0)
        super.onNewToken(p0)
    }

    private fun guardarEnSharedPreferences(p0: String) {
        val user =FirebaseAuth.getInstance().currentUser

        FirebaseFirestore.getInstance().document("/usuarios/${user?.uid}")
            .set(hashMapOf("notificationToken" to p0), SetOptions.merge())
            .addOnCompleteListener {
                Log.e("OnNewToken","Token guardado exitosamente $p0")
            }.addOnFailureListener {
                Log.e("OnNewToken","Error $it")

            }

    }
}