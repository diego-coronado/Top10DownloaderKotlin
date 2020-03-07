package com.diego_cor

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "oncreate called")

        val downloadData = DownloadData()
        downloadData.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml")
        Log.d(TAG, "oncreate: done")
    }

    companion object {
        private class DownloadData : AsyncTask<String, Void, String>() {
            private val TAG = "DownloadData"

            override fun doInBackground(vararg params: String?): String {
                Log.d(TAG, "doinbackground: starts with ${params[0]}")
                val rssFeed = downloadXML(params[0])
                if (rssFeed.isEmpty()) {
                    Log.e(TAG, "doinbackgrund: Error downloading")
                }
                return rssFeed
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                Log.d(TAG, "onpostexecute: parameter is $result")
            }

            private fun downloadXML(urlPath: String?): String {
                val xmlResult = StringBuilder()

                try {
                    val url = URL(urlPath)
                    val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                    val response = connection.responseCode
                    Log.d(TAG, "downloadxml: the response code was $response")


//                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
//                    val inputBuffer = CharArray(500)
//                    var charsRead = 0
//                    while (charsRead >= 0) {
//                        charsRead = reader.read(inputBuffer)
//                        if (charsRead > 0) {
//                            xmlResult.append((String(inputBuffer, 0, charsRead)))
//                        }
//                    }
//                    reader.close()

                    connection.inputStream.buffered().reader().use { reader ->
                        xmlResult.append(reader.readText())
                    }

                    Log.d(TAG, "Received ${xmlResult.length} bytes")
                    return xmlResult.toString()
                } catch (e: Exception) {
                    val errorMessage: String = when (e) {
                        is MalformedURLException -> "downloadXml:Invalid url ${e.message}"
                        is IOException -> "downloadxml: IO exception reading data: ${e.message}"
                        is SecurityException -> {
                            e.printStackTrace()
                            "doanloadxml: Security exception needs permission? ${e.message}"
                        }
                        else -> "downloadxml: Unknown error: ${e.message}"
                    }
                }

                return ""
            }
        }
    }


}
