package com.example.groceryapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() , GroceryRVAdapter.GroceryItemClickInterface {

  lateinit var itemsRV : RecyclerView
  lateinit var addFAB : FloatingActionButton
  lateinit var list : List<GroceryItems>
  lateinit var groceryRVAdapter: GroceryRVAdapter
  lateinit var groceryviewModal : GroceryViewModal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

      itemsRV = findViewById(R.id.idRVItems)
      addFAB = findViewById(R.id.idFABAdd)
      list = ArrayList<GroceryItems>()
      groceryRVAdapter = GroceryRVAdapter(list, this)
      itemsRV.layoutManager = LinearLayoutManager(this)
      itemsRV.adapter = groceryRVAdapter
      val groceryRepository = GroceryRepository(GroceryDatabase(this))
      val factory = GroceryViewModalFactory(groceryRepository)
      groceryviewModal = ViewModelProvider(this, factory).get(GroceryViewModal::class.java)
      groceryviewModal.getAllGroceryItems().observe(this, Observer {
        groceryRVAdapter.list = it
        groceryRVAdapter.notifyDataSetChanged()
      })

      addFAB.setOnClickListener{
        openDialog()
      }

    }

    fun openDialog(){
      val dialog = Dialog(this)
      dialog.setContentView(R.layout.grocery_add_dialof)
      val cancelBtn = dialog.findViewById<Button>(R.id.idBtnCancel)
      val addBtn = dialog.findViewById<Button>(R.id.idBtnAdd)
      val itemEdt = dialog.findViewById<EditText>(R.id.idEditItemName)
      val itemPriceEdt = dialog.findViewById<EditText>(R.id.idEditItemPrice)
      val itemQuantityEdt = dialog.findViewById<EditText>(R.id.idEditItemQuantity)
      cancelBtn.setOnClickListener {
        dialog.dismiss()
      }

      addBtn.setOnClickListener {
        val itemName : String = itemEdt.text.toString()
        val itemPrice :String = itemPriceEdt.text.toString()
        val itemQuantity : String = itemQuantityEdt.text.toString()

        if(itemName.isNotEmpty() && itemPrice.isNotEmpty() && itemQuantity.isNotEmpty()){
          val qty : Int = itemQuantity.toInt()
          val pr : Int = itemPrice.toInt()
          val items = GroceryItems(itemName, qty, pr)
          groceryviewModal.insert(items)
          Toast.makeText(applicationContext, "Item Inserted...", Toast.LENGTH_SHORT).show()
          groceryRVAdapter.notifyDataSetChanged()
          dialog.dismiss()
        }else{
          Toast.makeText(applicationContext, "Enter all the data...", Toast.LENGTH_SHORT).show()
        }
      }
      dialog.show()
    }

  override fun onItemClick(groceryItems: GroceryItems) {
      groceryviewModal.delete((groceryItems))
    groceryRVAdapter.notifyDataSetChanged()
    Toast.makeText(applicationContext, "Item Deleted...", Toast.LENGTH_LONG).show()
  }
}
