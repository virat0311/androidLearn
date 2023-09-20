package com.example.pandeyprojectandroid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class StaffFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_staff, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "View Orders (Staff)"

        val goToHomeButton = view.findViewById<Button>(R.id.goToHomeButton)
        val ordersRecyclerView = view.findViewById<RecyclerView>(R.id.ordersRecyclerView)
        //inflate recycler view
        ordersRecyclerView.layoutManager =
            LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
        //reference go to home button
        goToHomeButton.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_staffFragment_to_homeFragment)
        }

        val firebaseOrdersDbRef = FirebaseDatabase.getInstance().getReference("orders")

        firebaseOrdersDbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = ArrayList<OrderDataClass>()
                for (order in snapshot.children) {
                    val id = order.child("id").getValue(Int::class.java) ?: 0
                    val name = order.child("name").getValue(String::class.java) ?: ""
                    val contact = order.child("contact").getValue(String::class.java) ?: ""
                    val totalPrice = order.child("totalPrice").getValue(Float::class.java) ?: 0.0f

                    val items = mutableListOf<Int>()
                    val itemsSnapshot = order.child("items")
                    for (item in itemsSnapshot.children) {
                        val itemValue = item.getValue(Int::class.java)
                        if (itemValue != null) {
                            items.add(itemValue)
                        }
                    }

                    orders.add(
                        OrderDataClass(
                            id,
                            name,
                            contact,
                            items,
                            totalPrice,
                        )
                    )
                }

                if (orders.isEmpty()) {
                    val emptyListTextView = view.findViewById<TextView>(R.id.emptyListTextView)
                    emptyListTextView.visibility = View.VISIBLE
                    ordersRecyclerView.visibility = View.GONE
                } else {
                    val ordersRecyclerViewAdapter = OrdersRecyclerViewAdapter(orders)
                    ordersRecyclerView.adapter = ordersRecyclerViewAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //hand error
            }
        })


    }
}