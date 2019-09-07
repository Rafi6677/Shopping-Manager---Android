package com.example.shoppingmanager.activities.shopping

import android.content.Intent
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.shoppingmanager.R
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.example.shoppingmanager.models.ShoppingList
import com.example.shoppingmanager.viewmodels.ShoppingListProductBoughtItem
import com.example.shoppingmanager.viewmodels.ShoppingListProductToBuyItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_products.*
import kotlinx.android.synthetic.main.fragment_products.view.*
import java.text.SimpleDateFormat

class ProductsActivity : AppCompatActivity() {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    companion object {
        var shoppingList: ShoppingList?= null
        private val productsToBuyAdapter = GroupAdapter<ViewHolder>()
        private val boughtProductsAdapter = GroupAdapter<ViewHolder>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)

        shoppingList = intent.getParcelableExtra(
            ShoppingListsActivity.SHOPPING_LIST_KEY
        )

        val date = shoppingList!!.date
        val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm")
        val text: String = formatter.format(date)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Zakupy z: $text"

        productsToBuyAdapter.clear()
        boughtProductsAdapter.clear()

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.adapter = mSectionsPagerAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        Toast.makeText(this, "Przytrzymaj produkt, aby przenieść go do drugiej sekcji.", Toast.LENGTH_SHORT)
            .show()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_products, menu)
        return true
    }


    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return PlaceholderFragment.newInstance(
                position + 1
            )
        }

        override fun getCount(): Int {
            return 2
        }
    }


    class PlaceholderFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val rootView = inflater.inflate(R.layout.fragment_products, container, false)

            productsToBuyAdapter.clear()
            boughtProductsAdapter.clear()

            for ((key, _) in shoppingList!!.products) {
                if(shoppingList!!.products[key] == false) {
                    productsToBuyAdapter.add(ShoppingListProductToBuyItem(key))
                } else {
                    boughtProductsAdapter.add(ShoppingListProductBoughtItem(key))
                }
            }

            productsToBuyAdapter.setOnItemLongClickListener { item, _ ->
                Toast.makeText(this.context, "Dodano do sekcji 'KUPIONE'.",Toast.LENGTH_SHORT)
                    .show()

                val shoppingListProductItem = item as ShoppingListProductToBuyItem
                val productName = shoppingListProductItem.text
                val uid = FirebaseAuth.getInstance().uid
                val id = shoppingList!!.id
                val ref = FirebaseDatabase.getInstance().getReference("/shopping-lists/$uid/$id")

                for ((key, _) in shoppingList!!.products) {
                    if(key == productName) {
                        shoppingList!!.products[key] = true
                    }
                }

                boughtProductsAdapter.add(ShoppingListProductBoughtItem(productName))
                productsToBuyAdapter.remove(item)

                ref.setValue(shoppingList!!)

                item.isLongClickable
            }

            boughtProductsAdapter.setOnItemLongClickListener { item, _ ->
                Toast.makeText(this.context, "Dodano do sekcji 'DO KUPIENIA'.",Toast.LENGTH_SHORT)
                    .show()

                val shoppingListProductItem = item as ShoppingListProductBoughtItem
                val productName = shoppingListProductItem.text
                val uid = FirebaseAuth.getInstance().uid
                val id = shoppingList!!.id
                val ref = FirebaseDatabase.getInstance().getReference("/shopping-lists/$uid/$id")

                for ((key, _) in shoppingList!!.products) {
                    if(key == productName) {
                        shoppingList!!.products[key] = false
                    }
                }

                productsToBuyAdapter.add(ShoppingListProductToBuyItem(productName))
                boughtProductsAdapter.remove(item)

                ref.setValue(shoppingList!!)

                item.isLongClickable
            }

            if(arguments?.getInt(ARG_SECTION_NUMBER) == 1) {
                rootView.products_RecyclerView.adapter = productsToBuyAdapter
            } else {
                rootView.products_RecyclerView.adapter = boughtProductsAdapter
            }

            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)

            rootView.products_RecyclerView.layoutManager = layoutManager

            return rootView
        }

        companion object {
            private val ARG_SECTION_NUMBER = "section_number"

            fun newInstance(sectionNumber: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        val uid = FirebaseAuth.getInstance().uid
        val id = shoppingList!!.id
        val ref = FirebaseDatabase.getInstance().getReference("/shopping-lists/$uid/$id")
        ref.setValue(shoppingList!!)

        val previousIntent = Intent(this, ShoppingListsActivity::class.java)
        previousIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(previousIntent)

        finish()
    }
}
