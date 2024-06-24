package com.kodonho.aroc

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.util.Locale
import androidx.core.app.ActivityCompat

class TensorActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    lateinit var labels: List<String>
    var colors = listOf(
        Color.BLUE, Color.GREEN, Color.RED, Color.CYAN, Color.GRAY, Color.BLACK,
        Color.DKGRAY, Color.MAGENTA, Color.YELLOW, Color.RED
    )
    val paint = Paint()
    lateinit var imageProcessor: ImageProcessor
    lateinit var bitmap: Bitmap
    lateinit var imageView: ImageView
    lateinit var cameraDevice: CameraDevice
    lateinit var handler: Handler
    lateinit var cameraManager: CameraManager
    lateinit var textureView: TextureView
    lateinit var interpreter: Interpreter
    lateinit var tts: TextToSpeech
    var isSpeaking = false // TTS 상태 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tensor)
        get_permission()

        // TFLite 모델 로드
        val modelFile = FileUtil.loadMappedFile(this, "detect.tflite")
        interpreter = Interpreter(modelFile)

        labels = FileUtil.loadLabels(this, "labelmap.txt")
        imageProcessor = ImageProcessor.Builder().add(ResizeOp(300, 300, ResizeOp.ResizeMethod.BILINEAR)).build()

        val handlerThread = HandlerThread("videoThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)

        imageView = findViewById(R.id.imageView)

        textureView = findViewById(R.id.textureView)
        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(p0: SurfaceTexture, p1: Int, p2: Int) {
                open_camera()
            }

            override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture, p1: Int, p2: Int) {
            }

            override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean {
                return false
            }

            override fun onSurfaceTextureUpdated(p0: SurfaceTexture) {
                bitmap = textureView.bitmap!!
                var image = TensorImage.fromBitmap(bitmap)
                image = imageProcessor.process(image)

                val detectionResults = detectObjects(image)

                var mutable = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                val canvas = Canvas(mutable)

                val imageHeight = mutable.height
                val imageWidth = mutable.width
                val scaledImageHeight = image.height
                val scaledImageWidth = image.width

                paint.textSize = imageHeight / 15f
                paint.strokeWidth = imageWidth / 85f

                val maxObjects = 5
                var objectCount = 0
                var obstacleDetected = false

                detectionResults.forEachIndexed { index, detection ->
                    val category = detection.label
                    val score = detection.confidence

                    if ((category == "motorcycle" || category == "bicycle") && score > 0.6 && objectCount < maxObjects) {
                        val colorIndex = index % colors.size
                        paint.color = colors[colorIndex]
                        paint.style = Paint.Style.STROKE

                        val location = detection.locationAsRectF
                        val left = location.left * imageWidth / scaledImageWidth
                        val top = location.top * imageHeight / scaledImageHeight
                        val right = location.right * imageWidth / scaledImageWidth
                        val bottom = location.bottom * imageHeight / scaledImageHeight

                        canvas.drawRect(left, top, right, bottom, paint)
                        paint.style = Paint.Style.FILL
                        canvas.drawText("$category %.2f".format(score), left, top, paint)

                        obstacleDetected = true
                        objectCount++
                    }
                }

                if (obstacleDetected && !isSpeaking) {
                    isSpeaking = true
                    val detectedObject = if (detectionResults.any { it.label == "motorcycle" }) "오토바이" else "자전거"
                    tts.speak("$detectedObject 가 있습니다.", TextToSpeech.QUEUE_FLUSH, null, "ObstacleMessage")
                }

                runOnUiThread {
                    imageView.setImageBitmap(mutable)
                }
            }
        }

        cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        tts = TextToSpeech(this, this)
    }

    override fun onDestroy() {
        super.onDestroy()
        interpreter.close()
        tts.shutdown()
    }

    @SuppressLint("MissingPermission")
    fun open_camera() {
        cameraManager.openCamera(cameraManager.cameraIdList[0], object : CameraDevice.StateCallback() {
            override fun onOpened(p0: CameraDevice) {
                cameraDevice = p0

                var surfaceTexture = textureView.surfaceTexture
                var surface = Surface(surfaceTexture)

                var captureRequest = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                captureRequest.addTarget(surface)

                cameraDevice.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(p0: CameraCaptureSession) {
                        p0.setRepeatingRequest(captureRequest.build(), null, null)
                    }

                    override fun onConfigureFailed(p0: CameraCaptureSession) {
                    }
                }, handler)
            }

            override fun onDisconnected(p0: CameraDevice) {
            }

            override fun onError(p0: CameraDevice, p1: Int) {
            }
        }, handler)
    }

    fun get_permission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 101)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            get_permission()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.KOREA)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language specified is not supported!")
            }
            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                }

                override fun onDone(utteranceId: String?) {
                    isSpeaking = false
                }

                override fun onError(utteranceId: String?) {
                    isSpeaking = false
                }
            })
        } else {
            Log.e("TTS", "Initialization Failed!")
        }
    }

    private fun detectObjects(image: TensorImage): List<DetectionResult> {
        val inputBuffer = image.buffer
        val outputLocations = Array(1) { Array(10) { FloatArray(4) } }
        val outputClasses = Array(1) { FloatArray(10) }
        val outputScores = Array(1) { FloatArray(10) }
        val numDetections = FloatArray(1)

        val outputMap = mapOf(
            0 to outputLocations,
            1 to outputClasses,
            2 to outputScores,
            3 to numDetections
        )
        interpreter.runForMultipleInputsOutputs(arrayOf(inputBuffer), outputMap)

        val detectionResults = mutableListOf<DetectionResult>()
        for (i in 0 until numDetections[0].toInt()) {
            if (outputScores[0][i] > 0.5f) {
                val detection = DetectionResult(
                    outputLocations[0][i],
                    labels[outputClasses[0][i].toInt()],
                    outputScores[0][i]
                )
                detectionResults.add(detection)
            }
        }
        return detectionResults
    }
}

data class DetectionResult(
    val location: FloatArray,
    val label: String,
    val confidence: Float
) {
    val locationAsRectF: RectF
        get() = RectF(location[1], location[0], location[3], location[2])
}
