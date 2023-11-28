import blockchain.storage.StorageInternal

fun main(args: Array<String>) {
    for (i in 0..6) {
        val storage = StorageInternal(i.toString())
        storage.cleanUp()
    }
}