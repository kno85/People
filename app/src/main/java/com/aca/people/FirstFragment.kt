package com.aca.people

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.aca.people.databinding.FragmentFirstBinding


class FirstFragment : Fragment(), viewActions {

    private var _binding: FragmentFirstBinding? = null


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        setupView()
        return binding.root

    }

    private fun setupView() {

        _binding?.rv?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val heroList = getMockData()
        val dividerItemDecoration = DividerItemDecoration(
            _binding?.rv?.context, LinearLayoutManager.VERTICAL
        )
        _binding?.rv?.addItemDecoration(dividerItemDecoration)
        _binding?.rv?.adapter = CustomAdapter(heroList, this)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(item: People) {
        val bundle = Bundle()
        bundle.putString("name", item.name)
        bundle.putString("name", item.name)

        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundle)
    }
}