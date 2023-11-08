package blockchain.network.server

import blockchain.network.INetwork.Callback
import blockchain.utility.Log
import kotlinx.coroutines.CoroutineScope

open class BaseController(protected val scope: CoroutineScope, public var callback: Callback) {
    protected val log = Log.get(this)


}