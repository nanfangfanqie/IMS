package ims.chat.entity

/**
 * @author yangchen
 * on 2019/4/11 17:57
 */
class RobotSend (){
    private var key: String? = null
    private var info: String? = null
    private var userid: String? = null

    override fun toString(): String {
        return "&key=$key&info=$info&userid=$userid"
    }
}
