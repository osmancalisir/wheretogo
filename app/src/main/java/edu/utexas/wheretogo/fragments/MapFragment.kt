package edu.utexas.wheretogo.fragments

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.TileOverlay
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng
import edu.utexas.wheretogo.R
import edu.utexas.wheretogo.databinding.MapBinding
import edu.utexas.wheretogo.services.DataRepository
import edu.utexas.wheretogo.services.FirebaseService
import java.io.IOException
import java.util.Locale

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: MapBinding? = null
    private val binding get() = _binding!!

    private lateinit var map: GoogleMap
    private lateinit var firebaseService: FirebaseService
    private val dataService = DataRepository.dataService
    private lateinit var geocoder: Geocoder
    private var heatmapTileOverlay: TileOverlay? = null
    private var currentMarkers: MutableList<Marker> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = MapBinding.inflate(inflater, container, false)
        firebaseService = FirebaseService()
        geocoder = Geocoder(requireContext(), Locale.getDefault())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        binding.btnSignOut.setOnClickListener {
            firebaseService.signOut()
            navigateToLoginFragment()
        }
        setupSearchBar()
        setupSettingsButton()
    }

    private fun setupSettingsButton() {
        binding.settingsButton.setOnClickListener {
            SettingsFragment().show(parentFragmentManager, "SettingsFragment")
        }
    }

    private fun setupSearchBar() {
        val searchInput = view?.findViewById<EditText>(R.id.searchLocation)
        searchInput?.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = v.text.toString()
                if (query.isNotEmpty()) {
                    searchLocation(query)
                    hideKeyboard()
                }
                true
            } else {
                false
            }
        }
    }

    private fun searchLocation(locationName: String) {
        try {
            val addresses: List<Address> = geocoder.getFromLocationName(locationName, 1)!!
            if (addresses.isNotEmpty()) {
                clearMarkers()
                val address = addresses[0]
                val latLng = LatLng(address.latitude, address.longitude)
                currentMarkers.add(map.addMarker(MarkerOptions().position(latLng).title(locationName))!!)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
            } else {
                Toast.makeText(context, "Location not found", Toast.LENGTH_LONG).show()
            }
        } catch (e: IOException) {
            Toast.makeText(context, "Geocoder service not available", Toast.LENGTH_LONG).show()
            Log.e("MapFragment", "Geocoder failure", e)
        }
    }

    private fun clearMarkers() {
        for (marker in currentMarkers) {
            marker.remove()
        }
        currentMarkers.clear()
    }

    private fun hideKeyboard() {
        val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        setupMap()
        loadDataAndAddHeatMap()
    }

    private fun setupMap() {
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isCompassEnabled = true

        // Setup initial marker in New York for demonstration
        val nyc = LatLng(40.7128, -74.0060)
        currentMarkers.add(map.addMarker(MarkerOptions().position(nyc).title("New York City"))!!)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(nyc, 12f))
    }

    private fun loadDataAndAddHeatMap() {
        val isOptimized = PreferenceManager.getDefaultSharedPreferences(requireContext()).getBoolean("ShowOptimizedData", false)
        dataService.setDataReference(isOptimized)
        dataService.downloadData({ jsonData ->
            val data = parseData(jsonData)
            updateHeatMap(data)
        }, { error ->
            Log.e("MapFragment", "Error downloading heatmap data: ", error)
        })
    }

    private fun updateHeatMap(data: List<List<Double>>) {
        heatmapTileOverlay?.remove()
        val list = ArrayList<WeightedLatLng>()
        for (item in data) {
            val latLng = LatLng(item[0], item[1])
            val weightedLatLng = WeightedLatLng(latLng, item[2])
            list.add(weightedLatLng)
        }
        val provider = HeatmapTileProvider.Builder().weightedData(list).build()
        heatmapTileOverlay = map.addTileOverlay(TileOverlayOptions().tileProvider(provider))
    }

    private fun parseData(jsonData: String): List<List<Double>> {
        val typeToken = object : TypeToken<List<List<Double>>>() {}.type
        return Gson().fromJson(jsonData, typeToken)
    }

    private fun navigateToLoginFragment() {
        parentFragmentManager.beginTransaction().replace(R.id.fragmentContainer, LoginFragment()).commit()
    }

    fun reloadData() {
        loadDataAndAddHeatMap()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
