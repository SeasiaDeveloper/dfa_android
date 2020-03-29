package com.ngo.utils.alert

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class AlertDialogPolice : DialogFragment() {

    /** An interface to be implemented in the hosting activity for "OK" button click listener */
    private interface AlertPositiveListener {
        fun onPositiveClick(position: Int)
        /** This is a callback method executed when this fragment is attached to an activity.
         * This function ensures that, the hosting activity implements the interface AlertPositiveListener
         * */
        fun onAttach(activity: Activity) {
            onAttach(activity)
            context = activity
            try {
                alertPositiveListener = activity as AlertPositiveListener
            } catch (e: ClassCastException) {
                // The hosting activity does not implemented the interface AlertPositiveListener
                throw ClassCastException(activity.toString() + " must implement AlertPositiveListener")
            }
        }

        /** This is a callback method which will be executed
         * on creating this fragment
         */
        @Override
        fun onCreateDialog(savedInstanceState: Bundle): Dialog {
            /** Getting the arguments passed to this fragment */
            val bundle = Bundle()
            val position = bundle.getInt("position")
            /** Creating a builder for the alert dialog window */
            val b = AlertDialog.Builder(context)
            /** Setting a title for the window */
            b.setTitle("Choose your version")
            /** Setting items to the alert dialog */
            //   b.setSingleChoiceItems(Android.code, position, null)
            /** Setting a positive button and its listener */
            b.setPositiveButton("OK", positiveListener)
            /** Setting a positive button and its listener */
            b.setNegativeButton("Cancel", null)
            /** Creating the alert dialog window using the builder class */
            val d = b.create()
            /** Return the alert dialog window */
            return d
        }

        companion object {
            var alertPositiveListener: AlertPositiveListener? = null
            private lateinit var context: Context

            /** This is the OK button listener for the alert dialog,
             * which in turn invokes the method onPositiveClick(position)
             * of the hosting activity which is supposed to implement it
             */
            val positiveListener: DialogInterface.OnClickListener =
                DialogInterface.OnClickListener { dialog, which ->
                    val alert = dialog as AlertDialog
                    val position = alert.getListView().getCheckedItemPosition()
                    alertPositiveListener!!.onPositiveClick(position)
                }
        }
    }
}