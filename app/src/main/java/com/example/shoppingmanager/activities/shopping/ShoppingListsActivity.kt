package com.example.shoppingmanager.activities.shopping

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.shoppingmanager.R
import com.example.shoppingmanager.activities.registerlogin.RegistrationActivity
import com.example.shoppingmanager.activities.settings.SettingsActivity
import com.example.shoppingmanager.models.ShoppingList
import com.example.shoppingmanager.models.User
import com.example.shoppingmanager.viewmodels.ShoppingListAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_shopping_lists.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ShoppingListsActivity : AppCompatActivity(),
    ShoppingListAdapter.ItemClicked,
    ShoppingListAdapter.EditButtonClicked,
    ShoppingListAdapter.ItemLongClicked {

    private lateinit var layoutManager: RecyclerView.LayoutManager
    var shoppingLists: ArrayList<ShoppingList> = ArrayList<ShoppingList>()
    var numberOfShoppingLists = 0

    companion object {
        var currentUser: User? = null
        const val SHOPPING_LIST_KEY = "shoppingListKey"
        var shoppingListsAdapter: RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> ?= null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_lists)
        numberOfShoppingLists = 0

        verifyUserIsLoggedIn()

        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        shoppingLists_RecyclerView.setHasFixedSize(true)
        shoppingLists_RecyclerView.layoutManager = layoutManager

        addNewShoppingList_Button.setOnClickListener {
            val intent = Intent(this, AddNewShoppingListActivity::class.java)
            intent.putExtra("NumberOfShoppingLists", numberOfShoppingLists)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_shopping_lists, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menuSettings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.menuSignOut -> {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("UWAGA!")
                    .setMessage("Czy na pewno chcesz się wylogować?")
                    .setPositiveButton("TAK") { _, _ ->
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(this, RegistrationActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                    .setNegativeButton("NIE") { _, _ ->  }
                    .show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, RegistrationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else {
            fetchCurrentUser()
            showData()
        }
    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    private fun showData() {
        val date = Date()
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        val dateText: String = formatter.format(date)

        val dateInfo = "Dziś jest:\n<b>$dateText</b>"
        currentDate_TextView.text = Html.fromHtml(dateInfo)

        val uid = FirebaseAuth.getInstance().uid

        val userRef = FirebaseDatabase.getInstance().getReference("/users/$uid")

        userRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
                val userInfo = "Zalogowano:\n<b>${currentUser!!.username}</b>"
                currentUser_TextView.text = Html.fromHtml(userInfo)
            }

            override fun onCancelled(p0: DatabaseError) {}
        })

        reloadRecyclerView()
    }

    override fun onItemClicked(index: Int) {
        val intent = Intent(this, ProductsActivity::class.java)
        intent.putExtra(SHOPPING_LIST_KEY, shoppingLists[index])
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    override fun onItemLongClicked(index: Int) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("UWAGA!")
            .setMessage("Czy na pewno chcesz się usunąć tę listę zakupów?")
            .setPositiveButton("TAK") { _, _ ->
                val ref = FirebaseDatabase.getInstance()
                    .getReference("shopping-lists/${currentUser!!.uid}/${shoppingLists[index].id}")
                ref.removeValue()

                reloadRecyclerView()

                Toast.makeText(this, "Usunięto listę zakupów.", Toast.LENGTH_SHORT)
                    .show()
            }
            .setNegativeButton("NIE") { _, _ ->  }
            .show()
    }

    override fun onEditButtonClicked(index: Int) {
        val intent = Intent(this, EditShoppingListActivity::class.java)
        intent.putExtra(SHOPPING_LIST_KEY, shoppingLists[index])
        intent.putExtra("NumberOfShoppingLists", numberOfShoppingLists)
        startActivity(intent)
    }


    private fun reloadRecyclerView() {
        shoppingLists.clear()
        numberOfShoppingLists = 0

        val uid = FirebaseAuth.getInstance().uid

        val shoppingRef = FirebaseDatabase.getInstance().getReference("/shopping-lists/$uid").orderByChild("priority")

        shoppingRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val shoppingList = it.getValue(ShoppingList::class.java)
                    if(shoppingList != null) {
                        shoppingLists.add(shoppingList)
                        numberOfShoppingLists++
                    }
                }

                if(numberOfShoppingLists == 0) {
                    noShoppingListsInfo_TextView.visibility = View.VISIBLE
                } else {
                    noShoppingListsInfo_TextView.visibility = View.INVISIBLE
                }

                shoppingListsAdapter = ShoppingListAdapter(this@ShoppingListsActivity, shoppingLists)
                shoppingLists_RecyclerView.adapter = shoppingListsAdapter
            }

            override fun onCancelled(p0: DatabaseError) { }
        })
    }
}
