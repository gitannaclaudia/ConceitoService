package com.example.conceitoservice

import android.app.Service
import android.content.Intent
import android.os.IBinder

class LifeTimeStartedService : Service() {

    private var lifetime: Int = 0

    companion object {
        val EXTRA_LIFETIME = "EXTRA_LIFETIME"
    }

    private inner class WorkerThread : Thread() {
        var running = false

        override fun run() {
            running = true
            while (running) {
                sleep(1000)

                sendBroadcast(Intent("ACTION_RECEIVE_LIFETIME").also {
                    it.putExtra(EXTRA_LIFETIME, ++lifetime)
                })
            }
        }
    }

    private lateinit var workerThread: WorkerThread

    override fun onCreate() {
        super.onCreate()
        workerThread = WorkerThread()
    }

    /*Para serviço vinculado*/
    override fun onBind(intent: Intent): IBinder? = null

    /*Serviço instanciado. Chamado quando a Activity executa startService. Executa indefinidamente até que seja chamado o método stopSelf (serviço) ou stopService (activity)*/
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!workerThread.running) {
            workerThread.run()
        }
        return START_STICKY
    }

    /*Última função executada*/
    override fun onDestroy() {
        super.onDestroy()
        workerThread.running = false
    }

}