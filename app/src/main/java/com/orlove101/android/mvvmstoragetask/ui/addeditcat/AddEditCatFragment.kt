package com.orlove101.android.mvvmstoragetask.ui.addeditcat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.orlove101.android.mvvmstoragetask.databinding.FragmentAddEditCatBinding
import com.orlove101.android.mvvmstoragetask.util.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddEditCatFragment: Fragment() {
    private var _binding: FragmentAddEditCatBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: AddEditCatViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditCatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            editTextTextCatName.setText(viewModel.catName)
            editTextTextCatAge.setText(viewModel.catAge)
            editTextTextCatBreed.setText(viewModel.catBreed)

            editTextTextCatBreed.addTextChangedListener {
                viewModel.catBreed = it.toString()
            }
            editTextTextCatAge.addTextChangedListener {
                viewModel.catAge = it.toString()
            }
            editTextTextCatName.addTextChangedListener {
                viewModel.catName = it.toString()
            }
            floatingActionButton.setOnClickListener {
                viewModel.onSaveClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.addEditCatEvent.collect { event ->
                when (event) {
                    is AddEditCatViewModel.AddEditCatEvent.NavigateBackWithResult -> {
                        binding.apply {
                            editTextTextCatName.clearFocus()
                            editTextTextCatAge.clearFocus()
                            editTextTextCatBreed.clearFocus()
                        }
                        setFragmentResult(
                            "add_edit_request",
                            bundleOf("add_edit_result" to event.result)
                        )
                        findNavController().popBackStack()
                    }
                    is AddEditCatViewModel.AddEditCatEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                }.exhaustive
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}