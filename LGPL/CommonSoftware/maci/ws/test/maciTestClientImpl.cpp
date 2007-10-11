// -*- C++ -*-
//
// $Id: maciTestClientImpl.cpp,v 1.84 2007/10/11 16:06:00 msekoran Exp $

// ****  Code generated by the The ACE ORB (TAO) IDL Compiler ****
// TAO and the TAO IDL Compiler have been developed by:
//       Center for Distributed Object Computing
//       Washington University
//       St. Louis, MO
//       USA
//       http://www.cs.wustl.edu/~schmidt/doc-center.html
// and
//       Distributed Object Computing Laboratory
//       University of California at Irvine
//       Irvine, CA
//       USA
//       http://doc.ece.uci.edu/
//
// Information about TAO is available at:
//     http://www.cs.wustl.edu/~schmidt/TAO.html

#include <maciTestClientImpl.h>
#include <maciTestUtils.h>

const char *NULL_STR = "NULL";
const char *NON_NULL_STR = "NON_NULL";

ACE_CString HandleSeqToString(const maci::HandleSeq &seq)
{
  ACE_CString str("");
  for (CORBA::ULong j = 0; j < seq.length(); ++j)
    {
      char tmp[32];
      ACE_OS::sprintf(tmp, "%08x", seq[j]);
      if (str.length() > 0)
        str += ",";
      str += tmp;
    }
  return str;
}

// Implementation skeleton constructor
MaciTestClientImpl::MaciTestClientImpl (ACE_CString name,
      maci::Manager_ptr mgr, int onPing) :
    m_name (name),
    m_manager (mgr),
    m_onPing (onPing)
  {
    ACS_SHORT_LOG((LM_INFO, "MaciTestClientImpl::MaciTestClientImpl"));
  }

// Implementation skeleton destructor
MaciTestClientImpl::~MaciTestClientImpl (void)
  {
    ACS_SHORT_LOG((LM_INFO, "MaciTestClientImpl::~MaciTestClientImpl"));
  }

char * MaciTestClientImpl::name (
    
  )
  throw (
    CORBA::SystemException
  )

  {
    ACS_SHORT_LOG((LM_INFO, "MaciTestClientImpl::name"));
    return CORBA::string_dup (m_name.c_str());
  }

void MaciTestClientImpl::disconnect (
    
  )
  throw (
    CORBA::SystemException
  )

  {
    ACS_SHORT_LOG((LM_INFO, "MaciTestClientImpl::disconnect"));
    m_manager->logout (m_handle);
  }

maci::AuthenticationData * MaciTestClientImpl::authenticate (
    maci::ExecutionId execution_id,
    const char * question
  )
  throw (
    CORBA::SystemException
  )

  {
    ACS_SHORT_LOG((LM_INFO, "MaciTestClientImpl::authenticate"));
  
    maci::AuthenticationData_var data = new maci::AuthenticationData();
    data->answer = CORBA::string_dup("");
    data->client_type = maci::CLIENT_TYPE;
    data->impl_lang = maci::CPP;
    data->recover = false;
    data->timestamp = 0;
    data->execution_id = 0;

    return data._retn();
  }

void MaciTestClientImpl::message (
    CORBA::Short type,
    const char * message
    
  )
  throw (
    CORBA::SystemException
  )

  {
    ACS_SHORT_LOG((LM_INFO, "MaciTestClientImpl::message: '%s'", message));
  }

CORBA::Boolean MaciTestClientImpl::ping (
    
  )
  throw (
    CORBA::SystemException
  )

  {
    ACS_SHORT_LOG((LM_INFO,
                   "MaciTestClientImpl::ping (client '%s', action %d)",
                   m_name.c_str(), m_onPing));

    if (m_onPing == 0)
      return true;
    if (m_onPing == 1)
      return false;
    if (m_onPing == 2) {
      throw CORBA::TRANSIENT ();
      return 0;
    }

    return false;
  }

void MaciTestClientImpl::components_available (
    const maci::ComponentInfoSeq & components
    
  )
  throw (
    CORBA::SystemException
  )

  {
    ACS_SHORT_LOG((LM_INFO, "MaciTestClientImpl::components_available"));
    for (CORBA::ULong i = 0; i < components.length(); ++i)
      {
        ACE_CString clients;
        clients = HandleSeqToString(components[i].clients);
        ACS_SHORT_LOG((LM_INFO,
                       "\n\t\tHandle: %08x\n\t\tName: %s\n\t\tType: %s\n"
                       "\t\tReference: %s\n\t\tClients: %s\n\t\tContainer: %08x\n"
                       "\t\tAccess: %08x",
                       components[i].h, (const char*)components[i].name,
                       (const char*)components[i].type,
                       ((components[i].reference == NULL)?NULL_STR:NON_NULL_STR),
                       clients.c_str(), components[i].container,
                       components[i].access));
      }
  }

void MaciTestClientImpl::components_unavailable (
    const maci::stringSeq & component_names
    
  )
  throw (
    CORBA::SystemException
  )

  {
    ACS_SHORT_LOG((LM_INFO, "MaciTestClientImpl::components_unavailable"));
    ACE_CString str("");
    for(CORBA::ULong i = 0; i < component_names.length(); ++i)
      {
        if (str.length() > 0)
          str += ",";
        str += (const char*)component_names[i];
      }
    ACS_SHORT_LOG((LM_INFO, "Component names: %s", str.c_str()));
  }

// Implementation skeleton constructor
MaciTestContainerImpl::MaciTestContainerImpl (
      ACE_CString name,
      maci::Manager_ptr mgr,
      PortableServer::POA_ptr poa,
      int onPing,
      int onActivate,
      bool haveRecovery)
      : MaciTestClientImpl(name, mgr, onPing),
        m_onActivate(onActivate),
        m_poa(poa),
        m_haveRecovery(haveRecovery)
  {

  }

// Implementation skeleton destructor
MaciTestContainerImpl::~MaciTestContainerImpl (void)
  {
  }

maci::ComponentInfo * MaciTestContainerImpl::activate_component (
    maci::Handle h,
    maci::ExecutionId execution_id,
    const char * name,
    const char * exe,
    const char * type
    
  )
  throw (
    CORBA::SystemException
  )

  {
    ACS_SHORT_LOG((LM_INFO,
                   "MaciTestContainerImpl::activate_component: %08x, %s, %s, %s",
                    h, name, exe, type));
    maci::ComponentInfo_var ci = new maci::ComponentInfo();
    ci->h = 0;
    ci->reference = NULL;
    return ci._retn();
  }

void MaciTestContainerImpl::deactivate_components (
    const maci::HandleSeq & h
    
  )
  throw (
    CORBA::SystemException
  )

  {
    ACE_CString handles;
    handles = HandleSeqToString(h);
    ACS_SHORT_LOG((LM_INFO, "MaciTestContainerImpl::deactivate_components: %s",
                   handles.c_str()));
  }

void MaciTestContainerImpl::shutdown (
    CORBA::ULong action
    
  )
  throw (
    CORBA::SystemException
  )

  {
    ACS_SHORT_LOG((LM_INFO,
                   "MaciTestContainerImpl::shutdown: %d",
                   action));
  }

maci::ComponentInfoSeq * MaciTestContainerImpl::get_component_info (
    const maci::HandleSeq & h
    
  )
  throw (
    CORBA::SystemException
  )

  {
    ACS_SHORT_LOG((LM_INFO,
                   "MaciTestContainerImpl::get_component_info: %s",
                   HandleSeqToString(h).c_str()));
    return NULL;
  }

char * MaciTestContainerImpl::name (
    
  )
  throw (
    CORBA::SystemException
  )

  {
    ACS_SHORT_LOG((LM_INFO, "MaciTestContainerImpl::name"));
    return MaciTestClientImpl::name();
  }

void MaciTestContainerImpl::disconnect (
    
  )
  throw (
    CORBA::SystemException
  )

  {
    ACS_SHORT_LOG((LM_INFO, "MaciTestContainerImpl::disconnect"));
    MaciTestClientImpl::disconnect();
  }

maci::AuthenticationData * MaciTestContainerImpl::authenticate (
    maci::ExecutionId execution_id,
    const char * question
  )
  throw (
    CORBA::SystemException
  )

  {
    ACS_SHORT_LOG((LM_INFO, "MaciTestContainerImpl::authenticate"));
    maci::AuthenticationData_var data = new maci::AuthenticationData();
    data->answer = CORBA::string_dup("");
    data->client_type = maci::CONTAINER_TYPE;
    data->impl_lang = maci::CPP;
    data->recover = m_haveRecovery;
    data->timestamp = 0;
    data->execution_id = 0;

    return data._retn();
  }

void MaciTestContainerImpl::message (
    CORBA::Short type,
    const char * message
    
  )
  throw (
    CORBA::SystemException
  )

  {
    ACS_SHORT_LOG((LM_INFO, "MaciTestContainerImpl::message"));
    MaciTestClientImpl::message(type, message);
  }

CORBA::Boolean MaciTestContainerImpl::ping (
    
  )
  throw (
    CORBA::SystemException
  )

  {
    ACS_SHORT_LOG((LM_INFO, "MaciTestContainerImpl::ping"));
    return MaciTestClientImpl::ping();
  }

void MaciTestContainerImpl::components_available (
    const maci::ComponentInfoSeq & components
    
  )
  throw (
    CORBA::SystemException
  )

  {
    ACS_SHORT_LOG((LM_INFO, "MaciTestContainerImpl::components_available"));
    MaciTestClientImpl::components_available(components);
  }

void MaciTestContainerImpl::components_unavailable (
    const maci::stringSeq & component_names
    
  )
  throw (
    CORBA::SystemException
  )

  {
    ACS_SHORT_LOG((LM_INFO, "MaciTestContainerImpl::components_unavailable"));
    MaciTestClientImpl::components_unavailable(component_names);
  }

// Implementation skeleton constructor
MaciTestAdministratorImpl::MaciTestAdministratorImpl (
    ACE_CString name,
    maci::Manager_ptr mgr,
    int onPing)
    : MaciTestClientImpl(name, mgr, onPing)
  {
  }

// Implementation skeleton destructor
MaciTestAdministratorImpl::~MaciTestAdministratorImpl (void)
  {
  }

void MaciTestAdministratorImpl::client_logged_in (
    const maci::ClientInfo & info,
    ACS::Time timestamp,
    maci::ExecutionId execution_id
  )
  throw (
    CORBA::SystemException
  )

  {
    ACS_SHORT_LOG((LM_INFO, "MaciTestAdministratorImpl::client_logged_in"));
    ACS_SHORT_LOG((LM_INFO,
                   "\n\t\tHandle: %08x\n\t\tName: %s\n"
                   "\t\tReference: %s\n\t\tComponents: %s\n"
                   "\t\tAccess: %08x",
                   info.h, (const char*)info.name,
                   ((info.reference == NULL)?NULL_STR:NON_NULL_STR),
                   HandleSeqToString(info.components).c_str(),
                   info.access));
  }

void MaciTestAdministratorImpl::client_logged_out (
    maci::Handle h,
    ACS::Time timestamp
  )
  throw (
    CORBA::SystemException
  )

  {
    ACS_SHORT_LOG((LM_INFO,
                   "MaciTestAdministratorImpl::client_logged_out: %08x",
                   h));
  }

void MaciTestAdministratorImpl::container_logged_in (
    const maci::ContainerInfo & info,
    ACS::Time timestamp,
    maci::ExecutionId execution_id
    )
  throw (
    CORBA::SystemException
  )

  {
    ACS_SHORT_LOG((LM_INFO, "MaciTestAdministratorImpl::container_logged_in"));
    ACS_SHORT_LOG((LM_INFO,
                   "\n\t\tHandle: %08x\n\t\tName: %s\n"
                   "\t\tReference: %s\n\t\tComponents: %s",
                   info.h, (const char*)info.name,
                   ((info.reference == NULL)?NULL_STR:NON_NULL_STR),
                   HandleSeqToString(info.components).c_str()));
  }

void MaciTestAdministratorImpl::container_logged_out (
    maci::Handle h,
    ACS::Time timestamp 
  )
  throw (
    CORBA::SystemException
  )

  {
    ACS_SHORT_LOG((LM_INFO,
                   "MaciTestAdministratorImpl::container_logged_out: %08x",
                   h));
  }

void MaciTestAdministratorImpl::components_requested (
    const maci::HandleSeq & clients,
    const maci::HandleSeq & components,
    ACS::Time timestamp
  )
  throw (
    CORBA::SystemException
  )

  {
    ACS_SHORT_LOG((LM_INFO, "MaciTestAdministratorImpl::components_requested"));
    ACS_SHORT_LOG((LM_INFO,
                   "\n\t\tClients: %s\n\t\tComponents: %s",
                   HandleSeqToString(clients).c_str(),
                   HandleSeqToString(components).c_str()));
  }

void MaciTestAdministratorImpl::components_released (
    const maci::HandleSeq & clients,
    const maci::HandleSeq & components,
    ACS::Time timestamp
  )
  throw (
    CORBA::SystemException
  )

  {
    ACS_SHORT_LOG((LM_INFO, "MaciTestAdministratorImpl::components_released"));
    ACS_SHORT_LOG((LM_INFO,
                   "\n\t\tClients: %s\n\t\tComponents: %s",
                   HandleSeqToString(clients).c_str(),
                   HandleSeqToString(components).c_str()));
  }

void MaciTestAdministratorImpl::component_activated (
    const maci::ComponentInfo & info,
    ACS::Time timestamp,
    maci::ExecutionId execution_id
  )
  throw (
    CORBA::SystemException
  )
{}

void MaciTestAdministratorImpl::component_deactivated (
    maci::Handle h,
    ACS::Time timestamp
  )
  throw (
    CORBA::SystemException
  )
{}

char * MaciTestAdministratorImpl::name (
    
  )
  throw (
    CORBA::SystemException
  )

  {
    ACS_SHORT_LOG((LM_INFO, "MaciTestAdministratorImpl::name"));
    return MaciTestClientImpl::name();
  }

void MaciTestAdministratorImpl::disconnect (
    
  )
  throw (
    CORBA::SystemException
  )

  {
    ACS_SHORT_LOG((LM_INFO, "MaciTestAdministratorImpl::disconnect"));
    return MaciTestClientImpl::disconnect();
  }

maci::AuthenticationData* MaciTestAdministratorImpl::authenticate (
    maci::ExecutionId execution_id,
    const char * question
  )
  throw (
    CORBA::SystemException
  )

  {
    ACS_SHORT_LOG((LM_INFO, "MaciTestAdministratorImpl::authenticate"));

    maci::AuthenticationData_var data = new maci::AuthenticationData();
    data->answer = CORBA::string_dup("");
    data->client_type = maci::CLIENT_TYPE;
    data->impl_lang = maci::CPP;
    data->recover = false;
    data->timestamp = 0;
    data->execution_id = 0;

    return data._retn();
  }

void MaciTestAdministratorImpl::message (
    CORBA::Short type,
    const char * message
    
  )
  throw (
    CORBA::SystemException
  )

  {
    ACS_SHORT_LOG((LM_INFO, "MaciTestAdministratorImpl::message"));
    MaciTestClientImpl::message(type, message);
  }

CORBA::Boolean MaciTestAdministratorImpl::ping (
    
  )
  throw (
    CORBA::SystemException
  )

  {
    ACS_SHORT_LOG((LM_INFO, "MaciTestAdministratorImpl::ping"));
    return MaciTestClientImpl::ping();
  }

void MaciTestAdministratorImpl::components_available (
    const maci::ComponentInfoSeq & components
    
  )
  throw (
    CORBA::SystemException
  )

  {
    ACS_SHORT_LOG((LM_INFO, "MaciTestAdministratorImpl::components_available"));
    return MaciTestClientImpl::components_available(components);
  }

void MaciTestAdministratorImpl::components_unavailable (
    const maci::stringSeq & component_names
    
  )
  throw (
    CORBA::SystemException
  )

  {
    ACS_SHORT_LOG((LM_INFO, "MaciTestAdministratorImpl::components_unavailable"));
    return MaciTestClientImpl::components_unavailable(component_names);
  }





