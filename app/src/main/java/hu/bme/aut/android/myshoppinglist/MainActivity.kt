package hu.bme.aut.android.myshoppinglist

import android.content.Intent
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import hu.bme.aut.android.myshoppinglist.extension.validateNonEmpty
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnRegister.setOnClickListener { registerClick() }
        btnLogin.setOnClickListener { loginClick() }

        firebaseAuth = FirebaseAuth.getInstance()
    }

    private fun validateForm() = etEmail.validateNonEmpty() && etPassword.validateNonEmpty()

    private fun registerClick() {
        if (!validateForm()) {
            return
        }

        showProgressDialog()

        firebaseAuth
            .createUserWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString())
            .addOnSuccessListener { result ->
                hideProgressDialog()

                val firebaseUser = result.user
                val profileChangeRequest = UserProfileChangeRequest.Builder()
                    .setDisplayName(firebaseUser.email?.substringBefore('@'))
                    .build()
                firebaseUser.updateProfile(profileChangeRequest)

                toast("Registration successful")

                firebaseAuth
                    .signInWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString())
                    .addOnSuccessListener {
                        hideProgressDialog()

                        startActivity(Intent(this@MainActivity, ShopListsActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener { exception ->
                        hideProgressDialog()

                        toast(exception.localizedMessage)
                    }
            }
            .addOnFailureListener { exception ->
                hideProgressDialog()

                toast(exception.message)
            }
    }

    private fun loginClick() {
        if (!validateForm()) {
            return
        }

        showProgressDialog()

        firebaseAuth
            .signInWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString())
            .addOnSuccessListener {
                hideProgressDialog()

                startActivity(Intent(this@MainActivity, ShopListsActivity::class.java))
                finish()
            }
            .addOnFailureListener { exception ->
                hideProgressDialog()

                toast(exception.localizedMessage)
            }
    }

}
