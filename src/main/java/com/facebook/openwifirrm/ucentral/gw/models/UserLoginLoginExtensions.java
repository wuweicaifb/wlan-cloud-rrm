/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.openwifirrm.ucentral.gw.models;

import java.util.List;

public class UserLoginLoginExtensions {
	public List<MobilePhoneNumber> mobiles;
	public String authenticatorSecret;
	public MfaAuthInfo mfa;
}
