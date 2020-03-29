package com.ngo.utils.alert

import android.app.AlertDialog
import android.content.Context
import com.ngo.ui.updatepassword.view.GetLogoutDialogCallbacks

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
    }
}
