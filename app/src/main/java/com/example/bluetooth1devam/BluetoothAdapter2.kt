package com.example.bluetooth1devam

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class BluetoothAdapter2(var devices: MutableList<BluetoothDevice>) : RecyclerView.Adapter<BluetoothAdapter2.ViewHolder>() {



    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView
        val textView2: TextView

        init {
            // Define click listener for the ViewHolder's View.
            textView = view.findViewById(R.id.bluetoothName)
            textView2 = view.findViewById(R.id.bluetoothAddress)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row, parent, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val dev = devices[position]

        viewHolder.textView.text = dev.name
        viewHolder.textView2.text = dev.address
        val name = dev.name
        val address = dev.address
        viewHolder.itemView.setOnClickListener {
            val intent = Intent(it.context, BlueDetail::class.java)
            intent.putExtra("name", name)
            //Log.e("code", devices[position].name)
            intent.putExtra("address", address)
            it.context.startActivity(intent)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = devices.size


}