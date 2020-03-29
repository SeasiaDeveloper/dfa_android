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
            dialogBuilder.setMessage("Do you want to close this application ?")
                .setCancelable(false)
                .setPositiveButton("Proceed", { dialog, id ->
                    getLogoutDialogCallbacks.onClick()
                })
                .setNegativeButton("Cancel", { dialog, id ->
                    getLogoutDialogCallbacks.onCancelClick()
                })
            val alert = dialogBuilder.create()
            alert.setTitle("AlertDialogExample")
            alert.show()
        }
    }

}