package com.app.olx.ui.uploadPhoto

import android.Manifest
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.app.olx.BaseFragment
import com.app.olx.HomeActivity
import com.app.olx.R
import com.app.olx.ui.previewImage.PreviewImageActivity
import com.app.olx.ui.uploadPhoto.adapter.UploadImagesAdapter
import com.app.olx.utils.Constants
import com.app.olx.utils.SharedPref
import com.app.olx.utils.log
import com.app.olx.utils.onActivityResultData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_upload_photo.*
import net.alhazmy13.mediapicker.Image.ImagePicker
import java.io.File
import java.util.*

class UploadPhotoFragment : BaseFragment(), UploadImagesAdapter.AddImage, View.OnClickListener {

    private var dialog: BottomSheetDialog? = null
    private var selectedImage: File? = null
    private lateinit var outputFileUri: String
    private val selectedImagesArraylist = ArrayList<String>()
    private var imagesAdapter: UploadImagesAdapter? = null
    private lateinit var storageRef: StorageReference
    private lateinit var imageRef: StorageReference
    private lateinit var storage: FirebaseStorage
    private lateinit var uploadTask: UploadTask
    private val imageUriList = ArrayList<String>()
    private var count = 0
    private val db = FirebaseFirestore.getInstance()

    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 101

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        return inflater.inflate(R.layout.fragment_upload_photo, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recyclerView.layoutManager = GridLayoutManager(activity, 3)

        //accessing the firebase storage
        storage = FirebaseStorage.getInstance()

        //creates a storage reference
        storageRef = storage.reference

        //get Photo From Home Screen Result Observer
        getSelectedImage()

        listener()
        fetchLocation()

    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionCode
            )
            return
        }
        val task = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener {
            if (it != null) {
                currentLocation = it
            }
        }
    }

    private fun getSelectedImage() {
        (activity as HomeActivity).getOnActivityResult(onActivityResultData { bundle ->
            linearLayoutChoosePhoto.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            val mPaths = bundle?.getStringArrayList(Constants.IMAGE_PATHS)

            selectedImage = File(mPaths!![0])
            outputFileUri = mPaths[0]
            selectedImagesArraylist.add(mPaths[0])
            setAdapter()
        })

    }

    private fun listener() {
        imageViewChoosePhoto.setOnClickListener(this)
        buttonPreview.setOnClickListener(this)
        linearLayoutBack.setOnClickListener(this)
        linearLayoutNext.setOnClickListener(this)
        buttonUpload.setOnClickListener(this)
    }


    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.imageViewChoosePhoto -> {
                if (selectedImagesArraylist.size > 4) {
                    Toast.makeText(activity, "You already selected 5 photos", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    showBottomSheetDialog()

                }
            }

            R.id.buttonPreview ->
                if (selectedImage != null) {
                    log(outputFileUri)
                    startActivity(
                        Intent(activity, PreviewImageActivity::class.java).putExtra(
                            "imageuri",
                            outputFileUri
                        )
                    )
                } else
                    Toast.makeText(
                        activity,
                        "You need to add image first.",
                        Toast.LENGTH_SHORT
                    ).show()

            R.id.linearLayoutBack -> {
                fragmentManager!!.popBackStack()
            }

            R.id.linearLayoutNext -> {
                when {
                    imageUriList.size < 1 -> {
                        Toast.makeText(activity, "Please select photo", Toast.LENGTH_SHORT).show()
                    }
                    editTextAbout.text.toString().isEmpty() -> {
                        Toast.makeText(
                            activity,
                            "Please tell us About Business",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                    else -> {
                        // messageRef.push().setValue(BusinessDetailsFragment.requestJson)
                        Toast.makeText(activity, "Ad Posted Successfully", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
            R.id.buttonUpload -> {
                if (selectedImage == null || !selectedImage!!.exists()) {
                    Toast.makeText(activity, "Please select photo", Toast.LENGTH_SHORT).show()
                } else {
                    saveFileInFirebaseStorage()
                }
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        showBottomSheetDialog()
    }


    private fun setAdapter() {
        if (imagesAdapter != null)
            imagesAdapter!!.customNotify(selectedImagesArraylist)
        else {
            imagesAdapter = UploadImagesAdapter(activity!!, selectedImagesArraylist, this)
            recyclerView.adapter = imagesAdapter
        }
    }

    private fun showBottomSheetDialog() {
        val layoutInflater = activity!!.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.bottomsheet_dialog, null)
        dialog = BottomSheetDialog(activity!!)
        dialog!!.setContentView(view)
        dialog!!.window!!.findViewById<View>(R.id.design_bottom_sheet)
            .setBackgroundResource(android.R.color.transparent)
        val textViewGallery = dialog!!.findViewById<TextView>(R.id.textViewPhoto)
        val textViewCamera = dialog!!.findViewById<TextView>(R.id.textViewCamera)
        val textViewCancel = dialog!!.findViewById<TextView>(R.id.textViewCancel)
        textViewCamera!!.setOnClickListener {
            dialog!!.dismiss()
            chooseImage(ImagePicker.Mode.CAMERA)
        }
        textViewGallery!!.setOnClickListener {
            dialog!!.dismiss()
            chooseImage(ImagePicker.Mode.GALLERY)
        }
        textViewCancel!!.setOnClickListener { dialog!!.dismiss() }
        dialog!!.show()
        val lp = WindowManager.LayoutParams()
        val window = dialog!!.window
        lp.copyFrom(window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        window.attributes = lp
    }

    private fun chooseImage(mode: ImagePicker.Mode) {
        ImagePicker.Builder(activity)
            .mode(mode)
            .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
            .directory(ImagePicker.Directory.DEFAULT)
            .extension(ImagePicker.Extension.PNG)
            .allowMultipleImages(false)
            .enableDebuggingMode(true)
            .build()
    }

    override fun addPhotoClick() {
        if (selectedImagesArraylist.size > 4) {
            Toast.makeText(activity, "You already selected 5 photos", Toast.LENGTH_SHORT).show()
        } else
            showBottomSheetDialog()

    }

    private fun saveFileInFirebaseStorage() {
        showUploadProgress("Uploading Image " + (count + 1))
        val file = File(selectedImagesArraylist[count])
        uploadImage(file, file.name, count)
    }

    private fun uploadImage(file: File, name: String, i: Int) {
        //create reference to images folder and assign a name to the file that will be uploaded
        imageRef = storageRef.child("images/$name")
        //creating and showing progress dialog

        //starting upload
        uploadTask = imageRef.putFile(Uri.fromFile(file))
        // Observe state change events such as progress, pause, and resume
        uploadTask.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
            progressDialog?.incrementProgressBy(progress.toInt())
        }

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener {
                count++
                val url = it.toString()
                log(it.toString())
                imageUriList.add(url)
                progressDialog?.dismiss()
                if (count == selectedImagesArraylist.size) {
                    postAd()
                } else {
                    saveFileInFirebaseStorage()
                }
            }
        }

        uploadTask.addOnFailureListener { exception ->
            // Handle unsuccessful uploads
            Toast.makeText(activity, "Error in uploading!${exception.message}", Toast.LENGTH_SHORT)
                .show()
            progressDialog!!.dismiss()
        }
    }

    private fun postAd() {
        val docId = db.collection(arguments?.getString(Constants.KEY)!!).document().id
        val docData = hashMapOf(
            Constants.ADDRESS to arguments?.getString(Constants.ADDRESS),
            Constants.BRAND to arguments?.getString(Constants.BRAND),
            Constants.AD_DESCRIPTION to arguments?.getString(Constants.AD_DESCRIPTION),
            Constants.AD_TITLE to arguments?.getString(Constants.AD_TITLE),
            Constants.ADDRESS to arguments?.getString(Constants.ADDRESS),
            Constants.PHONE to arguments?.getString(Constants.PHONE),
            Constants.PRICE to arguments?.getString(Constants.PRICE),
            Constants.KM_DRIVEN to arguments?.getString(Constants.KM_DRIVEN),
            Constants.TYPE to arguments?.getString(Constants.KEY),
            Constants.Id to docId,
            Constants.USER_ID to SharedPref(activity!!).getString(Constants.USER_ID),
            Constants.CREATED_Date to Date(),
            "images" to imageUriList,
            "latitude" to currentLocation.latitude,
            "longitude" to currentLocation.longitude
        )

        db.collection(arguments?.getString(Constants.KEY)!!)

            .add(docData)
            .addOnSuccessListener {
                log("DocumentSnapshot successfully written!")
                log("DataId" + it.id)

                updateDocId(it.id)
            }
            .addOnFailureListener { e ->
                log("Error writing document: $e")
            }
    }

    private fun updateDocId(id: Any) {
        val docData = hashMapOf(
            Constants.Id to id
        )
        db.collection(arguments?.getString(Constants.KEY)!!)
            .document(id.toString())
            .update(docData).addOnSuccessListener {
                Toast.makeText(activity, "Ad Posted Successfully", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_upload_photo_to_my_ads)
            }
    }

}