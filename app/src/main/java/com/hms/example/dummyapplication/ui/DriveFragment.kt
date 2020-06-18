package com.hms.example.dummyapplication.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hms.example.dummyapplication.R
import com.hms.example.dummyapplication.adapter.FileAdapter
import com.hms.example.dummyapplication.utils.DriveManager
import com.hms.example.dummyapplication.utils.LoadingDialog
import com.huawei.cloud.base.auth.DriveCredential
import com.huawei.cloud.base.util.StringUtils
import com.huawei.cloud.services.drive.DriveScopes
import com.huawei.cloud.services.drive.model.File
import com.huawei.hms.support.api.entity.auth.Scope
import com.huawei.hms.support.hwid.HuaweiIdAuthAPIManager
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper
import java.util.*
import kotlin.collections.ArrayList


class DriveFragment : Fragment(), DriveManager.OnDriveEventListener,
    SwipeRefreshLayout.OnRefreshListener, FileAdapter.FileViewHolder.OnFileItemListener {

    private val REQUEST_SIGN_IN_LOGIN = 8888
    private val TAG = "DriveFragment"
    lateinit var driveManager: DriveManager
    private lateinit var loadingDialog: AlertDialog
    private lateinit var fileAdapter: FileAdapter
    private lateinit var refreshLayout:SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_drive, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog=LoadingDialog.createDialog(requireContext())
        loadingDialog.show()
        refreshLayout=view.findViewById(R.id.swipe_layout)
        refreshLayout.setOnRefreshListener(this)
        fileAdapter= FileAdapter(ArrayList(),this)
        recyclerView=view.findViewById<RecyclerView>(R.id.recycler_view).apply{
            layoutManager=LinearLayoutManager(requireContext())
            adapter=fileAdapter
        }
        performLogin()
        /*val filesDir = requireContext().filesDir
        val file = File("$filesDir/polygons.json")
        if (!file.exists()) {
            file.createNewFile()
        }*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        driveManager.close()
    }

    private fun onCredentialCreated(credential: DriveCredential){
        Log.e(TAG,"onCredentialCreated()")
        driveManager= DriveManager(requireContext(),credential,this)
        loadingDialog=LoadingDialog.createDialog(requireContext(),titleId =R.string.user_info )
        loadingDialog.show()


    }

    fun performLogin(){
        val params =
            HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setAccessToken()
                .setIdToken()
                .setScopeList(getScopes())
                .createParams()
        val service = HuaweiIdAuthManager.getService(requireContext(), params)
        startActivityForResult(service.signInIntent, REQUEST_SIGN_IN_LOGIN)
    }

    private fun getScopes(): MutableList<Scope> {
        val scopeList: MutableList<Scope> = LinkedList()
        scopeList.add(HuaweiIdAuthAPIManager.HUAWEIID_BASE_SCOPE) // Basic account permissions

        scopeList.add(Scope(DriveScopes.SCOPE_DRIVE)) // All permissions, except permissions for the app folder.

        scopeList.add(Scope(DriveScopes.SCOPE_DRIVE_FILE)) // Permissions to view and manage files.

        scopeList.add(Scope(DriveScopes.SCOPE_DRIVE_METADATA)) // Permissions to view and manage file metadata, excluding file content.

        scopeList.add(Scope(DriveScopes.SCOPE_DRIVE_METADATA_READONLY)) // Permissions only for viewing file metadata, excluding file entities.

        scopeList.add(Scope(DriveScopes.SCOPE_DRIVE_READONLY)) // Permissions to view file metadata and content.

        scopeList.add(Scope(DriveScopes.SCOPE_DRIVE_APPDATA)) // Permissions to view and manage app files.
        return scopeList

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(loadingDialog.isShowing){
            loadingDialog.dismiss()
        }
        Log.e(
            TAG,
            "onActivityResult, requestCode = " + requestCode + ", resultCode = " + resultCode
        );
        if (requestCode == REQUEST_SIGN_IN_LOGIN) {
            val authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data)
            if (authHuaweiIdTask.isSuccessful) {
                val huaweiAccount = authHuaweiIdTask.result
                val accessToken = huaweiAccount.accessToken
                val unionId = huaweiAccount.unionId
                if (StringUtils.isNullOrEmpty(unionId) || StringUtils.isNullOrEmpty(accessToken)){
                    //Login failed
                }
                else {
                    createCredential(unionId,accessToken)
                }

            } else {
                Log.d(
                    TAG,
                    "onActivityResult, signIn failed: " + authHuaweiIdTask.exception
                );
                Toast.makeText(
                    requireContext(),
                    "onActivityResult, signIn failed.",
                    Toast.LENGTH_LONG
                ).show()
            }

        }
    }



    private fun createCredential(unionId: String, at: String){
        val builder = DriveCredential.Builder(unionId, DriveCredential.AccessMethod { at })
        val driveCredential= builder.build().setAccessToken(at)
        onCredentialCreated(driveCredential)
    }

    override fun onUpdateRequired(intent: Intent) {
        try{
            startActivity(intent)
        }catch (e:ClassNotFoundException){
            Log.e(TAG,e.toString())
        }
    }

    override fun onAuthRequired() {
        performLogin()
    }

    override fun onUserInfo(userInfo: String) {
        if(loadingDialog.isShowing)
            loadingDialog.dismiss()
        //Do something with the info
        Log.i(TAG, "User Info: $userInfo")
        //Get the file list
        loadingDialog=LoadingDialog.createDialog(requireContext(),titleId = R.string.user_files)
        loadingDialog.show()
        driveManager.getFileList()
    }

    override fun onFileListUpdated(fileList: ArrayList<File>) {
        if(loadingDialog.isShowing){
            loadingDialog.dismiss()
        }
        if(refreshLayout.isRefreshing) refreshLayout.isRefreshing=false
        fileAdapter.fileList.addAll(fileList)
        requireActivity().runOnUiThread { fileAdapter.notifyDataSetChanged() }
    }

    override fun onRefresh() {
        driveManager.getFileList()
    }

    override fun onDownloadRequest(position: Int) {

    }

}