package at.cpickl.agrotlin.view

import android.content.Context
import android.app.AlertDialog
import android.content.DialogInterface.OnClickListener
import android.content.DialogInterface
import android.view.View

fun Context.showAlertOkCancelDialog(title: String,
                                    message: String,
                                    view: View,
                                    onOk: (DialogInterface) -> Unit,
                                    onCancel: ((DialogInterface) -> Unit)? = null) {
    showAlertDialog(title, message, view,
            positiveButton = Pair("Ok", onOk),
            negativeButton = Pair("Cancel", { dialog ->
                onCancel?.invoke(dialog)
                dialog.cancel()
            })
    )
}
fun Context.showAlertDialog(title: String,
                            message: String,
                            view: View? = null,
                            positiveButton: Pair<String, (DialogInterface) -> Unit>? = null,
                            negativeButton: Pair<String, (DialogInterface) -> Unit>? = null
                            ) {
    val builder = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setView(view)

    if (positiveButton != null) {
        builder.setPositiveButton(positiveButton.first, object : OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                positiveButton.second(dialog)
            }
        })
    }
    if (negativeButton != null) {
        builder.setNegativeButton(negativeButton.first, object : OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                negativeButton.second(dialog)
            }
        })
    }
    builder.show()
}