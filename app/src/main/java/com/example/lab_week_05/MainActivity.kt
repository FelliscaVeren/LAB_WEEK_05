package com.example.lab_week_05

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lab_week_05.api.CatApiService
import retrofit2.*
import retrofit2.converter.scalars.ScalarsConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var apiResponseView: TextView

    // Retrofit instance
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/v1/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }

    // API service
    private val catApiService by lazy {
        retrofit.create(CatApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Bind TextView dari XML
        apiResponseView = findViewById(R.id.api_response)

        // Atur padding (biar status bar tidak nutup UI)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Panggil API
        getCatImageResponse()
    }

    private fun getCatImageResponse() {
        val call = catApiService.searchImages(1, "full")
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    apiResponseView.text = response.body()
                } else {
                    Log.e(MAIN_ACTIVITY, "Error: ${response.errorBody()?.string()}")
                    apiResponseView.text = "Error: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e(MAIN_ACTIVITY, "Failed to get response", t)
                apiResponseView.text = "Failure: ${t.message}"
            }
        })
    }

    companion object {
        const val MAIN_ACTIVITY = "MainActivity"
    }
}
