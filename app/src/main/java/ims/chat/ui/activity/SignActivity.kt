package ims.chat.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView

import ims.chat.R
import ims.chat.ui.activity.ChatDetailActivity
import ims.chat.ui.activity.IBaseActivity
import ims.chat.ui.activity.PersonalActivity


class SignActivity : IBaseActivity() {

    private var mEd_sign: EditText? = null
    private var mTv_count: TextView? = null
    private var mLl_nickSign: LinearLayout? = null
//    private var mJmui_commit_btn: Button? = null
    internal var input: Int = 0

     @SuppressLint("WrongConstant")
     override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nick_sign)

        initView()
        val intent = intent
        if (intent.flags == PersonalActivity.FLAGS_SIGN) {
            initViewSign("个性签名", SIGN_COUNT)
            initData(SIGN_COUNT)
        } else if (intent.flags == PersonalActivity.FLAGS_NICK) {
            initViewNick("修改昵称", NICK_COUNT)
            initData(NICK_COUNT)
        } else if (intent.flags == ChatDetailActivity.FLAGS_GROUP_DESC) {
            initViewSign("群描述", GROUP_DESC)
            initData(GROUP_DESC)
        } else if (intent.flags == ChatDetailActivity.FLAGS_GROUP_NAME) {
            initViewNick("群名称", GROUP_NAME)
            initData(GROUP_NAME)
        }
        initListener(intent.getFlags())
    }

    private fun initListener(flags: Int) {
        mJmui_commit_btn!!.setOnClickListener {
            val sign = mEd_sign!!.text.toString()
            val intent = Intent()
            if (flags == PersonalActivity.FLAGS_NICK) {//3
                intent.putExtra(PersonalActivity.NICK_NAME_KEY, sign)
                setResult(PersonalActivity.NICK_NAME, intent)//4
            } else if (flags == PersonalActivity.FLAGS_SIGN) {//2
                intent.putExtra(PersonalActivity.SIGN_KEY, sign)
                setResult(PersonalActivity.SIGN, intent)//1

            } else if (flags == ChatDetailActivity.FLAGS_GROUP_DESC) {//71
                intent.putExtra(ChatDetailActivity.GROUP_DESC_KEY, sign)
                setResult(ChatDetailActivity.GROUP_DESC, intent)//70

            } else if (flags == ChatDetailActivity.FLAGS_GROUP_NAME) {//73
                intent.putExtra(ChatDetailActivity.GROUP_NAME_KEY, sign)
                setResult(ChatDetailActivity.GROUP_NAME, intent)//72

            }
            finish()
        }
    }

    private fun initData(countNum: Int) {
        mEd_sign!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                input = s.toString().substring(start).toByteArray().size
            }

            override fun afterTextChanged(s: Editable) {
                val num = countNum - s.toString().toByteArray().size
                mTv_count!!.text = num.toString() + ""
            }
        })
    }

    private fun initView() {
        mEd_sign = findViewById(R.id.ed_sign) as EditText
        mLl_nickSign = findViewById(R.id.ll_nickSign) as LinearLayout
        mTv_count = findViewById(R.id.tv_count) as TextView
        this.mJmui_commit_btn = findViewById(R.id.jmui_commit_btn) as Button

        if (intent.getStringExtra("group_name") != null) {
            mEd_sign!!.setText(getIntent().getStringExtra("group_name"))
        }
        if (intent.getStringExtra("group_desc") != null) {
            mEd_sign!!.setText(getIntent().getStringExtra("group_desc"))
        }
        if (intent.getStringExtra("old_nick") != null) {
            mEd_sign!!.setText(getIntent().getStringExtra("old_nick"))
        }
        if (intent.getStringExtra("old_sign") != null) {
            mEd_sign!!.setText(getIntent().getStringExtra("old_sign"))
        }

        mEd_sign!!.setSelection(mEd_sign!!.text.length)

    }

    private fun initViewSign(str: String, flag: Int) {
        initTitle(true, true, str, "", true, "完成")
        //限制输入的最大长度
        mEd_sign!!.filters = arrayOf<InputFilter>(MyLengthFilter(flag))
        //如果初始有昵称/签名,控制右下字符数
        val length = mEd_sign!!.text.toString().toByteArray().size
        mTv_count!!.text = (flag - length).toString() + ""
    }

    private fun initViewNick(str: String, flag: Int) {
        initTitle(true, true, str, "", true, "完成")
        mEd_sign!!.filters = arrayOf<InputFilter>(MyLengthFilter(flag))
        val length = mEd_sign!!.text.toString().toByteArray().size
        mTv_count!!.text = (flag - length).toString() + ""


        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val params = LinearLayout.LayoutParams(width, height)
        mLl_nickSign!!.layoutParams = params
    }

    class MyLengthFilter(
        /**
         * @return the maximum length enforced by this input filter
         */
        val max: Int
    ) : InputFilter {

        override fun filter(
            source: CharSequence, start: Int, end: Int, dest: Spanned,
            dstart: Int, dend: Int
        ): CharSequence? {
            val keep = max - (dest.toString().toByteArray().size - (dend - dstart))
            return if (keep <= 0) {
                ""
            } else if (keep >= source.toString().toByteArray().size) {
                null // keep original
            } else {
                ""
            }
        }
    }

    companion object {
        private const val SIGN_COUNT = 250
        private const val NICK_COUNT = 64

        private const val GROUP_DESC = 250
        private const val GROUP_NAME = 64
    }
}
