/**
 * 
 * Copyright (c) 2014-2015, Openflexo
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

package org.openflexo.technologyadapter.owl.fml;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.technologyadapter.owl.OWLModelSlot.OWLModelSlotImpl;
import org.openflexo.technologyadapter.owl.model.TypeStatement;

@ModelEntity
@ImplementationClass(TypeStatementRole.TypeStatementRoleImpl.class)
@FML("TypeStatementRole")
public interface TypeStatementRole extends StatementRole<TypeStatement> {

	public static abstract class TypeStatementRoleImpl extends StatementRoleImpl<TypeStatement> implements TypeStatementRole {

		static final Logger logger = FlexoLogger.getLogger(TypeStatementRole.class.getPackage().toString());

		@Override
		public Type getType() {
			return TypeStatement.class;
		}

		@Override
		public String getTypeDescription() {
			return getLocales().localizedForKey("type_statement");
		}

		@Override
		public TypeStatementActorReference makeActorReference(TypeStatement object, FlexoConceptInstance epi) {
			org.openflexo.pamela.factory.PamelaModelFactory factory = OWLModelSlotImpl.getModelFactory();
			TypeStatementActorReference returned = factory.newInstance(TypeStatementActorReference.class);
			returned.setFlexoRole(this);
			returned.setFlexoConceptInstance(epi);
			returned.setModellingElement(object);
			return returned;
		}
	}
}
