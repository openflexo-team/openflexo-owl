/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2012-2012, AgileBirds
 * 
 * This file is part of Flexo-foundation, a component of the software infrastructure 
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

package org.openflexo.technologyadapter.owl;

import java.lang.reflect.Type;

import org.openflexo.foundation.ontology.IndividualOfClass;
import org.openflexo.foundation.utils.FlexoObjectReference.ReferenceOwner;
import org.openflexo.technologyadapter.owl.model.OWLClass;
import org.openflexo.technologyadapter.owl.model.OWLIndividual;
import org.openflexo.toolbox.StringUtils;

/**
 * An type defined as an {@link OWLIndividual} of a given {@link OWLClass}
 *
 * @author sylvain
 *
 */
public class OWLIndividualType extends IndividualOfClass<OWLTechnologyAdapter, OWLIndividual, OWLClass> {

	public static OWLIndividualType getOWLIndividualOfClass(OWLClass anOntologyClass) {
		if (anOntologyClass == null) {
			return null;
		}
		return (OWLIndividualType) anOntologyClass.getTechnologyAdapter().getTechnologyContextManager()
				.getIndividualOfClass(anOntologyClass);
	}

	public static OWLIndividualType UNDEFINED_OWL_INDIVIDUAL_TYPE = new OWLIndividualType((OWLClass) null);

	/**
	 * Factory for {@link OWLIndividualType} instances
	 * 
	 * @author sylvain
	 * 
	 */
	public static class OWLIndividualTypeFactory extends
			IndividualOfClassTypeFactory<OWLTechnologyAdapter, OWLIndividual, OWLClass, OWLIndividualType> implements ReferenceOwner {

		public OWLIndividualTypeFactory(OWLTechnologyAdapter technologyAdapter) {
			super(technologyAdapter);
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public Class<OWLIndividualType> getCustomType() {
			return OWLIndividualType.class;
		}
	}

	public OWLIndividualType(OWLClass anOntologyClass) {
		super(anOntologyClass);
	}

	@Override
	public Class<OWLIndividual> getBaseClass() {
		return OWLIndividual.class;
	}

	@Override
	public boolean isTypeAssignableFrom(Type aType, boolean permissive) {
		// System.out.println("isTypeAssignableFrom " + aType + " (i am a " + this + ")");
		if (aType instanceof OWLIndividualType) {
			return getOntologyClass().isSuperConceptOf(((OWLIndividualType) aType).getOntologyClass());
		}
		if (aType instanceof OWLIndividual) {
			// TODO: something better to do here !!!
			return true;
		}
		return false;
	}

	@Override
	public String simpleRepresentation() {
		if (getSpecificTypeInfo() != null && StringUtils.isNotEmpty(getSpecificTypeInfo().getSerializationForm())) {
			return getSpecificTypeInfo().getSerializationForm();
		}
		return getOntologyClass() != null ? getOntologyClass().getName() : "OWLIndividual";
	}

	@Override
	public String fullQualifiedRepresentation() {
		return getClass().getName() + "(" + getSerializationRepresentation() + ")";
	}

	@Override
	public String toString() {
		return simpleRepresentation();
	}

}
