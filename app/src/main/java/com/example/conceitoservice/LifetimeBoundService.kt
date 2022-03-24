package com.example.conceitoservice

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class LifetimeBoundService : Service() {

    var lifetime: Int = 0
    private set

    private inner class WorkerThread : Thread() {
        var running = false

        override fun run() {
            running = true
            while (running) {
                sleep(1000)
                ++lifetime
            }
        }
    }

    private lateinit var workerThread: WorkerThread

    inner class LifetimeBoundServiceBinder : Binder() {
        fun getService() = this@LifetimeBoundService
    }

    private val lifetimeBoundServiceBinder = LifetimeBoundServiceBinder()

    override fun onCreate() {
        super.onCreate()
        workerThread = WorkerThread()
    }

    override fun onDestroy() {
        super.onDestroy()
        workerThread.running = false
    }

    override fun onBind(intent: Intent): IBinder {
        if (!workerThread.running) {
            workerThread.run()
        }
        return lifetimeBoundServiceBinder
    }
}