/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Owlconnector, a component of the software infrastructure 
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

package org.openflexo.technologyadapter.owl.model;

import java.beans.PropertyChangeSupport;
import java.lang.reflect.Type;

import org.openflexo.foundation.fml.TechnologySpecificType;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.technologyadapter.SpecificTypeInfo;
import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;

public class StatementWithProperty implements TechnologySpecificType<OWLTechnologyAdapter> {

	public static StatementWithProperty getStatementWithProperty(OWLProperty aProperty) {
		if (aProperty == null) {
			return null;
		}
		return aProperty.getTechnologyAdapter().getTechnologyContextManager().getStatementWithProperty(aProperty);
	}

	private final OWLProperty property;
	private final PropertyChangeSupport pcSupport;

	public StatementWithProperty(OWLProperty aProperty) {
		pcSupport = new PropertyChangeSupport(this);
		this.property = aProperty;
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	@Override
	public String getDeletedProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLProperty getProperty() {
		return property;
	}

	@Override
	public Class<?> getBaseClass() {
		if (property != null) {
			return property.getClass();
		}
		return IFlexoOntologyStructuralProperty.class;
	}

	@Override
	public boolean isTypeAssignableFrom(Type aType, boolean permissive) {
		// System.out.println("isTypeAssignableFrom " + aType + " (i am a " + this + ")");
		if (aType instanceof StatementWithProperty) {
			return property.isSuperConceptOf(((StatementWithProperty) aType).getProperty());
		}
		return false;
	}

	@Override
	public boolean isOfType(Object object, boolean permissive) {
		if (!(object instanceof OWLStatement)) {
			return false;
		}
		// TODO please implement me
		return true;
	}

	@Override
	public String simpleRepresentation() {
		return "Statement:" + property.getName();
	}

	@Override
	public String fullQualifiedRepresentation() {
		return simpleRepresentation();
	}

	@Override
	public String toString() {
		return simpleRepresentation();
	}

	@Override
	public OWLTechnologyAdapter getSpecificTechnologyAdapter() {
		if (getProperty() != null) {
			return getProperty().getTechnologyAdapter();
		}
		return null;
	}

	@Override
	public String getSerializationRepresentation() {
		return null;
	}

	@Override
	public boolean isResolved() {
		return true;
	}

	@Override
	public void resolve() {

	}

	@Override
	public void registerSpecificTypeInfo(SpecificTypeInfo<OWLTechnologyAdapter> typeInfo) {
		this.typeInfo = typeInfo;
	}

	public SpecificTypeInfo<OWLTechnologyAdapter> getSpecificTypeInfo() {
		return typeInfo;
	}

	private SpecificTypeInfo<OWLTechnologyAdapter> typeInfo;

}
