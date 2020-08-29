package com.sec.datacheck.checkdata.view.fragments.displayImageFragment

import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sec.datacheck.R
import com.sec.datacheck.databinding.FragmentDisplayImageBinding
import com.squareup.picasso.Picasso
import java.io.File

class DisplayImageFragment(private val image: File) : BottomSheetDialogFragment() {


    lateinit var binding: FragmentDisplayImageBinding
    private var registerBehavior = false


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {
            val bottomSheetDialog: BottomSheetDialog = dialog as BottomSheetDialog
            setupFullHeight(bottomSheetDialog)
        }
        return dialog
    }

    /**
     * setting bottom sheet dialog to 90% height
     * of window height
     * */
    private fun setupFullHeight(sheetDialog: BottomSheetDialog) {
        try {
            val bottomSheet = sheetDialog.findViewById<FrameLayout>(R.id.design_bottom_sheet)
            if (bottomSheet != null) {
                val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
                val layoutParams = bottomSheet.layoutParams
                val windowHeight = getWindowHeight()
                if (layoutParams != null) {
                    layoutParams.height = windowHeight
                }
                bottomSheet.layoutParams = layoutParams
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                if (!registerBehavior) {
                    registerBehavior = true
                    behavior.isHideable = false
                    behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                        override fun onStateChanged(bottomSheet: View, newState: Int) {}
                        override fun onSlide(bottomSheet: View, slideOffset: Float) {
                            behavior.state = BottomSheetBehavior.STATE_EXPANDED
                            setupFullHeight(sheetDialog)
                        }
                    })
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * getting Window height
     * */
    private fun getWindowHeight(): Int {
        val displayMatrix: DisplayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMatrix)
        return displayMatrix.heightPixels
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_display_image, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            Picasso.get().load(image).into(binding.imageView)

            binding.backArrowIc.setOnClickListener {
                dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}