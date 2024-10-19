package org.freedu.findyourfriend

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import org.freedu.findyourfriend.databinding.FragmentProfile2Binding
import org.freedu.findyourfriend.view.LoginActivity
import org.freedu.findyourfriend.view.MainActivity
import org.freedu.findyourfriend.viewmodel.AuthenticationViewModel
import org.freedu.findyourfriend.viewmodel.FirestoreViewModel
import org.freedu.findyourfriend.viewmodel.LocationViewModel


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfile2Binding
    private lateinit var authViewModel: AuthenticationViewModel
    private lateinit var firestoreViewModel: FirestoreViewModel
    private lateinit var locationViewModel: LocationViewModel
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfile2Binding.inflate(inflater, container, false)
        authViewModel = ViewModelProvider(this).get(AuthenticationViewModel::class.java)
        firestoreViewModel = ViewModelProvider(this).get(FirestoreViewModel::class.java)
        locationViewModel = ViewModelProvider(this).get(LocationViewModel::class.java)

        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }
        binding.homeBtn.setOnClickListener {
            startActivity(Intent(requireContext(), MainActivity::class.java))
        }
        loadUserInfo()

        binding.updateBtn.setOnClickListener {
            val newName = binding.nameEt.text.toString()
            val newLocation = binding.locationEt.text.toString()

            updateProfile(newName, newLocation)
        }

        return binding.root
    }

    private fun updateProfile(newName: String, newLocation: String) {
        val currentUser = authViewModel.getCurrentUser()
        if (currentUser != null) {
            val userId = currentUser.uid
            firestoreViewModel.updateUser(requireContext(),userId, newName, newLocation)
            Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
            startActivity(Intent(requireContext(), MainActivity::class.java))
        } else {
            // Handle the case where the current user is null
            Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUserInfo() {
        val currentUser = authViewModel.getCurrentUser()
        if (currentUser != null) {
            binding.emailEt.setText(currentUser.email)

            firestoreViewModel.getUser(requireContext(),currentUser.uid) {
                if (it != null) {
                    binding.nameEt.setText(it.displayName)

                    firestoreViewModel.getUserLocation(requireContext(),currentUser.uid) {
                        if (it.isNotEmpty()) {
                            binding.locationEt.setText(it)
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()

                }
            }
        } else {
            Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()

        }
    }


}