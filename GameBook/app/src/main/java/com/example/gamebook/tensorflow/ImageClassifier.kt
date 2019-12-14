package com.example.gamebook.tensorflow

import android.content.res.AssetManager
import android.graphics.Bitmap
import io.reactivex.Single
import org.tensorflow.lite.Interpreter
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Float
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class ImageClassifier constructor(private val assetManager: AssetManager) {

    private var interpreter: Interpreter? = null
    private var labelProb: Array<ByteArray>
    private val labels = Vector<String>()
    private val intValues by lazy { IntArray(Keys.INPUT_SIZE * Keys.INPUT_SIZE) }
    private var imgData: ByteBuffer

    init {
        try {
            val br =
                BufferedReader(InputStreamReader(assetManager.open(Keys.LABEL_PATH)))
            while (true) {
                val line = br.readLine() ?: break
                labels.add(line)
            }
            br.close()
        } catch (e: IOException) {
            throw RuntimeException("Problem reading label file!", e)
        }
        labelProb = Array(1) { ByteArray(labels.size) }
        imgData =
            ByteBuffer.allocateDirect(Keys.DIM_BATCH_SIZE * Keys.DIM_IMG_SIZE_X * Keys.DIM_IMG_SIZE_Y * Keys.DIM_PIXEL_SIZE)
        imgData!!.order(ByteOrder.nativeOrder())
        try {
            interpreter = Interpreter(
                loadModelFile(
                    assetManager,
                    Keys.MODEL_PATH
                )
            )
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap) {
        if (imgData == null) return
        imgData!!.rewind()
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        var pixel = 0
        for (i in 0 until Keys.DIM_IMG_SIZE_X) {
            for (j in 0 until Keys.DIM_IMG_SIZE_Y) {
                val value = intValues!![pixel++]
                imgData!!.put((value shr 16 and 0xFF).toByte())
                imgData!!.put((value shr 8 and 0xFF).toByte())
                imgData!!.put((value and 0xFF).toByte())
            }
        }
    }

    private fun loadModelFile(assets: AssetManager, modelFilename: String): MappedByteBuffer {
        val fileDescriptor = assets.openFd(modelFilename)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun recognizeImage(bitmap: Bitmap): Single<List<Result>> {
        return Single.just(bitmap).flatMap {
            convertBitmapToByteBuffer(it)
            interpreter!!.run(imgData, labelProb)
            val pq = PriorityQueue<Result>(3,
                Comparator<Result> { lhs, rhs ->
                    // Intentionally reversed to put high confidence at the head of the queue.
                    Float.compare(rhs.confidence!!, lhs.confidence!!)
                })
            for (i in labels.indices) {
                pq.add(
                    Result(
                        "" + i,
                        if (labels.size > i) labels[i] else "unknown",
                        labelProb[0][i].toFloat(),
                        null
                    )
                )
            }
            val recognitions =
                ArrayList<Result>()
            val recognitionsSize = Math.min(pq.size,
                Keys.MAX_RESULTS
            )
            for (i in 0 until recognitionsSize) recognitions.add(pq.poll())
            return@flatMap Single.just(recognitions)
        }
    }

    fun close() {
        interpreter?.close()
    }
}