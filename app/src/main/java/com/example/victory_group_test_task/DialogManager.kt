import android.app.AlertDialog
import android.content.Context
import com.example.victory_group_test_task.R

object DialogManager {
    fun locationSettingsDialog(context: Context, listener: Listener){
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle(context.getString(R.string.enable_location))
        dialog.setMessage(context.getString(R.string.question))
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok)){ _,_ ->
            listener.onClick()
            dialog.dismiss()
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.cancel)){ _,_ ->
            dialog.dismiss()
        }
        dialog.show()
    }
    interface Listener{
        fun onClick()
    }
}