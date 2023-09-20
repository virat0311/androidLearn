package com.example.pandeyprojectandroid

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*


class CustomerFragment : Fragment() {
    private val menuItems = ArrayList<MenuItemDataClass>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer, container, false)
    }//end on create

    //reference
    //getting value from firebase
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "Place Order (Customer)"

        val database = FirebaseDatabase.getInstance()
        val databaseReference = database.getReference("menu-items")

        val foodNameArray = ArrayList<String>()
        val foodPriceArray = ArrayList<Float>()

        val itemsList = ArrayList<String>()
        // database reference to take data from database
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val menuItem = dataSnapshot.getValue(MenuItemDataClass::class.java)
                    if (menuItem != null) {
                        menuItems.add(menuItem)
                        foodNameArray.add(menuItem.name)
                        foodPriceArray.add(menuItem.price)
                    }
                }

                for (i in foodNameArray.indices) {
                    val item = "${foodNameArray[i]}\n($${foodPriceArray[i]})"
                    itemsList.add(item)
                }

                val menuListView = view.findViewById<ListView>(R.id.menuItemsListView)
                val menuListViewAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_multiple_choice,
                    android.R.id.text1,
                    itemsList
                )

                menuListViewAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                menuListView.adapter = menuListViewAdapter
                menuListView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

//     reference the confirm button
        val menuConfirmButton = view.findViewById<Button>(R.id.menuConfirmButton)

        menuConfirmButton.setOnClickListener {
            val nameTextView = view.findViewById<EditText>(R.id.nameEditTextView)
            val contactTextView = view.findViewById<EditText>(R.id.phoneEditTextView)
            val menuListView = view.findViewById<ListView>(R.id.menuItemsListView)

            val name = nameTextView.text.toString()
            val contact = contactTextView.text.toString()
            val checkedItemPositions = menuListView.checkedItemPositions
            // set the inputs of customer
            if (name.isEmpty()) {
                nameTextView.error = "Please enter your name"
                return@setOnClickListener
            }

            if (contact.isEmpty()) {
                contactTextView.error = "Please enter your contact number"
                return@setOnClickListener
            }

            if (contact.length != 10) {
                contactTextView.error = "Please enter a valid contact number"
                return@setOnClickListener
            }
            // multiple check option atleas chk one
            if (checkedItemPositions.size() == 0) {
                Snackbar.make(
                    view,
                    "Please select at least one item",
                    Snackbar.LENGTH_LONG
                ).setAction("Dismiss") {}.show()
                return@setOnClickListener
            }

            val selectedItemIds = ArrayList<Int>()
            // total price
            var totalPrice = 0.0f
            for (i in 0 until checkedItemPositions.size()) {
                if (checkedItemPositions.valueAt(i)) {
                    totalPrice += menuItems[checkedItemPositions.keyAt(i)].price
                    selectedItemIds.add(menuItems[checkedItemPositions.keyAt(i)].id)
                }
            }


            val order = OrderDataClass(
                id = Random().nextInt(100000),
                name = name,
                contact = contact,
                items = selectedItemIds,
                totalPrice = totalPrice
            )

            val orderDatabaseRef = database.getReference("orders")
            orderDatabaseRef.child(order.id.toString()).setValue(order)

            val intent = Intent(requireContext(), ReviewOrderActivity::class.java)
            intent.putExtra("orderId", order.id)
            startActivity(intent)
        }


    }


}