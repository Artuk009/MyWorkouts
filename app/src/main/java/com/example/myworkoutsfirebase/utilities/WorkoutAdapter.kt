package com.example.myworkoutsfirebase.utilities

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myworkoutsfirebase.databinding.EachWorkoutItemBinding

class WorkoutAdapter(private val list: MutableList<WorkoutData>):
    RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    private var listener: WorkoutAdapterClickInterface? = null
    fun setListener(listener: WorkoutAdapterClickInterface) {
        this.listener = listener
    }

    inner class WorkoutViewHolder(val binding: EachWorkoutItemBinding):
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val binding = EachWorkoutItemBinding.inflate(LayoutInflater
            .from(parent.context), parent, false)
        return WorkoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.workoutNumber.text = this.workout

                binding.deleteWorkout.setOnClickListener {
                    listener?.onDeleteWorkoutBtnClicked(this)
                }

                binding.editWorkout.setOnClickListener {
                    listener?.onEditWorkoutBtnClicked(this)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface WorkoutAdapterClickInterface {
        fun onDeleteWorkoutBtnClicked(workoutData: WorkoutData)
        fun onEditWorkoutBtnClicked(workoutData: WorkoutData)
    }
}