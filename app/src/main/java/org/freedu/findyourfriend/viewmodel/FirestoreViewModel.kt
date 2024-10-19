package org.freedu.findyourfriend.viewmodel

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import org.freedu.findyourfriend.model.User
import java.io.IOException
import java.util.Locale

class FirestoreViewModel: ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")
    fun saveUser(context: Context, userId: String, displayName: String, email: String, location: String) {
        val user = hashMapOf(
            "displayName" to displayName,
            "email" to email,
            "location" to location
        )

        usersCollection.document(userId).set(user)
            .addOnSuccessListener {
                Toast.makeText(
                     context,
                    "User saved successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context,e.message.toString(), Toast.LENGTH_SHORT).show()
            }
    }
    fun getAllUsers(context: Context, callback: (List<User>) -> Unit) {
        usersCollection.get()
            .addOnSuccessListener {
                val userList = mutableListOf<User>()
                for (document in it) {
                    val userId = document.id
                    val displayName = document.getString("displayName") ?: ""
                    val email = document.getString("email") ?: ""
                    val location = document.getString("location") ?: ""
                    userList.add(User(userId, displayName, email, location))
                }
                callback(userList)
            }
            .addOnFailureListener { e ->
                Toast.makeText(context,e.message.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    fun updateUser(context: Context, userId: String, displayName: String, location: String) {
        val user = hashMapOf(
            "displayName" to displayName,
            "location" to location
        )
        // Convert HashMap to Map
        val userMap = user.toMap()
        usersCollection.document(userId).update(userMap)
            .addOnSuccessListener {
                Toast.makeText(
                    context,
                    "User update successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context,e.message.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    fun updateUserLocation(context: Context, userId: String, location: String) {
        if (userId.isEmpty()) {
            // Handle the case where userId is empty or null
            return
        }
        val user = hashMapOf(
            "location" to location
        )
        val userMap = user.toMap()
        usersCollection.document(userId).update(userMap)
            .addOnSuccessListener {
                Toast.makeText(
                    context,
                    "User update successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context,e.message.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    fun getUser(context: Context, userId: String, callback: (User?) -> Unit) {
        usersCollection.document(userId).get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                callback(user)
            }
            .addOnFailureListener { e ->
                Toast.makeText(context,e.message.toString(), Toast.LENGTH_SHORT).show()
                callback(null)
            }
    }

    fun getUserLocation(context: Context, userId: String, callback: (String) -> Unit) {
        usersCollection.document(userId).get()
            .addOnSuccessListener {
                val location = it.getString("location") ?: ""
                //val latLngSplit = location.split(", ")
                //val latitude = latLngSplit[0].substringAfter("Lat: ").toDouble()
                //val longitude = latLngSplit[1].substringAfter("Long: ").toDouble()
                //val locationName = getLocationName(context,latitude, longitude)
                callback(location)
            }
            .addOnFailureListener { e ->
                Toast.makeText(context,e.message.toString(), Toast.LENGTH_SHORT).show()
                callback("")
            }
    }


    private fun getLocationName(context: Context, latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        var locationName = "Unknown Location"
        try {
            val addressList: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addressList.isNullOrEmpty()) {
                val address: Address = addressList[0]
                locationName = address.getAddressLine(0)  // Get the complete address as a single string
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return locationName
    }


}