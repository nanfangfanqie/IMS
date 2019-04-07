package ims.yang.com.ims.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
@author yangchen
on 2019/4/7 14:52
 */
class MainModelFactory() : ViewModelProvider.NewInstanceFactory() {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel() as T

    }
}