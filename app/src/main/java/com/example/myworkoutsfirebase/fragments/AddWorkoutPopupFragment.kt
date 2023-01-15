package com.example.myworkoutsfirebase.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.myworkoutsfirebase.databinding.FragmentAddWorkoutPopupBinding
import com.example.myworkoutsfirebase.utilities.WorkoutData
import com.google.android.material.textfield.TextInputEditText

class AddWorkoutPopupFragment : DialogFragment() {

    /*
    Intitialize binding and listener for the next/save button
     */
    private lateinit var binding: FragmentAddWorkoutPopupBinding
    private lateinit var listener: DialogNextBtnClickListener
    private var workoutData: WorkoutData? = null

    /*
    Make the listener usable in the home fragment
     */
    fun setListener(listener: HomeFragment) {
        this.listener = listener
    }

    companion object {
        const val TAG = "AddTodoPopupFragment"

        @JvmStatic
        fun newInstance(workoutId: String, workout: String) =
            AddWorkoutPopupFragment().apply {
                arguments = Bundle().apply {
                    putString("workoutId", workoutId)
                    putString("workout", workout)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddWorkoutPopupBinding
            .inflate(inflater, container, false)
        Log.d("Fragments", "AddWorkoutPopupFragment Created/re-created")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Displays the workouts that have been previously added.
        displayCurrentWorkout()
        // When user presses button to add the next workout.
        binding.workoutNextBtn.setOnClickListener { registerEvents() }
    }

    /*
    When register events is called, waits for Next button to be clicked
    if teh workout is empty will throw an error message to the user and
    if not will save the workout
     */
    private fun registerEvents() {
        val workoutEntry = binding.workoutEt.text.toString()
        if (workoutEntry.isNotEmpty()) {
            if (workoutData == null) {
                listener.onSaveWorkout(workoutEntry, binding.workoutEt)
            } else {
                workoutData?.workout = workoutEntry
                listener.onUpdateWorkout(workoutData!!, binding.workoutEt)
            }
        } else {
            Toast.makeText(context, "Please enter a workout", Toast.LENGTH_SHORT).show()
        }

        // When the close button is clicked, the card view will be dismissed
        binding.workoutClose.setOnClickListener {
            dismiss()
        }
    }

    /*
    When this function is called, will check to see if there are any workouts already
    stored and then display them.
     */
    private fun displayCurrentWorkout() {
        if (arguments != null) {
            workoutData = WorkoutData(
                arguments?.getString("workoutId").toString(),
                arguments?.getString("workout").toString()
            )
            binding.workoutEt.setText(workoutData?.workout)
        }
    }

    /*
    interface to be used by the HomeFragment to save and update the workout in Firebase
     */
    interface DialogNextBtnClickListener {
        fun onSaveWorkout(workout: String, workoutEt: TextInputEditText)
        fun onUpdateWorkout(workoutData: WorkoutData, workoutEt: TextInputEditText)
    }
}