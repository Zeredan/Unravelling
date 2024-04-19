package com.example.unravelling

import android.app.Activity
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.Surface
import android.view.WindowManager

class TiltManager : SensorEventListener
{
    var orientationUpdated: MutableList<((Float, Float, Float) -> Unit)> = mutableListOf()

    lateinit var mWindowManager: WindowManager
    lateinit var mSensorManager: SensorManager

    private var mRotationSensor: Sensor? = null
    private var mLastAccuracy: Int = SensorManager.SENSOR_STATUS_UNRELIABLE

    constructor(activity: Activity)
    {
        mWindowManager = activity.window.windowManager
        mSensorManager = activity.getSystemService(Activity.SENSOR_SERVICE) as SensorManager
        mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    }

    fun startListening() {
        mSensorManager.registerListener(this, mRotationSensor, 16000)
    }

    fun stopListening() {
        mSensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        mLastAccuracy = accuracy
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (mLastAccuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            return
        }
        if (event.sensor == mRotationSensor) {
            updateOrientation(event.values)
        }
    }

    private fun updateOrientation(rotationVector: FloatArray) {
        val rotationMatrix = FloatArray(9)
        SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector)

        val (worldAxisForDeviceAxisX, worldAxisForDeviceAxisY) = when (mWindowManager.defaultDisplay.rotation) {
            Surface.ROTATION_0 -> Pair(SensorManager.AXIS_X, SensorManager.AXIS_Z)
            Surface.ROTATION_90 -> Pair(SensorManager.AXIS_Z, SensorManager.AXIS_MINUS_X)
            Surface.ROTATION_180 -> Pair(SensorManager.AXIS_MINUS_X, SensorManager.AXIS_MINUS_Z)
            Surface.ROTATION_270 -> Pair(SensorManager.AXIS_MINUS_Z, SensorManager.AXIS_X)
            else -> Pair(SensorManager.AXIS_X, SensorManager.AXIS_Z)
        }

        val adjustedRotationMatrix = FloatArray(9)
        SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisForDeviceAxisX,
            worldAxisForDeviceAxisY, adjustedRotationMatrix)

        // Transform rotation matrix into azimuth/pitch/roll
        val orientation = FloatArray(3)
        SensorManager.getOrientation(adjustedRotationMatrix, orientation)

        // Convert radians to degrees
        val pitch = orientation[1] * -57
        val roll = orientation[2] * -57
        val yaw = orientation[0] * -57

        orientationUpdated.forEach{ it(pitch, roll, yaw)}
    }
}