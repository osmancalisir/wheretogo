package edu.utexas.wheretogo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import edu.utexas.wheretogo.MainActivity
import edu.utexas.wheretogo.databinding.SettingsBinding

class SettingsFragment : DialogFragment() {

    private var _binding: SettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = SettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSwitch()
    }

    private fun initSwitch() {
        binding.switchShowOptimizedData.apply {
            isChecked = getOptimizedDataSetting()

            setOnCheckedChangeListener { _, isChecked ->
                saveOptimizedDataSetting(isChecked)
                (activity as MainActivity).updateDataServiceReference(isChecked)
                (activity as MainActivity).getMapFragment()?.reloadData()
            }
        }
    }

    private fun getOptimizedDataSetting(): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(requireContext())
            .getBoolean("ShowOptimizedData", false)
    }

    private fun saveOptimizedDataSetting(value: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(requireContext()).edit()
            .putBoolean("ShowOptimizedData", value)
            .apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
