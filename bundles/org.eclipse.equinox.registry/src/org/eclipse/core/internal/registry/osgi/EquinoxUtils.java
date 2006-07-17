/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.internal.registry.osgi;

import org.eclipse.osgi.service.environment.EnvironmentInfo;
import org.eclipse.osgi.service.resolver.PlatformAdmin;
import org.osgi.framework.*;

/**
 * The class contains a set of Equinox-specific helper methods. The methods were isolated
 * into separate class to prevent an attempt to load Equinox classes in a generic OSGi
 * environment.
 * 
 * This class uses OSGi services.
 * 
 * @since org.eclipse.equinox.registry 3.2.100
 */
public class EquinoxUtils {

	/**
	 * Get the command line arguments from the EnvironmentInfo service
	 */
	public static String[] getCommandLine(BundleContext context, ServiceReference ref) {
		if (ref == null)
			return null;
		try {
			EnvironmentInfo environmentInfo = (EnvironmentInfo) context.getService(ref);
			return environmentInfo == null ? null : environmentInfo.getNonFrameworkArgs();
		} finally {
			context.ungetService(ref);
		}
	}

	/**
	 * Get the time stamp from the PlatformAdmin service.
	 */
	public static long getContainerTimestamp(BundleContext context, ServiceReference ref) {
		if (ref == null)
			return -1;
		try {
			PlatformAdmin admin = (PlatformAdmin) context.getService(ref);
			return admin == null ? -1 : admin.getState(false).getTimeStamp();
		} finally {
			context.ungetService(ref);
		}
	}

	public static ServiceRegistration registerCommandProvider(BundleContext context) {
		// try to register the registry command provider
		try {
			// refer to the CommandProvider by name here so that even if VM
			// decides to pre-fetch all referred classes the expection will occur
			// inside the exception holder
			return context.registerService("org.eclipse.osgi.framework.console.CommandProvider", new RegistryCommandProvider(), null); //$NON-NLS-1$
		} catch (NoClassDefFoundError noClass) {
			// expected if CommandProvider is not available
		}
		return null;
	}
}