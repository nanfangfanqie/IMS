package com.yang.crypt

import android.annotation.SuppressLint
import android.os.Build
import android.support.annotation.RequiresApi
import java.io.ByteArrayOutputStream
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.crypto.Cipher

object RSACrypt {
    private const val transfomation = "RSA"
    //每次最大加密长度
    private const val ENCRYPT_MAX_SIZE = 245
    //每次解密最大长度
    private const val DECRYPT_MAX_SIZE = 256
    val kf = KeyFactory.getInstance("RSA")
    /**
     * @param input 输入内容
     * @param privateKey 私钥
     * @return 加密结果
     */
    @SuppressLint("NewApi")
    fun encryptByPrivateKey(input: String, privateKey: PrivateKey): String {

        val byteArray = input.toByteArray()
        //临时存储空间
        var temp: ByteArray?
        var offset = 0//当前偏移位置
        //1.创建Cipher 对象
        val cipher = Cipher.getInstance(transfomation)
        //2.初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, privateKey)
        //3.加密/解密
//      val encrypt = cipher.doFinal(input.toByteArray())
        val bos = ByteArrayOutputStream()
        while (byteArray.size - offset > 0) {
            //每次最大加密245个字节
            if (byteArray.size - offset >= ENCRYPT_MAX_SIZE) {
                //剩余部分大于245
                //加密完整245
                temp = cipher.doFinal(byteArray, offset, ENCRYPT_MAX_SIZE)
                offset += ENCRYPT_MAX_SIZE
            } else {
                //加密最后一块
                temp = cipher.doFinal(byteArray, offset, byteArray.size - offset)
                //重新计算偏移的位置
                offset = byteArray.size
            }
            bos.write(temp)
        }
        bos.close()
        return Base64.getEncoder().encodeToString(bos.toByteArray())
    }

    @SuppressLint("NewApi")
    fun encryptByPublicKey(input: String, publicKey: PublicKey): String {
        val byteArray = input.toByteArray()
        var temp: ByteArray?
        var offset = 0//当前偏移的位置
        //1.创建Cipher 对象
        val cipher = Cipher.getInstance(transfomation)
        //2.初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        //3.加密/解密
//        val encrypt = cipher.doFinal(input.toByteArray())
        val bos = ByteArrayOutputStream()
        while (byteArray.size - offset > 0) {
            //每次最大加密245个字节
            if (byteArray.size - offset >= ENCRYPT_MAX_SIZE) {
                //剩余部分大于245
                //加密完整245
                temp = cipher.doFinal(byteArray, offset, ENCRYPT_MAX_SIZE)
                offset += ENCRYPT_MAX_SIZE
            } else {
                //加密最后一块
                temp = cipher.doFinal(byteArray, offset, byteArray.size - offset)
                //重新计算偏移的位置
                offset = byteArray.size
            }
            bos.write(temp)
        }
        bos.close()
        //Base64 编码
        return Base64.getEncoder().encodeToString(bos.toByteArray())
    }

    //私钥解密
    @RequiresApi(Build.VERSION_CODES.O)
    fun decryptByPrivateKey(input: String, privateKey: PrivateKey): String {
        //Base64解码
        val byteArray = Base64.getDecoder().decode(input)
        var temp: ByteArray?
        var offset = 0//当前偏移的位置
        //1.创建Cipher 对象
        val cipher = Cipher.getInstance(transfomation)
        //2.初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        //3.加密/解密
//        val encrypt = cipher.doFinal(input.toByteArray())
        val bos = ByteArrayOutputStream()
        while (byteArray.size - offset > 0) {
            //每次最大解密256个字节
            if (byteArray.size - offset >= DECRYPT_MAX_SIZE) {
                //剩余部分大于256
                //加密完整256
                temp = cipher.doFinal(byteArray, offset, DECRYPT_MAX_SIZE)
                offset += DECRYPT_MAX_SIZE
            } else {
                //解密最后一块
                temp = cipher.doFinal(byteArray, offset, byteArray.size - offset)
                //重新计算偏移的位置
                offset = byteArray.size
            }
            bos.write(temp)
        }
        bos.close()
        return String(bos.toByteArray())
    }

    //公钥解密
    @SuppressLint("NewApi")
    fun decryptByPublicKey(input: String, publicKey: PublicKey): String {
        val byteArray = Base64.getDecoder().decode(input)
        var temp: ByteArray?
        var offset = 0//当前偏移的位置
        //1.创建Cipher 对象
        val cipher = Cipher.getInstance(transfomation)
        //2.初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, publicKey)
        //3.加密/解密
//        val encrypt = cipher.doFinal(input.toByteArray())
        val bos = ByteArrayOutputStream()
        while (byteArray.size - offset > 0) {
            //每次最大加密117个字节
            if (byteArray.size - offset >= DECRYPT_MAX_SIZE) {
                //剩余部分大于117
                //加密完整117
                temp = cipher.doFinal(byteArray, offset, DECRYPT_MAX_SIZE)
                offset += DECRYPT_MAX_SIZE
            } else {
                //解密最后一块
                temp = cipher.doFinal(byteArray, offset, byteArray.size - offset)
                //重新计算偏移的位置
                offset = byteArray.size
            }
            bos.write(temp)
        }
        bos.close()
        return String(bos.toByteArray())
    }


  @SuppressLint("NewApi")
  fun getPrivateKey():PrivateKey{
      val privateKeyStr = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCGIJrd+LvXKVa3Dwzjn4k2Ilg/sL9XuoOJS/9MHoequJCm0tcwo79nIovFD3YhDteEHNCmZ9zSg9mW9Di49tcbYhBx6wgCZAQMmpJHd2j2Mb+rlql2ZU1o5UG7a3sLia9d8vR2f65/ND/l7Muzbe/w3mU+vG2evYVOYx97qQ39odecLD7g53i3DP+q5QTKsoA6NkAS8MwJl/CVVLf6Yv74FKI3uJ1pQ/o5GXzeOcS59QY+9hQYjwGQOFC4sK6OHqICX4KHDLAF1lBi3WUXMGzROIYyIzAslILgxNnUWRXRYWPD4uPWOiOKV8LjDzi10kVS/UNO0Ron5xrOsrhUf7AFAgMBAAECggEAXSeWB+ayJ3y5kkhsrkbmWGfAHPQX4MlUg+35cRefKA4pwQOcm1aPIAVZR9M2Oz2Ap2wP6hMmxjr2y6XxI12lBoq0WzpehLKMMWjFgcdIaf0hXv4z0lwHSHw5i/7ZJalJ2BleelKxkf1rB7jPvV30kLkVXl4mUGLWjBvCCPPgGK/mPisnI2uKBUFolWwIYSjIy7XZ3L61i/s91sBOuPvXX5NngaKPSWvRG3cZQotEsPvfYq3iN5EFp+xbTADIs+j1ubs3mrT4e6BthSUdbnToVkgbOswvwDq1F50gCSkzUZL0GsfwZiyWtemBAgHBJYDW9c6+ns5Im0Z5IoPYRRyW/QKBgQDDMRY9Ff47By23rVhEoT8cz1F5+W/lycbzSaJLz7oArqGVh/XAIIoe+XBUQnupyarKYgakozu37pxqfTxkpdXWviW21FoR5w49sLbsVMTELanuBA3IyRlnJgJuKmYmExfHGeiuHDQpz0hu4u+RSNFy77wYaoJ+xno1hpUPEds0ZwKBgQCv6YRy+OWfQJ21byDmlIqlHKUnituFjBrtWGVPo4F1UoF4QCQ6AvZCFj+Diyo2stjlpTySpwgESMjnTfDXAVTFJ5fJYZqOUuy23u1anxNhnsHLgfYvogJMEX7qFatZGqRmmW43EIk1EdAlP6ghuWGC/1oukScZiRToPuonwY4UswKBgCN75vkP6Hm+olcD0koXriuEnOE5sVfqyJotxq1VhBDfrkxKvShCQhnDauJGe8iv+rZz/5Uqf46+d/z762C4rRku1Bhank0m3fXlDDwGZQbs7jlwzzizJkUUKdjxKES/r9DoKt6MDgeTwS03YCnfPQRSBLXDlxmI52qXnr1wl5L9AoGBAJJbWQVHK9SJsC0J3ne5Unj5EjeKAgZkgOrDxtaxlL4F/Q4NX5acbEKjvAg3utFzj53REkp5ieP2oqA/aoeX5e16eYDlIfAT8jpb02K4QynPKjGlWSxcQEkFrXAYw00JK4rpUJejvGRZA7JZ+grE7+pLhb6EqeFG+gwYfDzDfvCdAoGAfLN1RuwGybZnjmLG/roPIVE3hEoyyCUIANQgf5oEOjg/IFJTq8MqV2gYHBxBz+ldelLLxzvocDsP45tbsembRzOPqPio55NP8RqpWF9/DKVkivvpS4OW0nb/cJ7DCc110dtGrFEH0J18JEp4bO0EfoszsJ7BNegrQtBCtULHfwQ="
      return  kf.generatePrivate(PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyStr)))
    }
    @SuppressLint("NewApi")
    fun getPublicKey():PublicKey{
        val publicKeyStr = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhiCa3fi71ylWtw8M45+JNiJYP7C/V7qDiUv/TB6HqriQptLXMKO/ZyKLxQ92IQ7XhBzQpmfc0oPZlvQ4uPbXG2IQcesIAmQEDJqSR3do9jG/q5apdmVNaOVBu2t7C4mvXfL0dn+ufzQ/5ezLs23v8N5lPrxtnr2FTmMfe6kN/aHXnCw+4Od4twz/quUEyrKAOjZAEvDMCZfwlVS3+mL++BSiN7idaUP6ORl83jnEufUGPvYUGI8BkDhQuLCujh6iAl+ChwywBdZQYt1lFzBs0TiGMiMwLJSC4MTZ1FkV0WFjw+Lj1jojilfC4w84tdJFUv1DTtEaJ+cazrK4VH+wBQIDAQAB"
        return  kf.generatePublic(X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyStr)))
    }
}


fun main(args: Array<String>) {
//    生成密钥对
    val generator = KeyPairGenerator.getInstance("RSA")
    val keyPair = generator.genKeyPair()
//    val publicKey = keyPair.public
//    val privateKey = keyPair.private

    //保存密钥对
    val publicKeyStr = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhiCa3fi71ylWtw8M45+JNiJYP7C/V7qDiUv/TB6HqriQptLXMKO/ZyKLxQ92IQ7XhBzQpmfc0oPZlvQ4uPbXG2IQcesIAmQEDJqSR3do9jG/q5apdmVNaOVBu2t7C4mvXfL0dn+ufzQ/5ezLs23v8N5lPrxtnr2FTmMfe6kN/aHXnCw+4Od4twz/quUEyrKAOjZAEvDMCZfwlVS3+mL++BSiN7idaUP6ORl83jnEufUGPvYUGI8BkDhQuLCujh6iAl+ChwywBdZQYt1lFzBs0TiGMiMwLJSC4MTZ1FkV0WFjw+Lj1jojilfC4w84tdJFUv1DTtEaJ+cazrK4VH+wBQIDAQAB"
    val privateKeyStr = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCGIJrd+LvXKVa3Dwzjn4k2Ilg/sL9XuoOJS/9MHoequJCm0tcwo79nIovFD3YhDteEHNCmZ9zSg9mW9Di49tcbYhBx6wgCZAQMmpJHd2j2Mb+rlql2ZU1o5UG7a3sLia9d8vR2f65/ND/l7Muzbe/w3mU+vG2evYVOYx97qQ39odecLD7g53i3DP+q5QTKsoA6NkAS8MwJl/CVVLf6Yv74FKI3uJ1pQ/o5GXzeOcS59QY+9hQYjwGQOFC4sK6OHqICX4KHDLAF1lBi3WUXMGzROIYyIzAslILgxNnUWRXRYWPD4uPWOiOKV8LjDzi10kVS/UNO0Ron5xrOsrhUf7AFAgMBAAECggEAXSeWB+ayJ3y5kkhsrkbmWGfAHPQX4MlUg+35cRefKA4pwQOcm1aPIAVZR9M2Oz2Ap2wP6hMmxjr2y6XxI12lBoq0WzpehLKMMWjFgcdIaf0hXv4z0lwHSHw5i/7ZJalJ2BleelKxkf1rB7jPvV30kLkVXl4mUGLWjBvCCPPgGK/mPisnI2uKBUFolWwIYSjIy7XZ3L61i/s91sBOuPvXX5NngaKPSWvRG3cZQotEsPvfYq3iN5EFp+xbTADIs+j1ubs3mrT4e6BthSUdbnToVkgbOswvwDq1F50gCSkzUZL0GsfwZiyWtemBAgHBJYDW9c6+ns5Im0Z5IoPYRRyW/QKBgQDDMRY9Ff47By23rVhEoT8cz1F5+W/lycbzSaJLz7oArqGVh/XAIIoe+XBUQnupyarKYgakozu37pxqfTxkpdXWviW21FoR5w49sLbsVMTELanuBA3IyRlnJgJuKmYmExfHGeiuHDQpz0hu4u+RSNFy77wYaoJ+xno1hpUPEds0ZwKBgQCv6YRy+OWfQJ21byDmlIqlHKUnituFjBrtWGVPo4F1UoF4QCQ6AvZCFj+Diyo2stjlpTySpwgESMjnTfDXAVTFJ5fJYZqOUuy23u1anxNhnsHLgfYvogJMEX7qFatZGqRmmW43EIk1EdAlP6ghuWGC/1oukScZiRToPuonwY4UswKBgCN75vkP6Hm+olcD0koXriuEnOE5sVfqyJotxq1VhBDfrkxKvShCQhnDauJGe8iv+rZz/5Uqf46+d/z762C4rRku1Bhank0m3fXlDDwGZQbs7jlwzzizJkUUKdjxKES/r9DoKt6MDgeTwS03YCnfPQRSBLXDlxmI52qXnr1wl5L9AoGBAJJbWQVHK9SJsC0J3ne5Unj5EjeKAgZkgOrDxtaxlL4F/Q4NX5acbEKjvAg3utFzj53REkp5ieP2oqA/aoeX5e16eYDlIfAT8jpb02K4QynPKjGlWSxcQEkFrXAYw00JK4rpUJejvGRZA7JZ+grE7+pLhb6EqeFG+gwYfDzDfvCdAoGAfLN1RuwGybZnjmLG/roPIVE3hEoyyCUIANQgf5oEOjg/IFJTq8MqV2gYHBxBz+ldelLLxzvocDsP45tbsembRzOPqPio55NP8RqpWF9/DKVkivvpS4OW0nb/cJ7DCc110dtGrFEH0J18JEp4bO0EfoszsJ7BNegrQtBCtULHfwQ="
    //将字符串转换成密钥对象
    val kf = KeyFactory.getInstance("RSA")
    val privateKey = kf.generatePrivate(PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyStr)))
    val publicKey = kf.generatePublic(X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyStr)))

    println(String(Base64.getEncoder().encode(privateKey.encoded)))
    println(String(Base64.getEncoder().encode(publicKey.encoded)))
    val input = """你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好
        |你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好
        |你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好
        |你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好
        |你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好
        |你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好
        |你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好你好""".trimMargin("|")
    val encryptByPrivateKey = RSACrypt.encryptByPrivateKey(input, privateKey)
    val encryptByPublicKey = RSACrypt.encryptByPublicKey(input, publicKey)
    println("私钥加密：$encryptByPrivateKey")
    println("公钥加密：$encryptByPublicKey")
    /*
    公钥解密私钥
    私钥解密公钥
     */
    val decryptByPrivateKey = RSACrypt.decryptByPrivateKey(encryptByPublicKey, privateKey)
    println("私钥解密: $decryptByPrivateKey")
    val decryptByPublicKey = RSACrypt.decryptByPublicKey(encryptByPrivateKey, publicKey)
    println("公钥解密：$decryptByPublicKey")

    /**
     * 公钥加密私钥解密
     * 私钥加密公钥解密
     * 公钥可公开
     * 加密速度慢
     *
     */
}