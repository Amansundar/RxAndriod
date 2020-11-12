package com.aman.android.rxandroid.model

import io.reactivex.Observable


class Shop {
    private var brand: String? = null

    fun setBrand(brand: String) {
        this.brand = brand
    }

    fun brandDeferObservable(): Observable<String> {
        return Observable.defer { Observable.just(brand!!) }
    }
}