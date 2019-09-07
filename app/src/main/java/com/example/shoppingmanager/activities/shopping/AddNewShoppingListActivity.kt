package com.example.shoppingmanager.activities.shopping

import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.telephony.SmsManager
import android.view.Menu
import android.view.MenuItem
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
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AddNewShoppingListActivity : AppCompatActivity(), ProductAdapter.ItemClicked, ProductAdapter.ItemLongClicked {

    private var productsList: ArrayList<Product> = ArrayList()
    private var productAdapter: RecyclerView.Adapter<ProductAdapter.ViewHolder> ?= null
    private lateinit var layoutManager: RecyclerView.LayoutManager

    private var numberOfShoppingLists: Int = 0
    private val requestSendSms: Int = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_shopping_list)

        supportActionBar?.title = "Nowa lista zakupów:"

        numberOfShoppingLists = intent.getIntExtra("NumberOfShoppingLists", 0)
        numberOfShoppingLists *= (-1)

        layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        productsList_RecyclerView.setHasFixedSize(true)
        productsList_RecyclerView.layoutManager = layoutManager

        addProduct_Button.setOnClickListener {
            val productText = this.product_EditText.text.toString()

            if (productText == "") {
                Toast.makeText(this, "Pole nie może być puste.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val product = Product(productText)
                productsList.add(product)

                productsList.sortBy{
                    it.productName
                }

                productAdapter = ProductAdapter(this, productsList)
                productsList_RecyclerView.adapter = productAdapter

                this.product_EditText.setText("")
            }
        }

        saveShoppingList_Button.setOnClickListener {
            if (productsList.size == 0) {
                Toast.makeText(this, "Lista zakupów musi zawierać conajmniej 1 produkt.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val uid = FirebaseAuth.getInstance().uid
                val id = UUID.randomUUID().toString()
                val products = HashMap<String, Boolean>()

                productsList.sortBy{
                    it.productName
                }

                productsList.forEach {
                    products[it.productName] = false
                }

                val shoppingList = ShoppingList(id, products, Date(), numberOfShoppingLists)
                val ref = FirebaseDatabase.getInstance().getReference("/shopping-lists/$uid/$id")

                ref.setValue(shoppingList)
                trySendSmsNotification()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_shopping_list, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menuInfo -> {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Informacja")
                    .setMessage("Aby usunąć już dodany produkt: kliknij i przytrzymaj.")
                    .setPositiveButton("OK") { _, _ -> }
                    .show()
            }
        }

        return super.onOptionsItemSelected(item)
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
            ShoppingListsActivity.shoppingListsAdapter!!.notifyDataSetChanged()

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
        val text = "Została dodana nowa lista zakupów w aplikacji 'Shopping Manager'."

        SmsManager.getDefault().sendTextMessage(phoneNumber, null, text, null, null)
        Toast.makeText(this, "Powiadomienie sms zostało wysłane.", Toast.LENGTH_SHORT)
            .show()

        finish()
        val intent = Intent(this, ShoppingListsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onItemClicked(index: Int) {
        val productName = productsList[index].productName
        product_EditText.setText(productName)

        productsList.removeAt(index)

        productsList.sortBy{
            it.productName
        }

        productAdapter = ProductAdapter(this, productsList)
        productsList_RecyclerView.adapter = productAdapter
    }

    override fun onItemLongClicked(index: Int) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("UWAGA!")
            .setMessage("Czy na pewno chcesz się usunąć ten produkt z listy zakupów?")
            .setPositiveButton("TAK") { _, _ ->
                productsList.removeAt(index)

                productsList.sortBy{
                    it.productName
                }

                productAdapter = ProductAdapter(this, productsList)
                productsList_RecyclerView.adapter = productAdapter
            }
            .setNegativeButton("NIE") {_, _ -> }
            .show()
    }
}
