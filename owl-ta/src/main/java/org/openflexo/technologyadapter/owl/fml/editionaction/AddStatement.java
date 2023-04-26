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
import org.openflexo.foundation.fml.annotations.FMLAttribute;
import org.openflexo.foundation.fml.editionaction.TechnologySpecificActionDefiningReceiver;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.fml.editionaction.SetPropertyValueAction;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.technologyadapter.owl.OWLModelSlot;
import org.openflexo.technologyadapter.owl.model.OWLConcept;
import org.openflexo.technologyadapter.owl.model.OWLOntology;
import org.openflexo.technologyadapter.owl.model.OWLProperty;
import org.openflexo.technologyadapter.owl.model.OWLStatement;

@ModelEntity(isAbstract = true)
@ImplementationClass(AddStatement.AddStatementImpl.class)
public abstract interface AddStatement<ST extends OWLStatement, S extends OWLConcept<?>, P extends OWLProperty>
		extends TechnologySpecificActionDefiningReceiver<OWLModelSlot, OWLOntology, ST>, SetPropertyValueAction<ST, S, P>, OWLAction<ST> {

	@PropertyIdentifier(type = DataBinding.class)
	public static final String SUBJECT_KEY = "subject";

	@Override
	@Getter(value = SUBJECT_KEY)
	@XMLAttribute
	@FMLAttribute(value = SUBJECT_KEY, required = true)
	public DataBinding<S> getSubject();

	@Override
	@Setter(SUBJECT_KEY)
	public void setSubject(DataBinding<S> subject);

	public OWLOntology getMetaModel();

	public static abstract class AddStatementImpl<ST extends OWLStatement, S extends OWLConcept<?>, P extends OWLProperty>
			extends TechnologySpecificActionDefiningReceiverImpl<OWLModelSlot, OWLOntology, ST> implements AddStatement<ST, S, P> {

		private static final Logger logger = Logger.getLogger(AddStatement.class.getPackage().getName());

		public S getPropertySubject(RunTimeEvaluationContext evaluationContext) {
			try {
				return getSubject().getBindingValue(evaluationContext);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public OWLOntology getMetaModel() {
			// return this.getModelSlot().getMetaModelResource().getMetaModelData();
			if (getInferedModelSlot() != null) {
				return getInferedModelSlot().getMetaModelResource().getMetaModelData();
			}
			return null;
		}

		private DataBinding<S> subject;

		// Please override in subclasses
		@Override
		public Type getSubjectType() {
			return OWLConcept.class;
		}

		@Override
		public DataBinding<S> getSubject() {
			if (subject == null) {
				subject = new DataBinding<S>(this, getSubjectType(), BindingDefinitionType.GET) {
					@Override
					public Type getDeclaredType() {
						return getSubjectType();
					}
				};
				subject.setBindingName("subject");
			}
			return subject;
		}

		@Override
		public void setSubject(DataBinding<S> subject) {
			if (subject != null) {
				subject = new DataBinding<S>(subject.toString(), this, getSubjectType(), BindingDefinitionType.GET) {
					@Override
					public Type getDeclaredType() {
						return getSubjectType();
					}
				};
				subject.setBindingName("subject");
			}
			this.subject = subject;
		}

		public static class SubjectIsRequiredAndMustBeValid extends BindingIsRequiredAndMustBeValid<AddStatement> {
			public SubjectIsRequiredAndMustBeValid() {
				super("'subject'_binding_is_required_and_must_be_valid", AddStatement.class);
			}

			@Override
			public DataBinding<IFlexoOntologyConcept> getBinding(AddStatement object) {
				return object.getSubject();
			}

		}

	}
}
