/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.openwifirrm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Generic utility methods.
 */
public class Utils {
	/** Hex value array for use in {@link #longToMac(long)}. */
	private static final char[] HEX_VALUES = "0123456789abcdef".toCharArray();

	/** The gson instance. */
	private static final Gson gson = new Gson();

	// This class should not be instantiated.
	private Utils() {}

	/** Simple LRU cache, adapted from: https://stackoverflow.com/a/1953516 */
	public static class LruCache<K, V> extends LinkedHashMap<K, V> {
		private static final long serialVersionUID = 4884066392195453453L;

		/** The cache size. */
		private final int maxEntries;

		/** Constructor. */
		public LruCache(int maxEntries) {
			super(maxEntries + 1, 1.0f, true);
			this.maxEntries = maxEntries;
		}

		@Override
		protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
			return super.size() > maxEntries;
		}
	}

	/** Thread factory based on DefaultThreadFactory supporting name prefix. */
	public static class NamedThreadFactory implements ThreadFactory {
		private static final AtomicInteger poolNumber = new AtomicInteger(1);
		private final ThreadGroup group;
		private final AtomicInteger threadNumber = new AtomicInteger(1);
		private final String namePrefix;

		/** Constructor. */
		public NamedThreadFactory(String prefix) {
			SecurityManager s = System.getSecurityManager();
			group = (s != null)
				? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
			namePrefix =
				prefix + "-pool-" + poolNumber.getAndIncrement() + "-thread-";
		}

		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(
				group,
				r,
				namePrefix + threadNumber.getAndIncrement(),
				0
			);
			if (t.isDaemon()) {
				t.setDaemon(false);
			}
			if (t.getPriority() != Thread.NORM_PRIORITY) {
				t.setPriority(Thread.NORM_PRIORITY);
			}
			return t;
		}
	}

	/** Read a file to a UTF-8 string. */
	public static String readFile(File f) throws IOException {
		byte[] b = Files.readAllBytes(f.toPath());
		return new String(b, StandardCharsets.UTF_8);
	}

	/** Write a string to a file. */
	public static void writeFile(File f, String s)
		throws FileNotFoundException {
		try (PrintStream out = new PrintStream(new FileOutputStream(f))) {
			out.println(s);
		}
	}

	/** Write an object to a file as pretty-printed JSON. */
	public static void writeJsonFile(File f, Object o)
		throws FileNotFoundException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		writeFile(f, gson.toJson(o));
	}

	/** Read a resource to a UTF-8 string. */
	public static String readResourceToString(String path) {
		InputStream is = Utils.class.getClassLoader().getResourceAsStream(path);
		try (Scanner scanner = new Scanner(is, "UTF-8")) {
			return scanner.useDelimiter("\\A").next();
		}
	}

	/** Recursively merge JSONObject 'b' into 'a'. */
	public static void jsonMerge(JSONObject a, JSONObject b) {
		for (String k : b.keySet()) {
			Object aVal = a.has(k) ? a.get(k) : null;
			Object bVal = b.get(k);
			if (aVal instanceof JSONObject && bVal instanceof JSONObject) {
				jsonMerge((JSONObject) aVal, (JSONObject) bVal);
			} else {
				a.put(k, bVal);
			}
		}
	}

	/**
	 * Convert a MAC address to an integer (6-byte) representation.
	 *
	 * If the MAC address could not be parsed, throws IllegalArgumentException.
	 */
	public static long macToLong(String addr) throws IllegalArgumentException {
		String s = addr.replace("-", "").replace(":", "").replace(".", "");
		if (s.length() != 12) {
			throw new IllegalArgumentException("Invalid MAC address format");
		}
		try {
			return Long.parseLong(s, 16);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Convert a MAC address in integer (6-byte) representation to string
	 * notation.
	 */
	public static String longToMac(long addr) {
		char[] c = new char[17];
		c[0] = HEX_VALUES[(byte) ((addr >> 44) & 0xf)];
		c[1] = HEX_VALUES[(byte) ((addr >> 40) & 0xf)];
		c[2] = ':';
		c[3] = HEX_VALUES[(byte) ((addr >> 36) & 0xf)];
		c[4] = HEX_VALUES[(byte) ((addr >> 32) & 0xf)];
		c[5] = ':';
		c[6] = HEX_VALUES[(byte) ((addr >> 28) & 0xf)];
		c[7] = HEX_VALUES[(byte) ((addr >> 24) & 0xf)];
		c[8] = ':';
		c[9] = HEX_VALUES[(byte) ((addr >> 20) & 0xf)];
		c[10] = HEX_VALUES[(byte) ((addr >> 16) & 0xf)];
		c[11] = ':';
		c[12] = HEX_VALUES[(byte) ((addr >> 12) & 0xf)];
		c[13] = HEX_VALUES[(byte) ((addr >> 8) & 0xf)];
		c[14] = ':';
		c[15] = HEX_VALUES[(byte) ((addr >> 4) & 0xf)];
		c[16] = HEX_VALUES[(byte) (addr & 0xf)];
		return new String(c);
	}

	/** Return a hex representation of the given byte array. */
	public static String bytesToHex(byte[] b) {
		char[] c = new char[b.length * 2];
		for (int i = 0; i < b.length; i++) {
			int v = b[i] & 0xff;
			c[i * 2] = HEX_VALUES[(v >> 4) & 0xf];
			c[i * 2 + 1] = HEX_VALUES[v & 0xf];
		}
		return new String(c);
	}

	/** Return a deep copy using gson. DO NOT USE if performance is critical. */
	public static <T> T deepCopy(T obj, Class<T> classOfT) {
		return gson.fromJson(gson.toJson(obj), classOfT);
	}
}
