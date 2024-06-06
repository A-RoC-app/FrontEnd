package com.kodonho.aroc

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kodonho.aroc.databinding.ItemMapSearchBinding
import com.kodonho.aroc.dto.MapSearchDto

class MapSearchAdapter(private val items: List<MapSearchDto>) : RecyclerView.Adapter<MapSearchAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemMapSearchBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MapSearchDto) {
            binding.name.text = item.destination
            binding.address.text = item.address
            binding.lat.text = item.lat.toString()
            binding.lon.text = item.lon.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMapSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
