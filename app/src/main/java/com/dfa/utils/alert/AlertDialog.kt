package com.dfa.utils.alert

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.dfa.R
import com.dfa.ui.login.view.LoginActivity
import com.dfa.ui.updatepassword.view.GetLogoutDialogCallbacks
import com.dfa.utils.PoliceDialogCallback

class AlertDialog {
    companion object {
        fun onShowLogoutDialog(
            context: Context,
            getLogoutDialogCallbacks: GetLogoutDialogCallbacks
        ) {
            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder.setTitle("Logout")
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




        fun guesDialog(context:Context) {
            var dialog = Dialog(context!!)
            dialog.setContentView(R.layout.guest_dialog);
            dialog.setCanceledOnTouchOutside(false)
            dialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent);
            var btnLogout = dialog.findViewById<TextView>(R.id.tv_delete)
            var btnCancel = dialog.findViewById<TextView>(R.id.tv_cancel)
            btnLogout.setOnClickListener {
                dialog.dismiss()
                var intent = Intent(context, LoginActivity::class.java)
                intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or
                            Intent.FLAG_ACTIVITY_NEW_TASK
                )
                context.startActivity(intent)

            }
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }


    }


}
