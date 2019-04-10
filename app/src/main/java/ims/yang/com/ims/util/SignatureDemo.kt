package com.yang.crypt

import android.annotation.SuppressLint
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.util.*


/**
 * 数字签名
 */


object SignatureUtil{
    //签名
    @SuppressLint("NewApi")
    fun sign(input:String, privateKey:PrivateKey):String{
        //获取数字签名实例对象
        val signaturn = Signature.getInstance("SHA256withRSA")
        //初始化签名
        signaturn.initSign(privateKey)
        //设置数据源
        signaturn.update(input.toByteArray())
        //签名
        val sign = signaturn.sign()
        return Base64.getEncoder().encodeToString(sign)
    }

    /**
     * 校验
     */
    @SuppressLint("NewApi")
    fun verify(input: String, publicKey: PublicKey, sign:String):Boolean{
        val signaturn = Signature.getInstance("SHA256withRSA")
        //初始化签名
        signaturn.initVerify(publicKey)
        signaturn.update(input.toByteArray())
        return signaturn.verify(Base64.getDecoder().decode(sign))
    }
}
fun main(args: Array<String>) {
    val input = "你好"
    val pk = RSACrypt.getPrivateKey()
    val puk = RSACrypt.getPublicKey()
    val sign = SignatureUtil.sign(input,pk)
    val result = SignatureUtil.verify("你好",puk,sign)
    println(result)


}