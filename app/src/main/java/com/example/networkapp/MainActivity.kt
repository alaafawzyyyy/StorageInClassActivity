package com.example.networkapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.lang.StringBuilder
import java.io.BufferedReader

class MainActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var numberEditText: EditText
    private lateinit var showButton: Button
    private lateinit var comicImageView: ImageView

    private val comicFile = "saved_comic.json"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestQueue = Volley.newRequestQueue(this)
        titleTextView = findViewById(R.id.comicTitleTextView)
        descriptionTextView = findViewById(R.id.comicDescriptionTextView)
        numberEditText = findViewById(R.id.comicNumberEditText)
        showButton = findViewById(R.id.showComicButton)
        comicImageView = findViewById(R.id.comicImageView)

        loadSavedComic()

        showButton.setOnClickListener {
            downloadComic(numberEditText.text.toString())
        }
    }
    private fun downloadComic(comicId: String) {
        val url = "https://xkcd.com/$comicId/info.0.json"
        requestQueue.add(
            JsonObjectRequest(
                url,
                { showComic(it); saveComic(it) }, // Show and save the comic
                { Toast.makeText(this, "Failed to load comic", Toast.LENGTH_SHORT).show() }
            )
        )
    }
    private fun showComic(comicObject: JSONObject) {
        titleTextView.text = comicObject.getString("title")
        descriptionTextView.text = comicObject.getString("alt")
        Picasso.get().load(comicObject.getString("img")).into(comicImageView)
    }

    // Save comic info to a file
    private fun saveComic(comicObject: JSONObject) {
        try {
            val file = File(filesDir, comicFile)
            val outputStream = FileOutputStream(file)
            outputStream.write(comicObject.toString().toByteArray())
            outputStream.close()
            Toast.makeText(this, "Comic saved successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save comic", Toast.LENGTH_SHORT).show()
        }
    }

    // Load previously saved comic
    private fun loadSavedComic() {
        try {
            val file = File(filesDir, comicFile)
            if (file.exists()) {
                val br = BufferedReader(FileReader(file))
                val text = StringBuilder()
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    text.append(line)
                }
                br.close()
                val savedComic = JSONObject(text.toString())
                showComic(savedComic)
                Toast.makeText(this, "Loaded previously saved comic", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to load saved comic", Toast.LENGTH_SHORT).show()
        }
    }
}