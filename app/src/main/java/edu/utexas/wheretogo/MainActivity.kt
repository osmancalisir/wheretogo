package edu.utexas.wheretogo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseApp
import edu.utexas.wheretogo.fragments.LoginFragment
import edu.utexas.wheretogo.fragments.MapFragment
import edu.utexas.wheretogo.services.DataRepository
import edu.utexas.wheretogo.services.FirebaseService

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseService: FirebaseService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)
        firebaseService = FirebaseService()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (firebaseService.getCurrentUser() != null) {
            navigateToFragment(MapFragment())
        } else {
            navigateToFragment(LoginFragment())
        }
    }

    fun updateDataServiceReference(isOptimized: Boolean) {
        DataRepository.dataService.setDataReference(isOptimized)
    }

    fun getMapFragment(): MapFragment? {
        return supportFragmentManager.findFragmentById(R.id.fragmentContainer) as? MapFragment
    }

    private fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun FirebaseService.getCurrentUser() = this.auth.currentUser
}
