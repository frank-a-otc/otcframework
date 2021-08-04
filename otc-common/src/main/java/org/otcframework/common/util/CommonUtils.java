/**
* Copyright (c) otcframework.org
*
* @author  Franklin Abel
* @version 1.0
* @since   2020-06-08 
*
* This file is part of the OTC framework.
* 
*  The OTC framework is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, version 3 of the License.
*
*  The OTC framework is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  A copy of the GNU General Public License is made available as 'License.md' file, 
*  along with OTC framework project.  If not, see <https://www.gnu.org/licenses/>.
*
*/
package org.otcframework.common.util;

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

import org.otcframework.common.exception.OtcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class CommonUtils.
 */
// TODO: Auto-generated Javadoc
public class CommonUtils {

	/** The logger. */
	private static Logger LOGGER = LoggerFactory.getLogger(CommonUtils.class);

	/**
	 * Inits the cap.
	 *
	 * @param str the str
	 * @return the string
	 */
	public static String initCap(String str) {
		char[] chars = str.toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);
		return new String(chars);
	}

	/**
	 * Inits the lower.
	 *
	 * @param str the str
	 * @return the string
	 */
	public static String initLower(String str) {
		char[] chars = str.toCharArray();
		chars[0] = Character.toLowerCase(chars[0]);
		return new String(chars);
	}

	/**
	 * Replace last.
	 *
	 * @param orginalStr the orginal str
	 * @param searchStr  the search str
	 * @param replaceStr the replace str
	 * @return the string
	 */
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

	/**
	 * Checks if is empty.
	 *
	 * @param str the str
	 * @return true, if is empty
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}

	/**
	 * Gets the stack trace.
	 *
	 * @param throwable the throwable
	 * @return the stack trace
	 */
	public static String getStackTrace(Throwable throwable) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		throwable.printStackTrace(pw);
		return sw.getBuffer().toString();
	}

	/**
	 * Creates the filename filter.
	 *
	 * @param ext the ext
	 * @return the file filter
	 */
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

	/**
	 * Do asycn call.
	 *
	 * @param <T>             the generic type
	 * @param id              the id
	 * @param mapFutures      the map futures
	 * @param callable        the callable
	 * @param executorService the executor service
	 * @return the future
	 */
	public static <T> Future<T> doAsycnCall(String id, Map<String, Future<T>> mapFutures, Callable<T> callable,
			ExecutorService executorService) {
		Future<T> future = null;
		try {
			future = executorService.submit(callable);
			if (mapFutures != null && id != null) {
				mapFutures.put(id, future);
			}
		} catch (OtcException e) {
			LOGGER.warn(e.getMessage());
		}
		return future;
	}

	/**
	 * Wait for asych calls to complete.
	 *
	 * @param <T>                                  the generic type
	 * @param mapFutures                           the map futures
	 * @param responsePollingPauseDurationInMillis the response polling pause
	 *                                             duration in millis
	 * @return the map
	 */
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

	/**
	 * Wait for asych calls to complete.
	 *
	 * @param <T>               the generic type
	 * @param future            the future
	 * @param pollPauseDuration the poll pause duration
	 * @return the t
	 */
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
