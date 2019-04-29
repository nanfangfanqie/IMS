package ims.chat.ui.view

import android.text.TextUtils
import cn.jpush.im.android.api.model.Conversation

import java.util.Comparator


class SortTopConvList : Comparator<Conversation> {
    override fun compare(o: Conversation, o2: Conversation): Int {
        if (!TextUtils.isEmpty(o.extra) && !TextUtils.isEmpty(o2.extra)) {
            if (Integer.parseInt(o.extra) > Integer.parseInt(o2.extra)) {
                return 1
            } else if (Integer.parseInt(o.extra) < Integer.parseInt(o2.extra)) {
                return -1
            }
        }
        return 0
    }
}
