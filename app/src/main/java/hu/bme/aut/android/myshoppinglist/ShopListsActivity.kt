package hu.bme.aut.android.myshoppinglist

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.crashlytics.android.Crashlytics
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import hu.bme.aut.android.myshoppinglist.adapter.ListsAdapter
import hu.bme.aut.android.myshoppinglist.data.ShoppingList
import kotlinx.android.synthetic.main.activity_shop_lists.*
import kotlinx.android.synthetic.main.app_bar_shop_lists.*
import kotlinx.android.synthetic.main.content_shop_lists.*
import kotlinx.android.synthetic.main.nav_header_shop_lists.view.*

class ShopListsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var listsAdapter: ListsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_lists)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            val createPostIntent = Intent(this, CreateNewListActivity::class.java)
            startActivity(createPostIntent)
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        listsAdapter = ListsAdapter(applicationContext)
        rvPosts.layoutManager = LinearLayoutManager(this).apply {
            reverseLayout = true
            stackFromEnd = true
        }
        rvPosts.adapter = listsAdapter

        initPostsListener()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        }else{
            val builder = AlertDialog.Builder(this@ShopListsActivity)
            builder.setTitle("Kijelentkezés")
            builder.setMessage("Biztosan ki szeretnél jelentkezni?")
            builder.setPositiveButton("YES"){dialog, which ->
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, MainActivity::class.java))
                dialog.dismiss()
                finish()
            }

            builder.setNegativeButton("NO"){dialog, which ->
                dialog.dismiss()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.shop_lists, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_logout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            R.id.nav_error -> Crashlytics.getInstance().crash()
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun initPostsListener() {
        FirebaseDatabase.getInstance()
            .getReference("posts")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    val newList = dataSnapshot.getValue<ShoppingList>(ShoppingList::class.java)
                    listsAdapter.addPost(newList)
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                }

                override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                }

                override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
    }
}
