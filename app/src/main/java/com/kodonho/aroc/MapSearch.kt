package com.kodonho.aroc

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.kodonho.aroc.api.MapSearch
import com.kodonho.aroc.databinding.ActivityMapSearchBinding
import com.kodonho.aroc.dto.MapSearchDto
import com.kodonho.aroc.RetrofitInstance.RetrofitBuilder
import com.kodonho.aroc.api.MemberInfoUpdate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapSearch : AppCompatActivity() {

    private lateinit var binding: ActivityMapSearchBinding
    private var speechRecognizer: SpeechRecognizer? = null

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
        private const val TAG = "MapSearch"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        RetrofitBuilder.initialize(applicationContext) // 초기화

        // RecyclerView 초기화
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = MapSearchAdapter(emptyList())

        // 위치 권한이 허용되었는지 확인하고, 허용되지 않은 경우 요청
        if (!LocationHelper(this).isLocationPermitted()) {
            LocationHelper(this).requestLocation()
        }

        // 위치 권한이 허용된 경우 위치 정보를 가져옵니다.
        if (LocationHelper(this).isLocationPermitted()) {
            getLocation { location ->
                location?.let {
                    val latitude = it.latitude
                    val longitude = it.longitude
                    // 위치 정보가 성공적으로 받아졌을 때만 submit 버튼을 활성화
                    binding.submitBtn.setOnClickListener {
                        val destination = binding.destination.text.toString()
                        if (destination.isNotEmpty()) {
                            searchLocation(destination, latitude, longitude)
                        } else {
                            Toast.makeText(this, "목적지를 입력하세요.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } ?: run {
                    Toast.makeText(this, "위치 정보를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Log.e(TAG, "Location permission not granted")
        }

        // 음성 권한 설정
        requestPermission()

        // RecognizerIntent 생성
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000)
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 2000)
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 2000)

        // <말하기> 버튼 눌러서 음성인식 시작
        binding.speechBtn.setOnClickListener {
            startSpeechRecognition(intent)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(callback: (Location?) -> Unit) {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location: Location? ->
                callback(location)
            }
            .addOnFailureListener { fail ->
                Log.e(TAG, "Failed to get location: ${fail.localizedMessage}")
                callback(null)
            }
    }

    private fun searchLocation(destination: String, lat: Double, lon: Double) {
        //val retrofit = RetrofitBuilder.getRetrofit()
        val service = RetrofitBuilder.createService(MapSearch::class.java)
        //val service = retrofit.create(MapSearch::class.java)
        val call = service.search(destination, lat, lon)
        call.enqueue(object : Callback<List<MapSearchDto>> {
            override fun onResponse(call: Call<List<MapSearchDto>>, response: Response<List<MapSearchDto>>) {
                if (response.isSuccessful) {
                    val pois = response.body()
                    pois?.let {
                        binding.recyclerView.adapter = MapSearchAdapter(it)
                    }
                } else {
                    Log.e(TAG, "Request failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<MapSearchDto>>, t: Throwable) {
                Log.e(TAG, "Network request failed: ${t.localizedMessage}")
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LocationHelper.REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    getLocation { location ->
                        location?.let {
                            val latitude = it.latitude
                            val longitude = it.longitude
                            val destination = binding.destination.text.toString()
                            if (destination.isNotEmpty()) {
                                searchLocation(destination, latitude, longitude)
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "위치 권한 거부됨")
                }
            }
            REQUEST_RECORD_AUDIO_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "녹음 권한 허용됨")
                } else {
                    Toast.makeText(this, "녹음 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "녹음 권한 거부됨")
                }
            }
        }
    }

    private fun startSpeechRecognition(intent: Intent) {
        if (speechRecognizer != null) {
            speechRecognizer?.destroy()
            speechRecognizer = null
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer?.setRecognitionListener(recognitionListener)
        Log.d(TAG, "Start Listening")
        speechRecognizer?.startListening(intent)
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= 23 &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO_PERMISSION)
        }
    }

    private val recognitionListener: RecognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle) {
            Log.d(TAG, "onReadyForSpeech")
            Toast.makeText(applicationContext, "음성인식 시작", Toast.LENGTH_SHORT).show()
            binding.tvState.text = "이제 말씀하세요!"
        }

        override fun onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech")
            binding.tvState.text = "잘 듣고 있어요."
        }

        override fun onRmsChanged(rmsdB: Float) {
            Log.d(TAG, "onRmsChanged: $rmsdB")
        }

        override fun onBufferReceived(buffer: ByteArray) {
            Log.d(TAG, "onBufferReceived")
        }

        override fun onEndOfSpeech() {
            Log.d(TAG, "onEndOfSpeech")
            binding.tvState.text = "끝!"
            speechRecognizer?.destroy()
            speechRecognizer = null
        }

        override fun onError(error: Int) {
            val message = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "오디오 에러"
                SpeechRecognizer.ERROR_CLIENT -> "클라이언트 에러"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "퍼미션 없음"
                SpeechRecognizer.ERROR_NETWORK -> "네트워크 에러"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "네트웍 타임아웃"
                SpeechRecognizer.ERROR_NO_MATCH -> "찾을 수 없음. 다시 시도해 주세요."
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RECOGNIZER 가 바쁨"
                SpeechRecognizer.ERROR_SERVER -> "서버가 이상함"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "말하는 시간초과"
                else -> "알 수 없는 오류임"
            }
            Log.d(TAG, "onError: $error - $message")
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
            binding.tvState.text = "에러 발생: $message"
            speechRecognizer?.destroy()
            speechRecognizer = null
        }

        override fun onResults(results: Bundle) {
            Log.d(TAG, "onResults called")
            val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                Log.d(TAG, "Recognition result: ${matches[0]}")
                binding.destination.setText(matches[0])
                Log.d(TAG, "음성 인식 결과: ${matches[0]}")
            } else {
                Log.d(TAG, "No recognition results")
            }
            speechRecognizer?.destroy()
            speechRecognizer = null
        }

        override fun onPartialResults(partialResults: Bundle) {
            Log.d(TAG, "onPartialResults called")
            val partialResultsString = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!partialResultsString.isNullOrEmpty()) {
                binding.destination.setText(partialResultsString[0])
                Log.d(TAG, "부분 인식 결과: ${partialResultsString[0]}")
            }
        }

        override fun onEvent(eventType: Int, params: Bundle) {
            Log.d(TAG, "onEvent: $eventType")
        }
    }
}
