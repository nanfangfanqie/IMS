package ims.yang.com.ims.ui

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ims.yang.com.ims.R
import kotlinx.android.synthetic.main.base_fragment.*

/**
 * @author yangchen
 * on 2019/3/10 22:58
 */
class BaseFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textView.text = arguments!!.getString("info")
        textView.setOnClickListener { v -> Snackbar.make(v, "Don't click me.please!.", Snackbar.LENGTH_SHORT).show() }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.base_fragment, container, false)
    }

    companion object {
        fun newInstance(info: String): BaseFragment {
            val args = Bundle()
            val fragment = BaseFragment()
            args.putString("info", info)
            fragment.arguments = args
            return fragment
        }
    }

}