/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.openwifirrm.ucentral;

import java.util.Objects;

import com.facebook.openwifirrm.ucentral.models.WifiScanEntryResult;

/**
 * Extends {@link WifiScanEntryResult} to track the response time of the entry.
 */
public class WifiScanEntry extends WifiScanEntryResult {
	/**
	 * Unix time in milliseconds (ms). This field is not defined in the uCentral
	 * API. This is added it because {@link WifiScanEntryResult#tsf} is an unknown
	 * time reference.
	 */
	public long unixTimeMs;

	/** Default Constructor. */
	public WifiScanEntry() {}

	/** Copy Constructor. */
	public WifiScanEntry(WifiScanEntry o) {
		super(o);
		this.unixTimeMs = o.unixTimeMs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(unixTimeMs);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		WifiScanEntry other = (WifiScanEntry) obj;
		return unixTimeMs == other.unixTimeMs;
	}

	@Override
	public String toString() {
		return String.format(
			"WifiScanEntry[signal=%d, bssid=%s, unixTimeMs=%d]",
			signal,
			bssid,
			unixTimeMs
		);
	}
}