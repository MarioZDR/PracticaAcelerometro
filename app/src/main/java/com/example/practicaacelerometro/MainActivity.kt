package com.example.practicaacelerometro

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var gestorSensores: SensorManager
    private var acelerometro: Sensor? = null
    private lateinit var vistaVelocidadActual: TextView
    private lateinit var vistaVelocidadMaxima: TextView
    private var velocidadMaxima = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vistaVelocidadActual = findViewById(R.id.velocidadActual)
        vistaVelocidadMaxima = findViewById(R.id.velocidadMaxima)

        gestorSensores = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        acelerometro = gestorSensores.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        velocidadMaxima = leerVelocidadMaxima()
        vistaVelocidadMaxima.text = velocidadMaxima.toString()
    }

    override fun onResume() {
        super.onResume()
        acelerometro?.let { gestorSensores.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }
    }

    override fun onPause() {
        super.onPause()
        gestorSensores.unregisterListener(this)
        escribirVelocidadMaxima(velocidadMaxima)
    }

    override fun onSensorChanged(evento: SensorEvent) {
        if (evento.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val velocidad = sqrt(evento.values[0] * evento.values[0] +
                    evento.values[1] * evento.values[1] +
                    evento.values[2] * evento.values[2])

            vistaVelocidadActual.text = "Velocidad Actual: $velocidad"

            if (velocidad > velocidadMaxima) {
                velocidadMaxima = velocidad
                vistaVelocidadMaxima.text = "Velocidad Máxima: $velocidadMaxima"
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
    }

    private fun leerVelocidadMaxima(): Float {
        return try {
            val fis = openFileInput("velocidad_maxima")
            val bytes = ByteArray(fis.available())
            fis.read(bytes)
            fis.close()
            String(bytes).toFloat()
        } catch (e: IOException) {
            0f
        } catch (e: NumberFormatException) {
            0f
        }
    }

    private fun escribirVelocidadMaxima(velocidadMaxima: Float) {
        try {
            val fos = openFileOutput("velocidad_maxima", MODE_PRIVATE)
            fos.write(velocidadMaxima.toString().toByteArray())
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}