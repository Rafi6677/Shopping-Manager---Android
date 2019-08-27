package com.example.shoppingmanager.activities.shopping

import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.telephony.SmsManager
import android.widget.Toast
import com.example.shoppingmanager.R
import com.example.shoppingmanager.models.Product
import com.example.shoppingmanager.models.ShoppingList
import com.example.shoppingmanager.viewmodels.ProductAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_add_new_shopping_list.*
import kotlinx.android.synthetic.main.activity_edit_shopping_list.*
import java.util.*
import kotlin.collections.HashMap

class EditShoppingListActivity : AppCompatActivity(), ProductAdapter.ItemLongClicked {

    private var productsList: ArrayList<Product> = ArrayList()
    private var productAdapter: RecyclerView.Adapter<ProductAdapter.ViewHolder> ?= null
    private lateinit var layoutManager: RecyclerView.LayoutManager

    var shoppingList: ShoppingList ?= null
    private var numberOfShoppingLists = 0
    private val requestSendSms: Int = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_shopping_list)

        supportActionBar?.title = "Edycja listy zakupów:"

        shoppingList = intent.getParcelableExtra(
            ShoppingListsActivity.SHOPPING_LIST_KEY
        )

        layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        productsList_EditRecyclerView.setHasFixedSize(true)
        productsList_EditRecyclerView.layoutManager = layoutManager

        numberOfShoppingLists = intent.getIntExtra("NumberOfShoppingLists", 0)
        numberOfShoppingLists *= (-1)

        setupData()

        addProduct_EditButton.setOnClickListener {
            val productText = this.editProduct_EditText.text.toString()

            if (productText == "") {
                Toast.makeText(this, "Pole nie może być puste.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val product = Product(productText)
                productsList.add(product)

                productAdapter = ProductAdapter(this, productsList)
                productsList_EditRecyclerView.adapter = productAdapter

                this.editProduct_EditText.setText("")
            }
        }

        saveShoppingList_EditButton.setOnClickListener {
            if (productsList.size == 0) {
                Toast.makeText(this, "Lista zakupów musi zawierać conajmniej 1 produkt.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val uid = FirebaseAuth.getInstance().uid
                val id = shoppingList!!.id
                val products = HashMap<String, Boolean>()

                productsList.forEach {
                    products[it.productName] = false
                }

                val updatedShoppingList = ShoppingList(id, products, Date(), numberOfShoppingLists)
                val ref = FirebaseDatabase.getInstance().getReference("/shopping-lists/$uid/$id")

                ref.setValue(updatedShoppingList)
                trySendSmsNotification()
            }
        }

        /*updateShoppingList_Button.setOnClickListener {
            if (products_EditText.text.isNotEmpty()) {
                val shoppingListText = products_EditText.text.toString()
                val productsList = shoppingListText.split("\n")
                val uid = FirebaseAuth.getInstance().uid
                val id = shoppingList!!.id
                val products = HashMap<String, Boolean>()

                productsList.forEach {
                    products[it] = false
                }

                val updatedShoppingList = ShoppingList(id, products, Date(), numberOfShoppingLists)
                val ref = FirebaseDatabase.getInstance().getReference("/shopping-lists/$uid/${shoppingList!!.id}")

                ref.setValue(updatedShoppingList)

                trySendSmsNotification()
            } else {
                Toast.makeText(this, "Pole nie może być puste.", Toast.LENGTH_SHORT).show()
            }

            finish()
            val intent = Intent(this, ShoppingListsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }*/
    }

    private fun setupData() {
        for ((key, _) in shoppingList!!.products) {
            productsList.add(Product(key))
        }

        productAdapter = ProductAdapter(this, productsList)
        productsList_EditRecyclerView.adapter = productAdapter
    }

    private fun trySendSmsNotification() {
        val currentUser = ShoppingListsActivity.currentUser
        val phoneNumber = currentUser!!.phoneNumber

        if(phoneNumber.length == 9) {
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.SEND_SMS), requestSendSms)
            } else {
                sendSms(phoneNumber)
            }
        } else {
            finish()
            val intent = Intent(this, ShoppingListsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val currentUser = ShoppingListsActivity.currentUser
        val phoneNumber = currentUser!!.phoneNumber

        if(requestCode == requestSendSms) {
            sendSms(phoneNumber)
        }
        else {
            finish()
            val intent = Intent(this, ShoppingListsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun sendSms(phoneNumber: String) {
        val text = "Lista zakupów w aplikacji 'Shopping Manager' z dnia ${shoppingList!!.date} została zaktualizowana."

        SmsManager.getDefault().sendTextMessage(phoneNumber, null, text, null, null)
        Toast.makeText(this, "Powiadomienie sms zostało wysłane.", Toast.LENGTH_SHORT)
            .show()

        finish()
        val intent = Intent(this, ShoppingListsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onItemLongClicked(index: Int) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("UWAGA!")
            .setMessage("Czy na pewno chcesz się usunąć ten produkt z listy zakupów?")
            .setPositiveButton("TAK") { _, _ ->
                productsList.removeAt(index)
                productAdapter = ProductAdapter(this, productsList)
                productsList_EditRecyclerView.adapter = productAdapter
            }
            .setNegativeButton("NIE") {_, _ -> }
            .show()
    }
}
