package blockchain.network.server

import blockchain.network.Network.Callback
import blockchain.utility.Log
import kotlinx.coroutines.CoroutineScope

open class BaseController(protected val scope: CoroutineScope, protected val callback: Callback) {
    protected val log = Log.get(this)
}