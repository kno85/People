package com.aca.people

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.aca.people.data.UserRepositoryImpl
import com.aca.people.databinding.FragmentFirstBinding
import com.aca.people.domain.User
import com.aca.people.domain.UserUseCase
import com.aca.people.network.apiService
import com.aca.people.presentation.UserViewModel


class FirstFragment : Fragment(), viewActions {

    private var _binding: FragmentFirstBinding? = null


    private val binding get() = _binding!!
    private lateinit var userViewModel: UserViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = UserViewModel(UserUseCase(UserRepositoryImpl(apiService)))
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
        userViewModel.users.observe(viewLifecycleOwner, Observer { users ->
            _binding?.rv?.adapter = userAdapter
            userAdapter.addUsers(users)
        })
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
}