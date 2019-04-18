package ims.chat.ui.controller

import android.app.Activity

import java.util.ArrayList

/**
 * 活动控制器
 * @author yangchen
 * on 2019/2/28 23:59
 */
object ActivityController {

    /**
     * 存储活动的列表
     */
    var activities: MutableList<Activity> = ArrayList()

    /**
     * 添加活动
     * @param activity 活动
     */
    fun addActivity(activity: Activity) {
        activities.add(activity)
    }

    /**
     * 移除活动
     * @param activity 活动
     */
    fun removeActivity(activity: Activity) {
        activities.remove(activity)
    }

    /**
     * 结束所有活动
     */
    fun finishAll() {
        for (activity in activities) {
            if (!activity.isFinishing) {
                activity.finish()
            }
        }
    }
}
