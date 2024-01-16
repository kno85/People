package com.aca.people

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.aca.people.databinding.FragmentFirstBinding
import com.aca.people.domain.User
import com.aca.people.presentation.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FirstFragment : Fragment(), viewActions {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private val userViewModel : UserViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupOnBackPressedListerner()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }
    private fun setupView() {

        _binding?.rv?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val dividerItemDecoration = DividerItemDecoration(
            _binding?.rv?.context, LinearLayoutManager.VERTICAL
        )
        val userAdapter = UserAdapter(this)

        _binding?.rv?.addItemDecoration(dividerItemDecoration)
        _binding?.rv?.adapter = UserAdapter( this)
        userViewModel.submitUserList()
        userViewModel.users.observe(viewLifecycleOwner) { users ->
            _binding?.rv?.adapter = userAdapter
            userAdapter.addUsers(users)
        }
        userViewModel.errorMessage.observe(viewLifecycleOwner) {
            val errorMessage = it
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
        userViewModel.showExitDialog.observe(viewLifecycleOwner) {
            if (userViewModel.showExitDialog.value == true) {
                showExitDialog()
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(item: User) {
        val bundle = Bundle()
        bundle.putString("name", item.name?.first)
        bundle.putString("email", item.email)

        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundle)
    }
    private fun showExitDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.atencion)
            .setMessage(R.string.accion_salir)
            .setNegativeButton(R.string.no) { dialog, which ->
                userViewModel.userPressBackButton()

            }
            .setPositiveButton(R.string.si) { dialog, which ->
                requireActivity().finish()
            }
            .setCancelable(false)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }
    private fun setupOnBackPressedListerner() {

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    userViewModel.userPressBackButton()
                }
            }
        )
    }
}