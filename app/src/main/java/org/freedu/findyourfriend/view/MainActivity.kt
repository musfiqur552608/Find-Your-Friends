package org.freedu.findyourfriend.view

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import org.freedu.findyourfriend.R
import org.freedu.findyourfriend.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    lateinit var actionDrawerToggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navController = findNavController(R.id.fragmentContainerView)
        binding.bottomBar.setupWithNavController(navController)
        binding.drawerNav.setupWithNavController(navController)

        actionDrawerToggle = ActionBarDrawerToggle(
            this, binding.drawerlayout,
            R.string.nav_open,
            R.string.nav_close
        )
        actionDrawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.drawerNav.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.logout -> {
                    Firebase.auth.signOut()
                    startActivity(
                        Intent(this, LoginActivity::class.java)
                    )
                    finish()
                }
                R.id.profileFragment -> {
                    navController.navigate(R.id.profileFragment)

                }
                R.id.friendsFragment -> {
                    navController.navigate(R.id.friendsFragment)
                }

            }
            true
        }
        binding.bottomBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.logout -> {
                    Firebase.auth.signOut()
                    startActivity(
                        Intent(this, LoginActivity::class.java)
                    )
                    finish()
                }
                R.id.friendsFragment -> {
                    navController.navigate(R.id.friendsFragment)
                }
                R.id.profileFragment -> {
                    navController.navigate(R.id.profileFragment)
                }

            }
            true
        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }
}