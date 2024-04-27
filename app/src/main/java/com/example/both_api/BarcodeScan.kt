package com.example.both_api

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.both_api.databinding.ActivityBarcodeScanBinding
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class BarcodeScan : AppCompatActivity() {

    private lateinit var binding: ActivityBarcodeScanBinding
    private lateinit var barcodeDetector: BarcodeDetector
    private lateinit var cameraSource: CameraSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarcodeScanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        iniBc()
    }

    private fun iniBc(){
        try {
            barcodeDetector = BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.CODE_128)
                .build()
            cameraSource = CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true)
                .build()
        } catch (e: Exception){
            e.printStackTrace()
            Toast.makeText(applicationContext, "Error initializing barcode scanner", Toast.LENGTH_SHORT).show()
            return
        }

        binding.surfaceView!!.holder.addCallback(object: SurfaceHolder.Callback{
            @SuppressLint("MissingPermission")
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    cameraSource.start(binding.surfaceView!!.holder)
                }catch (e: IOException){
                    e.printStackTrace()
                    Toast.makeText(applicationContext, "Error starting camera", Toast.LENGTH_SHORT).show()
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                try {
                    cameraSource.stop()
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Handle camera stop error
                    Toast.makeText(applicationContext, "Error stopping camera", Toast.LENGTH_SHORT).show()
                }
            }
        })

        barcodeDetector.setProcessor(object : Detector.Processor<Barcode>{
            override fun release() {
                Toast.makeText(applicationContext, "barcode scanner has been stopped",
                    Toast.LENGTH_LONG).show()
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() > 0) {
                    val scannedBarcode = barcodes.valueAt(0).displayValue
                    checkBarcodeInDatabase(scannedBarcode)
                }
            }
        })
    }

//    private fun checkBarcodeInDatabase(barcode: String) {
//        lifecycleScope.launch {
//            try {
//                val api1 = createAPI()
//                val response1 = api1.getKeyValue(barcode)
//                val key = response1.key
//                binding.txtMessage.text = "Key for barcode $barcode from API_1: $key"
//
//                val api2 = createAPI2()
//                val response2 = api2.searchBarcode(BarcodeRequest(key))
//                val result = response2.result
//                binding.txtMessage1.text = "Result for barcode $barcode from API_2 using key from API_1: $result"
//            } catch (e1: Exception) {
//                try {
//                    val api2 = createAPI2()
//                    val response2 = api2.searchBarcode(BarcodeRequest(barcode))
//                    val result = response2.result
//                    binding.txtMessage1.text = "Result for barcode $barcode from API_2: $result"
//                } catch (e2: Exception) {
//                    binding.txtMessage.text = "Search value $barcode does not belong to any database."
//                }
//            }
//        }
//    }

//    private fun checkBarcodeInDatabase(barcode: String) {
//        lifecycleScope.launch {
//            try {
//                val api1 = createAPI()
//                val response1 = api1.getKeyValue(barcode)
//                val key = response1.key
//                binding.txtMessage.text = "Key for barcode $barcode from API_1: $key"
//
//                val api2 = createAPI2()
//                val response2 = api2.searchBarcode(BarcodeRequest(key))
//                val result = response2.result
//                binding.txtMessage1.text = "Distributor's Name: $result"
//            } catch (e1: IOException) {
//                // Show Toast message for network connectivity issue
//                Toast.makeText(applicationContext,"Unable to get data from the server", Toast.LENGTH_SHORT).show()
//            } catch (e2: HttpException) {
//                // Show Toast message for server error from API_1
//                Toast.makeText(applicationContext,"Server error occurred. Please try again later.", Toast.LENGTH_SHORT).show()
//            } catch (e3: Exception) {
//                // Handle other exceptions
//                binding.txtMessage.text = "Search value $barcode does not belong to any database."
//            }
//        }
//    }

//    private fun checkBarcodeInDatabase(barcode: String) {
//        lifecycleScope.launch {
//            try {
//                val api1 = createAPI()
//                val response1 = api1.getKeyValue(barcode)
//                val key = response1.key
//                binding.txtMessage.text = "Key for barcode $barcode from API_1: $key"
//
//                val api2 = createAPI2()
//                val response2 = api2.searchBarcode(BarcodeRequest(key))
//                val result = response2.result
//                binding.txtMessage1.text = "Result for barcode $barcode from API_2 using key from API_1: $result"
//            } catch (e1: IOException) {
//                // Show Toast message for network connectivity issue
//                Toast.makeText(applicationContext,"Network error occurred. Please check your internet connection.", Toast.LENGTH_SHORT).show()
//            } catch (e2: HttpException) {
//                // Show Toast message for server error from API_1
//                Toast.makeText(applicationContext,"Server error occurred. Please try again later.", Toast.LENGTH_SHORT).show()
//            } catch (e3: NoSuchElementException) {
//                // Barcode not found in API_1, send directly to API_2
//                try {
//                    val api2 = createAPI2()
//                    val response2 = api2.searchBarcode(BarcodeRequest(barcode))
//                    val result = response2.result
//                    binding.txtMessage.text = "Result for barcode $barcode from API_2: $result"
//                } catch (e: IOException) {
//                    // Show Toast message for network connectivity issue
//                    Toast.makeText(applicationContext,"Network error occurred. Please check your internet connection.", Toast.LENGTH_SHORT).show()
//                } catch (e: Exception) {
//                    // Handle other exceptions
//                    binding.txtMessage.text = "An error occurred: ${e.message}"
//                }
//            } catch (e: Exception) {
//                // Handle other exceptions
//                binding.txtMessage.text = "An error occurred: ${e.message}"
//            }
//        }
//    }


//    Toast.makeText(applicationContext, "Result for barcode $barcode from API_2: $result", Toast.LENGTH_SHORT).show()




//    private fun checkBarcodeInDatabase(barcode: String) {
//        lifecycleScope.launch {
//            try {
//                val api1 = createAPI()
//                val response1 = api1.getKeyValue(barcode)
//                val key = response1.key
//                binding.txtMessage.text = "Key for barcode $barcode from API_1: $key"
//
//                val api2 = createAPI2()
//                try {
//                    val response2 = api2.searchBarcode(BarcodeRequest(key))
//                    val result = response2.result
//                    binding.txtMessage1.text = "Result for barcode $barcode from API_2 using key from API_1: $result"
//                } catch (e2: HttpException) {
//                    // Show Toast message for server error from API_2
//                    Toast.makeText(applicationContext,"Server error occurred in API_2. Please try again later.", Toast.LENGTH_SHORT).show()
//                }
//            } catch (e1: IOException) {
//                // Show Toast message for network connectivity issue or server error from API_1
//                Toast.makeText(applicationContext,"Network error occurred in API_1. Please check your internet connection or try again later.", Toast.LENGTH_SHORT).show()
//            } catch (e: HttpException) {
//                // Barcode not found in API_1, send directly to API_2
//                try {
//                    val api2 = createAPI2()
//                    val response2 = api2.searchBarcode(BarcodeRequest(barcode))
//                    val result = response2.result
//                    binding.txtMessage.text = "Result for barcode $barcode from API_2: $result"
//                } catch (e: IOException) {
//                    // Show Toast message for network connectivity issue or server error from API_2
//                    Toast.makeText(applicationContext,"Network error occurred in API_2. Please check your internet connection or try again later.", Toast.LENGTH_SHORT).show()
//                } catch (e: Exception) {
//                    // Handle other exceptions from API_2
//                    binding.txtMessage.text = "An error occurred in API_2: ${e.message}"
//                }
//            } catch (e: Exception) {
//                // Handle other exceptions
//                binding.txtMessage.text = "An error occurred: ${e.message}"
//            }
//        }
//    }




//    private fun checkBarcodeInDatabase(barcode: String) {
//        lifecycleScope.launch {
//            try {
//                // Call API_1 to get the key for the scanned barcode
//                val api1 = createAPI()
//                val response1 = api1.getKeyValue(barcode)
//                val key = response1.key
//                binding.txtMessage.text = "Key for barcode $barcode from API_1: $key"
//
//                // Call API_2 with the obtained key from API_1
//                val api2 = createAPI2()
//                val response2 = api2.searchBarcode(BarcodeRequest(key))
//                val result = response2.result
//                binding.txtMessage1.text = "Result for barcode $barcode from API_2 using key from API_1: $result"
//            } catch (e1: IOException) {
//                // Show Toast message for network connectivity issue or server error from API_1
//                Toast.makeText(applicationContext,"Network error occurred in API_1. Please check your internet connection or try again later.", Toast.LENGTH_SHORT).show()
//            } catch (e: HttpException) {
//                if (e.code() == 404) {
//                    // Barcode not found in API_1, send directly to API_2
//                    try {
//                        val api2 = createAPI2()
//                        val response2 = api2.searchBarcode(BarcodeRequest(barcode))
//                        val result = response2.result
//                        binding.txtMessage.text = "Result for barcode $barcode from API_2: $result"
//                    } catch (e: IOException) {
//                        // Show Toast message for network connectivity issue or server error from API_2
//                        Toast.makeText(applicationContext,"Network error occurred in API_2. Please check your internet connection or try again later.", Toast.LENGTH_SHORT).show()
//                    } catch (e: Exception) {
//                        // Handle other exceptions from API_2
//                        binding.txtMessage.text = "An error occurred in API_2: ${e.message}"
//                    }
//                } else {
//                    // Show Toast message for server error from API_1
//                    Toast.makeText(applicationContext,"Server error occurred in API_1. Please try again later.", Toast.LENGTH_SHORT).show()
//                }
//            } catch (e: Exception) {
//                // Handle other exceptions
//                binding.txtMessage.text = "An error occurred: ${e.message}"
//            }
//        }
//    }



//    private var isBarcodeScanned = false
//
//    private fun checkBarcodeInDatabase(barcode: String) {
//        if (isBarcodeScanned) {
//            Toast.makeText(applicationContext,"Barcode has already been scanned.", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//
//
//        lifecycleScope.launch {
//
//
//
//            try {
//                // Call API_1 to get the key for the scanned barcode
//                val api1 = createAPI()
//                val response1 = api1.getKeyValue(barcode)
//                val key = response1.key
//                binding.txtMessage.text = "Key for barcode $barcode: $key"
//
//                // Call API_2 with the obtained key from API_1
//                val api2 = createAPI2()
//                val response2 = api2.searchBarcode(BarcodeRequest(key))
//                val result = response2.result
//                binding.txtMessage1.text = "Result for barcode $barcode: $result"
//
//                // Set the flag to true indicating that the barcode has been scanned
//                isBarcodeScanned = true
//            } catch (e1: IOException) {
//                // Show Toast message for network connectivity issue or server error from API_1
//                Toast.makeText(applicationContext,"Network error occurred in API_1. Please try again later.", Toast.LENGTH_SHORT).show()
//            } catch (e: HttpException) {
//                if (e.code() == 404) {
//                    // Barcode not found in API_1, send directly to API_2
//                    try {
//                        val api2 = createAPI2()
//                        val response2 = api2.searchBarcode(BarcodeRequest(barcode))
//                        val result = response2.result
//                        binding.txtMessage.text = "Result for barcode $barcode from API_2: $result"
//
//                        // Set the flag to true indicating that the barcode has been scanned
//                        isBarcodeScanned = true
//                    } catch (e: IOException) {
//                        // Show Toast message for network connectivity issue or server error from API_2
//                        Toast.makeText(applicationContext,"Network error occurred in API_2. Please try again later.", Toast.LENGTH_SHORT).show()
//                    } catch (e: Exception) {
//                        // Handle other exceptions from API_2
//                        binding.txtMessage.text = "An error occurred in API_2: ${e.message}"
//                    }
//                } else {
//                    // Show Toast message for server error from API_1
//                    Toast.makeText(applicationContext,"Server error occurred in API_1. Please try again later.", Toast.LENGTH_SHORT).show()
//                }
//            } catch (e: Exception) {
//                // Handle other exceptions
//                binding.txtMessage.text = "An error occurred: ${e.message}"
//            } finally {
//                // Reset the flag to false so that the barcode can be scanned again
//                isBarcodeScanned = false
//            }
//        }
//    }

    private var isBarcodeScanned = false

    private fun checkBarcodeInDatabase(barcode: String) {
        // Check if the barcode has already been scanned
        if (isBarcodeScanned) {
            Toast.makeText(applicationContext, "Barcode has already been scanned.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                // Clear the text of all TextViews
                binding.txtMessage.text = ""
                binding.txtMessage1.text = ""

                // Call API_1 to get the key for the scanned barcode
                val api1 = createAPI()
                val response1 = api1.getKeyValue(barcode)
                val key = response1.key
                binding.txtMessage.text = "Key for barcode $barcode: $key"

                // Call API_2 with the obtained key from API_1
                val api2 = createAPI2()
//                val response2 = api2.searchBarcode(BarcodeRequest(key))
                val response2 = if (key != null) {
                    // If key is not null, use it to create the request
                    api2.searchBarcode(BarcodeRequest(key))
                } else {
                    // If key is null, use the barcode directly to create the request
                    api2.searchBarcode(BarcodeRequest(barcode))
                }

                val result = response2.result
                binding.txtMessage1.text = "Result for barcode $barcode: $result"

                // Set the flag to true indicating that the barcode has been scanned
                isBarcodeScanned = true
            } catch (e1: IOException) {
                // Show Toast message for network connectivity issue or server error from API_1
                Toast.makeText(applicationContext, "Network error occurred in API_1. Please try again later.", Toast.LENGTH_SHORT).show()
            } catch (e: HttpException) {
                if (e.code() == 404) {
                    // Barcode not found in API_1, send directly to API_2
                    try {
                        val api2 = createAPI2()
                        val response2 = api2.searchBarcode(BarcodeRequest(barcode))
                        val result = response2.result
                        binding.txtMessage.text = "Result for barcode $barcode from API_2: $result"

                        // Set the flag to true indicating that the barcode has been scanned
                        isBarcodeScanned = true
                    } catch (e: IOException) {
                        // Show Toast message for network connectivity issue or server error from API_2
                        Toast.makeText(applicationContext, "Network error occurred in API_2. Please try again later.", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        // Handle other exceptions from API_2
                        binding.txtMessage.text = "An error occurred in API_2: ${e.message}"
                    }
                } else {
                    // Show Toast message for server error from API_1
                    Toast.makeText(applicationContext, "Server error occurred in API_1. Please try again later.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Handle other exceptions
                binding.txtMessage.text = "An error occurred: ${e.message}"
            } finally {
                // Reset the flag to false so that the barcode can be scanned again
                isBarcodeScanned = false
            }
        }
    }

//    private var isBarcodeScanned = false
//
//    private fun checkBarcodeInDatabase(barcode: String) {
//        // Check if the barcode has already been scanned
//        if (isBarcodeScanned) {
//            Toast.makeText(applicationContext, "Barcode has already been scanned.", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        lifecycleScope.launch {
//            try {
//                // Clear the text of all TextViews
//                binding.txtMessage.text = ""
//                binding.txtMessage1.text = ""
//
//                // Call API_1 to get the key for the scanned barcode
//                val api1 = createAPI()
//                val response1 = api1.getKeyValue(barcode)
//                val key = response1.key
//                binding.txtMessage.text = "Key for barcode $barcode: $key"
//
//                // Call API_2 with the obtained key from API_1
//                val api2 = createAPI2()
//                val response2 = api2.searchBarcode(BarcodeRequest(searchValue = barcode))
//                val result = response2.result
//                binding.txtMessage1.text = "Result for barcode $barcode: $result"
//
//                // Set the flag to true indicating that the barcode has been scanned
//                isBarcodeScanned = true
//            } catch (e1: IOException) {
//                // Show Toast message for network connectivity issue or server error from API_1
//                Toast.makeText(applicationContext, "Network error occurred in API_1. Please try again later.", Toast.LENGTH_SHORT).show()
//            } catch (e: HttpException) {
//                if (e.code() == 404) {
//                    // Barcode not found in API_1, send directly to API_2
//                    try {
//                        val api2 = createAPI2()
//                        val response2 = api2.searchBarcode(BarcodeRequest(searchValue = barcode))
//                        val result = response2.result
//                        binding.txtMessage.text = "Result for barcode $barcode from API_2: $result"
//
//                        // Set the flag to true indicating that the barcode has been scanned
//                        isBarcodeScanned = true
//                    } catch (e: IOException) {
//                        // Show Toast message for network connectivity issue or server error from API_2
//                        Toast.makeText(applicationContext, "Network error occurred in API_2. Please check your internet connection or try again later.", Toast.LENGTH_SHORT).show()
//                    } catch (e: Exception) {
//                        // Handle other exceptions from API_2
//                        binding.txtMessage.text = "An error occurred in API_2: ${e.message}"
//                    }
//                } else {
//                    // Show Toast message for server error from API_1
//                    Toast.makeText(applicationContext, "Server error occurred in API_1. Please try again later.", Toast.LENGTH_SHORT).show()
//                }
//            } catch (e: Exception) {
//                // Handle other exceptions
//                binding.txtMessage.text = "An error occurred: ${e.message}"
//            } finally {
//                // Reset the flag to false so that the barcode can be scanned again
//                isBarcodeScanned = false
//            }
//        }
//    }

//    private var isBarcodeScanned = false
//
//    private fun checkBarcodeInDatabase(barcode: String) {
//        // Check if the barcode has already been scanned
//        if (isBarcodeScanned) {
//            Toast.makeText(applicationContext, "Barcode has already been scanned.", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        lifecycleScope.launch {
//            try {
//                // Clear the text of all TextViews
//                binding.txtMessage.text = ""
//                binding.txtMessage1.text = ""
//
//                // Call API_1 to get the key for the scanned barcode
//                val api1 = createAPI()
//                val response1 = api1.getKeyValue(barcode)
//                val key = response1.key
//                binding.txtMessage.text = "Key for barcode $barcode: $key"
//
//                // Call API_2 with the obtained key from API_1
//                val api2 = createAPI2()
//                val response2 = api2.searchBarcode(BarcodeRequest(searchValue = barcode))
//                val result = response2.result
//                binding.txtMessage1.text = "Result for barcode $barcode: $result"
//
//                // Set the flag to true indicating that the barcode has been scanned
//                isBarcodeScanned = true
//            } catch (e1: IOException) {
//                // Show Toast message for network connectivity issue or server error from API_1
//                Toast.makeText(applicationContext, "Network error occurred in API_1. Please try again later.", Toast.LENGTH_SHORT).show()
//            } catch (e: HttpException) {
//                if (e.code() == 404) {
//                    // Barcode not found in API_1, send directly to API_2
//                    try {
//                        val api2 = createAPI2()
//                        val response2 = api2.searchBarcode(BarcodeRequest(searchValue = barcode))
//                        val result = response2.result
//                        binding.txtMessage.text = "Result for barcode $barcode from API_2: $result"
//
//                        // Set the flag to true indicating that the barcode has been scanned
//                        isBarcodeScanned = true
//                    } catch (e: IOException) {
//                        // Show Toast message for network connectivity issue or server error from API_2
//                        Toast.makeText(applicationContext, "Network error occurred in API_2. Please check your internet connection or try again later.", Toast.LENGTH_SHORT).show()
//                    } catch (e: Exception) {
//                        // Handle other exceptions from API_2
//                        binding.txtMessage.text = "An error occurred in API_2: ${e.message}"
//                    }
//                } else {
//                    // Show Toast message for server error from API_1
//                    Toast.makeText(applicationContext, "Server error occurred in API_1. Please try again later.", Toast.LENGTH_SHORT).show()
//                }
//            } catch (e: Exception) {
//                // Handle other exceptions
//                binding.txtMessage.text = "An error occurred: ${e.message}"
//            } finally {
//                // Reset the flag to false so that the barcode can be scanned again
//                isBarcodeScanned = false
//            }
//        }
//    }

//    private var isBarcodeScanned = false
//
//    private fun checkBarcodeInDatabase(barcode: String) {
//        // Check if the barcode has already been scanned
//        if (isBarcodeScanned) {
//            Toast.makeText(applicationContext, "Barcode has already been scanned.", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        lifecycleScope.launch {
//            try {
//                // Clear the text of all TextViews
//                binding.txtMessage.text = ""
//                binding.txtMessage1.text = ""
//
//                // Call API_1 to get the key for the scanned barcode
//                val api1 = createAPI()
//                val response1 = api1.getKeyValue(barcode)
//                val key = response1.key
//                binding.txtMessage.text = "Key for barcode $barcode: $key"
//
//                // Call API_2 with the obtained key from API_1
//                val api2 = createAPI2()
//                val response2 = api2.searchBarcode(BarcodeRequest(searchValue = barcode))
//                val result = response2.result
//                binding.txtMessage1.text = "Result for barcode $barcode: $result"
//
//                // Set the flag to true indicating that the barcode has been scanned
//                isBarcodeScanned = true
//
//            } catch (e1: IOException) {
//                // Show Toast message for network connectivity issue or server error from API_1
//                Toast.makeText(applicationContext, "Network error occurred in API_1. Please try again later.", Toast.LENGTH_SHORT).show()
//            } catch (e: HttpException) {
//                if (e.code() == 404) {
//                    // Barcode not found in API_1, send directly to API_2
//                    try {
//                        val api2 = createAPI2()
//                        val response2 = api2.searchBarcode(BarcodeRequest(searchValue = barcode))
//                        val result = response2.result
//                        binding.txtMessage.text = "Result for barcode $barcode from API_2: $result"
//
//                        // Set the flag to true indicating that the barcode has been scanned
//                        isBarcodeScanned = true
//                    } catch (e: IOException) {
//                        // Show Toast message for network connectivity issue or server error from API_2
//                        Toast.makeText(applicationContext, "Network error occurred in API_2. Please check your internet connection or try again later.", Toast.LENGTH_SHORT).show()
//                    } catch (e: Exception) {
//                        // Handle other exceptions from API_2
//                        binding.txtMessage.text = "An error occurred in API_2: ${e.message}"
//                    }
//                } else {
//                    // Show Toast message for server error from API_1
//                    Toast.makeText(applicationContext, "Server error occurred in API_1. Please try again later.", Toast.LENGTH_SHORT).show()
//                }
//            } catch (e: Exception) {
//                // Handle other exceptions
//                binding.txtMessage.text = "An error occurred: ${e.message}"
//            } finally {
//                // Reset the flag to false so that the barcode can be scanned again
//                isBarcodeScanned = false
//            }
//        }
//    }



//    private var isBarcodeScanned = false
//
//    private fun checkBarcodeInDatabase(barcode: String) {
//        // Check if the barcode has already been scanned
//        if (isBarcodeScanned) {
//            Toast.makeText(applicationContext, "Barcode has already been scanned.", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        lifecycleScope.launch {
//            try {
//                // Clear the text of all TextViews
//                binding.txtMessage.text = ""
//                binding.txtMessage1.text = ""
//
//                // Call API_1 to get the key for the scanned barcode
//                val api1 = createAPI()
//                val response1 = api1.getKeyValue(barcode)
//                val key = response1.key
//                binding.txtMessage.text = "Key for barcode $barcode: $key"
//
//                // Call API_2 to get the key for the scanned barcode
//                val api2 = createAPI2()
//                val response2 = api2.searchBarcode(BarcodeRequest(key))
//                val result = response2.result
//
//                if (key == null){
//                    binding.txtMessage.text = "Key for barcode $barcode: $key"
//                    binding.txtMessage1.text = "Result for barcode $barcode: $result"
//                }
//                else{
//                    val api2 = createAPI2()
//                    val response2 = api2.searchBarcode(BarcodeRequest(barcode))
//                    val result = response2.result
//                    binding.txtMessage.text = "Key for barcode $barcode: $key"
//                    binding.txtMessage1.text = "Result for barcode $barcode: $result"
//                }
//
////                // Call API_2 with the obtained key from API_1
////
////                binding.txtMessage1.text = "Result for barcode $barcode: $result"
//
//                // Set the flag to true indicating that the barcode has been scanned
//                isBarcodeScanned = true
//            } catch (e1: IOException) {
//                // Show Toast message for network connectivity issue or server error from API_1
//                Toast.makeText(applicationContext, "Network error occurred in API_1. Please try again later.", Toast.LENGTH_SHORT).show()
//            } catch (e: HttpException) {
//                if (e.code() == 404) {
//                    // Barcode not found in API_1, send directly to API_2
//                    try {
//                        val api2 = createAPI2()
//                        val response2 = api2.searchBarcode(BarcodeRequest(barcode))
//                        val result = response2.result
//                        binding.txtMessage.text = "Result for barcode $barcode from API_2: $result"
//
//                        // Set the flag to true indicating that the barcode has been scanned
//                        isBarcodeScanned = true
//                    } catch (e: IOException) {
//                        // Show Toast message for network connectivity issue or server error from API_2
//                        Toast.makeText(applicationContext, "Network error occurred in API_2. Please try again later.", Toast.LENGTH_SHORT).show()
//                    } catch (e: Exception) {
//                        // Handle other exceptions from API_2
//                        binding.txtMessage.text = "An error occurred in API_2: ${e.message}"
//                    }
//                } else {
//                    // Show Toast message for server error from API_1
//                    Toast.makeText(applicationContext, "Server error occurred in API_1. Please try again later.", Toast.LENGTH_SHORT).show()
//                }
//            } catch (e: Exception) {
//                // Handle other exceptions
//                binding.txtMessage.text = "An error occurred: ${e.message}"
//            } finally {
//                // Reset the flag to false so that the barcode can be scanned again
//                isBarcodeScanned = false
//            }
//        }
//    }




    override fun onPause() {
        super.onPause()
        cameraSource.release()
    }

    override fun onResume() {
        super.onResume()
        iniBc()
    }
}