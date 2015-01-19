/*
ALMA - Atacama Large Millimiter Array
* Copyright (c) European Southern Observatory, 2014 
* 
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.
* 
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA
*/

package alma.alarmsystem.statistics;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import cern.laser.business.pojo.AlarmMessageProcessorImpl;

import com.cosylab.acs.laser.AlarmSourcesListenerCached;

import alma.acs.logging.AcsLogLevel;

/**
 * A class to calculate various statistics 
 * on the alarms processed by the alarm server.
 * <P>
 * The statistics are
 * <UL>
 * 	<LI>logged at definite time intervals to avoid flooding the logging systems
 * 	<LI>saved on a file
 * <P>
 * <code>StatsCalculator</code> logs a minimal set of statistics on the logging system
 * but the full statistics are saved on the file.
 * <P>
 * Statistics are logged every time interval whose default is {@value #DEFAULTTIMEINTERVAL} minutes.
 * The time interval can be customized by setting the {@value #TIMEINTERVALPROPERTYNAME} java property.
 * A time interval of 0 minutes disable the logging of statistics (@see {@value #TIMEINTERVALPROPERTYNAME}).
 * <P>
 * Life cycle: 
 * {@link #start()} must be called to start gathering statistics and {@link #shutdown()} must be
 * executed when finished.
 * 
 * @author  acaproni
 * @since   ACS 2015.2
 */
public class StatsCalculator implements Runnable {

	/**
	 * The name of the property to customize the time interval (in minutes) to log
	 * statistics.
	 * <BR>Setting the value of this property to <code>0</code> disables the statistics.
	 */
	public static final String TIMEINTERVALPROPERTYNAME="alma.acs.alarmsystem.statistics.timeinterval";
	
	/**
	 * The default time interval is {@value #DEFAULTTIMEINTERVAL} minutes
	 */
	public static final int DEFAULTTIMEINTERVAL=10;
	
	/**
	 * Statistics are logged every time interval
	 */
	public final int timeInterval=Integer.getInteger(TIMEINTERVALPROPERTYNAME, DEFAULTTIMEINTERVAL);
	
	/**
	 * The executor to schedule the writing of statistics at every time interval.
	 */
	private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	
	/**
	 * The number of activations received in the last time interval.
	 * <P>
	 * These are the active alarms received from sources.
	 * 
	 */
	private final AtomicLong alarmsActivationForTimeInterval = new AtomicLong();
	
	/**
	 * The number of terminations received in the last time interval.
	 * <P>
	 * These are the terminate alarms received from sources.
	 */
	private final AtomicLong alarmsTerminationForTimeInterval = new AtomicLong();
	
	/**
	 * The number of messages received from the sources NC.
	 * <BR>
	 * Note that a message can contain more then one FS to process
	 */
	private final AtomicLong messagesFromSources = new AtomicLong();
	
	/**
	 * The logger
	 */
	private final Logger logger;
	
	/**
	 * The queue of messages received from the sources NC and waiting to be processed
	 */
	private final AlarmSourcesListenerCached sourceNCQueue;
	
	/**
	 * Constructor
	 * 
	 * @param logger The logger
	 */
	public StatsCalculator(Logger logger, AlarmSourcesListenerCached sourceNCQueue) {
		if (logger==null) {
			throw new IllegalArgumentException("The logger can't be null!");
		}
		this.logger=logger;
		if (sourceNCQueue==null) {
			throw new IllegalArgumentException("The AlarmSourcesListenerCached can't be null!");
		}
		this.sourceNCQueue=sourceNCQueue;
	}
	
	/**
	 * An FaultState with the passed ID and activation state 
	 * has been processed.
	 * <P>
	 * For the statistics, <code>StatsCalculator</code> does not distinguish if a 
	 * alarm is a change (@see {@link AlarmMessageProcessorImpl#processChange(cern.laser.source.alarmsysteminterface.FaultState, String, String, java.sql.Timestamp)}
	 * or a backup (<code>AlarmMessageProcessorImpl#processBackup(...)</code>).
	 * 
	 * @param alarmID The ID of the received alarm
	 * @param active The status of the alarm (<code>true</code> means active)
	 */
	public void processedFS(String alarmID,boolean active) {
		if (active) {
			alarmsActivationForTimeInterval.incrementAndGet();
		} else {
			alarmsTerminationForTimeInterval.incrementAndGet();
		}
	}
	
	/**
	 * A message from the source NC has been received.
	 * <BR>
	 * Note that a source can encapsulate more FS changes in the
	 * same message.
	 */
	public void msgFromSourceNCReceived() {
		messagesFromSources.incrementAndGet();
	}
	
	/**
	 * Start to collect statistics and spawn the timer task.
	 */
	public void start() {
		// Start the scheduled task
		if (timeInterval>0) {
			executor.scheduleWithFixedDelay(this, timeInterval, timeInterval, TimeUnit.MINUTES);
			Object[] params={"Time interval",Integer.valueOf(timeInterval)};
			logger.log(AcsLogLevel.INFO,"Gathering of statistics enabled",params);
		} else {
			logger.log(AcsLogLevel.INFO,"Gathering of statistics disabled (time interval<=0)");
		}
		
	}
	
	/**
	 * This method must be called when no more statistics must be collected and 
	 * published.
	 * It stops the timer task and frees all the resources.
	 */
	public void shutdown() {
		// Stop the timer task
		if (timeInterval>0) {
			logger.log(AcsLogLevel.INFO,"Shutting down stats calucaltion");
			executor.shutdown();
		}
	}
	
	/**
	 * The scheduler invokes this method at fixed time intervals to generate and publish statistics. 
	 * It also reset the variable to calculate the correct statistics at the next iteration.
	 */
	@Override
	public void run() {
		logStats();
		logStatsOnFile();
		resetStats();
	}
	
	/**
	 * Logs statistics.
	 * <P>
	 * <code>logStats()</code> publishes a single log using providing a map of
	 * the calculated values i.e. a map of &lt;name,Value&gt; pairs. 
	 */
	private void logStats() {
		//LogRecord log = new LogRecord(AcsLogLevel.INFO, "Alarm server statistics");
		Map<String, Long> params = new HashMap<String, Long>();
		long activations=alarmsActivationForTimeInterval.get();
		long terminations=alarmsTerminationForTimeInterval.get();
		params.put("FaultStatesProcessed", activations+terminations);
		params.put("Activations", activations);
		params.put("Terminations", terminations);
		params.put("NumOFMsgsFromSources",Long.valueOf(messagesFromSources.get()));
		params.put("MsgsWaitingToBeProcessed", Long.valueOf(sourceNCQueue.getQueueSize()));
		logger.log(AcsLogLevel.INFO,"Alarm server statistics",params);
	}
	
	/**
	 * Logs the statistic in a file
	 * <P>
	 * The information written on file is more complete then the 
	 * statistics logged by {@link #logStats()} 
	 */
	private void logStatsOnFile() {
		
	}
	
	/**
	 * Reset the fields of the statistics to be ready to gather numbers 
	 * for the next iteration.
	 */
	private void resetStats() {
		alarmsActivationForTimeInterval.set(0);
		alarmsTerminationForTimeInterval.set(0);
		messagesFromSources.set(0);
	}
}
