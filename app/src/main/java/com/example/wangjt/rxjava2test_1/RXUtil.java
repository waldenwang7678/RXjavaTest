package com.example.wangjt.rxjava2test_1;

import android.os.SystemClock;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
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

    /**
     * observer 类型转化
     *
     * @param presenter
     */
    public static void map(final TextViewPresenter presenter) {

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
                        return jsonObject.optString("head", "图片地址解析错误");
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        presenter.showInfo(s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        presenter.showInfo(throwable.getMessage());
                    }
                });
    }

    /**
     * 处理多个 Observable 发射事件 ,只有前一个处理完毕后后面的才可以执行
     * 适合在网络请求 (先寻找本地缓存数据,如果没有则再请求网络数据)
     * 有执行的先后顺序 , 只有前面的 onComplete 执行后 , 后面的 Obsrvable 才能执行
     *
     * @param presenter
     */
    public static void concat(final TextViewPresenter presenter) {
        Observable<String> ob1 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final @NonNull ObservableEmitter<String> e) throws Exception {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        e.onNext("I emission immediately !"); //
                        //e.onComplete();    //如果不执行, 后面的 observable 不会执行
                    }
                }).start();

            }
        });
        Observable<String> ob2 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final @NonNull ObservableEmitter<String> e) throws Exception {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(2000);
                        e.onNext("I sleep 2 second and then immediately!");
                    }
                }).start();
            }
        });

        Observable.concat(ob1, ob2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        presenter.showInfo(s);
                    }
                });
    }

    public static void flatmap(final TextViewPresenter presenter) {
        ArrayList<String> list = new ArrayList();
        Observable.just("asd", "def")
                .flatMap(new Function<String, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(@NonNull String str) throws Exception {
                        return createObservable(str);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        presenter.showInfo(o.toString());
                    }
                });
    }

    private static Observable<String> createObservable(final String str) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext(str);
            }
        });
    }

}
