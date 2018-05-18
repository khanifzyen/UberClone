package id.my.khanifzyen.uberclone

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import id.my.khanifzyen.uberclone.Model.User
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_register.*
import kotlinx.android.synthetic.main.layout_register.view.*
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseDatabase
    lateinit var users: DatabaseReference

    private var TAG = MainActivity::class.java.simpleName

    //press ctrl+oauth = FirebaseAuth.getInstance()
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //before setcontentview
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                                            .setDefaultFontPath("fonts/Arkhip_font.ttf")
                                            .setFontAttrId(R.attr.fontPath)
                                            .build())
        setContentView(R.layout.activity_main)

        //init firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        users = db.getReference("Users")

        //init view
        //no need to do that ^_^

        //event
        btnRegister.setOnClickListener {
            Toast.makeText(this,"Register Clicked",Toast.LENGTH_SHORT).show()
            showRegisterDialog()
        }

        btnSignIn.setOnClickListener {
            Toast.makeText(this,"Sign In Clicked",Toast.LENGTH_SHORT).show()
        }



    }

    private fun showRegisterDialog() {
        var dialog = AlertDialog.Builder(this)
        dialog.setTitle("REGISTER")
        dialog.setMessage("Please use email to register")

        val inflater = LayoutInflater.from(this)
        val register_layout = inflater.inflate(R.layout.layout_register,null)

        dialog.setView(register_layout)

        //set button
        dialog.setPositiveButton("REGISTER",object: DialogInterface.OnClickListener {
            override fun onClick(dialogInterface: DialogInterface?, i: Int) {
                dialogInterface?.dismiss()
                //Snackbar.make(rootLayout,"Register Clicked",Snackbar.LENGTH_SHORT).show()

                Log.d(TAG,"edt_email: " + register_layout.edt_email.text.toString())
                Log.d(TAG,"edt_phone: " + register_layout.edt_phone.text.toString())
                Log.d(TAG,"edt_name: " + register_layout.edt_name.text.toString())
                Log.d(TAG,"edt_password: " + register_layout.edt_password.text.toString())

                //check validation
                if(TextUtils.isEmpty(register_layout.edt_email.text.toString())){
                    Snackbar.make(rootLayout,"Please enter email address", Snackbar.LENGTH_SHORT).show()
                    return
                }
                if(TextUtils.isEmpty(register_layout.edt_phone.text.toString())){
                    Snackbar.make(rootLayout,"Please enter phone", Snackbar.LENGTH_SHORT).show()
                    return
                }
                if(TextUtils.isEmpty(register_layout.edt_name.text.toString())){
                    Snackbar.make(rootLayout,"Please enter your name", Snackbar.LENGTH_SHORT).show()
                    return
                }
                if(TextUtils.isEmpty(register_layout.edt_password.text.toString())){
                    Snackbar.make(rootLayout,"Please enter password", Snackbar.LENGTH_SHORT).show()
                    return
                }
                if(register_layout.edt_password.text.length < 6) {
                    Snackbar.make(rootLayout,"Password too short",Snackbar.LENGTH_SHORT).show()
                    return
                }

                //register new user
                auth.createUserWithEmailAndPassword(edt_email.text.toString(),edt_password.text.toString())
                        .addOnSuccessListener(object: OnSuccessListener<AuthResult>{
                            override fun onSuccess(authResult: AuthResult?) {
                                //save user to db
                                lateinit var user: User
                                user.email = edt_email.text.toString()
                                user.password = edt_password.text.toString()
                                user.phone = edt_phone.text.toString()
                                user.name = edt_name.text.toString()

                                //user email as key
                                users.child(user.email)
                                        .setValue(user)
                                        .addOnSuccessListener(object: OnSuccessListener<Void>{
                                            override fun onSuccess(aVoid: Void?) {
                                                Snackbar.make(rootLayout,"Register Success",Snackbar.LENGTH_SHORT).show()
                                            }
                                        })
                                        .addOnFailureListener(object: OnFailureListener{
                                            override fun onFailure(e: Exception) {
                                                Snackbar.make(rootLayout,"Register Failed "+ e.message,Snackbar.LENGTH_SHORT).show()
                                            }
                                        })
                            }
                        })
                        .addOnFailureListener(object: OnFailureListener{
                            override fun onFailure(e: Exception) {
                                Snackbar.make(rootLayout,"Something went wrong "+e.message,Snackbar.LENGTH_SHORT).show()
                            }
                        })
            }
        })

        dialog.setNegativeButton("CANCEL",object: DialogInterface.OnClickListener{
            override fun onClick(dialogInterface: DialogInterface?, i: Int) {
                dialogInterface?.dismiss()
                Snackbar.make(rootLayout,"Cancel Clicked",Snackbar.LENGTH_SHORT).show()
            }
        })

        dialog.show()
    }
}
