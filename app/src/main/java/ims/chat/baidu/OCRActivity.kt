/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package ims.chat.baidu

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.ClipboardManager
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.baidu.ocr.sdk.OCR
import com.baidu.ocr.sdk.OnResultListener
import com.baidu.ocr.sdk.exception.OCRError
import com.baidu.ocr.sdk.model.AccessToken
import com.baidu.ocr.ui.camera.CameraActivity
import ims.chat.R
import kotlinx.android.synthetic.main.toolbar.*

class OCRActivity : AppCompatActivity() {

    private val mPic: ImageView? = null
    private var hasGotToken = false

    private var alertDialog: AlertDialog.Builder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ocr)
        alertDialog = AlertDialog.Builder(this)
        toolbar.title = "OCR工具集"
        // 通用文字识别
        findViewById<View>(R.id.general_basic_button).setOnClickListener(View.OnClickListener {
            if (!checkTokenStatus()) {
                return@OnClickListener
            }
            val intent = Intent(this@OCRActivity, CameraActivity::class.java)
            intent.putExtra(
                CameraActivity.KEY_OUTPUT_FILE_PATH,
                FileUtil.getSaveFile(application).absolutePath
            )
            intent.putExtra(
                CameraActivity.KEY_CONTENT_TYPE,
                CameraActivity.CONTENT_TYPE_GENERAL
            )
            startActivityForResult(intent, REQUEST_CODE_GENERAL_BASIC)
        })

        // 通用文字识别(高精度版)
        findViewById<View>(R.id.accurate_basic_button).setOnClickListener(View.OnClickListener {
            if (!checkTokenStatus()) {
                return@OnClickListener
            }
            val intent = Intent(this@OCRActivity, CameraActivity::class.java)
            intent.putExtra(
                CameraActivity.KEY_OUTPUT_FILE_PATH,
                FileUtil.getSaveFile(application).absolutePath
            )
            intent.putExtra(
                CameraActivity.KEY_CONTENT_TYPE,
                CameraActivity.CONTENT_TYPE_GENERAL
            )
            startActivityForResult(intent, REQUEST_CODE_ACCURATE_BASIC)
        })

        // 通用文字识别（含位置信息版）
        findViewById<View>(R.id.general_button).setOnClickListener(View.OnClickListener {
            if (!checkTokenStatus()) {
                return@OnClickListener
            }
            val intent = Intent(this@OCRActivity, CameraActivity::class.java)
            intent.putExtra(
                CameraActivity.KEY_OUTPUT_FILE_PATH,
                FileUtil.getSaveFile(application).absolutePath
            )
            intent.putExtra(
                CameraActivity.KEY_CONTENT_TYPE,
                CameraActivity.CONTENT_TYPE_GENERAL
            )
            startActivityForResult(intent, REQUEST_CODE_GENERAL)
        })

        // 通用文字识别（含位置信息高精度版）
        findViewById<View>(R.id.accurate_button).setOnClickListener(View.OnClickListener {
            if (!checkTokenStatus()) {
                return@OnClickListener
            }
            val intent = Intent(this@OCRActivity, CameraActivity::class.java)
            intent.putExtra(
                CameraActivity.KEY_OUTPUT_FILE_PATH,
                FileUtil.getSaveFile(application).absolutePath
            )
            intent.putExtra(
                CameraActivity.KEY_CONTENT_TYPE,
                CameraActivity.CONTENT_TYPE_GENERAL
            )
            startActivityForResult(intent, REQUEST_CODE_ACCURATE)
        })

        // 通用文字识别（含生僻字版）
        findViewById<View>(R.id.general_enhance_button).setOnClickListener(View.OnClickListener {
            if (!checkTokenStatus()) {
                return@OnClickListener
            }
            val intent = Intent(this@OCRActivity, CameraActivity::class.java)
            intent.putExtra(
                CameraActivity.KEY_OUTPUT_FILE_PATH,
                FileUtil.getSaveFile(application).absolutePath
            )
            intent.putExtra(
                CameraActivity.KEY_CONTENT_TYPE,
                CameraActivity.CONTENT_TYPE_GENERAL
            )
            startActivityForResult(intent, REQUEST_CODE_GENERAL_ENHANCED)
        })

        // 网络图片识别
        findViewById<View>(R.id.general_webimage_button).setOnClickListener(View.OnClickListener {
            if (!checkTokenStatus()) {
                return@OnClickListener
            }
            val intent = Intent(this@OCRActivity, CameraActivity::class.java)
            intent.putExtra(
                CameraActivity.KEY_OUTPUT_FILE_PATH,
                FileUtil.getSaveFile(application).absolutePath
            )
            intent.putExtra(
                CameraActivity.KEY_CONTENT_TYPE,
                CameraActivity.CONTENT_TYPE_GENERAL
            )
            startActivityForResult(intent, REQUEST_CODE_GENERAL_WEBIMAGE)
        })

        // 身份证识别
        findViewById<View>(R.id.idcard_button).setOnClickListener(View.OnClickListener {
            if (!checkTokenStatus()) {
                return@OnClickListener
            }
            val intent = Intent(this@OCRActivity, IDCardActivity::class.java)
            startActivity(intent)
        })

        // 银行卡识别
        findViewById<View>(R.id.bankcard_button).setOnClickListener(View.OnClickListener {
            if (!checkTokenStatus()) {
                return@OnClickListener
            }
            val intent = Intent(this@OCRActivity, CameraActivity::class.java)
            intent.putExtra(
                CameraActivity.KEY_OUTPUT_FILE_PATH,
                FileUtil.getSaveFile(application).absolutePath
            )
            intent.putExtra(
                CameraActivity.KEY_CONTENT_TYPE,
                CameraActivity.CONTENT_TYPE_BANK_CARD
            )
            startActivityForResult(intent, REQUEST_CODE_BANKCARD)
        })

        // 行驶证识别
        findViewById<View>(R.id.vehicle_license_button).setOnClickListener(View.OnClickListener {
            if (!checkTokenStatus()) {
                return@OnClickListener
            }
            val intent = Intent(this@OCRActivity, CameraActivity::class.java)
            intent.putExtra(
                CameraActivity.KEY_OUTPUT_FILE_PATH,
                FileUtil.getSaveFile(application).absolutePath
            )
            intent.putExtra(
                CameraActivity.KEY_CONTENT_TYPE,
                CameraActivity.CONTENT_TYPE_GENERAL
            )
            startActivityForResult(intent, REQUEST_CODE_VEHICLE_LICENSE)
        })

        // 驾驶证识别
        findViewById<View>(R.id.driving_license_button).setOnClickListener(View.OnClickListener {
            if (!checkTokenStatus()) {
                return@OnClickListener
            }
            val intent = Intent(this@OCRActivity, CameraActivity::class.java)
            intent.putExtra(
                CameraActivity.KEY_OUTPUT_FILE_PATH,
                FileUtil.getSaveFile(application).absolutePath
            )
            intent.putExtra(
                CameraActivity.KEY_CONTENT_TYPE,
                CameraActivity.CONTENT_TYPE_GENERAL
            )
            startActivityForResult(intent, REQUEST_CODE_DRIVING_LICENSE)
        })

        // 车牌识别
        findViewById<View>(R.id.license_plate_button).setOnClickListener(View.OnClickListener {
            if (!checkTokenStatus()) {
                return@OnClickListener
            }
            val intent = Intent(this@OCRActivity, CameraActivity::class.java)
            intent.putExtra(
                CameraActivity.KEY_OUTPUT_FILE_PATH,
                FileUtil.getSaveFile(application).absolutePath
            )
            intent.putExtra(
                CameraActivity.KEY_CONTENT_TYPE,
                CameraActivity.CONTENT_TYPE_GENERAL
            )
            startActivityForResult(intent, REQUEST_CODE_LICENSE_PLATE)
        })

        // 营业执照识别
        findViewById<View>(R.id.business_license_button).setOnClickListener(View.OnClickListener {
            if (!checkTokenStatus()) {
                return@OnClickListener
            }
            val intent = Intent(this@OCRActivity, CameraActivity::class.java)
            intent.putExtra(
                CameraActivity.KEY_OUTPUT_FILE_PATH,
                FileUtil.getSaveFile(application).absolutePath
            )
            intent.putExtra(
                CameraActivity.KEY_CONTENT_TYPE,
                CameraActivity.CONTENT_TYPE_GENERAL
            )
            startActivityForResult(intent, REQUEST_CODE_BUSINESS_LICENSE)
        })

        // 通用票据识别
        findViewById<View>(R.id.receipt_button).setOnClickListener(View.OnClickListener {
            if (!checkTokenStatus()) {
                return@OnClickListener
            }
            val intent = Intent(this@OCRActivity, CameraActivity::class.java)
            intent.putExtra(
                CameraActivity.KEY_OUTPUT_FILE_PATH,
                FileUtil.getSaveFile(application).absolutePath
            )
            intent.putExtra(
                CameraActivity.KEY_CONTENT_TYPE,
                CameraActivity.CONTENT_TYPE_GENERAL
            )
            startActivityForResult(intent, REQUEST_CODE_RECEIPT)
        })

        // 护照识别
        findViewById<View>(R.id.passport_button).setOnClickListener(View.OnClickListener {
            if (!checkTokenStatus()) {
                return@OnClickListener
            }
            val intent = Intent(this@OCRActivity, CameraActivity::class.java)
            intent.putExtra(
                CameraActivity.KEY_OUTPUT_FILE_PATH,
                FileUtil.getSaveFile(application).absolutePath
            )
            intent.putExtra(
                CameraActivity.KEY_CONTENT_TYPE,
                CameraActivity.CONTENT_TYPE_PASSPORT
            )
            startActivityForResult(intent, REQUEST_CODE_PASSPORT)
        })

        // 二维码识别
        findViewById<View>(R.id.qrcode_button).setOnClickListener(View.OnClickListener {
            if (!checkTokenStatus()) {
                return@OnClickListener
            }
            val intent = Intent(this@OCRActivity, CameraActivity::class.java)
            intent.putExtra(
                CameraActivity.KEY_OUTPUT_FILE_PATH,
                FileUtil.getSaveFile(application).absolutePath
            )
            intent.putExtra(
                CameraActivity.KEY_CONTENT_TYPE,
                CameraActivity.CONTENT_TYPE_GENERAL
            )
            startActivityForResult(intent, REQUEST_CODE_QRCODE)
        })

        // 数字识别
        findViewById<View>(R.id.numbers_button).setOnClickListener(View.OnClickListener {
            if (!checkTokenStatus()) {
                return@OnClickListener
            }
            val intent = Intent(this@OCRActivity, CameraActivity::class.java)
            intent.putExtra(
                CameraActivity.KEY_OUTPUT_FILE_PATH,
                FileUtil.getSaveFile(application).absolutePath
            )
            intent.putExtra(
                CameraActivity.KEY_CONTENT_TYPE,
                CameraActivity.CONTENT_TYPE_GENERAL
            )
            startActivityForResult(intent, REQUEST_CODE_NUMBERS)
        })

        // 名片识别
        findViewById<View>(R.id.business_card_button).setOnClickListener(View.OnClickListener {
            if (!checkTokenStatus()) {
                return@OnClickListener
            }
            val intent = Intent(this@OCRActivity, CameraActivity::class.java)
            intent.putExtra(
                CameraActivity.KEY_OUTPUT_FILE_PATH,
                FileUtil.getSaveFile(application).absolutePath
            )
            intent.putExtra(
                CameraActivity.KEY_CONTENT_TYPE,
                CameraActivity.CONTENT_TYPE_GENERAL
            )
            startActivityForResult(intent, REQUEST_CODE_BUSINESSCARD)
        })

        // 增值税发票识别
        findViewById<View>(R.id.vat_invoice_button).setOnClickListener(View.OnClickListener {
            if (!checkTokenStatus()) {
                return@OnClickListener
            }
            val intent = Intent(this@OCRActivity, CameraActivity::class.java)
            intent.putExtra(
                CameraActivity.KEY_OUTPUT_FILE_PATH,
                FileUtil.getSaveFile(application).absolutePath
            )
            intent.putExtra(
                CameraActivity.KEY_CONTENT_TYPE,
                CameraActivity.CONTENT_TYPE_GENERAL
            )
            startActivityForResult(intent, REQUEST_CODE_VATINVOICE)
        })

        // 彩票识别
        findViewById<View>(R.id.lottery_button).setOnClickListener(View.OnClickListener {
            if (!checkTokenStatus()) {
                return@OnClickListener
            }
            val intent = Intent(this@OCRActivity, CameraActivity::class.java)
            intent.putExtra(
                CameraActivity.KEY_OUTPUT_FILE_PATH,
                FileUtil.getSaveFile(application).absolutePath
            )
            intent.putExtra(
                CameraActivity.KEY_CONTENT_TYPE,
                CameraActivity.CONTENT_TYPE_GENERAL
            )
            startActivityForResult(intent, REQUEST_CODE_LOTTERY)
        })

        // 手写识别
        findViewById<View>(R.id.handwritting_button).setOnClickListener(View.OnClickListener {
            if (!checkTokenStatus()) {
                return@OnClickListener
            }
            val intent = Intent(this@OCRActivity, CameraActivity::class.java)
            intent.putExtra(
                CameraActivity.KEY_OUTPUT_FILE_PATH,
                FileUtil.getSaveFile(application).absolutePath
            )
            intent.putExtra(
                CameraActivity.KEY_CONTENT_TYPE,
                CameraActivity.CONTENT_TYPE_GENERAL
            )
            startActivityForResult(intent, REQUEST_CODE_HANDWRITING)
        })

        // 自定义模板
        findViewById<View>(R.id.custom_button).setOnClickListener(View.OnClickListener {
            if (!checkTokenStatus()) {
                return@OnClickListener
            }
            val intent = Intent(this@OCRActivity, CameraActivity::class.java)
            intent.putExtra(
                CameraActivity.KEY_OUTPUT_FILE_PATH,
                FileUtil.getSaveFile(application).absolutePath
            )
            intent.putExtra(
                CameraActivity.KEY_CONTENT_TYPE,
                CameraActivity.CONTENT_TYPE_GENERAL
            )
            startActivityForResult(intent, REQUEST_CODE_CUSTOM)
        })


        // 初始化
        initAccessToken()
        //initAccessTokenWithAkSk();
    }

    private fun checkTokenStatus(): Boolean {
        if (!hasGotToken) {
            Toast.makeText(applicationContext, "token还未成功获取", Toast.LENGTH_LONG).show()
        }
        return hasGotToken
    }

    /**
     * 以license文件方式初始化
     */
    private fun initAccessToken() {
        OCR.getInstance(this).initAccessToken(object : OnResultListener<AccessToken> {
            override fun onResult(accessToken: AccessToken) {
                val token = accessToken.accessToken
                hasGotToken = true
            }

            override fun onError(error: OCRError) {
                error.printStackTrace()
                error.message?.let { alertText("licence方式获取token失败", it) }
            }
        }, applicationContext)
    }

    /**
     * 用明文ak，sk初始化
     */
    private fun initAccessTokenWithAkSk() {
        OCR.getInstance(this).initAccessTokenWithAkSk(object : OnResultListener<AccessToken> {
            override fun onResult(result: AccessToken) {
                val token = result.accessToken
                hasGotToken = true
            }

            override fun onError(error: OCRError) {
                error.printStackTrace()
                error.message?.let { alertText("AK，SK方式获取token失败", it) }
            }
        }, applicationContext, "请填入您的AK", "请填入您的SK")
    }

    /**
     * 自定义license的文件路径和文件名称，以license文件方式初始化
     */
    private fun initAccessTokenLicenseFile() {
        OCR.getInstance(this).initAccessToken(object : OnResultListener<AccessToken> {
            override fun onResult(accessToken: AccessToken) {
                val token = accessToken.accessToken
                hasGotToken = true
            }

            override fun onError(error: OCRError) {
                error.printStackTrace()
                error.message?.let { alertText("自定义文件路径licence方式获取token失败", it) }
            }
        }, "aip.license", applicationContext)
    }


    private fun alertText(title: String, message: String) {
        this.runOnUiThread {
            val cm =  getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager;
            // 将文本内容放到系统剪贴板里。
            cm.text = message;
            alertDialog!!.setTitle(title)
                .setMessage(message)
                .setPositiveButton("确定", null)
                .show()
        }
    }

    private fun infoPopText(result: String) {
        alertText("内容已复制到剪贴板", result)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initAccessToken()
        } else {
            Toast.makeText(applicationContext, "需要android.permission.READ_PHONE_STATE", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (null ==requestCode || null ==resultCode || null == data) return
        // 识别成功回调，通用文字识别（含位置信息）
        if (requestCode == REQUEST_CODE_GENERAL && resultCode == Activity.RESULT_OK) {
            RecognizeService.recGeneral(
                this, FileUtil.getSaveFile(applicationContext).absolutePath
            ) { result -> infoPopText(result) }
        }

        // 识别成功回调，通用文字识别（含位置信息高精度版）
        if (requestCode == REQUEST_CODE_ACCURATE && resultCode == Activity.RESULT_OK) {
            RecognizeService.recAccurate(
                this, FileUtil.getSaveFile(applicationContext).absolutePath
            ) { result -> infoPopText(result) }
        }

        // 识别成功回调，通用文字识别
        if (requestCode == REQUEST_CODE_GENERAL_BASIC && resultCode == Activity.RESULT_OK) {
            RecognizeService.recGeneralBasic(
                this, FileUtil.getSaveFile(applicationContext).absolutePath
            ) { result -> infoPopText(result) }
        }

        // 识别成功回调，通用文字识别（高精度版）
        if (requestCode == REQUEST_CODE_ACCURATE_BASIC && resultCode == Activity.RESULT_OK) {
            RecognizeService.recAccurateBasic(
                this, FileUtil.getSaveFile(applicationContext).absolutePath
            ) { result -> infoPopText(result) }
        }

        // 识别成功回调，通用文字识别（含生僻字版）
        if (requestCode == REQUEST_CODE_GENERAL_ENHANCED && resultCode == Activity.RESULT_OK) {
            RecognizeService.recGeneralEnhanced(
                this, FileUtil.getSaveFile(applicationContext).absolutePath
            ) { result -> infoPopText(result) }
        }

        // 识别成功回调，网络图片文字识别
        if (requestCode == REQUEST_CODE_GENERAL_WEBIMAGE && resultCode == Activity.RESULT_OK) {
            RecognizeService.recWebimage(
                this, FileUtil.getSaveFile(applicationContext).absolutePath
            ) { result -> infoPopText(result) }
        }

        // 识别成功回调，银行卡识别
        if (requestCode == REQUEST_CODE_BANKCARD && resultCode == Activity.RESULT_OK) {
            RecognizeService.recBankCard(
                this, FileUtil.getSaveFile(applicationContext).absolutePath
            ) { result -> infoPopText(result) }
        }

        // 识别成功回调，行驶证识别
        if (requestCode == REQUEST_CODE_VEHICLE_LICENSE && resultCode == Activity.RESULT_OK) {
            RecognizeService.recVehicleLicense(
                this, FileUtil.getSaveFile(applicationContext).absolutePath
            ) { result -> infoPopText(result) }
        }

        // 识别成功回调，驾驶证识别
        if (requestCode == REQUEST_CODE_DRIVING_LICENSE && resultCode == Activity.RESULT_OK) {
            RecognizeService.recDrivingLicense(
                this, FileUtil.getSaveFile(applicationContext).absolutePath
            ) { result -> infoPopText(result) }
        }

        // 识别成功回调，车牌识别
        if (requestCode == REQUEST_CODE_LICENSE_PLATE && resultCode == Activity.RESULT_OK) {
            RecognizeService.recLicensePlate(
                this, FileUtil.getSaveFile(applicationContext).absolutePath
            ) { result -> infoPopText(result) }
        }

        // 识别成功回调，营业执照识别
        if (requestCode == REQUEST_CODE_BUSINESS_LICENSE && resultCode == Activity.RESULT_OK) {
            RecognizeService.recBusinessLicense(
                this, FileUtil.getSaveFile(applicationContext).absolutePath
            ) { result -> infoPopText(result) }
        }

        // 识别成功回调，通用票据识别
        if (requestCode == REQUEST_CODE_RECEIPT && resultCode == Activity.RESULT_OK) {
            RecognizeService.recReceipt(
                this, FileUtil.getSaveFile(applicationContext).absolutePath
            ) { result -> infoPopText(result) }
        }

        // 识别成功回调，护照
        if (requestCode == REQUEST_CODE_PASSPORT && resultCode == Activity.RESULT_OK) {
            RecognizeService.recPassport(
                this, FileUtil.getSaveFile(applicationContext).absolutePath
            ) { result -> infoPopText(result) }
        }

        // 识别成功回调，二维码
        if (requestCode == REQUEST_CODE_QRCODE && resultCode == Activity.RESULT_OK) {
            RecognizeService.recQrcode(
                this, FileUtil.getSaveFile(applicationContext).absolutePath
            ) { result -> infoPopText(result) }
        }

        // 识别成功回调，彩票
        if (requestCode == REQUEST_CODE_LOTTERY && resultCode == Activity.RESULT_OK) {
            RecognizeService.recLottery(
                this, FileUtil.getSaveFile(applicationContext).absolutePath
            ) { result -> infoPopText(result) }
        }

        // 识别成功回调，增值税发票
        if (requestCode == REQUEST_CODE_VATINVOICE && resultCode == Activity.RESULT_OK) {
            RecognizeService.recVatInvoice(
                this, FileUtil.getSaveFile(applicationContext).absolutePath
            ) { result -> infoPopText(result) }
        }

        // 识别成功回调，数字
        if (requestCode == REQUEST_CODE_NUMBERS && resultCode == Activity.RESULT_OK) {
            RecognizeService.recNumbers(
                this, FileUtil.getSaveFile(applicationContext).absolutePath
            ) { result -> infoPopText(result) }
        }

        // 识别成功回调，手写
        if (requestCode == REQUEST_CODE_HANDWRITING && resultCode == Activity.RESULT_OK) {
            RecognizeService.recHandwriting(
                this, FileUtil.getSaveFile(applicationContext).absolutePath
            ) { result -> infoPopText(result) }
        }

        // 识别成功回调，名片
        if (requestCode == REQUEST_CODE_BUSINESSCARD && resultCode == Activity.RESULT_OK) {
            RecognizeService.recBusinessCard(
                this, FileUtil.getSaveFile(applicationContext).absolutePath
            ) { result -> infoPopText(result) }
        }

        // 识别成功回调，自定义模板
        if (requestCode == REQUEST_CODE_CUSTOM && resultCode == Activity.RESULT_OK) {
            RecognizeService.recCustom(
                this, FileUtil.getSaveFile(applicationContext).absolutePath
            ) { result -> infoPopText(result) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 释放内存资源
        OCR.getInstance(this).release()
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    companion object {

        private val REQUEST_CODE_GENERAL = 105
        private val REQUEST_CODE_GENERAL_BASIC = 106
        private val REQUEST_CODE_ACCURATE_BASIC = 107
        private val REQUEST_CODE_ACCURATE = 108
        private val REQUEST_CODE_GENERAL_ENHANCED = 109
        private val REQUEST_CODE_GENERAL_WEBIMAGE = 110
        private val REQUEST_CODE_BANKCARD = 111
        private val REQUEST_CODE_VEHICLE_LICENSE = 120
        private val REQUEST_CODE_DRIVING_LICENSE = 121
        private val REQUEST_CODE_LICENSE_PLATE = 122
        private val REQUEST_CODE_BUSINESS_LICENSE = 123
        private val REQUEST_CODE_RECEIPT = 124
        private val REQUEST_CODE_PASSPORT = 125
        private val REQUEST_CODE_NUMBERS = 126
        private val REQUEST_CODE_QRCODE = 127
        private val REQUEST_CODE_BUSINESSCARD = 128
        private val REQUEST_CODE_HANDWRITING = 129
        private val REQUEST_CODE_LOTTERY = 130
        private val REQUEST_CODE_VATINVOICE = 131
        private val REQUEST_CODE_CUSTOM = 132
    }
}
