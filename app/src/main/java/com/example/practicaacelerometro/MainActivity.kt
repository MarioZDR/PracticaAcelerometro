package com.example.practicaacelerometro
import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var gestorSensores: SensorManager
    private var acelerometro: Sensor? = null

    private lateinit var vistaAceleracionActual: TextView
    private lateinit var vistaAceleracionMaxima: TextView

    private lateinit var sharedPreferences: SharedPreferences

    private var aceleracionMaxima = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vistaAceleracionActual = findViewById(R.id.aceleracionActual)
        vistaAceleracionMaxima = findViewById(R.id.aceleracionMaxima)

        sharedPreferences = getSharedPreferences("prefAcelerometro", Context.MODE_PRIVATE)

        gestorSensores = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        acelerometro = gestorSensores.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        aceleracionMaxima = leerAceleracionMaxima()
        actualizarAceleracionMaxima(aceleracionMaxima)
    }

    override fun onResume() {
        super.onResume()
        gestorSensores.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        gestorSensores.unregisterListener(this)
    }

    override fun onSensorChanged(evento: SensorEvent) {
        if (evento.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val aceleracion = sqrt(evento.values[0] * evento.values[0] +
                    evento.values[1] * evento.values[1] +
                    evento.values[2] * evento.values[2])

            actualizarAceleracionActual(aceleracion)

            if (aceleracion > aceleracionMaxima) {
                aceleracionMaxima = aceleracion
                actualizarAceleracionMaxima(aceleracionMaxima)
                guardarAceleracionMaxima(aceleracionMaxima)
            }
        }
    }

    private fun actualizarAceleracionMaxima(aceleracionMaxima: Float){
        vistaAceleracionMaxima.text = "Aceleración Máxima: ${String.format("%.1f", aceleracionMaxima)}"
    }

    private fun actualizarAceleracionActual(aceleracion: Float){
        vistaAceleracionActual.text = "Aceleración Actual: ${String.format("%.1f", aceleracion)}"
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
    }

    private fun leerAceleracionMaxima(): Float {
        return sharedPreferences.getFloat("aceleracionMaxima", 0f)
    }

    private fun guardarAceleracionMaxima(aceleracionMaxima: Float) {
        val editor = sharedPreferences.edit()
        editor.putFloat("aceleracionMaxima", aceleracionMaxima)
        editor.apply()
    }
}