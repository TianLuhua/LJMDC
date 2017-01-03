package com.han.dlnaplayercontroller.rxbus;

import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.components.ActivityLifecycleProvider;
import com.trello.rxlifecycle.components.FragmentLifecycleProvider;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by tlh on 2017/1/3.
 */

public class RxBus {

    private static RxBus instance;
    //为了防止内存泄露在完成任务后需要进行退订阅操作类，继承自Subscription
    private static CompositeSubscription mCompositeSubscription;
    //PublishSubject:源码中你会发现PublishSubjet继承抽象类Subject，Subject而又继承了Observable，实现了Observer
    //所以可以说PublishSubject既是Observable，又是Observer。意思就是说既可以发送信息又可以接收信息
    private final Subject<Object, Object> _bus = new SerializedSubject<>(PublishSubject.<Object>create());

    //构造方法私有化
    private RxBus() {

    }

    //实例化RxBus
    public static RxBus getInstance() {
        if (instance == null)
            synchronized (RxBus.class) {
                if (instance == null)
                    instance = new RxBus();
                mCompositeSubscription = new CompositeSubscription();
            }
        return instance;
    }

    //发送事件的
    public void send(Object o) {
        _bus.onNext(o);
    }


    private Observable<Object> toObservable() {
        return _bus;
    }

    //判断是否存在订阅关系
    private boolean hasObservers() {
        return _bus.hasObservers();
    }

    //用于接收事件的函数函数，可以过滤掉不想接收的事件
    public void receive(Func1<Object, Boolean> func1, Action1<Object> onNext, Action1<Throwable> onError, Action0 onCompleted) {
        mCompositeSubscription.add(RxBus.getInstance().toObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).filter(func1).subscribe(onNext, onError == null ? new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
            }
        } : onError, onCompleted));

    }

    //退订阅操作接口方法
    public void unsubscribe() {
        if (instance.hasObservers() && mCompositeSubscription.isUnsubscribed())
            mCompositeSubscription.unsubscribe();
    }

    //---------------强大的分割线，下面是Rxjava和Activity以及Fragment生命周期邦定有关的-------------------------//
    public static SubscriberBuilder with(FragmentLifecycleProvider fragmentLifecycleProvider) {
        return new SubscriberBuilder(fragmentLifecycleProvider);
    }

    public static SubscriberBuilder with(ActivityLifecycleProvider activityLifecycleProvider) {
        return new SubscriberBuilder(activityLifecycleProvider);
    }

    public static class SubscriberBuilder {

        private FragmentLifecycleProvider mFragLifecycleProvider;
        private ActivityLifecycleProvider mActLifecycleProvider;
        private FragmentEvent mFragmentEndEvent;
        private ActivityEvent mActivityEndEvent;
        private int what;

        public SubscriberBuilder(FragmentLifecycleProvider provider) {
            this.mFragLifecycleProvider = provider;
        }

        public SubscriberBuilder(ActivityLifecycleProvider provider) {
            this.mActLifecycleProvider = provider;
        }

        public SubscriberBuilder what(int what) {
            this.what = what;
            return this;
        }

        public SubscriberBuilder stopEvent(FragmentEvent event) {
            this.mFragmentEndEvent = event;
            return this;
        }

        public SubscriberBuilder stopEvent(ActivityEvent event) {
            this.mActivityEndEvent = event;
            return this;
        }


        public Subscription receive(Action1<? super Object> onNext, Action1<Throwable> onError) {
            if (mFragLifecycleProvider != null) {
                return RxBus.getInstance().toObservable()
                        .compose(mFragmentEndEvent == null ? mFragLifecycleProvider.<Object>bindToLifecycle() : mFragLifecycleProvider.<Object>bindUntilEvent(mFragmentEndEvent)) // 绑定生命周期
                        //过滤 根据code判断返回事件
                        .subscribe(onNext, onError == null ? new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        } : onError);
            }
            if (mActLifecycleProvider != null) {
                return RxBus.getInstance().toObservable()
                        .compose(mActivityEndEvent == null ? mActLifecycleProvider.<Object>bindToLifecycle() : mActLifecycleProvider.<Object>bindUntilEvent(mActivityEndEvent))

                        .subscribe(onNext, onError == null ? new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        } : onError);
            }

            return null;
        }
    }

}