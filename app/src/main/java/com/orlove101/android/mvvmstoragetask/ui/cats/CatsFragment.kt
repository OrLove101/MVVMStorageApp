package com.orlove101.android.mvvmstoragetask.ui.cats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orlove101.android.mvvmstoragetask.data.models.Cat
import com.orlove101.android.mvvmstoragetask.databinding.FragmentCatsBinding
import com.orlove101.android.mvvmstoragetask.util.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class CatsFragment: Fragment(), CatsAdapter.OnItemClickListener {
    private var _binding: FragmentCatsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CatsViewModel by viewModels()
    private var catsAdapter: CatsAdapter? = null
    private var searchView: SearchView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerViewSetUp()

        viewModel.cats.observe(viewLifecycleOwner) {
            catsAdapter?.submitList(it)
        }

        catsEventHandler()
    }

    private fun catsEventHandler() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.catsEvent.collect { event ->
                when (event) {
                    CatsViewModel.CatsEvent.NavigateToAddCatScreen -> TODO()
                    is CatsViewModel.CatsEvent.NavigateToEditCatScreen -> TODO()
                    is CatsViewModel.CatsEvent.ShowCatSavedConfirmationMessage -> TODO()
                    is CatsViewModel.CatsEvent.ShowUndoDeleteCatMessage -> TODO()
                }
            }.exhaustive
        }
    }

    private fun recyclerViewSetUp() {
        catsAdapter = CatsAdapter(this)

        binding.apply {
            recyclerView.apply {
                adapter = catsAdapter
                layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL,
                    false
                )
                setHasFixedSize(true)
            }
            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val cat = catsAdapter?.currentList?.get(viewHolder.adapterPosition)

                    viewModel.onCatSwiped(requireNotNull(cat))
                }
            }).attachToRecyclerView(recyclerView)

            setFragmentResultListener("add_edit_request") { _, bundle ->
                val result = bundle.getInt("add_edit_result")

                viewModel.onAddEditResult(result)
            }
        }
    }

    override fun onItemClick(cat: Cat) {
        viewModel.onCatSelected(cat)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}