package ims.yang.com.ims.data.network

/**
 * @author yangchen
 * on 2019/4/7 16:48
 */
class ImsNetwork{


    companion object {
        private var network: ImsNetwork? = null
        fun getInstance(): ImsNetwork {
            if (network == null) {
                synchronized(ImsNetwork::class.java) {
                    if (network == null) {
                        network = ImsNetwork()
                    }
                }
            }
            return network!!
        }
    }
}
