package com.hms.example.dummyapplication.utils

import android.content.Context
import android.content.Context.POWER_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.HandlerThread
import android.os.PowerManager
import android.os.Process.THREAD_PRIORITY_FOREGROUND
import android.util.Log
import com.huawei.cloud.base.auth.DriveCredential
import com.huawei.cloud.base.util.StringUtils
import com.huawei.cloud.services.drive.Drive
import org.json.JSONObject


class DriveManager(val context: Context, private val credential: DriveCredential, private val listener: OnDriveEventListener?) {
    private var mWorkerThread: HandlerThread
    private var mHandler: Handler
    private var mWakeLock: PowerManager.WakeLock
    private val TAG = "DriveManager"

    init {
        val pm = context.getSystemService(POWER_SERVICE) as PowerManager?
        mWakeLock = pm!!.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, javaClass.simpleName)
        mWakeLock.acquire(360000)
        mWorkerThread = HandlerThread(javaClass.simpleName, THREAD_PRIORITY_FOREGROUND)
        mWorkerThread.priority = Thread.MAX_PRIORITY
        mWorkerThread.start()
        mHandler = Handler(mWorkerThread.looper)
        getAbout()

    }

    fun close() {
        mWorkerThread.quitSafely()
        mWakeLock.release()
    }

    private fun enqueueJob(job: Runnable) {
        mHandler.post(job)
    }

    fun getFileList() {
        enqueueJob(Runnable {
            val drive = buildDrive()
            val request = drive.files().list()
            val fileList: ArrayList<com.huawei.cloud.services.drive.model.File> = ArrayList()
            var cursor: String?
            do {
                val result = request.execute()
                for (file in result.files) {
                    fileList.add(file)
                }
                cursor = result.nextCursor
                request.cursor = cursor
            } while (!StringUtils.isNullOrEmpty(cursor))
            listener?.onFileListUpdated(fileList)
        })
    }

    private fun getAbout() {
        enqueueJob(Runnable {
            try {
                val drive: Drive = buildDrive()
                val about = drive.about()
                val response = about.get().setFields("*").execute()
                val res: String = response.toString()
                listener?.onUserInfo(res)
            } catch (e: Exception) {
                Log.e(TAG, "getInfo error: ${e.message}")
                checkException(e)
            }
        })
    }

    private fun checkException(e: Exception) {
        if(e.message!!.contains("401")){
            listener?.onAuthRequired()
        }
    }

    fun buildDrive(): Drive {
        return Drive.Builder(credential, context).build()
    }

    fun checkUpdateProtocol(about: JSONObject) {
        enqueueJob(Runnable {
            val updateValue: Any = about.get("needUpdate")
            var isNeedUpdate = false
            if (updateValue is Boolean) {
                isNeedUpdate = updateValue
            }
            if (isNeedUpdate) {
                val urlValue: Any = about.get("updateUrl")
                var url = ""
                if (urlValue is String) {
                    url = urlValue
                }
                if (url != "") {
                    val uri = Uri.parse(url)
                    if ("https" == uri.getScheme()) {
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        listener?.onUpdateRequired(intent)
                    }

                }

            }

        })
    }

    public interface OnDriveEventListener{
        fun onUpdateRequired(intent:Intent)
        fun onAuthRequired()
        fun onUserInfo(userInfo:String)
        fun onFileListUpdated(fileList: ArrayList<com.huawei.cloud.services.drive.model.File>)
    }
}