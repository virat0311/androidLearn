package com.example.pandeyprojectandroid

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

//activiy act as intent
class ReviewOrderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_order)
        title = "Order successfully placed!"

        val orderId = intent.getIntExtra("orderId", 0)
        if (orderId == 0) {
            return finish()
        }

        val database = FirebaseDatabase.getInstance()
        val orderDatabaseRef = database.getReference("orders")

        orderDatabaseRef.child(orderId.toString()).get().addOnSuccessListener { dataSnapshot ->
            val order = dataSnapshot.getValue(OrderDataClass::class.java)


            if (order == null) {
                finish()
                return@addOnSuccessListener
            }

            val orderItemsTextView = findViewById<TextView>(R.id.orderItemsTextView)

            val menuItemsDatabaseRef = database.getReference("menu-items")
            order.items.forEach { itemId ->
                menuItemsDatabaseRef.child(itemId.toString()).get()
                    .addOnSuccessListener { dataSnapshot ->
                        val menuItem = dataSnapshot.getValue(MenuItemDataClass::class.java)
                        if (menuItem != null) {
                            orderItemsTextView.text = "${orderItemsTextView.text}" +
                                    "${menuItem.name} ($${menuItem.price})\n"
                        }
                    }
            }
            // refere
            val nameTextView = findViewById<TextView>(R.id.nameTextView)
            val contactTextView = findViewById<TextView>(R.id.contactTextView)
            val totalPriceTextView = findViewById<TextView>(R.id.totalPriceTextView)

            nameTextView.text = order.name
            contactTextView.text = order.contact
            totalPriceTextView.text = "$${order.totalPrice}"
        }
    }
}