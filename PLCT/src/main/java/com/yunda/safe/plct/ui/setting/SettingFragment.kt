package com.yunda.safe.plct.ui.setting

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yunda.safe.plct.R
import com.yunda.safe.plct.common.StringConstants
import com.yunda.safe.plct.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {

    companion object {
        fun newInstance() = SettingFragment()
    }

    private val viewModel: SettingViewModel by viewModels()
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(layoutInflater)

        return inflater.inflate(R.layout.fragment_setting, container, false)
    }


    private fun toWebPage(uriStr: String) {
        val navController = findNavController()
        if (navController.currentDestination?.id != R.id.webPageFragment) {
            return
        }

        val uri = Uri.parse(uriStr)
        val bundle = Bundle().apply {
            putParcelable(StringConstants.WEB_URI, uri)
        }

//        navController.navigate(R.id.action_webUri_to_webPage, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}