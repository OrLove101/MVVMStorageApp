package com.orlove101.android.mvvmstoragetask.ui.cats

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.orlove101.android.mvvmstoragetask.data.models.Cat
import com.orlove101.android.mvvmstoragetask.databinding.ItemCatBinding

class CatsAdapter(private val listener: OnItemClickListener)
    : ListAdapter<Cat, CatsAdapter.CatsViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatsViewHolder {
        val binding = ItemCatBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CatsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CatsViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class CatsViewHolder(private val binding: ItemCatBinding)
        : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    val task = getItem(position)

                    if ( position != RecyclerView.NO_POSITION ) {
                        listener.onItemClick(task)
                    }
                }
            }
        }

        fun bind(item: Cat) {
            binding.apply {
                nameTextView.text = item.name
                ageTextView.text = item.age.toString()
                breedTextView.text = item.breed
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(cat: Cat)
    }

    class DiffCallback: DiffUtil.ItemCallback<Cat>() {
        override fun areItemsTheSame(oldItem: Cat, newItem: Cat): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Cat, newItem: Cat): Boolean =
            oldItem == newItem
    }
}