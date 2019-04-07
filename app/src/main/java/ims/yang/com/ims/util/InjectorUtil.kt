package ims.yang.com.ims.util


import ims.yang.com.ims.ui.MainModelFactory

object InjectorUtil {
    fun getMainModelFactory() = MainModelFactory(getWeatherRepository())

}