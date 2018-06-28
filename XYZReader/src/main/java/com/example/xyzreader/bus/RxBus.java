package com.example.xyzreader.bus;

import com.jakewharton.rxrelay2.PublishRelay;

public class RxBus {

	private static final RxBus INSTANCE = new RxBus();

	private final PublishRelay<Boolean> listRefreshingRelay = PublishRelay.create();

	// To prevent instantiation outside the class
	private RxBus() {

	}

	public static RxBus getInstance() {
		return INSTANCE;
	}

	public void setListRefreshing(final boolean refreshing) {
		listRefreshingRelay.accept(refreshing);
	}

	public PublishRelay<Boolean> getListRefreshingRelay() {
		return listRefreshingRelay;
	}
}
