package com.example.wangjt.rxjava2test_1;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wangjt on 2017/8/1.
 * rxjava demo
 * intention of scheduler change:
 * subscribeOn - only the setting of first time  is userful
 * observabeOn - every setting is userful for later scheduler
 */

public class RXUtil {

    private static Disposable subscribe;

    /**
     * 可用在网络请求里面,线程切换比较方便
     *
     * @param presenter
     */
    public static void observable(final TextViewPresenter presenter) {
        Observable.create(
                new ObservableOnSubscribe<String>() {  //被观察者 泛型返回的数据
                    @Override
                    public void subscribe(final @NonNull ObservableEmitter<String> e) throws Exception {
                        String url = "http://walden-wang.cn/API/api1.php?uid=6";
                        HttpUtil.get(url, new HttpUtil.CallBack() {
                            @Override
                            public void success(String str) {
                                e.onNext(str);
                            }

                            @Override
                            public void fail(String str) {
                                e.onNext(str);
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .doOnNext(new Consumer<String>() {  // 消费者/观察者 接收数据之前 可以执行的步骤
                    @Override
                    public void accept(@NonNull String integer) throws Exception {

                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String str) {
                        try {
                            JSONObject strJson = new JSONObject(str);
                            String name = strJson.optString("name", "未知");
                            String describe = strJson.optString("describe", "未知");
                            presenter.showInfo(name + " : " + describe);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 轮询执行 可用在首页 viewpager轮询切换
     *
     * @param presenter
     */
    public static void intervalImp(final TextViewPresenter presenter) {
        subscribe = Flowable.interval(3, 2, TimeUnit.SECONDS)
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        Log.d("asdasd", "doOnNext : " + aLong);
                    }
                })
                .subscribeOn(Schedulers.io())               // 被观察者执行所在线程
                .observeOn(AndroidSchedulers.mainThread())  // 观察者接收数据的线程
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        Log.d("asdasd", "subscribe : " + aLong);
                        presenter.showInfo("" + aLong);
                    }
                });
    }

    public static void intervalCancle(TextViewPresenter presenter) {
        if (subscribe != null) {
            subscribe.dispose();
            presenter.showInfo("interval 已取消");
        }
    }

    public static void map(TextViewPresenter presenter) {

        Observable.create(
                new ObservableOnSubscribe<JSONObject>() {
                    @Override
                    public void subscribe(final @NonNull ObservableEmitter<JSONObject> e) throws Exception {
                        String url = "http://walden-wang.cn/API/api1.php?uid=6";
                        HttpUtil.getJson(url, new HttpUtil.CallBack2() {
                            @Override
                            public void success(JSONObject is) {
                                e.onNext(is);
                            }

                            @Override
                            public void fail(JSONObject str) {
                                e.onNext(str);
                            }
                        });
                    }
                })
                .map(new Function<JSONObject, String>() {
                    @Override
                    public String apply(@NonNull JSONObject jsonObject) throws Exception {
                        jsonObject.optString("",)

                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {

                    }
                })
        ;


    }

}
