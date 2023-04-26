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

package org.openflexo.technologyadapter.owl.fml.editionaction;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.annotations.FMLAttribute;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.technologyadapter.owl.model.OWLClass;
import org.openflexo.technologyadapter.owl.model.OWLIndividual;
import org.openflexo.technologyadapter.owl.model.OWLObjectProperty;
import org.openflexo.technologyadapter.owl.model.TypeStatement;

@ModelEntity
@ImplementationClass(AddTypeStatement.AddTypeStatementImpl.class)
@FML("AddTypeStatement")
public interface AddTypeStatement extends AddStatement<TypeStatement, OWLIndividual, OWLObjectProperty> {

	@PropertyIdentifier(type = DataBinding.class)
	public static final String TYPE_KEY = "type";

	@Getter(value = TYPE_KEY)
	@XMLAttribute
	@FMLAttribute(value = TYPE_KEY, required = true)
	public DataBinding<OWLClass> getType();

	@Setter(TYPE_KEY)
	public void setType(DataBinding<OWLClass> father);

	public static abstract class AddTypeStatementImpl extends AddStatementImpl<TypeStatement, OWLIndividual, OWLObjectProperty>
			implements AddTypeStatement {

		private static final Logger logger = Logger.getLogger(AddTypeStatement.class.getPackage().getName());

		@Override
		public OWLObjectProperty getProperty() {
			// This is the object property type in RDFS
			return null;
		}

		@Override
		public void setProperty(OWLObjectProperty aProperty) {
		}

		public OWLClass getPropertyType(RunTimeEvaluationContext evaluationContext) {
			try {
				return getType().getBindingValue(evaluationContext);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
			}
			return null;
		}

		private DataBinding<OWLClass> type;

		@Override
		public DataBinding<OWLClass> getType() {
			if (type == null) {
				type = new DataBinding<>(this, OWLClass.class, BindingDefinitionType.GET);
				type.setBindingName("type");
			}
			return type;
		}

		@Override
		public void setType(DataBinding<OWLClass> type) {
			if (type != null) {
				type.setOwner(this);
				type.setBindingName("type");
				type.setDeclaredType(IFlexoOntologyConcept.class);
				type.setBindingDefinitionType(BindingDefinitionType.GET);
			}
			this.type = type;
		}

		@Override
		public Type getAssignableType() {
			return TypeStatement.class;
		}

		@Override
		public TypeStatement execute(RunTimeEvaluationContext evaluationContext) {
			OWLIndividual subject = getPropertySubject(evaluationContext);
			OWLClass type = getPropertyType(evaluationContext);
			return subject.addToTypes(type);
		}

	}
}
