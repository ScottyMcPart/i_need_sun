package com.example.i_need_sun.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.i_need_sun.databinding.ItemSceneBinding
import com.example.i_need_sun.domain.model.SceneConfig
import com.example.i_need_sun.ui.view.ShadowView

class SceneAdapter(
    private val scenes: List<SceneConfig>
) : RecyclerView.Adapter<SceneAdapter.VH>() {

    private var minuteOfDay: Int = 720

    inner class VH(val binding: ItemSceneBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemSceneBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val scene = scenes[position]
        holder.binding.tvSceneName.text = scene.name
        holder.binding.shadowView.scene       = scene
        holder.binding.shadowView.minuteOfDay = minuteOfDay
    }

    override fun getItemCount() = scenes.size

    // Called from MainActivity when the slider moves
    fun updateTime(minutes: Int) {
        minuteOfDay = minutes
        notifyItemRangeChanged(0, scenes.size)
    }
}