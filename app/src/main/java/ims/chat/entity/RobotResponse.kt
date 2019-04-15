package ims.chat.entity

import com.google.gson.annotations.SerializedName

/**
 * @author yangchen
 * on 2019/4/11 18:04
 */
class RobotResponse {

    @SerializedName("code")
    var code: String? = null
    @SerializedName("text")
    var text: String? = null

    constructor(code: String) {
        this.code = code
    }

    constructor(code: String, text: String) {
        this.code = code
        this.text = text
    }

}
