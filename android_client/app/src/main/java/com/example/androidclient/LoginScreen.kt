package com.example.androidclient


import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.activity.result.IntentSenderRequest




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onTokenReceived: (String) -> Unit) {
    val context = LocalContext.current
    val firebaseAuth = remember { FirebaseAuth.getInstance() }
    val oneTapClient = remember { Identity.getSignInClient(context) }

    val signInRequest = remember {
        BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId("649303745201-l6p7r6u66rr4mn8v142n3d5068eg0d9q.apps.googleusercontent.com")
                    .setFilterByAuthorizedAccounts(false) //  zezwala na logowanie każdego
                    .build()
            )
            .setAutoSelectEnabled(false) //  wyłącza autowybór, który czasem blokuje One Tap
            .build()
    }


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
            val idToken = credential.googleIdToken
            if (idToken != null) {
                // Zaloguj do Firebase (opcjonalnie)
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                firebaseAuth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Zalogowano!", Toast.LENGTH_SHORT).show()
                            Log.d("LoginScreen", "TOKEN: $idToken")
                            CoroutineScope(Dispatchers.IO).launch {
                                val success = sendTokenToBackend(idToken)
                                withContext(Dispatchers.Main) {
                                    if (success) {
                                        Toast.makeText(context, "Token wysłany do backendu!", Toast.LENGTH_SHORT).show()
                                        onTokenReceived(idToken)
                                    } else {
                                        Toast.makeText(context, "Błąd wysyłania tokena!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }


                        } else {
                            Toast.makeText(context, "Logowanie nieudane", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ToDoApp") }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = {
                oneTapClient.beginSignIn(signInRequest)
                    .addOnSuccessListener { result ->
                        launcher.launch(
                            IntentSenderRequest.Builder(result.pendingIntent).build()
                        )
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Błąd logowania: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }) {
                Text("Zaloguj przez Google")
            }
        }
    }
}

suspend fun sendTokenToBackend(idToken: String): Boolean {
    val client = OkHttpClient()

    val request = Request.Builder()
        .url("http://10.0.2.2:8080/api/auth/google")
        .addHeader("Authorization", "Bearer $idToken") //
        .post("".toRequestBody("application/json".toMediaType())) // pusty body, ale POST
        .build()

    return try {
        val response = client.newCall(request).execute()
        Log.d("LoginScreen", "🛰 Response code: ${response.code}")
        response.isSuccessful
    } catch (e: Exception) {
        Log.e("LoginScreen", " Błąd wysyłania tokena: ${e.message}")
        false
    }
}


