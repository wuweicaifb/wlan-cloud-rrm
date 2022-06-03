/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.openwifirrm.ucentral.gw.models;

import com.google.gson.JsonObject;

public class WifiScanRequest {
	public String serialNumber;
	public boolean verbose;
	public boolean activeScan;
	public JsonObject selector;
}
