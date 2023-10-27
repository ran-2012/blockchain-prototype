package blockchain.data;

public interface Callback<T> {

    void onResult(T result);

    void onError(Throwable error);
}
