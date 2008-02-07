/*******************************************************************************
 * 
 * Copyright (c) 2007 Thomas Holland (thomas@innot.de) and others
 * 
 * This program and the accompanying materials are made
 * available under the terms of the GNU Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Thomas Holland - initial API and implementation
 *     
 * $Id$
 *     
 *******************************************************************************/
package de.innot.avreclipse.mbs.scannerconfig;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.make.core.scannerconfig.IScannerInfoCollector3;
import org.eclipse.cdt.make.core.scannerconfig.ScannerInfoTypes;
import org.eclipse.cdt.make.internal.core.scannerconfig2.GCCSpecsRunSIProvider;
import org.eclipse.cdt.make.internal.core.scannerconfig2.PerProjectSICollector;
import org.eclipse.cdt.managedbuilder.scannerconfig.IManagedScannerInfoCollector;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;

import de.innot.avreclipse.core.preferences.AVRTargetProperties;

/**
 * Gather built in compiler settings.
 * <p>
 * This extends {@link PerProjectSICollector} to add the "-mmcu" option to the
 * {@link GCCSpecsRunSIProvider} compiler arguments.
 * </p>
 * <p>
 * With this the ScannerInfoProvider will get the correct #defines for the
 * selected AVR Target. The MCU info is gathered from the project (not the build
 * configuration)
 * </p>
 * 
 */
public class AVRGCCScannerInfoCollector extends PerProjectSICollector implements
        IScannerInfoCollector3, IManagedScannerInfoCollector {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.cdt.make.internal.core.scannerconfig2.PerProjectSICollector#getCollectedScannerInfo(java.lang.Object,
	 *      org.eclipse.cdt.make.core.scannerconfig.ScannerInfoTypes)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<String> getCollectedScannerInfo(Object resource, ScannerInfoTypes type) {
		// Check if the requested ScannerInfoType is TARGET_SPECIFIC_OPTION
		// If no, let the superclass handle this.
		// If yes, return the "-mmcu" compiler option with the project MCDU
		// type.

		if (!type.equals(ScannerInfoTypes.TARGET_SPECIFIC_OPTION)) {
			return super.getCollectedScannerInfo(resource, type);
		}

		if (getDefinedSymbols().size() == 0) {
			// no symbols defined: this probably means
			// that the call is coming from createProject and
			// that the MCUType has not been set. 
			// return gracefully without adding the -mmcu option
			return null;
		}
		List<String> rv = new ArrayList<String>(1);

		IProject project = (IProject) resource;
		IPreferenceStore store = AVRTargetProperties.getPropertyStore(project);
		String targetmcu = store.getString(AVRTargetProperties.KEY_MCUTYPE);
		if ((targetmcu != null) && (!targetmcu.isEmpty())) {
			rv.add("-mmcu=" + targetmcu);
		}
		String fcpu = store.getString(AVRTargetProperties.KEY_FCPU);
		if ((fcpu != null) && (!fcpu.isEmpty())) {
			rv.add("-DF_CPU=" + fcpu + "UL");
		}
		
		return rv;
	}

}
