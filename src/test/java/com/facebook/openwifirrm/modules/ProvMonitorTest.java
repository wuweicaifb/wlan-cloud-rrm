/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.openwifirrm.modules;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.facebook.openwifirrm.DeviceDataManager;
import com.facebook.openwifirrm.RRMConfig;
import com.facebook.openwifirrm.mysql.DatabaseManager;
import com.facebook.openwifirrm.ucentral.UCentralClient;
import com.facebook.openwifirrm.ucentral.UCentralKafkaConsumer;

public class ProvMonitorTest {
	/** Test device data manager. */
	private DeviceDataManager deviceDataManager;

	/** Test provisioning monitor. */
	private ProvMonitor provMonitor;

	@BeforeEach
	void setup(TestInfo testInfo) {
		this.deviceDataManager = new DeviceDataManager();

		// Create config
		RRMConfig rrmConfig = new RRMConfig();

		// Create clients (null for now)
		UCentralClient client = null;
		UCentralKafkaConsumer consumer = null;
		DatabaseManager dbManager = null;

		// Instantiate dependent instances
		ConfigManager configManager = new ConfigManager(
			rrmConfig.moduleConfig.configManagerParams,
			deviceDataManager,
			client
		);
		DataCollector dataCollector = new DataCollector(
			rrmConfig.moduleConfig.dataCollectorParams,
			deviceDataManager,
			client,
			consumer,
			configManager,
			dbManager
		);
		Modeler modeler = new Modeler(
			rrmConfig.moduleConfig.modelerParams,
			deviceDataManager,
			consumer,
			client,
			dataCollector,
			configManager
		);

		// Instantiate ProvMonitor
		this.provMonitor = new ProvMonitor(
			rrmConfig.moduleConfig.provMonitorParams,
			deviceDataManager,
			modeler,
			client,
			null
		);
	}

	@Test
	@Order(1)
	void test_syncDataToProv() throws Exception {
		// TODO
	}
}
