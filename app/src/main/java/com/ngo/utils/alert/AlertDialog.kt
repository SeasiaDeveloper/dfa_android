package com.ngo.utils.alert

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.ngo.R
import com.ngo.ui.updatepassword.view.GetLogoutDialogCallbacks
import com.ngo.utils.PoliceDialogCallback

class AlertDialog {
    companion object {
        fun onShowLogoutDialog(
            context: Context,
            getLogoutDialogCallbacks: GetLogoutDialogCallbacks
        ) {
            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder.setTitle("LOGOUT")
            dialogBuilder.setMessage("Do you want to Logout?")
                .setCancelable(false)
                .setPositiveButton("Ok", { dialog, id ->
                    getLogoutDialogCallbacks.onClick()
                })
                .setNegativeButton("Cancel", { dialog, id ->
                    dialog.dismiss()
                })
            val alert = dialogBuilder.create()
            alert.show()
        }
        fun showDialog(title: String,context: PoliceDialogCallback,mContext:Context) {
            val dialog = Dialog(mContext)
            dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog .setCancelable(false)
            dialog .setContentView(R.layout.custom_dialog_police)
            val contactNumber = dialog .findViewById(R.id.tvContact) as TextView
            val tvDate = dialog .findViewById(R.id.tvDate) as TextView
            val tvTime = dialog .findViewById(R.id.tvTime) as TextView
            val btnAccept = dialog .findViewById(R.id.btnAccept) as Button
            val btnReject = dialog .findViewById(R.id.btnReject) as Button
            val btnOpen = dialog .findViewById(R.id.btnOpen) as Button

            contactNumber.text = "Syall"
            tvDate.text="romy"
            tvTime.text="syalll"

            btnAccept.setOnClickListener {
                dialog .dismiss()
                context.onAcceptClick()
            }
            btnReject.setOnClickListener {
                dialog .dismiss()
                context.onRejectClick()
            }
            btnOpen.setOnClickListener {
                dialog .dismiss()
                context.onOpenClick()
            }
            dialog .show()

            val width = (mContext.getResources().getDisplayMetrics().widthPixels * 0.80) as Int
            val height = (mContext.getResources().getDisplayMetrics().heightPixels * 0.90) as Int

            dialog.getWindow()!!.setLayout(width, height)

        }
    }


}
