/*
 *    ALMA - Atacama Large Millimiter Array
 *    (c) European Southern Observatory, 2004
 *    Copyright by ESO (in the framework of the ALMA collaboration),
 *    All rights reserved
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, 
 *    MA 02111-1307  USA
 */
package alma.acs.container;

import alma.JavaContainerError.wrappers.AcsJContainerServicesEx;

/**
 * This class defines the more exotic methods from the container services interface,
 * which have been refactored out of <code>ContainerServices</code> into this separate interface.
 * <p>
 * The idea is
 * <ol> 
 * <li> to keep the standard <code>ContainerServices</code> interface lean,
 * <li> to discourage using these methods when not really needed, because they may violate architectural principles,
 * <li> perhaps later to require a flag in the CDB for a component to be allowed to access these methods.
 * </ol> 
 * @author hsommer
 * created May 2, 2005 5:46:23 PM
 */
public interface AdvancedContainerServices {
    /**
     * Encapsulates {@link org.omg.CORBA.ORB#object_to_string(org.omg.CORBA.Object)}.
     * @param objRef the corba stub 
     * @return standardized string representation of <code>objRef</code>. 
     */
    public String corbaObjectToString(org.omg.CORBA.Object objRef);

    /**
     * Encapsulates {@link org.omg.CORBA.ORB#string_to_object(String)}.
     * @param strObjRef
     * @return org.omg.CORBA.Object
     */
    public org.omg.CORBA.Object corbaObjectFromString(String strObjRef);

    /**
     * Returns a reference to a new CORBA Any. Int Java the only way to do 
     * this is through the ORB itself (i.e., the create_any method).
     * @return org.omg.CORBA.Any
     */
    public org.omg.CORBA.Any getAny();

    /**
     * Forcefully unloads a component, even if there are still clients using it.
     * Caution: using this method can damage system integrity! 
     * 
     * @param curl  name (url) of the component that should be released.
     * @throws ContainerException if the forceful release failed, for example because the calling client (component) did not acquire a reference to the target component.
     * @deprecated  introduced in ACS 5.0.4 as a workaround until we have a proper concept of a "weak" component reference. 
     *              Then this method will be removed.
     */
    public void forceReleaseComponent(String curl) throws AcsJContainerServicesEx;
}
