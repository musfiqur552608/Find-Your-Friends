package org.freedu.findyourfriend.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserProfileViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storageRef = FirebaseStorage.getInstance().reference

    private val _profileImageUrl = MutableLiveData<String>()
    val profileImageUrl: LiveData<String> get() = _profileImageUrl

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _isUploading = MutableLiveData<Boolean>()
    val isUploading: LiveData<Boolean> get() = _isUploading

    // Function to upload image to Firebase Storage and save the download URL to Firestore
    fun uploadProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            _isUploading.value = true
            try {
                val userId = firebaseAuth.currentUser?.uid ?: throw Exception("User not logged in")
                val imageRef = storageRef.child("profile_images/$userId.jpg")

                // Upload image to Firebase Storage
                imageRef.putFile(imageUri).await()
                val downloadUrl = imageRef.downloadUrl.await()

                // Save the download URL to Firestore
                firestore.collection("users").document(userId).update("profileImageUrl", downloadUrl.toString()).await()

                // Update the LiveData to notify observers
                _profileImageUrl.value = downloadUrl.toString()

            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
            _isUploading.value = false
        }
    }

    // Function to retrieve profile image URL from Firestore
    fun fetchProfileImageUrl() {
        viewModelScope.launch {
            try {
                val userId = firebaseAuth.currentUser?.uid ?: throw Exception("User not logged in")
                val userDocument = firestore.collection("users").document(userId).get().await()

                if (userDocument.exists()) {
                    val imageUrl = userDocument.getString("profileImageUrl") ?: ""
                    _profileImageUrl.value = imageUrl
                } else {
                    throw Exception("User document does not exist")
                }

            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
}