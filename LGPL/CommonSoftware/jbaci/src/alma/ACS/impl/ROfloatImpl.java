/*******************************************************************************
 * ALMA - Atacama Large Millimiter Array
 * (c) European Southern Observatory, 2002
 * Copyright by ESO (in the framework of the ALMA collaboration)
 * and Cosylab 2002, All rights reserved
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

package alma.ACS.impl;

import org.omg.CORBA.NO_IMPLEMENT;

import alma.ACS.Alarmfloat;
import alma.ACS.CBDescIn;
import alma.ACS.CBDescOut;
import alma.ACS.CBfloat;
import alma.ACS.Callback;
import alma.ACS.Monitorfloat;
import alma.ACS.MonitorfloatHelper;
import alma.ACS.MonitorfloatPOATie;
import alma.ACS.NoSuchCharacteristic;
import alma.ACS.ROfloatOperations;
import alma.ACS.Subscription;
import alma.ACS.TimeSeqHolder;
import alma.ACS.floatSeqHolder;
import alma.ACS.jbaci.CallbackDispatcher;
import alma.ACS.jbaci.CompletionUtil;
import alma.ACS.jbaci.DataAccess;
import alma.ACS.jbaci.PropertyInitializationFailed;
import alma.ACSErr.Completion;
import alma.ACSErr.CompletionHolder;
import alma.ACSErrTypeCommon.wrappers.AcsJCouldntPerformActionEx;
import alma.acs.exceptions.AcsJException;

/**
 * Implementation of <code>alma.ACS.ROfloat</code>.
 * @author <a href="mailto:cmenayATcsrg.inf.utfsm.cl">Camilo Menay</a>
 * @author <a href="mailto:cmaureirATinf.utfsm.cl">Cristian Maureira</a>
 * @version $id$
 */
public class ROfloatImpl
	extends ROCommonComparablePropertyImpl
	implements ROfloatOperations {

	/**
	 * @param name
	 * @param parentComponent
	 * @throws PropertyInitializationFailed
	 */
	public ROfloatImpl(
		String name,
		CharacteristicComponentImpl parentComponent)
		throws PropertyInitializationFailed {
		super(float.class, name, parentComponent);
	}

	/**
	 * @param name
	 * @param parentComponent
	 * @param dataAccess
	 * @throws PropertyInitializationFailed
	 */
	public ROfloatImpl(
		String name,
		CharacteristicComponentImpl parentComponent,
		DataAccess dataAccess)
		throws PropertyInitializationFailed {
		super(float.class, name, parentComponent, dataAccess);
	}

	/**
	 * @see alma.ACS.CommonPropertyImpl#readPropertyTypeCharacteristic(java.lang.String)
	 */
	public Object readPropertyTypeCharacteristic(String name)
		throws NoSuchCharacteristic {
		return new Float(characteristicModelImpl.getDouble(name));
	}

	/**
	 * @see alma.ACS.ROfloatOperations#alarm_high_off()
	 */
	public float alarm_high_off() {
		return ((Float)alarmHighOff).floatValue();
	}

	/**
	 * @see alma.ACS.ROfloatOperations#alarm_high_on()
	 */
	public float alarm_high_on() {
		return ((Float)alarmHighOn).floatValue();
	}

	/**
	 * @see alma.ACS.ROfloatOperations#alarm_low_off()
	 */
	public float alarm_low_off() {
		return ((Float)alarmLowOff).floatValue();
	}

	/**
	 * @see alma.ACS.ROfloatOperations#alarm_low_on()
	 */
	public float alarm_low_on() {
		return ((Float)alarmLowOn).floatValue();
	}

	/**
	 * @see alma.ACS.ROfloatOperations#new_subscription_Alarm(alma.ACS.Alarmfloat, alma.ACS.CBDescIn)
	 */
	public Subscription new_subscription_Alarm(
		Alarmfloat arg0,
		CBDescIn arg1) {
		// TODO NO_IMPLEMENT
		throw new NO_IMPLEMENT();
	}

	/**
	 * @see alma.ACS.PfloatOperations#create_monitor(alma.ACS.CBfloat, alma.ACS.CBDescIn)
	 */
	public Monitorfloat create_monitor(CBfloat callback, CBDescIn descIn) {
		return create_postponed_monitor(0, callback, descIn);
	}

	/**
	 * @see alma.ACS.PfloatOperations#create_postponed_monitor(long, alma.ACS.CBfloat, alma.ACS.CBDescIn)
	 */
	public Monitorfloat create_postponed_monitor(
		long startTime,
		CBfloat callback,
		CBDescIn descIn) {
			
		// create monitor and its servant
		MonitorfloatImpl monitorImpl = new MonitorfloatImpl(this, callback, descIn, startTime);
		MonitorfloatPOATie monitorTie = new MonitorfloatPOATie(monitorImpl);

		// register and activate		
		return MonitorfloatHelper.narrow(this.registerMonitor(monitorImpl, monitorTie));
	
	}

	/**
	 * @see alma.ACS.PfloatOperations#default_value()
	 */
	public float default_value() {
		return ((Float)defaultValue).floatValue();
	}

	/**
	 * @see alma.ACS.PfloatOperations#get_async(alma.ACS.CBfloat, alma.ACS.CBDescIn)
	 */
	public void get_async(CBfloat arg0, CBDescIn arg1) {
		getAsync(arg0, arg1);
	}

	/**
	 * @see alma.ACS.PfloatOperations#get_history(int, alma.ACS.floatSeqHolder, alma.ACS.TimeSeqHolder)
	 */
	public int get_history(
		int arg0,
		floatSeqHolder arg1,
		TimeSeqHolder arg2) {
		arg1.value = (float[])getHistory(arg0, arg2);
		return arg1.value.length;
	}

	/**
	 * @see alma.ACS.PfloatOperations#get_sync(alma.ACSErr.CompletionHolder)
	 */
	public float get_sync(CompletionHolder completionHolder) {
		try
		{
			return ((Float)getSync(completionHolder)).floatValue();
		}
		catch (AcsJException acsex)
		{
			AcsJCouldntPerformActionEx cpa =
				new AcsJCouldntPerformActionEx("Failed to retrieve value", acsex);
			completionHolder.value = CompletionUtil.generateCompletion(cpa);
			// return default value in case of error
			return default_value();
		}
	}

	/**
	 * @see alma.ACS.PfloatOperations#graph_max()
	 */
	public float graph_max() {
		return ((Float)graphMax).floatValue();
	}

	/**
	 * @see alma.ACS.PfloatOperations#graph_min()
	 */
	public float graph_min() {
		return ((Float)graphMin).floatValue();
	}

	/**
	 * @see alma.ACS.PfloatOperations#min_delta_trigger()
	 */
	public float min_delta_trigger() {
		return ((Float)minDeltaTrigger).floatValue();
	}

	/**
	 * @see alma.ACS.PfloatOperations#min_step()
	 */
	public float min_step() {
		return ((Float)minStep).floatValue();
	}


	/**
	 * @see alma.ACS.CommonComparablePropertyImpl#lessThanDelta(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	public boolean lessThanDelta(Object value1, Object value2, Object delta) {
		return Math.abs(((Float)value1).floatValue()-((Float)value2).floatValue()) < ((Float)delta).floatValue();
	}

	/**
	 * @see alma.ACS.CommonComparablePropertyImpl#noDelta(java.lang.Object)
	 */
	public boolean noDelta(Object value) {
		return ((Float)value).floatValue() == 0;
	}

	/**
	 * @see alma.ACS.CommonComparablePropertyImpl#sum(java.lang.Object, java.lang.Object, boolean)
	 */
	public Object sum(Object value1, Object value2, boolean substract) {
		float val2 = ((Float)value2).floatValue();
		if (substract)
			val2 = -val2;
		return new Float(((Float)value1).floatValue() + val2);
	}

	/**
	 * @see alma.ACS.jbaci.CallbackDispatcher#dispatchCallback(int, java.lang.Object, alma.ACSErr.Completion, alma.ACS.CBDescOut)
	 */
	public boolean dispatchCallback(
		CallbackDispatcher.CallbackType type,
		Object value,
		Callback callback,
		Completion completion,
		CBDescOut desc) {
		try
		{	
			if (type == CallbackDispatcher.CallbackType.DONE_TYPE)
				((CBfloat)callback).done(((Float)value).floatValue(), completion, desc);
			else if (type == CallbackDispatcher.CallbackType.WORKING_TYPE)
				((CBfloat)callback).working(((Float)value).floatValue(), completion, desc);
			else 
				return false;
				
			return true;
		}
		catch (Throwable th)
		{
			return false;
		}
	}
	
	/**
	 * @see alma.ACS.ROlongOperations#enable_alarm_system()
	 */
	public void enable_alarm_system() {
		// TODO NO_IMPLEMENT
		throw new NO_IMPLEMENT();
	}
	
	/**
	 * @see alma.ACS.ROlongOperations#disable_alarm_system() throws alma.baciErrTypeProperty.DisableAlarmsErrorEx
	 */
	public void disable_alarm_system() throws alma.baciErrTypeProperty.DisableAlarmsErrorEx {
		// TODO NO_IMPLEMENT
		throw new NO_IMPLEMENT();
	}
	
	/**
	 * @see alma.ACS.ROlongOperations#alarm_system_enabled()
	 */
	public boolean alarm_system_enabled() {
		// TODO NO_IMPLEMENT
		throw new NO_IMPLEMENT();
	}

}
