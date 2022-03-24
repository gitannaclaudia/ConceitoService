package com.example.conceitoservice

import android.content.*
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import com.example.conceitoservice.LifeTimeStartedService.Companion.EXTRA_LIFETIME
import com.example.conceitoservice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val activityMainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val lifeTimeServiceIntent: Intent by lazy {
        Intent(this, LifetimeBoundService::class.java)
    }

    private lateinit var lifetimeBoundService: LifetimeBoundService
    private var connected = false
    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
            lifetimeBoundService =
                (binder as LifetimeBoundService.LifetimeBoundServiceBinder).getService()
            connected = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            connected = false
        }
    }

    private inner class LifetimeServiceHandler(lifetimeServiceLooper: Looper) :
        Handler(lifetimeServiceLooper) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (connected) {
                runOnUiThread {
                    activityMainBinding.serviceLifetimeTv.text = lifetimeBoundService.lifetime.toString()
                }
                obtainMessage().also {
                    sendMessageDelayed(it, 1000)
                }
            }
        }
    }
    private lateinit var lifetimeServiceHandler: LifetimeServiceHandler

//    private val receivedLifetimeBr: BroadcastReceiver by lazy {
//        object: BroadcastReceiver() {
//            override fun onReceive(p0: Context?, p1: Intent?) {
//                intent?.getIntExtra(EXTRA_LIFETIME, 0).also { lifetime ->
//                    activityMainBinding.serviceLifetimeTv.text = lifetime.toString()
//                }
//            }
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)
        HandlerThread("LifetimeHandlerThread").apply {
            start()
            lifetimeServiceHandler = LifetimeServiceHandler(looper)
        }

        with(activityMainBinding) {
            iniciarServicoBt.setOnClickListener {
                bindService(lifeTimeServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
//                startService(lifeTimeServiceIntent)
                lifetimeServiceHandler.obtainMessage().also {
                    lifetimeServiceHandler.sendMessageDelayed(it, 1000)
                }
            }
            finalizarServicoBt.setOnClickListener {
                unbindService(serviceConnection)
                connected = false
//                stopService(lifeTimeServiceIntent)
            }
        }
    }

//    override fun onStart() {
//        super.onStart()
////        registerReceiver(receivedLifetimeBr,
////            IntentFilter("ACTION_RECEIVE_LIFETIME")
////        )
//    }
//
//    override fun onStop() {
//        super.onStop()
////        unregisterReceiver(receivedLifetimeBr)
//    }
}