package alanis.jorge.alarmaproximidad

import alanis.jorge.alarmaproximidad.databinding.ActivityMainBinding
import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

// Victor Ponce Matricula : 518755
class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null
    private var proximitySensor: Sensor? = null

    private var lightValue: Float = Float.MAX_VALUE
    private var proximityValue: Float = Float.MAX_VALUE

    private var mediaPlayer: MediaPlayer? = null

    private lateinit var mainLayout: LinearLayout
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//TODO Obten el SensorManager y los sensores

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)




    }

    override fun onResume() {
        super.onResume()

        

        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL)

    }

    override fun onPause() {
        super.onPause()


        sensorManager.unregisterListener(this)


        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            lightValue = event.values[0]
            binding.lightValueTextView.text = "Luz: $lightValue lux"
            if (lightValue < 10 && proximityValue < 5) {
                playAlarm()
                blinkBackground()
            }
        } else if (event?.sensor?.type == Sensor.TYPE_PROXIMITY) {
            proximityValue = event.values[0]
            binding.proximityValueTextView.text = "Proximidad: $proximityValue cm"
            if (lightValue < 10 && proximityValue < 5) {
                playAlarm()
                blinkBackground()
            }
        }
    }


    private fun playAlarm() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound)
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
        }
        blinkBackground()
    }

    private fun blinkBackground() {
        val colors = arrayOf(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW)
        var colorIndex = 0

        val runnable = object : Runnable {
            override fun run() {
                binding.mainLayout.setBackgroundColor(colors[colorIndex % 2])
                colorIndex++
                handler.postDelayed(this, 500)  // Parpadea cada 500ms
            }
        }

        handler.post(runnable)
    }

    private fun stopAlarm() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        handler.removeCallbacksAndMessages(null)  // Detiene el parpadeo
        binding.mainLayout.setBackgroundColor(Color.WHITE)  // Restablece el color de fondo
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No es necesario implementar este método para esta demostración
    }
}
