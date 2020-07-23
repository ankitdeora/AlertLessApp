package com.example.alertless.utils;

import com.example.alertless.database.AppDatabase;
import com.example.alertless.exceptions.AlertlessDatabaseException;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class DBUtils {

    private static ExecutorService executor = Executors.newFixedThreadPool(10);


    public static <T> void executeTask(Consumer<T> consumer, T val, String errMsg) throws AlertlessDatabaseException{
        Runnable task = getDBRunnableTask(consumer, val);
        try {
            executeTask(task);
        } catch (Exception e) {
            String exceptionMsg = String.format(errMsg + " due to : %s",  e.getMessage());
            throw new AlertlessDatabaseException(exceptionMsg, e);
        }
    }

    public  static <T> T executeTaskAndGet(Supplier<T> supplier, String errMsg) throws AlertlessDatabaseException {
        Callable<T> task = getDBCallableTask(supplier);
        return executeTaskAndGet(task, errMsg);
    }

    public static <T,R> R executeTaskAndGet(Function<T,R> fn, T val, String errMsg) throws AlertlessDatabaseException {
        Supplier<R> supplier = () -> fn.apply(val);
        return executeTaskAndGet(supplier, errMsg);
    }

    public static <T,U,R> R executeTaskAndGet(BiFunction<T, U, R> biFn, T inputA, U inputB, String errMsg) throws AlertlessDatabaseException {
        Supplier<R> supplier = () -> biFn.apply(inputA, inputB);
        return executeTaskAndGet(supplier, errMsg);
    }

    public static <T,U> void executeTask(BiConsumer<T, U> biConsumer, T inputA, U inputB, String errMsg) throws AlertlessDatabaseException {
        Runnable dbRunnableTask = getDBRunnableTask(biConsumer, inputA, inputB);

        try {
            executeTask(dbRunnableTask);
        } catch (Exception e) {
            String exceptionMsg = String.format(errMsg + " due to : %s",  e.getMessage());
            throw new AlertlessDatabaseException(exceptionMsg, e);
        }
    }

    private static void executeTask(Runnable task) {
        AppDatabase.databaseWriteExecutor.execute(task);
    }

    private static  <T> T executeTaskAndGet(Callable<T> task, String errMsg) throws AlertlessDatabaseException {
        Future<T> future = AppDatabase.databaseWriteExecutor.submit(task);

        T t = null;
        try {
            t = future.get();
        } catch (Exception e) {
            String exceptionMsg = String.format(errMsg + " due to : %s",  e.getMessage());
            throw new AlertlessDatabaseException(exceptionMsg, e);
        }

        return t;
    }

    public static <T> Runnable getDBRunnableTask(Consumer<T> consumer, T val) {
        return () -> consumer.accept(val);
    }

    public static <T> Callable<T> getDBCallableTask(Supplier<T> supplier) {
        return () -> supplier.get();
    }

    public static <T, R> Callable<R> getDBTask(Function<T,R> fn, T val) {
        return getDBCallableTask(() -> fn.apply(val));
    }

    public static <T,U> Runnable getDBRunnableTask(BiConsumer<T,U> biConsumer, T inputA, U inputB) {
        return () -> biConsumer.accept(inputA, inputB);
    }
}

