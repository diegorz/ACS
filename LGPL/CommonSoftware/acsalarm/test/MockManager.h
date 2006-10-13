#ifndef mockManager_h
#define mockManager_h

#include <acsutil.h>
#include <maciExport.h>
#include <maciS.h>
#include <cdb.h>
#include <logging.h>

namespace maci 
{
	using namespace cdb;

	class maci_EXPORT MockManager: public virtual POA_maci::Manager, public virtual maci::Manager
	{
		public:

		MockManager(void) { }

		/* ----------------------------------------------------------------*/
		/* --------------------- [ CORBA interface ] ----------------------*/
		/* ----------------------------------------------------------------*/

		//virtual void refresh_logging_config() throw (CORBA::SystemException);

		/** 
		   Login to MACI. 
		   Containers, Clients and Administrative clients call 
		   this function first to identify themselves 
		   with the Manager. 
		   The Manager authenticates them (through the authenticate 
		   function), and assigns them access rights and a handle, 
		   through which they will identify themselves 
		   at subsequent calls to the Manager.

		   @return A ClientInfo structure with handle (h) 
		   and access fields filled-in. 
		   If the client with this name did not logout prior to 
		   calling login, the components sequence in ClientInfo 
		   contains the handles of all components that the 
		   client was using. 
		   (For containers, the components sequence contains 
		   handles of all components previously hosted by the container.)
		*/
 		virtual maci::ClientInfo * login (maci::Client_ptr reference) throw (CORBA::SystemException) { return NULL; }

		/** 
	    Logout from MACI.
	 	*/
		virtual void logout (maci::Handle id) throw (CORBA::SystemException) {}

		/** 
	    Register a CORBA object as a component, 
	    assigning it a CURL and making it accessible 
	    through the Manager. 
	    The component is treated as an immortal component.

	    @return Returns the handle of the newly created component.
	 	*/
		virtual ::maci::Handle register_component (maci::Handle id, const char * component_url, const char * type, CORBA::Object_ptr c)
           throw (CORBA::SystemException, maciErrType::CannotRegisterComponentEx) { return 0; }

		/** 
	    Unregister a component from the Manager.
		 */
		virtual void unregister_component (maci::Handle id, maci::Handle h) throw (CORBA::SystemException, maciErrType::CannotUnregisterComponentEx) {}

		/** 
	    Get a service, activating it if necessary (components). 
	    The client represented by id (the handle) 
	    must have adequate access rights to access 
	    the service. 
	    NOTE: a component is also a service, i.e. a service activated by a container.
	    
	    @return Reference to the service. 
	    If the service could not be activated, a nil 
	    reference is returned, and the status contains 
	    an error code detailing the cause of failure 
	    (one of the COMPONENT_* constants).
	 	*/
		virtual CORBA::Object_ptr get_service (maci::Handle id, const char * service_url, CORBA::Boolean activate)
         throw (CORBA::SystemException, maciErrType::CannotGetComponentEx, maciErrType::ComponentNotAlreadyActivatedEx, 
                maciErrType::ComponentConfigurationNotFoundEx);

		/** 
	    Get a component, activating it if necessary. 
	    The client represented by id (the handle) must 
	    have adequate access rights to access the component.
	    
	    @return Reference to the component. 
	    If the component could not be activated, 
	    a nil reference is returned, and the status 
	    contains an error code detailing the cause of 
	    failure (one of the COMPONENT_* constants).
		*/
		virtual ::CORBA::Object_ptr get_component (maci::Handle id, const char * service_url, CORBA::Boolean activate)
			throw (CORBA::SystemException, maciErrType::CannotGetComponentEx, maciErrType::ComponentNotAlreadyActivatedEx, 
			maciErrType::ComponentConfigurationNotFoundEx) { return CORBA::Object::_nil(); }

		/** 
		    Get a non-sticky reference to a component.

		    A non-sticky reference does not bind the
		    Manager to keep alive the Component and 
		    the Client requesting for a non-sticky references
		    is not considered when checking for reference
		    counts. 
		    The Manager can deactivate Components
		    independently from any non-sticky reference.

		    This is typically used by "weak clients" like
		    graphical user interfaces.

                    Since a non-sticky reference is not considered in
                    reference counting, it will also not activate
		    the component if it is not already active.

		    As a consequence, asking for a non-sticky reference
                    to a not-active Component throws an exception.
  
		    The client represented by id (the handle) must 
		    have adequate access rights to access the component.
		    
		    @return Reference to the component. 
		*/
		virtual ::CORBA::Object_ptr get_component_non_sticky (maci::Handle id, const char * component_url)
      	throw (CORBA::SystemException, maciErrType::CannotGetComponentEx, maciErrType::ComponentNotAlreadyActivatedEx) { return CORBA::Object::_nil(); }

		/** 
		    Used for retrieving several services with one call. 
		    See get_service.

		    @deprecated This method is deprecated and will be
		    removed.

		    @return A sequence of requested services.
		 */
		virtual ::maci::ObjectSeq * get_services (maci::Handle id, const ::maci::CURLSeq & service_urls, CORBA::Boolean activate, 
			maci::ulongSeq_out status)
      	throw (CORBA::SystemException, maciErrType::CannotGetServiceEx) { return NULL; }

		/** 
		    Used for retrieving several components with one call. 
		    See get_component.
		    
		    @deprecated This method is deprecated and will be
		    removed.

		    @return A sequence of requested components.
		 */
		virtual ::maci::ObjectSeq * get_components (maci::Handle id, const maci::CURLSeq & component_urls, CORBA::Boolean activate, 
			maci::ulongSeq_out status)
      	throw (CORBA::SystemException, maciErrType::CannotGetComponentEx) { return NULL; }

		/** Change mortality state of an component. 
		    Component must be already active, otherwise 
		    CORBA::NO_RESOURCE exception will be thrown. 
		    The caller must be an owner of an component or 
		    have administator rights, otherwise CORBA::NO_PERMISSION 
		    exception will be thrown. 
		*/
		virtual void make_component_immortal (maci::Handle id, const char * component_url, CORBA::Boolean immortal_state)
      	throw (CORBA::SystemException) {}
    
		/** 
		    Release a component. 
		    In order for this operation to be possible, 
		    the caller represented by the id must have 
		    previously successfuly requested the 
		    component via a call to get_component.

		    Releasing a component more times than 
		    requesting it should be avoided, 
		    but it produces no errors.

		    @return Number of clients that are still using the 
		    component after the operation completed. 
		    This is a useful debugging tool.
		 */
		virtual ::CORBA::Long release_component (maci::Handle id, const char * component_url)
      	throw (CORBA::SystemException) { return 0; }

		/** 
		    Releases a component also if still referenced by other components/clients.
                    @return Number of clients that were still referencing the component 
		    after the operation completed. This is a useful debugging tool.
		 */
		virtual ::CORBA::Long force_release_component (maci::Handle id, const char * component_url)
      	throw (CORBA::SystemException) {  return 0; }
    
		/** Release components.
		 */
		virtual void release_components (maci::Handle id, const ::maci::CURLSeq & component_urls)
      	throw (CORBA::SystemException) {}

		/** 
		    Shutdown the Manager.

		    <B>Warning:</B> This call will also deactivate all
		    components active in the system, including startup
		    and immortal components. 
		 */
		virtual void shutdown (maci::Handle id, CORBA::ULong containers)
      	throw (CORBA::SystemException) {}

		/** 
		    Get all the information that the Manager has about its known
		    containers. To invoke this method, the caller must have
		    INTROSPECT_MANAGER access rights, or it must be the object whose info
		    it is requesting.
		    
		    Calling this function does not affect the internal state of the Manager.
		    
		    @return A sequence of ContainerInfo structures
		    containing the entire Manager's knowledge about the
		    containers. If access is denied to a subset of
		    objects, the handles to those objects are set to 0. 
		*/
		virtual ::maci::ContainerInfoSeq * get_container_info (maci::Handle id, const maci::HandleSeq & h, const char * name_wc)
      	throw (CORBA::SystemException) { return NULL; }

		/** 
		    Get all the information that the Manager has about its current
		    clients. To invoke this method, the caller must have
		    INTROSPECT_MANAGER access rights, or it must be the object whose info
		    it is requesting.

		    Calling this function does not affect the internal state of the Manager.
		    
		    @return A sequence of ClientInfo structures
		    containing the entire Manager's knowledge about the
		    clients. If access is denied to a subset of objects,
		    the handles to those objects are set to 0. 
		*/
		virtual maci::ClientInfoSeq * get_client_info (maci::Handle id, const maci::HandleSeq & h, const char * name_wc)
      	throw (CORBA::SystemException) { return NULL; }
    
		/** 
		    Get all the information that the Manager has about components. To
		    invoke this method, the caller must have INTROSPECT_MANAGER access
		    rights, or it must have adequate privileges to access the component
		    (the same as with the get_component method).

		    Information about all components is returned,
		    unless the active_only parameter is set to True,
		    in which case only information about those
		    components that are currently registered with the
		    Manager and activated is returned. 

		    Calling this function does not affect the internal
		    state of the Manager. 

		    @return A sequence of ComponentInfo structures
		    containing the entire Manager's knowledge about
		    the components. If access is denied to a subset of
		    objects, the handles to those objects are set to
		    0.

		 */
		virtual maci::ComponentInfoSeq * get_component_info (maci::Handle id, const maci::HandleSeq & h, const char * name_wc,
			const char * type_wc, CORBA::Boolean active_only)
      	throw (CORBA::SystemException) { return NULL; }

		/** 
		    Restarts a component. 
		*/
		virtual ::CORBA::Object_ptr restart_component (maci::Handle client, const char * component_url)
      	throw (CORBA::SystemException, maciErrType::CannotGetComponentEx) { return CORBA::Object::_nil(); }

		/** 
		    Activation of dynamic component. 
		*/
		virtual maci::ComponentInfo * get_dynamic_component (maci::Handle client, const maci::ComponentSpec & c, CORBA::Boolean mark_as_default)
      	throw (CORBA::SystemException, maciErrType::IncompleteComponentSpecEx, maciErrType::InvalidComponentSpecEx, 
				maciErrType::ComponentSpecIncompatibleWithActiveComponentEx, maciErrType::CannotGetComponentEx) { return NULL; }

		/** 
		    Group request of dynamic components. 
		*/
		virtual maci::ComponentInfoSeq * get_dynamic_components (maci::Handle client, const maci::ComponentSpecSeq & components)
      	throw (CORBA::SystemException, maciErrType::IncompleteComponentSpecEx, maciErrType::InvalidComponentSpecEx, 
				maciErrType::ComponentSpecIncompatibleWithActiveComponentEx, maciErrType::CannotGetComponentEx) { return NULL; }

		/** 
		    Activation of a component so that it runs in the same process as
		    another given component. 
		*/
		virtual maci::ComponentInfo * get_collocated_component (maci::Handle client, const maci::ComponentSpec & c, CORBA::Boolean mark_as_default,
			const char * target_component)
      	throw (CORBA::SystemException, maciErrType::IncompleteComponentSpecEx, maciErrType::InvalidComponentSpecEx, 
				maciErrType::ComponentSpecIncompatibleWithActiveComponentEx, maciErrType::CannotGetComponentEx) { return NULL; }

		/** 
	    Returns the default component of specific type. 
		*/
		virtual maci::ComponentInfo * get_default_component (maci::Handle client, const char * component_type)
      	throw (CORBA::SystemException, maciErrType::NoDefaultComponentEx, maciErrType::CannotGetComponentEx) { return NULL; }

		/** 
	    Shutdown a container.
		*/
		virtual void shutdown_container (maci::Handle id, const char * container_name, CORBA::ULong action)
      	throw (CORBA::SystemException) {}


		virtual maci::LoggingConfigurable::LogLevels get_default_logLevels() throw (CORBA::SystemException) { maci::LoggingConfigurable::LogLevels ll; return ll; }
		virtual maci::LoggingConfigurable::LogLevels get_logLevels(const char*) throw (CORBA::SystemException) { maci::LoggingConfigurable::LogLevels ll; return ll; }
		virtual void set_logLevels(const char*, const maci::LoggingConfigurable::LogLevels&) throw (CORBA::SystemException) {}
		virtual void set_default_logLevels(const maci::LoggingConfigurable::LogLevels&) throw (CORBA::SystemException) {}
		virtual void refresh_logging_config() throw (CORBA::SystemException) {}

		virtual maci::stringSeq* get_logger_names() throw (CORBA::SystemException) { return NULL; }
		virtual char* domain_name() throw (CORBA::SystemException) { return NULL; }
  	        virtual CORBA::Boolean ping() throw (CORBA::SystemException) { return false; }

	};
}

#endif // mockManager_h
