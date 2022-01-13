package caseopening.util;

public interface CallableChanger<T> {

    T call();
    void change(T t);

}
