package com.orlove101.android.mvvmstoragetask.ui.cats

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.orlove101.android.mvvmstoragetask.R
import com.orlove101.android.mvvmstoragetask.data.models.Cat
import com.orlove101.android.mvvmstoragetask.databinding.FragmentCatsBinding
import com.orlove101.android.mvvmstoragetask.persistence.SortOrder
import com.orlove101.android.mvvmstoragetask.util.exhaustive
import com.orlove101.android.mvvmstoragetask.util.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CatsFragment: Fragment(), CatsAdapter.OnItemClickListener {
    private var _binding: FragmentCatsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CatsViewModel by viewModels()
    private var catsAdapter: CatsAdapter? = null
    private var catsLayoutManager: LinearLayoutManager? = null
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
                    is CatsViewModel.CatsEvent.ShowCatSavedConfirmationMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                    is CatsViewModel.CatsEvent.ShowUndoDeleteCatMessage -> {
                        Snackbar.make(requireView(), "Cat deleted", Snackbar.LENGTH_LONG)
                            .setAction("Undo") {
                                viewModel.onUndoDeleteClick(event.cat)
                            }.show()
                    }
                }
            }.exhaustive
        }
    }

    private fun recyclerViewSetUp() {
        catsAdapter = CatsAdapter(this)
        catsLayoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        binding.apply {
            recyclerView.apply {
                adapter = catsAdapter
                layoutManager = catsLayoutManager

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

            val dividerItemDecoration = DividerItemDecoration(recyclerView.context,
                requireNotNull(catsLayoutManager?.orientation))
            recyclerView.addItemDecoration(dividerItemDecoration)

            setFragmentResultListener("add_edit_request") { _, bundle ->
                val result = bundle.getInt("add_edit_result")

                viewModel.onAddEditResult(result)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_cats, menu)

        val searchItem = menu.findItem(R.id.action_search)

        searchView = searchItem.actionView as SearchView

        val pendingQuery = viewModel.searchQuery.value

        if ( pendingQuery != null && pendingQuery.isNotEmpty() ) {
            searchItem.expandActionView()
            searchView?.setQuery(pendingQuery, false)
        }

        searchView?.onQueryTextChanged {
            viewModel.searchQuery.value = it
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_sort_by_name -> {
                viewModel.onSortOrderSelected(SortOrder.BY_NAME)
                true
            }
            R.id.action_sort_by_age -> {
                viewModel.onSortOrderSelected(SortOrder.BY_AGE)
                true
            }
            R.id.action_sort_by_breed -> {
                viewModel.onSortOrderSelected(SortOrder.BY_BREED)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClick(cat: Cat) {
        viewModel.onCatSelected(cat)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView?.setOnQueryTextListener(null)
        _binding = null
    }
}