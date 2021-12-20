package com.example.adictless1

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Base64
import java.io.UnsupportedEncodingException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.Security
import java.time.Instant
import java.time.LocalDate
import java.util.*
import javax.crypto.*
import javax.crypto.spec.SecretKeySpec

class RegisterActivity2 : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register2)

        auth = Firebase.auth

        val regEmail = findViewById<TextView>(R.id.emailRegister)
        val regUsername = findViewById<TextView>(R.id.nickNameRegister)
        val regPassword = findViewById<TextView>(R.id.passwordRegister)
        val regConfPassword = findViewById<TextView>(R.id.passwordConfirm)
        val regRegistro = findViewById<Button>(R.id.confirmRegister)
        val regRedes = findViewById<CheckBox>(R.id.redesSociales)
        val regApuestas = findViewById<CheckBox>(R.id.Apuestas)
        val regVideojuegos = findViewById<CheckBox>(R.id.Videojuegos)
        val regNose = findViewById<CheckBox>(R.id.NoSabe)
        val progressBar = findViewById<ProgressBar>(R.id.reg_progress)

        regRegistro.setOnClickListener(View.OnClickListener {
            val email: String
            val username: String
            val password: String
            val confirmPassword: String
            var type: String = ""
            val level: Float = 1.0F

            email = regEmail.getText().toString()
            username = regUsername.getText().toString()
            password = regPassword.getText().toString()
            confirmPassword = regConfPassword.getText().toString()

            if (email != "" && username != "" && password != "" && (regRedes.isChecked == true ||
                        regApuestas.isChecked == true || regVideojuegos.isChecked == true ||
                        regNose.isChecked == true)) {

                if (password.length >= 8) {
                    if (regNose.isChecked == true)
                        type += "NoProcede "
                    if (regRedes.isChecked == true)
                        type += "RedesSociales "
                    if (regApuestas.isChecked == true)
                        type += "Apuestas "
                    if (regVideojuegos.isChecked == true)
                        type += "Videojuegos"

                } else {
                    Toast.makeText(
                        applicationContext,
                        "El password debe contener al menos 8 caracteres",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                if (password != confirmPassword){
                    Toast.makeText(
                        applicationContext,
                        "Los passwords introducidos no son iguales",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {

                    progressBar.setVisibility(View.VISIBLE)
                    val key: String = "-JaNdRgUkXp2s5v8y/B?E(G+KbPeShVm"
                    val encryptedPassword : String? = encrypt(password,key)
                    crearCuenta(email, username, password, type, level, encryptedPassword)
                    progressBar.setVisibility(View.GONE)
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "Todos los campos son requeridos",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })

        val survey = findViewById<TextView>(R.id.survey)
        survey.setOnClickListener {
            val surveyAct = Intent(this, SurveyActivityFinal::class.java)
            startActivity(surveyAct)
        }


        /* Intercambio de bloqueos entre checkbox */
        val noResponse = findViewById<CheckBox>(R.id.NoSabe)

        noResponse.setOnCheckedChangeListener { buttonView, isChecked ->
            regRedes.isEnabled = !isChecked
            regApuestas.isEnabled = !isChecked
            regVideojuegos.isEnabled = !isChecked
        }

        regRedes.setOnCheckedChangeListener { buttonView, isChecked ->
            noResponse.isEnabled = !isChecked && !regApuestas.isChecked && !regVideojuegos.isChecked
        }
        regApuestas.setOnCheckedChangeListener { buttonView, isChecked ->
            noResponse.isEnabled = !isChecked && !regRedes.isChecked && !regVideojuegos.isChecked
        }
        regVideojuegos.setOnCheckedChangeListener { buttonView, isChecked ->
            noResponse.isEnabled = !isChecked && !regApuestas.isChecked && !regRedes.isChecked
        }
    }

    companion object{
        private var TAG = "EmailPassword"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun crearCuenta (email: String, username: String, password: String, type: String, level: Float, encryptedPassword: String?){
        val query = db.collection("users").whereEqualTo("username", username).get()
            .addOnCompleteListener(this) {query ->
                if (query.result!!.isEmpty) {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "createUserWithEmail:success")
                                val user = auth.currentUser
                                Toast.makeText(
                                    baseContext, "Registro Correcto",
                                    Toast.LENGTH_SHORT
                                ).show()
                                updateUI(user)

                                val arrayRedes = arrayListOf(0F,0F,0F,0F,0F,0F,0F)
                                val arrayApuestas = arrayListOf(0F,0F,0F,0F,0F,0F,0F)
                                val arrayVideojuegos = arrayListOf(0F,0F,0F,0F,0F,0F,0F)

                                val logros = arrayListOf(0,0,0)
                                val logro_foro = 0
                                val logro_estadistica = 0

                                val current = Timestamp.now()

                                val usuario = hashMapOf(
                                    "email" to email,
                                    "username" to username,
                                    "password" to encryptedPassword,
                                    "type" to type,
                                    "level" to level,
                                    "last_login" to current,
                                    "stats_socialmedia" to arrayRedes,
                                    "stats_bets" to arrayApuestas,
                                    "stats_videogames" to arrayVideojuegos,
                                    "awards" to logros,
                                    "cont_award_forum" to logro_foro,
                                    "cont_award_stats" to logro_estadistica
                                )

                                TAG = "DocSnippets"
                                 if (user != null) {
                                    db.collection("users").document(user.uid)
                                        .set(usuario)
                                        .addOnSuccessListener {
                                            Log.d(
                                                TAG,
                                                "Documento escrito de forma exitosa"
                                            )
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w(
                                                TAG,
                                                "Error al escribir el documento",
                                                e
                                            )
                                        }
                                }
                                user!!.sendEmailVerification()
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Log.d(TAG, "Email de verificacion.")
                                            Toast.makeText(
                                                baseContext, "Revisa el correo electronico para verificar el correo",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }


                                val registro = Intent(applicationContext, MainActivity::class.java)
                                startActivity(registro)
                                // Sign in success, update UI with the signed-in user's information

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                                Toast.makeText(
                                    baseContext, "Fallo de Registro",
                                    Toast.LENGTH_SHORT
                                ).show()
                                updateUI(null)
                            }
                        }
                } else {
                    Toast.makeText(
                        baseContext, "El usuario ya existe",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    private fun updateUI(user: FirebaseUser?) {

    }

    private fun reload() {

    }
    fun encrypt(strToEncrypt: String, secret_key: String): String? {
        Security.addProvider(BouncyCastleProvider())
        var keyBytes: ByteArray

        try {
            keyBytes = secret_key.toByteArray(charset("UTF8"))
            val skey = SecretKeySpec(keyBytes, "AES")
            val input = strToEncrypt.toByteArray(charset("UTF8"))

            synchronized(Cipher::class.java) {
                val cipher = Cipher.getInstance("AES/ECB/PKCS7Padding")
                cipher.init(Cipher.ENCRYPT_MODE, skey)

                val cipherText = ByteArray(cipher.getOutputSize(input.size))
                var ctLength = cipher.update(
                    input, 0, input.size,
                    cipherText, 0
                )
                ctLength += cipher.doFinal(cipherText, ctLength)
                return String(
                    Base64.encode(cipherText)
                )
            }
        } catch (uee: UnsupportedEncodingException) {
            uee.printStackTrace()
        } catch (ibse: IllegalBlockSizeException) {
            ibse.printStackTrace()
        } catch (bpe: BadPaddingException) {
            bpe.printStackTrace()
        } catch (ike: InvalidKeyException) {
            ike.printStackTrace()
        } catch (nspe: NoSuchPaddingException) {
            nspe.printStackTrace()
        } catch (nsae: NoSuchAlgorithmException) {
            nsae.printStackTrace()
        } catch (e: ShortBufferException) {
            e.printStackTrace()
        }

        return null
    }
}