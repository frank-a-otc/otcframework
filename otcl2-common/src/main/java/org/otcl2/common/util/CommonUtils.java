/**
* Copyright (c) otcl2.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*/
package org.otcl2.common.util;

import java.io.File;
import java.io.FileFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.otcl2.common.exception.OtclException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonUtils {

	private static Logger LOGGER = LoggerFactory.getLogger(CommonUtils.class);

	public static Class<?> loadClass(String clzName) {
		if (clzName == null) {
			throw new OtclException("", "Invalid value : null!");
		}
		Class<?> cls = null;
		try {
			cls = Class.forName(clzName);
		} catch (ClassNotFoundException e) {
			throw new OtclException("", e);
		}
		return cls;
	}

	public static String initCap(String str) {
		char[] chars = str.toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);
		return new String(chars);
	}

	public static String initLower(String str) {
		char[] chars = str.toCharArray();
		chars[0] = Character.toLowerCase(chars[0]);
		return new String(chars);
	}

	public static String replaceLast(String orginalStr, String searchStr, String replaceStr) {
		if (orginalStr == null || searchStr == null || replaceStr == null) {
			return orginalStr;
		}
		int idx = orginalStr.lastIndexOf(searchStr);
		if (idx < 0) {
			return orginalStr;
		}
		return orginalStr.substring(0, idx) + replaceStr + orginalStr.substring(idx + searchStr.length());
	}

	public static boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}

	public static String getStackTrace(Throwable throwable) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		throwable.printStackTrace(pw);
		return sw.getBuffer().toString();
	}

	public static FileFilter createFilenameFilter(final String ext) {
		FileFilter fileFilter = new FileFilter() {
			public boolean accept(File file) {
				if (file.getName().endsWith(ext) || file.isDirectory()) {
					return true;
				}
				return false;
			}
		};
		return fileFilter;
	}

	public static <T> Future<T> doAsycnCall(String id, Map<String, Future<T>> mapFutures, Callable<T> callable,
			ExecutorService executorService) {
		Future<T> future = null;
		try {
			future = executorService.submit(callable);
			if (mapFutures != null && id != null) {
				mapFutures.put(id, future);
			}
		} catch (OtclException e) {
			LOGGER.warn(e.getMessage());
		}
		return future;
	}

	public static <T> Map<String, T> waitForAsychCallsToComplete(Map<String, Future<T>> mapFutures,
			int responsePollingPauseDurationInMillis) {
		boolean isStillRunning = true;
		Set<String> processed = new HashSet<>();
//		long startTimeMillis = System.currentTimeMillis();
		Map<String, T> mapResponses = null;
		while (isStillRunning) {
			try {
//				if (!isStillRunning) {
//					break;
//				}
				Thread.sleep(responsePollingPauseDurationInMillis);
			} catch (InterruptedException e) {
				LOGGER.warn(e.getMessage());
			}
			isStillRunning = false;
			for (Entry<String, Future<T>> entry : mapFutures.entrySet()) {
				String thirdPartyId = entry.getKey();
				if (processed.contains(thirdPartyId)) {
					continue;
				}
				Future<T> future = entry.getValue();
				if (future.isDone()) {
					try {
						T result = future.get();
						if (mapResponses == null) {
							mapResponses = new HashMap<>();
						}
						mapResponses.put(thirdPartyId, result);
						processed.add(thirdPartyId);
					} catch (InterruptedException | ExecutionException e) {
						LOGGER.warn(getStackTrace(e));
						if (e instanceof ExecutionException) {
							processed.add(thirdPartyId);
							future.cancel(true);
						}
					} catch (Exception e) {
						processed.add(thirdPartyId);
						future.cancel(true);
					}
//				} else if (!future.isCancelled()){
//					boolean timeoutEnabled = vtConfigDto.getBoolean(TIMEOUT_ENABLED, responsePollingTimeoutEnabled);
//					long currentTimeMillis = System.currentTimeMillis();
//					if (timeoutEnabled && (currentTimeMillis - startTimeMillis) > 
//								vtConfigDto.getLong(TIMEOUT_DURATION, responsePollingTimeoutDurationMillis)) {
//						future.cancel(true);
//						String msg = new StringBuilder("Timedout! Cancelled Task of '").append(thirdPartyId).append("'").toString();
//						LOGGER.error(msg);
//						Object result = NdcUtilFacade.buildResponseWithError(requestWrapper, msg);
//						lstResponses.add(result);
//					} else {
//						isStillRunning = true; 
//					}
				} else {
					isStillRunning = true;
				}
			}
//			try {
////				if (!isStillRunning) {
////					break;
////				}
//				Thread.sleep(responsePollingPauseDurationInMillis);
//			} catch (InterruptedException e) {
//				LOGGER.warn(e.getMessage());
//			}
		}
		return mapResponses;
	}

	public static <T> T waitForAsychCallsToComplete(Future<T> future, int pollPauseDuration) {
		boolean isStillRunning = true;
		while (isStillRunning) {
			try {
//				if (!isStillRunning) {
//					break;
//				}
				Thread.sleep(pollPauseDuration);
			} catch (InterruptedException e) {
				LOGGER.warn(e.getMessage());
			}
			isStillRunning = false;
			if (future.isDone()) {
				try {
					T result = future.get();
				} catch (InterruptedException | ExecutionException e) {
					LOGGER.warn(getStackTrace(e));
					if (e instanceof ExecutionException) {
						future.cancel(true);
					}
				} catch (Exception e) {
					future.cancel(true);
				}
			} else {
				isStillRunning = true;
			}
		}
		try {
			return future.get();
		} catch (InterruptedException | ExecutionException e) {
			LOGGER.warn(e.getMessage(), e);
		}
		return null;
	}
}
