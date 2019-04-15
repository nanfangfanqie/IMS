package com.yang.crypt

import java.lang.StringBuilder
import java.security.MessageDigest

/**
 * 消息摘要
 * 常用算法：MD5 ，sha1，sha256
 * 特点，不可逆
 */

object MessageDigetUtil {
    /**
     * 生成32位MD5码
     */
    fun md5(input: String): String {
        val md5 = MessageDigest.getInstance("MD5").digest(input.toByteArray())
//        println(md5.size) //加密后16个字节 转成16进制  32个
        return toHex(md5)

    }

    fun sha1(input: String): String {
        val sha1 = MessageDigest.getInstance("SHA-1").digest(input.toByteArray())
//        println(sha1.size)//20个字节 转成16进制  40
        //转成16进制并返回
        return toHex(sha1)
    }

    fun sha256(input: String): String {
        val sha256 = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
//        println(sha256.size)//32个字节 转成16进制  64
        //转成16进制并返回
        return toHex(sha256)
    }

    /**
     * 16进制转换
     */
    private fun toHex(byteArray: ByteArray): String {
        //转成16进制
        return with(StringBuilder()) {
            byteArray.forEach {
                //拼接结果
                val hex = it.toInt() and (0xFF)
                val hexStr = Integer.toHexString(hex)
                if (hexStr.length == 1) {
                    append(0).append(hexStr)
                } else {
                    append(hexStr)
                }
            }
            this.toString()
        }
    }
}

//fun main(args: Array<String>) {
//    val input = "你好，世界，你好，世界，你好，世界"
//    println(MessageDigetUtil.md5(input))
//    println(MessageDigetUtil.sha1(input))
//    println(MessageDigetUtil.sha256(input))
//}