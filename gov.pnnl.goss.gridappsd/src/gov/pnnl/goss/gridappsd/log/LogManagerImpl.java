/*******************************************************************************
 * Copyright � 2017, Battelle Memorial Institute All rights reserved.
 * Battelle Memorial Institute (hereinafter Battelle) hereby grants permission to any person or entity 
 * lawfully obtaining a copy of this software and associated documentation files (hereinafter the 
 * Software) to redistribute and use the Software in source and binary forms, with or without modification. 
 * Such person or entity may use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of 
 * the Software, and may permit others to do so, subject to the following conditions:
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the 
 * following disclaimers.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and 
 * the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Other than as used herein, neither the name Battelle Memorial Institute or Battelle may be used in any 
 * form whatsoever without the express written consent of Battelle.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS �AS IS� AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * BATTELLE OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED 
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * General disclaimer for use with OSS licenses
 * 
 * This material was prepared as an account of work sponsored by an agency of the United States Government. 
 * Neither the United States Government nor the United States Department of Energy, nor Battelle, nor any 
 * of their employees, nor any jurisdiction or organization that has cooperated in the development of these 
 * materials, makes any warranty, express or implied, or assumes any legal liability or responsibility for 
 * the accuracy, completeness, or usefulness or any information, apparatus, product, software, or process 
 * disclosed, or represents that its use would not infringe privately owned rights.
 * 
 * Reference herein to any specific commercial product, process, or service by trade name, trademark, manufacturer, 
 * or otherwise does not necessarily constitute or imply its endorsement, recommendation, or favoring by the United 
 * States Government or any agency thereof, or Battelle Memorial Institute. The views and opinions of authors expressed 
 * herein do not necessarily state or reflect those of the United States Government or any agency thereof.
 * 
 * PACIFIC NORTHWEST NATIONAL LABORATORY operated by BATTELLE for the 
 * UNITED STATES DEPARTMENT OF ENERGY under Contract DE-AC05-76RL01830
 ******************************************************************************/

package gov.pnnl.goss.gridappsd.log;

import org.apache.felix.dm.annotation.api.Component;
import org.apache.felix.dm.annotation.api.ServiceDependency;
import org.apache.felix.dm.annotation.api.Start;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import gov.pnnl.goss.gridappsd.api.LogDataManager;
import gov.pnnl.goss.gridappsd.api.LogManager;
import gov.pnnl.goss.gridappsd.dto.LogMessage;
import gov.pnnl.goss.gridappsd.dto.LogMessage.LogLevel;
import gov.pnnl.goss.gridappsd.dto.LogMessage.ProcessStatus;

/**
 * This class implements functionalities for Internal Function 409 Log Manager.
 * LogManager is responsible for logging messages coming from platform and other
 * processes in log file/stream as well as data store.
 * 
 * @author shar064
 *
 */
@Component
public class LogManagerImpl implements LogManager {
	
	private static Logger log = LoggerFactory.getLogger(LogManagerImpl.class);

	@ServiceDependency 
	private volatile LogDataManager logDataManager;
	
	public LogManagerImpl() { }
	
	public LogManagerImpl(LogDataManager logDataManager) {
		this.logDataManager = logDataManager;
	}
	
	@Start
	public void start() {
		System.out.println("STARTING LOG MANAGER");
		log.debug("Starting "+this.getClass().getName());

	}

	
	@Override
	public void log(LogMessage message) {
		
		String process_id = message.getProcess_id();
		long timestamp = message.getTimestamp();
		String log_message = message.getLog_message();
		LogLevel log_level = message.getLog_level();
		ProcessStatus process_status = message.getProcess_status();
		Boolean storeToDB = message.getStoreToDB();
		String username = "system";
		String logString = String.format("%s|%s|%s|%s|%s\n%s\n", timestamp, process_id,
				process_status, username, log_level, log_message);
		switch(message.getLog_level()) {
			case TRACE:	log.trace(logString);
						break;
			case DEBUG:	log.debug(logString);
						break;
			case INFO:	log.info(logString);
						break;
			case WARN:	log.warn(logString);
						break;		
			case ERROR:	log.error(logString);
						break;
			case FATAL:	log.error(logString);
						break;
			default:	log.debug(logString);
						break;
				
		}
		

		if(storeToDB)
			store(process_id,username,timestamp,log_message,log_level,process_status);
		
	}
	
	private void store(String process_id, String username, long timestamp,
			String log_message, LogLevel log_level, ProcessStatus process_status) {
		
		//TODO: Save log in data store using DataManager
		logDataManager.store(process_id, username, timestamp,
				log_message, log_level, process_status);
		log.debug("log saved");
		

	}
	

	@Override
	public void get(LogMessage message) {
		
		String process_id = message.getProcess_id();
		long timestamp = message.getTimestamp();
		LogLevel log_level = message.getLog_level();
		ProcessStatus process_status = message.getProcess_status();
		String username = "system";
//		logDataManager.query(process_id, timestamp, log_level, process_status, username);
		
	}

}
