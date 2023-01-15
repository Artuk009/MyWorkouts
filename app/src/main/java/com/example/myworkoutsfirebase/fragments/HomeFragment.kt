package com.example.myworkoutsfirebase.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myworkoutsfirebase.databinding.FragmentHomeBinding
import com.example.myworkoutsfirebase.utilities.WorkoutAdapter
import com.example.myworkoutsfirebase.utilities.WorkoutData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeFragment : Fragment(), AddWorkoutPopupFragment.DialogNextBtnClickListener,
    WorkoutAdapter.WorkoutAdapterClickInterface {

    /*
    Declare the variables to be used in the HomeFragment
     */
    private lateinit var mAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var navController: NavController
    private lateinit var binding: FragmentHomeBinding
    private var popUpFragment: AddWorkoutPopupFragment? = null
    private lateinit var adapter: WorkoutAdapter
    private lateinit var mutableList: MutableList<WorkoutData>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        Log.d("Fragments", "HomeFragment Created")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // call the initializer and registerEvents functions
        init(view)
        getDataFromFirebase()
        registerEvents()

    }

    private fun getDataFromFirebase() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mutableList.clear()
                for (workoutSnapshot in snapshot.children) {
                    val workout = workoutSnapshot.key?.let {
                        WorkoutData(it, workoutSnapshot.value.toString())
                    }

                    if (workout != null) {
                        mutableList.add(workout)
                    }
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    context,
                    error.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    private fun registerEvents() {
        // Prevents multiple instances of the pop up fragment
        if (popUpFragment != null) {
            childFragmentManager.beginTransaction().remove(popUpFragment!!).commit()
        }
        binding.addWorkoutBtn.setOnClickListener {
            popUpFragment = AddWorkoutPopupFragment()
            popUpFragment!!.setListener(this)
            popUpFragment!!.show(
                childFragmentManager,
                AddWorkoutPopupFragment.TAG
            )
        }
    }

    /*
    Initializer for the variables related to Firebase and Navigation
     */
    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        // instance of Firebase Auth
        mAuth = FirebaseAuth.getInstance()
        // instance of the database and with a unique ID
        databaseReference = FirebaseDatabase.getInstance().reference
            .child("Workouts").child(mAuth.currentUser?.uid.toString())

        binding.mainRecyclerView.setHasFixedSize(true)
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(context)
        mutableList = mutableListOf()
        adapter = WorkoutAdapter(mutableList)
        adapter.setListener(this)
        binding.mainRecyclerView.adapter = adapter
    }


    override fun onSaveWorkout(workout: String, workoutEt: TextInputEditText) {
        databaseReference.push().setValue(workout).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(
                    context,
                    "Workout Saved",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    context,
                    it.exception?.message,
                    Toast.LENGTH_SHORT
                ).show()
            }

            workoutEt.text = null
            popUpFragment!!.dismiss()
        }
    }

    override fun onUpdateWorkout(workoutData: WorkoutData, workoutEt: TextInputEditText) {
        val map = HashMap<String, Any>()
        map[workoutData.workoutId] = workoutData.workout
        databaseReference.updateChildren(map).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(
                    context,
                    "Updated Successfully",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    context,
                    it.exception?.message,
                    Toast.LENGTH_SHORT
                ).show()
            }

            workoutEt.text = null
            popUpFragment!!.dismiss()
        }
    }

    override fun onDeleteWorkoutBtnClicked(workoutData: WorkoutData) {
        databaseReference.child(workoutData.workoutId).removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(
                        context,
                        "Deleted Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        it.exception?.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    override fun onEditWorkoutBtnClicked(workoutData: WorkoutData) {
        if (popUpFragment != null) {
            childFragmentManager.beginTransaction().remove(popUpFragment!!).commit()
        }

        popUpFragment =
            AddWorkoutPopupFragment.newInstance(workoutData.workoutId, workoutData.workout)
        popUpFragment!!.setListener(this)
        popUpFragment!!.show(childFragmentManager, AddWorkoutPopupFragment.TAG)
    }
}