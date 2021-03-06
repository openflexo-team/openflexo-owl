/**
 * 
 * Copyright (c) 2014-2015, Openflexo
 * 
 * This file is part of Flexodiagram, a component of the software infrastructure 
 * developed at Openflexo.
 * 
 * 
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either 
 * version 1.1 of the License, or any later version ), which is available at 
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any 
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * 
 * You can redistribute it and/or modify under the terms of either of these licenses
 * 
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

package org.openflexo.technologyadapter.owl.nature;

import java.util.List;

import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.ontology.IFlexoOntologyObject;
import org.openflexo.foundation.ontology.nature.FlexoOntologyVirtualModelNature;
import org.openflexo.foundation.ontology.technologyadapter.FlexoOntologyModelSlot;
import org.openflexo.technologyadapter.owl.OWLModelSlot;
import org.openflexo.technologyadapter.owl.model.OWLClass;
import org.openflexo.technologyadapter.owl.model.OWLDataProperty;
import org.openflexo.technologyadapter.owl.model.OWLIndividual;
import org.openflexo.technologyadapter.owl.model.OWLObjectProperty;
import org.openflexo.technologyadapter.owl.model.OWLProperty;

/**
 * Define the "FlexoOntology" nature of a {@link VirtualModel}<br>
 * 
 * A {@link VirtualModel} with this nature has a least a {@link FlexoOntologyModelSlot}
 * 
 * @author sylvain
 * 
 */
public class OWLOntologyVirtualModelNature extends FlexoOntologyVirtualModelNature {

	public static OWLOntologyVirtualModelNature INSTANCE = new OWLOntologyVirtualModelNature();

	// Prevent external instantiation
	private OWLOntologyVirtualModelNature() {
		super();
	}

	/**
	 * Return boolean indicating if supplied {@link VirtualModel} has at least a {@link FlexoOntologyModelSlot}
	 */
	@Override
	public boolean hasNature(VirtualModel virtualModel) {

		if (virtualModel == null) {
			return false;
		}

		// VirtualModel should have one and only one TypedDiagramModelSlot
		if (virtualModel.getModelSlots(OWLModelSlot.class).size() > 0) {
			return true;
		}

		return false;
	}

	public static List<OWLModelSlot> getOWLOntologyModelSlots(VirtualModel virtualModel) {
		return INSTANCE._getOWLOntologyModelSlots(virtualModel);
	}

	private static List<OWLModelSlot> _getOWLOntologyModelSlots(VirtualModel virtualModel) {
		if (virtualModel != null && virtualModel.getModelSlots(OWLModelSlot.class).size() > 0) {
			return virtualModel.getModelSlots(OWLModelSlot.class);
		}
		return null;
	}

	/**
	 * Retrieve {@link IFlexoOntologyObject} referenced by its URI, asserting that supplied {@link VirtualModel} has the
	 * {@link FlexoOntologyVirtualModelNature}<br>
	 * 
	 * @param uri
	 * @param virtualModel
	 *            the {@link VirtualModel} used to define search scope
	 * @return
	 */
	public static IFlexoOntologyObject<?> getOntologyObject(String uri, VirtualModel virtualModel) {
		Object returned = getObject(uri, virtualModel);
		if (returned instanceof IFlexoOntologyObject) {
			return (IFlexoOntologyObject) returned;
		}
		return null;
	}

	/**
	 * Retrieve {@link OWLClass} referenced by its URI, asserting that supplied {@link VirtualModel} has the
	 * {@link OWLOntologyVirtualModelNature}<br>
	 * 
	 * @param uri
	 * @param virtualModel
	 *            the {@link VirtualModel} used to define search scope
	 * @return
	 */
	public static OWLClass getOWLClass(String uri, VirtualModel virtualModel) {
		return (OWLClass) getOntologyClass(uri, virtualModel);
	}

	/**
	 * Retrieve {@link OWLIndividual} referenced by its URI, asserting that supplied {@link VirtualModel} has the
	 * {@link OWLOntologyVirtualModelNature}<br>
	 * 
	 * @param uri
	 * @param virtualModel
	 *            the {@link VirtualModel} used to define search scope
	 * @return
	 */
	public static OWLIndividual getOWLIndividual(String uri, VirtualModel virtualModel) {
		return (OWLIndividual) getOntologyIndividual(uri, virtualModel);
	}

	/**
	 * Retrieve {@link OWLProperty} referenced by its URI, asserting that supplied {@link VirtualModel} has the
	 * {@link OWLOntologyVirtualModelNature}<br>
	 * 
	 * @param uri
	 * @param virtualModel
	 *            the {@link VirtualModel} used to define search scope
	 * @return
	 */
	public static OWLProperty getOWLProperty(String uri, VirtualModel virtualModel) {
		return (OWLProperty) getOntologyProperty(uri, virtualModel);
	}

	/**
	 * Retrieve {@link OWLObjectProperty} referenced by its URI, asserting that supplied {@link VirtualModel} has the
	 * {@link OWLOntologyVirtualModelNature}<br>
	 * 
	 * @param uri
	 * @param virtualModel
	 *            the {@link VirtualModel} used to define search scope
	 * @return
	 */
	public static OWLObjectProperty getOWLObjectProperty(String uri, VirtualModel virtualModel) {
		return (OWLObjectProperty) getOntologyObjectProperty(uri, virtualModel);
	}

	/**
	 * Retrieve {@link OWLDataProperty} referenced by its URI, asserting that supplied {@link VirtualModel} has the
	 * {@link OWLOntologyVirtualModelNature}<br>
	 * 
	 * @param uri
	 * @param virtualModel
	 *            the {@link VirtualModel} used to define search scope
	 * @return
	 */
	public static OWLDataProperty getOWLDataProperty(String uri, VirtualModel virtualModel) {
		return (OWLDataProperty) getOntologyDataProperty(uri, virtualModel);
	}
}
