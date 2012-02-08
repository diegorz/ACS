/*******************************************************************************
 * ALMA - Atacama Large Millimeter Array
 * Copyright (c) ESO - European Southern Observatory, 2012
 * (in the framework of the ALMA collaboration).
 * All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA
 *******************************************************************************/
package alma.acs.commandcenter.serviceshelper;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.hibernate.Session;
import org.omg.CORBA.StringHolder;

import alma.ACSErr.Completion;
import alma.acs.commandcenter.engine.Executor.RemoteServicesDaemonFlow;
import alma.acs.commandcenter.serviceshelper.TMCDBServicesHelper.AcsServiceToStart;
import alma.acs.container.ContainerServices;
import alma.acs.logging.AcsLogger;
import alma.acs.util.AcsLocations;
import alma.acsdaemon.DaemonSequenceCallback;
import alma.acsdaemon.DaemonSequenceCallbackHelper;
import alma.acsdaemon.DaemonSequenceCallbackPOA;
import alma.acsdaemon.ServiceDefinitionBuilder;
import alma.acsdaemon.ServicesDaemon;
import alma.acsdaemon.ServicesDaemonHelper;
import alma.acsdaemonErrType.AcsStartServices;

/**
 * An helper to start the ACS services defined in the TMCDB with the 
 * services daemon.
 * <P>
 * The reference to the services daemon is acquired before starting/stopping services
 * and released when the operation terminates.
 * 
 * @author acaproni
 *
 */
public class StartServicesHelper {

	/**
     * The callback to notify the listeners about the progress of the async start/stop
     * of services.
     * 
     * @author acaproni
     *
     */
    public class DaemonSequenceCallbackImpl extends DaemonSequenceCallbackPOA
    {
    	/**
    	 * The logger
    	 */
        private final AcsLogger logger;

        public DaemonSequenceCallbackImpl(AcsLogger logger) {
        	if (logger==null) {
        		throw new IllegalArgumentException("The logger can't be null");
        	}
            this.logger = logger;
        }

        public void done(Completion comp) {
            logger.info("done: comp=" + comp.timeStamp);
        }

        public void working(String service, String host, short instance_number, Completion comp) {
            logger.info("working: service=" + service + " host=" + host + " instance=" + instance_number + " comp=" + comp.timeStamp);
        }
    }

	/**
	 * The listener of events generated starting and stopping 
	 * the services.
	 * 
	 * @author acaproni
	 *
	 */
	public interface ServicesListener {
		
		/**
		 * Invoked when all the start/stop of services terminated.
		 */
		public void done();
		
		/**
		 * Invoked when the service with the passed name has been
		 * successfully started.
		 * 
		 * @param name The name of the service
		 */
		public void serviceStarted(String name);
		
		/**
		 * Invoked when the service with the passed name has been
		 * successfully stopped.
		 * 
		 * @param name The name of the service
		 */
		public void serviceStopped(String name);
		
		/**
		 * Invoked when an error occurred trying to start or stop
		 * the service with the passed name.
		 * 
		 * @param name The name of the service
		 */
		public void error(String name);
		
	}
	
	/**
	 * The listener of events
	 */
	private final Set<ServicesListener> listeners = 
			Collections.synchronizedSet(new HashSet<ServicesListener>()); 
	
	/**
	 * The session to access the TMCDB
	 */
	private final Session session;
	
	/**
	 * The name of the configuration to get the list of services from
	 * the tmcdb
	 */
	private final String configurationName;
	
	/**
	 * The ContainerServices
	 */
	private final ContainerServices contSvcs;
	
	/**
	 * The host where the service daemon runs
	 */
	private final String daemonHost;
	
	/**
	 * The ACS instance
	 */
	private final int instance;
	
	/** 
	 * Constructor.
	 * 
	 * @param Session The hibernate session to read data from the TMCDB 
	 * @param configurationName The name of the configuration 
	 * @param contSvcs The {@link ContainerServices}
	 * @param daemonHost The host where the service daemon runs
	 * @param instance The ACS instance
	 */ 
	public StartServicesHelper(
			Session session, 
			String configurationName, 
			ContainerServices contSvcs,
			String daemonHost,
			int instance) {
		if (session==null) {
			throw new IllegalArgumentException("The name of the configuration can't be null nor empty!");
		}
		this.session=session;
		if (configurationName==null || configurationName.isEmpty()) {
			throw new IllegalArgumentException("The Session can't be NULL nor empty!");
		}
		this.configurationName=configurationName;
		if (contSvcs==null) {
			throw new IllegalArgumentException("The ContainerServices can't be null!");
		}
		this.contSvcs=contSvcs;
		if (daemonHost==null || daemonHost.isEmpty()) {
			throw new IllegalArgumentException("The host address of the service daemon can't be NULL nor empty!");
		}
		this.daemonHost=daemonHost;
		this.instance=instance;
	}

	/** 
	 * Reads from the TMCDB the list of ACS services to start. 
	 * The definition of the services and the host where we want to deploy them 
	 * is sent to an acs daemon in order to build an XML representation 
	 * that can be later used by the daemons to start all the services. 
	 * <P>
	 * Using this method is optional and expected only for special cases (in conjunction with {@link #startACSServices}), 
	 * because method {@link #startACSServices} includes this step.
	 * <P>
	 * This method delegates to {@link #internalGetServicesDescritpion(ServicesDaemon, ServiceDefinitionBuilder)}
	 * @return An XML representation of the services to start.
	 * @throws Exception In case of error getting the services
	 * 
	 *  @see #internalGetServicesDescritpion(ServicesDaemon, ServiceDefinitionBuilder)
	 */ 
	public String getServicesDescription() throws Exception {
		// Get the reference to the daemon
		ServicesDaemon daemon = getServicesDaemon();
		
		return internalGetServicesDescritpion(daemon);
	}
	
	/**
	 * Return the the XML string describing the services to start by reading the services from the
	 * TMCDB.
	 *  
	 * @param daemon That services deamon
	 * @return The XML string with the services to start
	 * @throws Exception In case of error reading the TMCDB or creating the XML with the {@link ServiceDefinitionBuilder}
	 */
	private String internalGetServicesDescritpion(
			ServicesDaemon daemon) throws Exception {
		if (daemon==null) {
			throw new IllegalArgumentException("The daemon can't be null");
		}
		// Get the service definition builder for the current instance
		ServiceDefinitionBuilder srvDefBuilder=daemon.create_service_definition_builder((short)instance);
		// Get the services from the TMCDB
		List<AcsServiceToStart> services = getServicesList();
		if (services.isEmpty()) {
			throw new Exception("No services to start from TMCDB");
		}
		// Add the services to the service definition builder
		for (AcsServiceToStart svc: services) {
			switch (svc.serviceType) {
			case MANAGER: {
				srvDefBuilder.add_manager(svc.hostName, "", false);
				break;
			}
			case ALARM: {
				srvDefBuilder.add_alarm_service(svc.hostName);
				break;
			}
			case CDB: {
				srvDefBuilder.add_xml_cdb(svc.hostName, false, "");
				break;
			}
			case IFR: {
				srvDefBuilder.add_interface_repository(svc.hostName, true, false);
				break;
			}
			case LOGGING: {
				srvDefBuilder.add_logging_service(svc.hostName, svc.serviceName);
				break;
			}
			case LOGPROXY: {
				srvDefBuilder.add_acs_log(svc.hostName);
				break;
			}
			case NAMING: {
				srvDefBuilder.add_naming_service(svc.hostName);
				break;
			}
			case NOTIFICATION: {
				srvDefBuilder.add_notification_service(svc.serviceName, svc.hostName);
				break;
			}
			default: {
				throw new Exception("Unknown type of service to start: "+svc.serviceType+", on "+svc.hostName);
			}
			}
		}
		String svcsXML = srvDefBuilder.get_services_definition();
		StringHolder errorStr = new StringHolder();
		if (!srvDefBuilder.is_valid(errorStr)) {
			// Error in the XML
			throw new Exception("Error in the services definition: "+errorStr.value);
		}
		return svcsXML;
	}

	/** 
	 * Starts the services whose XML definition is in the parameter. 
	 * <P> 
	 * The real starting of services is delegated to {@link #internalStartServices(ServicesDaemon, ServiceDefinitionBuilder, String)}
	 * 
	 * The starting of the services is delegated to a acs service daemon. 
	 * @param xmlListOfServices The XML describing the list of services to start. 
	 * It is generally returned by getServicesDescription() 
	 */ 
	public void startACSServices(String xmlListOfServices) throws Exception {
		if (xmlListOfServices==null || xmlListOfServices.isEmpty()) {
			throw new IllegalArgumentException("The XML list of services can't be null nor empty");
		}
		// Get the reference to the daemon
		ServicesDaemon daemon = getServicesDaemon();
		// Get the service definition builder for the current instance
		ServiceDefinitionBuilder srvDefBuilder=daemon.create_service_definition_builder((short)instance);
		srvDefBuilder.add_services_definition(xmlListOfServices);
		StringHolder errorStr = new StringHolder();
		if (!srvDefBuilder.is_valid(errorStr)) {
			// Error in the XML
			throw new Exception("Error in the services definition: "+errorStr.value);
		}
		internalStartServices(daemon, xmlListOfServices);
	}

	/** 
	 * Starts the services as they are defined in the TMCDB. 
	 * This method is a convenience that before first gets the list of services from the TMCDB 
	 * by calling getServicesDescription() and then executes startACSServices(String xmlListOfServices)
	 * <P> 
	 * The real starting of services is delegated to {@link #internalStartServices(ServicesDaemon, ServiceDefinitionBuilder, String)}
	 * 
	 * @return An XML representation of the started services, to be used in {@link #stopServices}.
	 * @throws Exception In case of error getting the list of services from the TMCDB 
	 *                   or getting the reference of the services daemon 
	 */
	public String startACSServices() throws Exception {
		// Get the reference to the daemon
		ServicesDaemon daemon = getServicesDaemon();
		// Get the services from the TMCDB
		String svcsXML=internalGetServicesDescritpion(daemon);
		// Start the services
		internalStartServices(daemon, svcsXML);
		return svcsXML;
	}
	
	/**
	 * Asks the daemon to start the services whose description is in the passed XML.
	 * <P>
	 * This method assumes that the XML has already been validated 
	 * by {@link ServiceDefinitionBuilder#is_valid(StringHolder)}
	 * 
	 * @param daemon The daemon to start the services
	 * @param builder The builder to validate the XML;
	 *                 If <code>null</code> an instance is retrieved from the daemon.
	 * @param svcsXML The XML string describing the services to start
	 * @throws Exception
	 */
	private void internalStartServices(
			ServicesDaemon daemon, 
			String svcsXML) 
	throws Exception {
		if (daemon==null) {
			throw new IllegalArgumentException("The daemon can't be null");
		}
		if (svcsXML==null || svcsXML.isEmpty()) {
			throw new IllegalArgumentException("The XML list of services can't be null nor empty");
		}
		 DaemonSequenceCallbackImpl callback = new DaemonSequenceCallbackImpl(contSvcs.getLogger());
         DaemonSequenceCallback daemonSequenceCallback = 
        		 DaemonSequenceCallbackHelper.narrow(contSvcs.activateOffShoot(callback));
         daemon.start_services(svcsXML, true, daemonSequenceCallback);
	}

	/** 
	 * Stops the services whose definition is in the passed parameter. 
	 * 
	 * @param xmlListOfServices The XML describing the list of services to stop.
	 * @throws Exception In case of error getting the services daemon 
	 */ 
	public void stopServices(String xmlListOfServices) throws Exception {
		if (xmlListOfServices==null || xmlListOfServices.isEmpty()) {
			throw new IllegalArgumentException("The XML list of services can't be null nor empty");
		}
		// Get the reference to the daemon
		ServicesDaemon daemon = getServicesDaemon();
		
		DaemonSequenceCallbackImpl callback = new DaemonSequenceCallbackImpl(contSvcs.getLogger());
        DaemonSequenceCallback daemonSequenceCallback = 
       		 DaemonSequenceCallbackHelper.narrow(contSvcs.activateOffShoot(callback));
        daemon.stop_services(xmlListOfServices, daemonSequenceCallback);
	}
	
	/**
	 * Add a listener to be notified about the progress of the start/stop of services.
	 *  
	 * @param listener The not <code>null</code> listener to add.
	 */
	public void addServiceListener(ServicesListener listener) {
		if (listener==null) {
			throw new IllegalArgumentException("The listener can't be null");
		}
		listeners.add(listener);
	}
	

	/**
	 * Remove the passed listener of the start/stop of services.
	 *  
	 * @param listener The not <code>null</code> listener to remove.
	 */
	public void removeServiceListener(ServicesListener listener) {
		if (listener==null) {
			throw new IllegalArgumentException("The listener can't be null");
		}
		listeners.remove(listener);
	}
	
	/**
	 * Get the list of services to start from the TMCDB by delegating to {@link TMCDBServicesHelper}
	 * @return The list of services to start.
	 * @throws Exception In case of error getting the list of services from the TMCDB
	 */
	private List<AcsServiceToStart> getServicesList() throws Exception {
		TMCDBServicesHelper tmcdbHelper = new TMCDBServicesHelper(contSvcs.getLogger(), session);
		return tmcdbHelper.getServicesList(configurationName);
	}
	
	/**
	 * Connects to the services daemon building the corba loc from its
	 * host address in ({@link #daemonHost}.
	 * 
	 * @return the reference to the services daemon
	 * 
	 * @throws Exception in case of error getting the services daemon
	 */
	private ServicesDaemon getServicesDaemon() throws Exception {
		ServicesDaemon daemon;
		String daemonLoc = AcsLocations.convertToServicesDaemonLocation(daemonHost);
		try {
			org.omg.CORBA.Object object = 
					contSvcs.getAdvancedContainerServices().getORB().string_to_object(daemonLoc);
			daemon = ServicesDaemonHelper.narrow(object);
			if (daemon == null)
				throw new NullPointerException("Received null trying to retrieve acs services daemon on "+daemonHost);
			if (daemon._non_existent()) // this may be superfluous with daemons but shouldn't hurt either
				throw new RuntimeException("Acs services daemon not existing on "+daemonHost);
		} catch (Throwable t) {
			throw new Exception("Error getting the services daemon "+t.getMessage(), t);
		}
		return daemon;
	}
}
