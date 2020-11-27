package com.aman.android.rxandroid.ui

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.aman.android.rxandroid.R
import com.aman.android.rxandroid.model.ApiUser
import com.aman.android.rxandroid.model.Shop
import com.aman.android.rxandroid.model.User
import com.aman.android.rxandroid.utils.Utils
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.AsyncSubject
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.ReplaySubject
import java.util.concurrent.TimeUnit

class OperatorsActivity : AppCompatActivity() {
    private val disposables = CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_operators)
    }

    fun startSimple(view: View)  {
        val getObserver = object : Observer<String> {

            override fun onSubscribe(d: Disposable) {
                Log.d("RXOperator", "###################################")
                Log.d("RXOperator", "Simple onSubscribe : " + d.isDisposed)
            }

            override fun onNext(value: String) {
                Log.d("RXOperator", " onNext : value : $value\n")
            }

            override fun onError(e: Throwable) {
                Log.d("RXOperator", " onError : " + e.message + "\n")
            }

            override fun onComplete() {
                Log.d("RXOperator", " onComplete" + "\n")
                Log.d("RXOperator", "###################################")
            }
        }
        Observable.just("RxJava", "RxAndroid")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(getObserver)
    }

    fun startMap(view: View) {
        val observable = Observable.create<List<ApiUser>> { e ->
            if (!e.isDisposed) {
                e.onNext(Utils().getApiUserList())
                e.onComplete()
            }
        }

        val observer = object : Observer<List<User>> {

            override fun onSubscribe(d: Disposable) {
                Log.d("RXOperator", "###################################")
                Log.d("RXOperator", "Map onSubscribe : " + d.isDisposed)
            }

            override fun onNext(userList: List<User>) {
                Log.d("RXOperator", " onNext" + "\n")
                for (user in userList) {
                    Log.d("RXOperator", " login Name : " + user.login + "\n")
                }
                Log.d("RXOperator", " onNext : " + userList.size)
            }

            override fun onError(e: Throwable) {
                Log.d("RXOperator", " onError : " + e.message + "\n")
            }

            override fun onComplete() {
                Log.d("RXOperator", " onComplete" + "\n")
            }
        }

        observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { apiUser -> Utils().convertApiUserListToUserList(apiUser) }
            .subscribe(observer)
    }

    fun startZip(view: View) {
        val getKotlinFansObservable = Observable.create<List<User>> { e ->
            if (!e.isDisposed) {
                e.onNext(Utils().getUserListWhoLovesKotlin())
                e.onComplete()
            }
        }

        val getJavaFansObservable = Observable.create<List<User>> { e ->
            if (!e.isDisposed) {
                e.onNext(Utils().getUserListWhoLovesJava())
                e.onComplete()
            }
        }

        val observer = object : Observer<List<User>> {

            override fun onSubscribe(d: Disposable) {
                Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
            }

            override fun onNext(userList: List<User>) {
                Log.d("RXOperator", " onNext" + "\n")
                for (user in userList) {
                    Log.d("RXOperator", " loginName : " + user.login + "\n")
                }
                Log.d("RXOperator", " onNext : " + userList.size)
            }

            override fun onError(e: Throwable) {
                Log.d("RXOperator", " onError : " + e.message + "\n")
            }

            override fun onComplete() {
                Log.d("RXOperator", " onComplete" + "\n")
            }
        }

        Observable.zip(
            getKotlinFansObservable,
            getJavaFansObservable,
            BiFunction<List<User>, List<User>, List<User>> { kotlinFans, javaFans ->
                Utils().filterUserWhoLovesBoth(
                    kotlinFans,
                    javaFans
                )
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }

    fun startDisposable(view: View) {
        val observable = Observable.defer {
            SystemClock.sleep(2000)
            Observable.just("Firebase", "Fabric", "Analytics", "Github", "LinkedIn")
        }

        disposables.add(
            observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<String>() {
                    override fun onComplete() {
                        Log.d("RXOperator", " onComplete" + "\n")
                    }

                    override fun onError(e: Throwable) {
                        Log.d("RXOperator", " onError : " + e.message + "\n")
                    }

                    override fun onNext(value: String) {
                        Log.d("RXOperator", " onNext value : " + value + "\n")
                    }
                })
        )
    }

    fun startTake(view: View) {
        val observable = Observable.just(1, 2, 3, 4, 5)

        val observer = object : Observer<Int> {
            override fun onSubscribe(d: Disposable) {
                Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
            }

            override fun onNext(value: Int) {
                Log.d("RXOperator", " onNext value : $value")
            }

            override fun onError(e: Throwable) {
                Log.d("RXOperator", " onError : " + e.message)
            }

            override fun onComplete() {
                Log.d("RXOperator", " onComplete")
            }
        }
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .take(3)
            .subscribe(observer)
    }

    fun startTimer(view: View) {
        val observable = Observable.timer(2, TimeUnit.SECONDS)

        val observer = object : Observer<Long> {

            override fun onSubscribe(d: Disposable) {
                Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
            }

            override fun onNext(value: Long) {
                Log.d("RXOperator", " onNext : value : $value")
            }

            override fun onError(e: Throwable) {
                Log.d("RXOperator", " onError : " + e.message)
            }

            override fun onComplete() {
                Log.d("RXOperator", " onComplete")
            }
        }

        observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }

    fun startFlatMap(view: View) {
        val observable = Observable.just(1, 2, 3, 4, 5)

        val observer = object : Observer<Int> {
            override fun onSubscribe(d: Disposable) {
                Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
            }

            override fun onNext(value: Int) {
                Log.d("RXOperator", " onNext value : $value")
            }

            override fun onError(e: Throwable) {
                Log.d("RXOperator", " onError : " + e.message)
            }

            override fun onComplete() {
                Log.d("RXOperator", " onComplete")
            }
        }

        fun multiplyInt(integer: Int?, multiplier: Int): Observable<Int> {
            return Observable.just(integer!! * multiplier)
        }

        observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { integer -> multiplyInt(integer, 2) }
            .flatMap { integer -> multiplyInt(integer, 3) }
            .flatMap { integer -> multiplyInt(integer, 5) }
            .subscribe(observer)

    }

    fun startInterval(view: View) {
        val observable = Observable.interval(1, 2, TimeUnit.SECONDS)

        val observer = object : Observer<Long> {
            override fun onSubscribe(d: Disposable) {
                Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
            }

            override fun onNext(value: Long) {
                Log.d("RXOperator", " onNext : value : $value")
            }

            override fun onError(e: Throwable) {
                Log.d("RXOperator", " onError : " + e.message)
            }

            override fun onComplete() {
                Log.d("RXOperator", " onComplete")
            }
        }

        observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }

    fun startDebounce(view: View) {
        val observable = Observable.create<Int> { emitter ->
            // send events with simulated time wait
            emitter.onNext(1) // skip
            Thread.sleep(400)
            emitter.onNext(2) // deliver
            Thread.sleep(505)
            emitter.onNext(3) // skip
            Thread.sleep(100)
            emitter.onNext(4) // deliver kj h ihioh
            Thread.sleep(605) ///////////////
            emitter.onNext(5) // deliver
            Thread.sleep(510)
            emitter.onComplete()
        }

        val observer = object : Observer<Int> {
            override fun onSubscribe(d: Disposable) {
                Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
            }

            override fun onNext(value: Int) {
                Log.d("RXOperator", " onNext value : $value")
            }

            override fun onError(e: Throwable) {
                Log.d("RXOperator", " onError : " + e.message)
            }

            override fun onComplete() {
                Log.d("RXOperator", " onComplete")
            }
        }

        observable
            .debounce(500, TimeUnit.MILLISECONDS)
            // Run on a background thread
            .subscribeOn(Schedulers.io())
            // Be notified on the main thread
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)

    }

    fun startSingleObserver(view: View) {
        val observer = object : SingleObserver<String> {
            override fun onSubscribe(d: Disposable) {
                Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
            }

            override fun onSuccess(value: String) {
                Log.d("RXOperator", " onNext value : $value")
            }

            override fun onError(e: Throwable) {
                Log.d("RXOperator", " onError : " + e.message)
            }
        }

        Single.just("test")
            .subscribe(observer)

    }

    fun startCompletableObserver(view: View) {
        val observer = object : CompletableObserver {
            override fun onSubscribe(d: Disposable) {
                Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
            }

            override fun onComplete() {
                Log.d("RXOperator", " onComplete")
            }

            override fun onError(e: Throwable) {
                Log.d("RXOperator", " onError : " + e.message)
            }
        }

        val completable = Completable.timer(1000, TimeUnit.MILLISECONDS)

        completable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }

    fun startFlowable(view: View) {
        val observer = object : SingleObserver<Int> {

            override fun onSubscribe(d: Disposable) {
                Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
            }

            override fun onSuccess(value: Int) {
                Log.d("RXOperator", " onSuccess : value : $value")
            }

            override fun onError(e: Throwable) {
                Log.d("RXOperator", " onError : " + e.message)
            }
        }

        val flowable = Flowable.just(1, 2, 3, 4)
        flowable
            .reduce(50) { t1, t2 -> t1 + t2 }
            .subscribe(observer)
    }

    fun startReduce(view: View) {
        val observable = Observable.just<Int>(1, 2, 3, 4, 1, 1, 1, 1, 1, 1)

        val observer = object : MaybeObserver<Int> {
            override fun onSubscribe(d: Disposable) {
                Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
            }

            override fun onSuccess(value: Int) {
                Log.d("RXOperator", " onSuccess : value : $value")
            }

            override fun onError(e: Throwable) {
                Log.d("RXOperator", " onError : " + e.message)
            }

            override fun onComplete() {
                Log.d("RXOperator", " onComplete")
            }
        }

        observable
            .reduce { t1, t2 -> t1 + t2 }
            .subscribe(observer)
    }

    fun startBuffer(view: View) {
        val observable = Observable.just("one", "two", "three", "four", "five")

        val observer = object : Observer<List<String>> {

            override fun onSubscribe(d: Disposable) {
                Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
            }

            override fun onNext(stringList: List<String>) {
                Log.d("RXOperator", " onNext : size :" + stringList.size)
                for (value in stringList) {
                    Log.d("RXOperator", " : value :$value")
                }

            }

            override fun onError(e: Throwable) {
                Log.d("RXOperator", " onError : " + e.message)
            }

            override fun onComplete() {
                Log.d("RXOperator", " onComplete")
            }
        }
        val buffered = observable.buffer(3, 1)
        buffered.subscribe(observer)
    }

    fun startFilter(view: View) {
        val observer = object : Observer<Int> {

            override fun onSubscribe(d: Disposable) {
                Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
            }

            override fun onNext(value: Int) {
                Log.d("RXOperator", " onNext ")
                Log.d("RXOperator", " value : $value")
            }

            override fun onError(e: Throwable) {
                Log.d("RXOperator", " onError : " + e.message)
            }

            override fun onComplete() {
                Log.d("RXOperator", " onComplete")
            }
        }

        Observable.just(1, 2, 3, 4, 5, 6)
            .filter { integer -> integer % 2 == 0 }
            .subscribe(observer)
    }

    fun startSkip(view: View) {
        val observable = Observable.just(1, 2, 3, 4, 5)

        val observer = object : Observer<Int> {

            override fun onSubscribe(d: Disposable) {
                Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
            }

            override fun onNext(value: Int) {
                Log.d("RXOperator", " onNext value : $value")
            }

            override fun onError(e: Throwable) {
                Log.d("RXOperator", " onError : " + e.message)
            }

            override fun onComplete() {
                Log.d("RXOperator", " onComplete")
            }
        }

        observable
            // Run on a background thread
            .subscribeOn(Schedulers.io())
            // Be notified on the main thread
            .observeOn(AndroidSchedulers.mainThread())
            .skip(2)
            .subscribe(observer)
    }

    fun startScan(view: View) {
        val observable = Observable.just(1, 2, 3, 4, 5)

        val observer = object : Observer<Int> {

            override fun onSubscribe(d: Disposable) {
                Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
            }

            override fun onNext(value: Int) {
                Log.d("RXOperator", " onNext value : $value")
            }

            override fun onError(e: Throwable) {
                Log.d("RXOperator", " onError : " + e.message)
            }

            override fun onComplete() {
                Log.d("RXOperator", " onComplete")
            }
        }

        observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .scan { int1, int2 -> int1 + int2 }
            .subscribe(observer)
    }

    fun startReplay(view: View) {
        val firstObserver = object : Observer<Int> {

            override fun onSubscribe(d: Disposable) {
                Log.d("RXOperator", " First onSubscribe : " + d.isDisposed)
            }

            override fun onNext(value: Int) {
                Log.d("RXOperator", " First onNext value : $value")
            }

            override fun onError(e: Throwable) {
                Log.d("RXOperator", " First onError : " + e.message)
            }

            override fun onComplete() {
                Log.d("RXOperator", " First onComplete")
            }
        }

        val secondObserver = object : Observer<Int> {

            override fun onSubscribe(d: Disposable) {
                Log.d("RXOperator", " Second onSubscribe : " + d.isDisposed)
            }

            override fun onNext(value: Int) {
                Log.d("RXOperator", " Second onNext value : $value")
            }

            override fun onError(e: Throwable) {
                Log.d("RXOperator", " Second onError : " + e.message)
            }

            override fun onComplete() {
                Log.d("RXOperator", " Second onComplete")
            }
        }

        val source = PublishSubject.create<Int>()
        val connectableObservable = source.replay(3) // bufferSize = 3 to retain 3 values to replay
        connectableObservable.connect() // connecting the connectableObservable

        connectableObservable.subscribe(firstObserver)
        source.onNext(1)
        source.onNext(2)
        source.onNext(3)
        source.onNext(4)
        source.onComplete()

        /*
         * it will emit 2, 3, 4 as (count = 3), retains the 3 values for replay
         */
        connectableObservable.subscribe(secondObserver)
    }

    fun startConcat(view: View) {
        val observer = object : Observer<String> {

            override fun onSubscribe(d: Disposable) {
                Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
            }

            override fun onNext(value: String) {
                Log.d("RXOperator", " onNext : value : $value")
            }

            override fun onError(e: Throwable) {
                Log.d("RXOperator", " onError : " + e.message)
            }

            override fun onComplete() {
                Log.d("RXOperator", " onComplete")
            }
        }

        val aStrings = arrayOf("A1", "A2", "A3", "A4")
        val bStrings = arrayOf("B1", "B2", "B3")

        val aObservable = Observable.fromArray(*aStrings)
        val bObservable = Observable.fromArray(*bStrings)

        Observable.concat(aObservable, bObservable)
            .subscribe(observer)
    }

    fun startMerge(view: View) {
        val observer = object : Observer<String> {

            override fun onSubscribe(d: Disposable) {
                Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
            }

            override fun onNext(value: String) {
                Log.d("RXOperator", " onNext : value : $value")
            }

            override fun onError(e: Throwable) {
                Log.d("RXOperator", " onError : " + e.message)
            }

            override fun onComplete() {
                Log.d("RXOperator", " onComplete")
            }
        }

        val aStrings = arrayOf("A1", "A2", "A3", "A4")
        val bStrings = arrayOf("B1", "B2", "B3")

        val aObservable = Observable.fromArray(*aStrings)
        val bObservable = Observable.fromArray(*bStrings)

        Observable.merge(aObservable, bObservable)
            .subscribe(observer)
    }

    fun startDefer(view: View) {
        val observer = object : Observer<String> {

            override fun onSubscribe(d: Disposable) {
                Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
            }

            override fun onNext(value: String) {
                Log.d("RXOperator", " onNext : value : $value")
            }

            override fun onError(e: Throwable) {
                Log.d("RXOperator", " onError : " + e.message)
            }

            override fun onComplete() {
                Log.d("RXOperator", " onComplete")
            }
        }
        val bike = Shop()
        val brandDeferObservable = bike.brandDeferObservable()
        bike.setBrand("Harley Davidson")
        brandDeferObservable.subscribe(observer)
    }

    fun startDistinct(view: View) {
        val observable = Observable.just(1, 2, 1, 1, 2, 3, 4, 6, 4)

        val observer = object : Observer<Int> {

            override fun onSubscribe(d: Disposable) {
                Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
            }

            override fun onNext(value: Int) {
                Log.d("RXOperator", " onNext value : $value")
            }

            override fun onError(e: Throwable) {
                Log.d("RXOperator", " onError : " + e.message)
            }

            override fun onComplete() {
                Log.d("RXOperator", " onComplete")
            }
        }

        observable
            .distinct()
            .subscribe(observer)
    }

    fun startReplaySubject(view: View) {
        val firstObserver = object : Observer<Int> {

            override fun onSubscribe(d: Disposable) {
                Log.d("RXOperator", " First onSubscribe : " + d.isDisposed)
            }

            override fun onNext(value: Int) {
                Log.d("RXOperator", " First onNext value : $value")
            }

            override fun onError(e: Throwable) {
                Log.d("RXOperator", " First onError : " + e.message)
            }

            override fun onComplete() {
                Log.d("RXOperator", " First onComplete")
            }
        }

        val secondObserver = object : Observer<Int> {

            override fun onSubscribe(d: Disposable) {
                Log.d("RXOperator", " Second onSubscribe : " + d.isDisposed)
            }

            override fun onNext(value: Int) {
                Log.d("RXOperator", " Second onNext value : $value")
            }

            override fun onError(e: Throwable) {
                Log.d("RXOperator", " Second onError : " + e.message)
            }

            override fun onComplete() {
                Log.d("RXOperator", " Second onComplete")
            }
        }
        val source = ReplaySubject.create<Int>()
        source.subscribe(firstObserver) // it will get 1, 2, 3, 4
        source.onNext(1)
        source.onNext(2)
        source.onNext(3)
        source.onNext(4)
        source.onComplete()
        source.subscribe(secondObserver)
    }

    fun startPublishSubject(view: View) {
        val observer = object : Observer<Int> {

            override fun onSubscribe(d: Disposable) {
                Log.d("RXOperator", " First onSubscribe : " + d.isDisposed)
            }

            override fun onNext(value: Int) {
                Log.d("RXOperator", " First onNext value : $value")
            }

            override fun onError(e: Throwable) {
                Log.d("RXOperator", " First onError : " + e.message)
            }

            override fun onComplete() {
                Log.d("RXOperator", " First onComplete")
            }
        }

        val secondObserver = object : Observer<Int> {

            override fun onSubscribe(d: Disposable) {
                Log.d("RXOperator", " Second onSubscribe : " + d.isDisposed)
            }

            override fun onNext(value: Int) {
                Log.d("RXOperator", " Second onNext value : $value")
            }

            override fun onError(e: Throwable) {

                Log.d("RXOperator", " Second onError : " + e.message)
            }

            override fun onComplete() {
                Log.d("RXOperator", " Second onComplete")
            }
        }
        val source = PublishSubject.create<Int>()
        source.subscribe(observer) // it will get 1, 2, 3, 4 and onComplete
        source.onNext(1)
        source.onNext(2)
        source.onNext(3)
        source.subscribe(secondObserver)
        source.onNext(4)
        source.onComplete()
    }

    fun startBehaviorSubject(view: View) {
        val observer = object : Observer<Int> {

            override fun onSubscribe(d: Disposable) {
                Log.d("RXOperator", " First onSubscribe : " + d.isDisposed)
            }

            override fun onNext(value: Int) {
                Log.d("RXOperator", " First onNext value : $value")
            }

            override fun onError(e: Throwable) {
                Log.d("RXOperator", " First onError : " + e.message)
            }

            override fun onComplete() {
                Log.d("RXOperator", " First onComplete")
            }
        }

        val secondObserver = object : Observer<Int> {

            override fun onSubscribe(d: Disposable) {
                Log.d("RXOperator", " Second onSubscribe : " + d.isDisposed)
            }

            override fun onNext(value: Int) {
                Log.d("RXOperator", " Second onNext value : $value")
            }

            override fun onError(e: Throwable) {
                Log.d("RXOperator", " Second onError : " + e.message)
            }

            override fun onComplete() {
                Log.d("RXOperator", " Second onComplete")
            }
        }
        val source = BehaviorSubject.create<Int>()
        source.subscribe(observer) // it will get 1, 2, 3, 4 and onComplete
        source.onNext(1)
        source.onNext(2)
        source.onNext(3)
        /*
         * it will emit 3(last emitted), 4 and onComplete for second observer also.
         */
        source.subscribe(secondObserver)
        source.onNext(4)
        source.onComplete()
    }

    fun startAsyncSubject(view: View) {
        val firstObserver = object : Observer<Int> {

            override fun onSubscribe(d: Disposable) {
                Log.d("RXOperator", " First onSubscribe : " + d.isDisposed)
            }

            override fun onNext(value: Int) {
                Log.d("RXOperator", " First onNext value : $value")
            }

            override fun onError(e: Throwable) {
                Log.d("RXOperator", " First onError : " + e.message)
            }

            override fun onComplete() {
                Log.d("RXOperator", " First onComplete")
            }
        }

        val secondObserver = object : Observer<Int> {

            override fun onSubscribe(d: Disposable) {
                Log.d("RXOperator", " Second onSubscribe : " + d.isDisposed)
            }

            override fun onNext(value: Int) {
                Log.d("RXOperator", " Second onNext value : $value")
            }

            override fun onError(e: Throwable) {
                Log.d("RXOperator", " Second onError : " + e.message)
            }

            override fun onComplete() {
                Log.d("RXOperator", " Second onComplete")
            }
        }

        val source = AsyncSubject.create<Int>()
        source.subscribe(firstObserver) // it will emit only 4 and onComplete
        source.onNext(1)
        source.onNext(2)
        source.onNext(3)
        /*
         * it will emit 4 and onComplete for second observer also.
         */
        source.subscribe(secondObserver)
        source.onNext(4)
        source.onComplete()
    }

    fun startThrottleFirst(view: View) {
        val observable = Observable.create<Int> { emitter ->
            // send events with simulated time wait
            Thread.sleep(0)
            emitter.onNext(1) // deliver
            emitter.onNext(2) // skip
            Thread.sleep(505)
            emitter.onNext(3) // deliver
            Thread.sleep(99)
            emitter.onNext(4) // skip
            Thread.sleep(100)
            emitter.onNext(5) // skip
            emitter.onNext(6) // skip
            Thread.sleep(305)
            emitter.onNext(7) // deliver
            Thread.sleep(510)
            emitter.onComplete()
        }

        val observer = object : Observer<Int> {

            override fun onSubscribe(d: Disposable) {
                Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
            }

            override fun onNext(value: Int) {
                Log.d("RXOperator", " onNext ")
                Log.d("RXOperator", " value : $value")
            }

            override fun onError(e: Throwable) {
                Log.d("RXOperator", " onError : " + e.message)
            }

            override fun onComplete() {
                Log.d("RXOperator", " onComplete")
            }
        }

        observable.throttleFirst(500, TimeUnit.MILLISECONDS)
            // Run on a background thread
            .subscribeOn(Schedulers.io())
            // Be notified on the main thread
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }

    fun startThrottleLast(view: View) {
        val observable = Observable.create<Int> { emitter ->
            // send events with simulated time wait
            Thread.sleep(0)
            emitter.onNext(1) // skip
            emitter.onNext(2) // deliver
            Thread.sleep(505)
            emitter.onNext(3) // skip
            Thread.sleep(99)
            emitter.onNext(4) // skip
            Thread.sleep(100)
            emitter.onNext(5) // skip
            emitter.onNext(6) // deliver
            Thread.sleep(305)
            emitter.onNext(7) // deliver
            Thread.sleep(510)
            emitter.onComplete()
        }

        val observer = object : Observer<Int> {

            override fun onSubscribe(d: Disposable) {
                Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
            }

            override fun onNext(value: Int) {
                Log.d("RXOperator", " onNext ")
                Log.d("RXOperator", " value : $value")
            }

            override fun onError(e: Throwable) {
                Log.d("RXOperator", " onError : " + e.message)
            }

            override fun onComplete() {
                Log.d("RXOperator", " onComplete")
            }
        }

        observable
            .throttleLast(500, TimeUnit.MILLISECONDS)
            // Run on a background thread
            .subscribeOn(Schedulers.io())
            // Be notified on the main thread
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }

    fun startWindow(view: View) {
        val consumer = Consumer<Observable<Long>> { observable ->
            Log.d("RXOperator", "Sub Divide begin....")
            observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { value ->
                    Log.d("RXOperator", "Next:$value")
                }
        }

        Observable.interval(1, TimeUnit.SECONDS).take(12)
            .window(3, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(consumer)
    }

    fun startDelay(view: View) {

        val observer = object : Observer<String> {

            override fun onSubscribe(d: Disposable) {
                Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
            }

            override fun onNext(value: String) {
                Log.d("RXOperator", " onNext : value : $value")
            }

            override fun onError(e: Throwable) {
                Log.d("RXOperator", " onError : " + e.message)
            }

            override fun onComplete() {
                Log.d("RXOperator", " onComplete")
            }
        }

        Observable.just("aman").delay(2, TimeUnit.SECONDS)
            // Run on a background thread
            .subscribeOn(Schedulers.io())
            // Be notified on the main thread
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }

    fun startRetry(view: View) {
        val observable = Observable.create<Int> { emitter ->
            // send events with simulated time wait
            emitter.onNext(1) // skip
            Thread.sleep(400)
            emitter.onNext(2) // deliver
            Thread.sleep(505)
            emitter.onNext(3) // skip
            Thread.sleep(100)
            emitter.onNext(4) // deliver kj h ihioh
            Thread.sleep(605) ///////////////
            emitter.onNext(5) // deliver
            Thread.sleep(510)
            emitter.onComplete()
        }

        val observer = object : Observer<Int> {
            override fun onSubscribe(d: Disposable) {
                Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
            }

            override fun onNext(value: Int) {
                Log.d("RXOperator", " onNext value : $value")
            }

            override fun onError(e: Throwable) {
                Log.d("RXOperator", " onError : " + e.message)
            }

            override fun onComplete() {
                Log.d("RXOperator", " onComplete")
            }
        }

        observable
            .retry()
//                .retryWhen(source -> source.delay(100, TimeUnit.MILLISECONDS))
            .debounce(500, TimeUnit.MILLISECONDS)
            // Run on a background thread
            .subscribeOn(Schedulers.io())
            // Be notified on the main thread
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear() // do not send event after activity has been destroyed
    }

}