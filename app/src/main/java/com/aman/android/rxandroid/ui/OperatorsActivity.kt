package com.aman.android.rxandroid.ui

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.aman.android.rxandroid.R
import com.aman.android.rxandroid.model.*
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

    fun startSimpleActivity(view: View) {
         fun getObserver(): Observer<String> {
            return object : Observer<String> {

                override fun onSubscribe(d: Disposable) {
                    Log.d("RXOperator", "###################################")
                    Log.d("RXOperator", "Simple onSubscribe : " + d.isDisposed)
                }

                override fun onNext(value: String) {
                    Log.d("RXOperator"," onNext : value : " + value+"\n")
                }

                override fun onError(e: Throwable) {
                    Log.d("RXOperator", " onError : " + e.message+"\n")
                    //progress.visibility = View.GONE
                }

                override fun onComplete() {
                    Log.d("RXOperator", " onComplete"+"\n")
                    Log.d("RXOperator", "###################################")
                }
            }
        }
        Observable.just("RxJava", "RxAndroid")
                // Run on a background thread
                .subscribeOn(Schedulers.io())
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObserver())
    }

    fun startMap(view: View) {
        fun getObservable(): Observable<List<ApiUser>> {
        return Observable.create<List<ApiUser>> { e ->
            if (!e.isDisposed) {
                e.onNext(Utils().getApiUserList())
                e.onComplete()
            }
            }
        }
        fun getObserver(): Observer<List<User>> {
        return object : Observer<List<User>> {

            override fun onSubscribe(d: Disposable) {
                Log.d("RXOperator", "###################################")
                Log.d("RXOperator", "Map onSubscribe : " + d.isDisposed)
            }

            override fun onNext(userList: List<User>) {
                Log.d("RXOperator", " onNext"+"\n")
                for (user in userList) {
                    Log.d("RXOperator", " login Name : " + user.login+"\n")
                }
                Log.d("RXOperator", " onNext : " + userList.size)
            }

            override fun onError(e: Throwable) {
                Log.d("RXOperator"," onError : " + e.message+"\n")
            }

            override fun onComplete() {
                Log.d("RXOperator", " onComplete"+"\n")
            }
        }
    }
        getObservable()
                // Run on a background thread
                .subscribeOn(Schedulers.io())
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .map { apiUser -> Utils().convertApiUserListToUserList(apiUser) }
                .subscribe(getObserver())
    }

    fun startZip(view: View) {
         fun getKotlinFansObservable(): Observable<List<User>> {
            return Observable.create<List<User>> { e ->
                if (!e.isDisposed) {
                    e.onNext(Utils().getUserListWhoLovesKotlin())
                    e.onComplete()
                }
            }
        }

         fun getJavaFansObservable(): Observable<List<User>> {
            return Observable.create<List<User>> { e ->
                if (!e.isDisposed) {
                    e.onNext(Utils().getUserListWhoLovesJava())
                    e.onComplete()
                }
            }
        }

         fun getObserver(): Observer<List<User>> {
            return object : Observer<List<User>> {

                override fun onSubscribe(d: Disposable) {
                    Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
                }

                override fun onNext(userList: List<User>) {
                    Log.d("RXOperator", " onNext"+"\n")
                    for (user in userList) {
                        Log.d("RXOperator", " loginName : " + user.login+"\n")
                    }
                    Log.d("RXOperator", " onNext : " + userList.size)
                }

                override fun onError(e: Throwable) {
                    Log.d("RXOperator"," onError : " + e.message+"\n")
                }

                override fun onComplete() {
                    Log.d("RXOperator", " onComplete"+"\n")
                }
            }
        }

        Observable.zip<List<User>, List<User>, List<User>>(getKotlinFansObservable(), getJavaFansObservable(),
                BiFunction<List<User>, List<User>, List<User>> { kotlinFans, javaFans -> Utils().filterUserWhoLovesBoth(kotlinFans, javaFans) })
                // Run on a background thread
                .subscribeOn(Schedulers.io())
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObserver())
    }

    fun startDisposable(view: View) {
         fun sampleObservable(): Observable<String> {
            return Observable.defer {
                // Do some long running operation
                SystemClock.sleep(2000)
                Observable.just("Firebase", "Fabric", "Analytics", "Github", "LinkedIn")
            }
        }

        disposables.add(sampleObservable()
                // Run on a background thread
                .subscribeOn(Schedulers.io())
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<String>() {
                    override fun onComplete() {
                        Log.d("RXOperator", " onComplete"+"\n")
                    }

                    override fun onError(e: Throwable) {
                        Log.d("RXOperator", " onError : " + e.message+"\n")
                    }

                    override fun onNext(value: String) {
                        Log.d("RXOperator", " onNext value : " + value+"\n")
                    }
                }))
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear() // do not send event after activity has been destroyed
    }

    fun startTake(view: View) {
         fun getObservable(): Observable<Int> {
            return Observable.just(1, 2, 3, 4, 5)
        }

         fun getObserver(): Observer<Int> {
            return object : Observer<Int> {
                override fun onSubscribe(d: Disposable) {
                    Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
                }

                override fun onNext(value: Int) {
                    Log.d("RXOperator", " onNext value : " + value)
                }

                override fun onError(e: Throwable) {
                    Log.d("RXOperator", " onError : " + e.message)
                }

                override fun onComplete() {
                    Log.d("RXOperator", " onComplete")
                }
            }
        }
        getObservable()
                // Run on a background thread
                .subscribeOn(Schedulers.io())
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .take(3)
                .subscribe(getObserver())
    }

    fun startTimer(view: View) {
         fun getObservable(): Observable<out Long> {
            return Observable.timer(2, TimeUnit.SECONDS)
        }

         fun getObserver(): Observer<Long> {
            return object : Observer<Long> {

                override fun onSubscribe(d: Disposable) {
                    Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
                }

                override fun onNext(value: Long) {
                    Log.d("RXOperator", " onNext : value : " + value)
                }

                override fun onError(e: Throwable) {
                    Log.d("RXOperator", " onError : " + e.message)
                }

                override fun onComplete() {
                    Log.d("RXOperator", " onComplete")
                }
            }
        }

        getObservable()
                // Run on a background thread
                .subscribeOn(Schedulers.io())
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObserver())
    }

    fun startFlatMap(view: View) {
        fun getObservable(): Observable<Int> {
            return Observable.just(1, 2, 3, 4, 5)
        }

        fun getObserver(): Observer<Int> {
            return object : Observer<Int> {
                override fun onSubscribe(d: Disposable) {
                    Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
                }

                override fun onNext(value: Int) {
                    Log.d("RXOperator", " onNext value : " + value)
                }

                override fun onError(e: Throwable) {
                    Log.d("RXOperator", " onError : " + e.message)
                }

                override fun onComplete() {
                    Log.d("RXOperator", " onComplete")
                }
            }
        }

        fun multiplyInt(integer: Int?, mulplier: Int): Observable<Int> {
            //simulating a heavy duty computational expensive operation
            for (i in 0..100) {
            }
            return Observable.just(integer!! * mulplier)
        }

        getObservable()
                .subscribeOn(Schedulers.io())
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { integer -> multiplyInt(integer, 2) }
                .flatMap { integer -> multiplyInt(integer, 3) }
                .flatMap { integer -> multiplyInt(integer, 5) }
                .subscribe(getObserver())

    }

    fun startInterval(view: View) {
         fun getObservable(): Observable<out Long> {
            return Observable.interval(1, 2, TimeUnit.SECONDS)
        }

         fun getObserver(): Observer<Long> {
            return object : Observer<Long> {

                override fun onSubscribe(d: Disposable) {
                    Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
                }

                override fun onNext(value: Long) {
                    Log.d("RXOperator", " onNext : value : " + value)
                }

                override fun onError(e: Throwable) {
                    Log.d("RXOperator", " onError : " + e.message)
                }

                override fun onComplete() {
                    Log.d("RXOperator", " onComplete")
                }
            }
        }

        getObservable()
                // Run on a background thread
                .subscribeOn(Schedulers.io())
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObserver())
    }

    fun startDebounce(view: View) {
         fun getObservable(): Observable<Int> {
            return Observable.create { emitter ->
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
        }


         fun getObserver(): Observer<Int> {
            return object : Observer<Int> {
                override fun onSubscribe(d: Disposable) {
                    Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
                }

                override fun onNext(value: Int) {
                    Log.d("RXOperator", " onNext value : " + value)
                }

                override fun onError(e: Throwable) {
                    Log.d("RXOperator", " onError : " + e.message)
                }

                override fun onComplete() {
                    Log.d("RXOperator", " onComplete")
                }
            }
        }

        getObservable()
                .debounce(500, TimeUnit.MILLISECONDS)
                // Run on a background thread
                .subscribeOn(Schedulers.io())
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObserver())

    }

    fun startSingleObserver(view: View) {
        fun getSingleObserver(): SingleObserver<String> {
            return object : SingleObserver<String> {
                override fun onSubscribe(d: Disposable) {
                    Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
                }

                override fun onSuccess(value: String) {
                    Log.d("RXOperator", " onNext value : " + value)
                }

                override fun onError(e: Throwable) {
                    Log.d("RXOperator", " onError : " + e.message)
                }
            }
        }

        Single.just("test")
                .subscribe(getSingleObserver())

    }

    fun startCompletableObserver(view: View) {
        fun getCompletableObserver(): CompletableObserver {
            return object : CompletableObserver {
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
        }

        val completable = Completable.timer(1000, TimeUnit.MILLISECONDS)

        completable
                .subscribeOn(Schedulers.io())
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getCompletableObserver())
    }

    fun startFlowable(view: View) {
        fun getObserver(): SingleObserver<Int> {

            return object : SingleObserver<Int> {


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
        }

        val observable = Flowable.just(1, 2, 3, 4)
        observable
                .reduce(50) { t1, t2 -> t1 + t2 }
                .subscribe(getObserver())
    }

    fun startReduce(view: View) {
         fun getObservable(): Observable<Int> {
            return Observable.just(1, 2, 3, 4)
        }

         fun getObserver(): MaybeObserver<Int> {
            return object : MaybeObserver<Int> {
                override fun onSubscribe(d: Disposable) {
                    Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
                }

                override fun onSuccess(value: Int) {
                    Log.d("RXOperator", " onSuccess : value : " + value)
                }

                override fun onError(e: Throwable) {
                    Log.d("RXOperator", " onError : " + e.message)
                }

                override fun onComplete() {
                    Log.d("RXOperator", " onComplete")
                }
            }
        }

        getObservable()
                .reduce { t1, t2 -> t1 + t2 }
                .subscribe(getObserver())
    }

    fun startBuffer(view: View) {
         fun getObservable(): Observable<String> {
            return Observable.just("one", "two", "three", "four", "five")
        }

         fun getObserver(): Observer<List<String>> {
            return object : Observer<List<String>> {

                override fun onSubscribe(d: Disposable) {
                    Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
                }

                override fun onNext(stringList: List<String>) {
                    Log.d("RXOperator", " onNext : size :" + stringList.size)
                    for (value in stringList) {
                        Log.d("RXOperator", " : value :" + value)
                    }

                }

                override fun onError(e: Throwable) {
                    Log.d("RXOperator", " onError : " + e.message)
                }

                override fun onComplete() {
                    Log.d("RXOperator", " onComplete")
                }
            }
        }

        val buffered = getObservable().buffer(3, 1)

        // 3 means,  it takes max of three from its start index and create list
        // 1 means, it jumps one step every time
        // so the it gives the following list
        // 1 - one, two, three
        // 2 - two, three, four
        // 3 - three, four, five
        // 4 - four, five
        // 5 - five

        buffered.subscribe(getObserver())
    }

    fun startFilter(view: View) {
         fun getObserver(): Observer<Int> {
            return object : Observer<Int> {

                override fun onSubscribe(d: Disposable) {
                    Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
                }

                override fun onNext(value: Int) {
                    Log.d("RXOperator", " onNext ")
                    Log.d("RXOperator", " value : " + value)
                }

                override fun onError(e: Throwable) {
                    Log.d("RXOperator", " onError : " + e.message)
                }

                override fun onComplete() {
                    Log.d("RXOperator", " onComplete")
                }
            }
        }

        Observable.just(1, 2, 3, 4, 5, 6)
                .filter { integer -> integer % 2 == 0 }
                .subscribe(getObserver())
    }

    fun startSkip(view: View) {
         fun getObservable(): Observable<Int> {
            return Observable.just(1, 2, 3, 4, 5)
        }

         fun getObserver(): Observer<Int> {
            return object : Observer<Int> {

                override fun onSubscribe(d: Disposable) {
                    Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
                }

                override fun onNext(value: Int) {
                    Log.d("RXOperator", " onNext value : " + value)
                }

                override fun onError(e: Throwable) {
                    Log.d("RXOperator", " onError : " + e.message)
                }

                override fun onComplete() {
                    Log.d("RXOperator", " onComplete")
                }
            }
        }

        getObservable()
                // Run on a background thread
                .subscribeOn(Schedulers.io())
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .skip(2)
                .subscribe(getObserver())
    }

    fun startScan(view: View) {
         fun getObservable(): Observable<Int> {
            return Observable.just(1, 2, 3, 4, 5)
        }

         fun getObserver(): Observer<Int> {
            return object : Observer<Int> {

                override fun onSubscribe(d: Disposable) {
                    Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
                }

                override fun onNext(value: Int) {
                    Log.d("RXOperator", " onNext value : " + value)
                }

                override fun onError(e: Throwable) {
                    Log.d("RXOperator", " onError : " + e.message)
                }

                override fun onComplete() {
                    Log.d("RXOperator", " onComplete")
                }
            }
        }

        getObservable()
                // Run on a background thread
                .subscribeOn(Schedulers.io())
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .scan { int1, int2 -> int1 + int2 }
                .subscribe(getObserver())
    }

    fun startReplay(view: View) {
         fun getFirstObserver(): Observer<Int> {
            return object : Observer<Int> {

                override fun onSubscribe(d: Disposable) {
                    Log.d("RXOperator", " First onSubscribe : " + d.isDisposed)
                }

                override fun onNext(value: Int) {
                    Log.d("RXOperator", " First onNext value : " + value)
                }

                override fun onError(e: Throwable) {
                    Log.d("RXOperator", " First onError : " + e.message)
                }

                override fun onComplete() {
                    Log.d("RXOperator", " First onComplete")
                }
            }
        }

         fun getSecondObserver(): Observer<Int> {
            return object : Observer<Int> {

                override fun onSubscribe(d: Disposable) {
                    Log.d("RXOperator", " Second onSubscribe : " + d.isDisposed)
                }

                override fun onNext(value: Int) {
                    Log.d("RXOperator", " Second onNext value : " + value)
                }

                override fun onError(e: Throwable) {
                    Log.d("RXOperator", " Second onError : " + e.message)
                }

                override fun onComplete() {
                    Log.d("RXOperator", " Second onComplete")
                }
            }
        }

        val source = PublishSubject.create<Int>()
        val connectableObservable = source.replay(3) // bufferSize = 3 to retain 3 values to replay
        connectableObservable.connect() // connecting the connectableObservable

        connectableObservable.subscribe(getFirstObserver())

        source.onNext(1)
        source.onNext(2)
        source.onNext(3)
        source.onNext(4)
        source.onComplete()

        /*
         * it will emit 2, 3, 4 as (count = 3), retains the 3 values for replay
         */
        connectableObservable.subscribe(getSecondObserver())
    }

    fun startConcat(view: View) {
        fun getObserver(): Observer<String> {
            return object : Observer<String> {

                override fun onSubscribe(d: Disposable) {
                    Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
                }

                override fun onNext(value: String) {
                    Log.d("RXOperator", " onNext : value : " + value)
                }

                override fun onError(e: Throwable) {
                    Log.d("RXOperator", " onError : " + e.message)
                }

                override fun onComplete() {
                    Log.d("RXOperator", " onComplete")
                }
            }
        }

        val aStrings = arrayOf("A1", "A2", "A3", "A4")
        val bStrings = arrayOf("B1", "B2", "B3")

        val aObservable = Observable.fromArray(*aStrings)
        val bObservable = Observable.fromArray(*bStrings)

        Observable.concat(aObservable, bObservable)
                .subscribe(getObserver())
    }

    fun startMerge(view: View) {
        fun getObserver(): Observer<String> {
            return object : Observer<String> {

                override fun onSubscribe(d: Disposable) {
                    Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
                }

                override fun onNext(value: String) {
                    Log.d("RXOperator", " onNext : value : " + value)
                }

                override fun onError(e: Throwable) {
                    Log.d("RXOperator", " onError : " + e.message)
                }

                override fun onComplete() {
                    Log.d("RXOperator", " onComplete")
                }
            }
        }

        val aStrings = arrayOf("A1", "A2", "A3", "A4")
        val bStrings = arrayOf("B1", "B2", "B3")

        val aObservable = Observable.fromArray(*aStrings)
        val bObservable = Observable.fromArray(*bStrings)

        Observable.merge(aObservable, bObservable)
                .subscribe(getObserver())
    }

    fun startDefer(view: View) {
        /*
  * Defer used for Deferring Observable code until subscription in RxJava
  */
         fun getObserver(): Observer<String> {
            return object : Observer<String> {

                override fun onSubscribe(d: Disposable) {
                    Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
                }

                override fun onNext(value: String) {
                    Log.d("RXOperator", " onNext : value : " + value)
                }

                override fun onError(e: Throwable) {
                    Log.d("RXOperator", " onError : " + e.message)
                }

                override fun onComplete() {
                    Log.d("RXOperator", " onComplete")
                }
            }
        }

        val bike = Shop()

        val brandDeferObservable = bike.brandDeferObservable()

        bike.setBrand("Harley Davidson")  // Even if we are setting the brand after creating Observable
        // we will get the brand as Harley Davidson.
        // If we had not used defer, we would have got null as the brand.

        brandDeferObservable.subscribe(getObserver())
    }

    fun startDistinct(view: View) {
        fun getObservable(): Observable<Int> {
            return Observable.just(1, 2, 1, 1, 2, 3, 4, 6, 4)
        }


        fun getObserver(): Observer<Int> {
            return object : Observer<Int> {

                override fun onSubscribe(d: Disposable) {
                    Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
                }

                override fun onNext(value: Int) {
                    Log.d("RXOperator", " onNext value : " + value)
                }

                override fun onError(e: Throwable) {
                    Log.d("RXOperator", " onError : " + e.message)
                }

                override fun onComplete() {
                    Log.d("RXOperator", " onComplete")
                }
            }
        }

        getObservable()
                .distinct()
                .subscribe(getObserver())
    }

    fun startReplaySubject(view: View) {
         fun getFirstObserver(): Observer<Int> {
            return object : Observer<Int> {

                override fun onSubscribe(d: Disposable) {
                    Log.d("RXOperator", " First onSubscribe : " + d.isDisposed)
                }

                override fun onNext(value: Int) {
                    Log.d("RXOperator", " First onNext value : " + value)
                }

                override fun onError(e: Throwable) {
                    Log.d("RXOperator", " First onError : " + e.message)
                }

                override fun onComplete() {
                    Log.d("RXOperator", " First onComplete")
                }
            }
        }

         fun getSecondObserver(): Observer<Int> {
            return object : Observer<Int> {

                override fun onSubscribe(d: Disposable) {
                    Log.d("RXOperator", " Second onSubscribe : " + d.isDisposed)
                }

                override fun onNext(value: Int) {
                    Log.d("RXOperator", " Second onNext value : " + value)
                }

                override fun onError(e: Throwable) {
                    Log.d("RXOperator", " Second onError : " + e.message)
                }

                override fun onComplete() {
                    Log.d("RXOperator", " Second onComplete")
                }
            }
        }

        val source = ReplaySubject.create<Int>()

        source.subscribe(getFirstObserver()) // it will get 1, 2, 3, 4

        source.onNext(1)
        source.onNext(2)
        source.onNext(3)
        source.onNext(4)
        source.onComplete()
        /*
         * it will emit 1, 2, 3, 4 for second observer also as we have used replay
         */
        source.subscribe(getSecondObserver())
    }

    fun startPublishSubject(view: View) {
         fun getFirstObserver(): Observer<Int> {
            return object : Observer<Int> {

                override fun onSubscribe(d: Disposable) {
                    Log.d("RXOperator", " First onSubscribe : " + d.isDisposed)
                }

                override fun onNext(value: Int) {
                    Log.d("RXOperator", " First onNext value : " + value)
                }

                override fun onError(e: Throwable) {
                    Log.d("RXOperator", " First onError : " + e.message)
                }

                override fun onComplete() {
                    Log.d("RXOperator", " First onComplete")
                }
            }
        }

         fun getSecondObserver(): Observer<Int> {
            return object : Observer<Int> {

                override fun onSubscribe(d: Disposable) {
                    Log.d("RXOperator", " Second onSubscribe : " + d.isDisposed)
                }

                override fun onNext(value: Int) {
                    Log.d("RXOperator", " Second onNext value : " + value)
                }

                override fun onError(e: Throwable) {

                    Log.d("RXOperator", " Second onError : " + e.message)
                }

                override fun onComplete() {
                    Log.d("RXOperator", " Second onComplete")
                }
            }
        }

        val source = PublishSubject.create<Int>()

        source.subscribe(getFirstObserver()) // it will get 1, 2, 3, 4 and onComplete

        source.onNext(1)
        source.onNext(2)
        source.onNext(3)

        /*
         * it will emit 4 and onComplete for second observer also.
         */
        source.subscribe(getSecondObserver())

        source.onNext(4)
        source.onComplete()
    }

    fun startBehaviorSubject(view: View) {
         fun getFirstObserver(): Observer<Int> {
            return object : Observer<Int> {

                override fun onSubscribe(d: Disposable) {
                    Log.d("RXOperator", " First onSubscribe : " + d.isDisposed)
                }

                override fun onNext(value: Int) {
                    Log.d("RXOperator", " First onNext value : " + value)
                }

                override fun onError(e: Throwable) {
                    Log.d("RXOperator", " First onError : " + e.message)
                }

                override fun onComplete() {
                    Log.d("RXOperator", " First onComplete")
                }
            }
        }

         fun getSecondObserver(): Observer<Int> {
            return object : Observer<Int> {

                override fun onSubscribe(d: Disposable) {
                    Log.d("RXOperator", " Second onSubscribe : " + d.isDisposed)
                }

                override fun onNext(value: Int) {
                    Log.d("RXOperator", " Second onNext value : " + value)
                }

                override fun onError(e: Throwable) {
                    Log.d("RXOperator", " Second onError : " + e.message)
                }

                override fun onComplete() {
                    Log.d("RXOperator", " Second onComplete")
                }
            }
        }

        val source = BehaviorSubject.create<Int>()

        source.subscribe(getFirstObserver()) // it will get 1, 2, 3, 4 and onComplete

        source.onNext(1)
        source.onNext(2)
        source.onNext(3)

        /*
         * it will emit 3(last emitted), 4 and onComplete for second observer also.
         */
        source.subscribe(getSecondObserver())

        source.onNext(4)
        source.onComplete()
    }

    fun startAsyncSubject(view: View) {
         fun getFirstObserver(): Observer<Int> {
            return object : Observer<Int> {

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
        }

         fun getSecondObserver(): Observer<Int> {
            return object : Observer<Int> {

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
        }

        val source = AsyncSubject.create<Int>()

        source.subscribe(getFirstObserver()) // it will emit only 4 and onComplete

        source.onNext(1)
        source.onNext(2)
        source.onNext(3)

        /*
         * it will emit 4 and onComplete for second observer also.
         */
        source.subscribe(getSecondObserver())

        source.onNext(4)
        source.onComplete()
    }

    fun startThrottleFirst(view: View) {
         fun getObservable(): Observable<Int> {
            return Observable.create { emitter ->
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
        }

         fun getObserver(): Observer<Int> {
            return object : Observer<Int> {

                override fun onSubscribe(d: Disposable) {
                    Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
                }

                override fun onNext(value: Int) {
                    Log.d("RXOperator", " onNext ")
                    Log.d("RXOperator", " value : " + value)
                }

                override fun onError(e: Throwable) {
                    Log.d("RXOperator", " onError : " + e.message)
                }

                override fun onComplete() {
                    Log.d("RXOperator", " onComplete")
                }
            }
        }

        getObservable()
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                // Run on a background thread
                .subscribeOn(Schedulers.io())
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObserver())
    }

    fun startThrottleLast(view: View) {
         fun getObservable(): Observable<Int> {
            return Observable.create { emitter ->
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
        }

         fun getObserver(): Observer<Int> {
            return object : Observer<Int> {

                override fun onSubscribe(d: Disposable) {
                    Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
                }

                override fun onNext(value: Int) {
                    Log.d("RXOperator", " onNext ")
                    Log.d("RXOperator", " value : " + value)
                }

                override fun onError(e: Throwable) {
                    Log.d("RXOperator", " onError : " + e.message)
                }

                override fun onComplete() {
                    Log.d("RXOperator", " onComplete")
                }
            }
        }

        getObservable()
                .throttleLast(500, TimeUnit.MILLISECONDS)
                // Run on a background thread
                .subscribeOn(Schedulers.io())
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObserver())
    }

    fun startWindow(view: View) {
        fun getConsumer(): Consumer<Observable<Long>> {
            return Consumer { observable ->
                Log.d("RXOperator", "Sub Divide begin....")
                observable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { value ->
                            Log.d("RXOperator", "Next:" + value)
                        }
            }
        }

        Observable.interval(1, TimeUnit.SECONDS).take(12)
                .window(3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getConsumer())
    }

    fun startDelay(view: View) {
        fun getObservable(): Observable<String> {
            return Observable.just("aman")
        }

        fun getObserver(): Observer<String> {
            return object : Observer<String> {

                override fun onSubscribe(d: Disposable) {
                    Log.d("RXOperator", " onSubscribe : " + d.isDisposed)
                }

                override fun onNext(value: String) {
                    Log.d("RXOperator", " onNext : value : " + value)
                }

                override fun onError(e: Throwable) {
                    Log.d("RXOperator", " onError : " + e.message)
                }

                override fun onComplete() {
                    Log.d("RXOperator", " onComplete")
                }
            }
        }

        getObservable().delay(2, TimeUnit.SECONDS)
                // Run on a background thread
                .subscribeOn(Schedulers.io())
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObserver())
    }

    fun startRetry(view: View) {
        fun getObservable(): Observable<Int> {
            return Observable.create { emitter ->
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
        }


        fun getObserver(): Observer<Int> {
            return object : Observer<Int> {
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
        }

        getObservable()
                .retry()
//                .retryWhen(source -> source.delay(100, TimeUnit.MILLISECONDS))
                .debounce(500, TimeUnit.MILLISECONDS)
                // Run on a background thread
                .subscribeOn(Schedulers.io())
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObserver())
    }
}