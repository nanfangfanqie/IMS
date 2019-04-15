package ims.chat.utils

import android.content.Context
import android.widget.Toast

/**Toast
 * @author yangchen
 * on 2019/3/1 2:48
 */
object MyToast{
    fun toastLong(context: Context, string: String) {
        Toast.makeText(context, string, Toast.LENGTH_LONG).show()
    }
    fun toastShort(context: Context, string: String) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
    }

}
