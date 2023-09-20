package com.example.pandeyprojectandroid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrdersRecyclerViewAdapter(private val orderData: ArrayList<OrderDataClass>) :
    RecyclerView.Adapter<OrdersRecyclerViewAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater =
            LayoutInflater.from(parent.context).inflate(R.layout.order_review_card, parent, false)
        return ViewHolder(layoutInflater)
    }//on create view

    override fun getItemCount(): Int {
        return orderData.size
    }//count

    // bind view on text views holder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data: OrderDataClass = orderData[position]

        holder.orderIdTextView?.text = data.id.toString()
        holder.nameTextView?.text = data.name
        holder.contactTextView?.text = data.contact
        holder.totalPriceTextView?.text = "$${data.totalPrice}"

        holder.setOnItemClickListener { pos ->
            val context = holder.itemView.context
            fetchMenuItems { menuItems ->
                val orderItems = data.items.mapNotNull { menuItems[it] }
                val orderItemsDetails = orderItems.joinToString(separator = "\n") {
                    "${it.name} - $${it.price}"
                }

                val alertDialog = MaterialAlertDialogBuilder(context)
                alertDialog.setTitle("Order Details")
                alertDialog.setMessage("Items in this order:\n$orderItemsDetails")
                alertDialog.setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                alertDialog.setNegativeButton("Delete") { dialog, _ ->
                    val firebaseOrdersDbRef = FirebaseDatabase.getInstance().getReference("orders")
                    firebaseOrdersDbRef.child(data.id.toString()).removeValue()
                    dialog.dismiss()
                }
                alertDialog.show()
            }
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val orderIdTextView = itemView.findViewById<TextView>(R.id.orderIdTextView)
        val nameTextView = itemView.findViewById<TextView>(R.id.nameTextView)
        val contactTextView = itemView.findViewById<TextView>(R.id.contactTextView)
        val totalPriceTextView = itemView.findViewById<TextView>(R.id.totalPriceTextView)

        private var onItemClickListener: ((Int) -> Unit)? = null

        init {
            itemView.setOnClickListener(this)
        }

        fun setOnItemClickListener(listener: (Int) -> Unit) {
            onItemClickListener = listener
        }

        override fun onClick(v: View?) {
            onItemClickListener?.invoke(adapterPosition)
        }
    }

    private fun fetchMenuItems(onMenuItemsFetched: (Map<Int, MenuItemDataClass>) -> Unit) {
        val menuItemsDbRef = FirebaseDatabase.getInstance().getReference("menu-items")
        menuItemsDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val menuItems = mutableMapOf<Int, MenuItemDataClass>()
                for (itemSnapshot in snapshot.children) {
                    val itemId = itemSnapshot.child("id").getValue(Int::class.java) ?: continue
                    val itemName = itemSnapshot.child("name").getValue(String::class.java) ?: ""
                    val itemPrice = itemSnapshot.child("price").getValue(Float::class.java) ?: 0.0f

                    menuItems[itemId] = MenuItemDataClass(itemId, itemName, itemPrice)
                }
                onMenuItemsFetched(menuItems)
            }

            override fun onCancelled(error: DatabaseError) {
                //handle error
            }
        })
    }

}