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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.foundation.fml.FMLRepresentationContext;
import org.openflexo.foundation.fml.FMLRepresentationContext.FMLRepresentationOutput;
import org.openflexo.foundation.fml.FlexoProperty;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.editionaction.AssignationAction;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IFlexoOntologyObjectProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.ontology.IndividualOfClass;
import org.openflexo.foundation.ontology.fml.editionaction.SetObjectPropertyValueAction;
import org.openflexo.pamela.annotations.DefineValidationRule;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.pamela.validation.FixProposal;
import org.openflexo.pamela.validation.ValidationIssue;
import org.openflexo.pamela.validation.ValidationRule;
import org.openflexo.pamela.validation.ValidationWarning;
import org.openflexo.technologyadapter.owl.fml.ObjectPropertyStatementRole;
import org.openflexo.technologyadapter.owl.model.OWLConcept;
import org.openflexo.technologyadapter.owl.model.OWLObjectProperty;
import org.openflexo.technologyadapter.owl.model.ObjectPropertyStatement;
import org.openflexo.technologyadapter.owl.model.StatementWithProperty;
import org.openflexo.technologyadapter.owl.nature.OWLOntologyVirtualModelNature;
import org.openflexo.toolbox.StringUtils;

@ModelEntity
@ImplementationClass(AddObjectPropertyStatement.AddObjectPropertyStatementImpl.class)
@XMLElement
@FML("AddObjectPropertyStatement")
public interface AddObjectPropertyStatement
		extends AddStatement<ObjectPropertyStatement>, SetObjectPropertyValueAction<ObjectPropertyStatement> {

	@PropertyIdentifier(type = DataBinding.class)
	public static final String OBJECT_KEY = "object";
	@PropertyIdentifier(type = String.class)
	public static final String OBJECT_PROPERTY_URI_KEY = "objectPropertyURI";

	@Override
	@Getter(value = OBJECT_KEY)
	@XMLAttribute
	public DataBinding<?> getObject();

	@Override
	@Setter(OBJECT_KEY)
	public void setObject(DataBinding<?> object);

	@Getter(value = OBJECT_PROPERTY_URI_KEY)
	@XMLAttribute
	public String _getObjectPropertyURI();

	@Setter(OBJECT_PROPERTY_URI_KEY)
	public void _setObjectPropertyURI(String objectPropertyURI);

	@Override
	public IFlexoOntologyStructuralProperty getProperty();

	@Override
	public void setProperty(IFlexoOntologyStructuralProperty aProperty);

	public static abstract class AddObjectPropertyStatementImpl extends AddStatementImpl<ObjectPropertyStatement>
			implements AddObjectPropertyStatement {

		private static final Logger logger = Logger.getLogger(AddObjectPropertyStatement.class.getPackage().getName());

		private String objectPropertyURI = null;
		private DataBinding<?> object;

		public AddObjectPropertyStatementImpl() {
			super();
		}

		@Override
		public ObjectPropertyStatementRole getAssignedFlexoProperty() {
			FlexoProperty<?> superFlexoRole = super.getAssignedFlexoProperty();
			if (superFlexoRole instanceof ObjectPropertyStatementRole) {
				return (ObjectPropertyStatementRole) superFlexoRole;
			}
			else if (superFlexoRole != null) {
				// logger.warning("Unexpected pattern property of type " + superPatternRole.getClass().getSimpleName());
				return null;
			}
			return null;
		}

		@Override
		public String getFMLRepresentation(FMLRepresentationContext context) {
			FMLRepresentationOutput out = new FMLRepresentationOutput(context);
			String objstr;
			String subjstr;
			String propstr;

			if (getSubject() != null)
				subjstr = getSubject().toString();
			else
				subjstr = "<No Subject>";

			if (getObject() != null)
				objstr = getObject().toString();
			else
				objstr = "<No Object>";

			if (getObjectProperty() != null)
				propstr = getObjectProperty().getName();
			else
				propstr = "<No Property>";

			out.append(subjstr + "." + propstr + " = " + objstr + ";", context);

			return out.toString();
		}

		@Override
		public Type getSubjectType() {
			if (getObjectProperty() != null && getObjectProperty().getDomain() instanceof IFlexoOntologyClass) {
				return IndividualOfClass.getIndividualOfClass((IFlexoOntologyClass) getObjectProperty().getDomain());
			}
			return super.getSubjectType();
		}

		/*@Override
		public List<ObjectPropertyStatementRole> getAvailablePatternRoles() {
			return getFlexoConcept().getPatternRoles(ObjectPropertyStatementRole.class);
		}*/

		@Override
		public IFlexoOntologyStructuralProperty getProperty() {
			return getObjectProperty();
		}

		@Override
		public void setProperty(IFlexoOntologyStructuralProperty aProperty) {
			setObjectProperty((OWLObjectProperty) aProperty);
		}

		@Override
		public OWLObjectProperty getObjectProperty() {
			if (StringUtils.isNotEmpty(objectPropertyURI) && OWLOntologyVirtualModelNature.INSTANCE.hasNature(getOwningVirtualModel())) {
				return OWLOntologyVirtualModelNature.getOWLObjectProperty(objectPropertyURI, getOwningVirtualModel());
			}
			else {
				if (getAssignedFlexoProperty() != null) {
					return getAssignedFlexoProperty().getObjectProperty();
				}
			}
			return null;
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void setObjectProperty(IFlexoOntologyObjectProperty ontologyProperty) {
			if (ontologyProperty != null) {
				if (getAssignedFlexoProperty() != null) {
					if (getAssignedFlexoProperty().getObjectProperty().isSuperConceptOf(ontologyProperty)) {
						objectPropertyURI = ontologyProperty.getURI();
					}
					else {
						getAssignedFlexoProperty().setObjectProperty((OWLObjectProperty) ontologyProperty);
					}
				}
				else {
					objectPropertyURI = ontologyProperty.getURI();
				}
			}
			else {
				objectPropertyURI = null;
			}
		}

		@Override
		public String _getObjectPropertyURI() {
			if (getObjectProperty() != null) {
				if (getAssignedFlexoProperty() != null && getAssignedFlexoProperty().getObjectProperty() == getObjectProperty()) {
					// No need to store an overriding type, just use default provided by pattern property
					return null;
				}
				return getObjectProperty().getURI();
			}
			return objectPropertyURI;
		}

		@Override
		public void _setObjectPropertyURI(String objectPropertyURI) {
			this.objectPropertyURI = objectPropertyURI;
		}

		public OWLConcept<?> getPropertyObject(RunTimeEvaluationContext evaluationContext) {
			try {
				return (OWLConcept<?>) getObject().getBindingValue(evaluationContext);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			return null;
		}

		public Type getObjectType() {
			if (getObjectProperty() instanceof IFlexoOntologyObjectProperty && getObjectProperty().getRange() != null) {
				return IndividualOfClass.getIndividualOfClass((IFlexoOntologyClass) getObjectProperty().getRange());
			}
			return IFlexoOntologyConcept.class;
		}

		@Override
		public DataBinding<?> getObject() {
			if (object == null) {
				object = new DataBinding<Object>(this, getObjectType(), BindingDefinitionType.GET) {
					@Override
					public Type getDeclaredType() {
						return getObjectType();
					}
				};
				object.setBindingName("object");
			}
			return object;
		}

		@Override
		public void setObject(DataBinding<?> object) {
			if (object != null) {
				object = new DataBinding<Object>(object.toString(), this, getObjectType(), BindingDefinitionType.GET) {
					@Override
					public Type getDeclaredType() {
						return getObjectType();
					}
				};
				object.setBindingName("object");
			}
			this.object = object;
		}

		@Override
		public Type getAssignableType() {
			if (getObjectProperty() == null) {
				return ObjectPropertyStatement.class;
			}
			return StatementWithProperty.getStatementWithProperty(getObjectProperty());
		}

		@Override
		public String getStringRepresentation() {
			if (getSubject() == null || getObjectProperty() == null || getObject() == null) {
				return "Add ObjectPropertyStatement";
			}
			return getSubject() + " " + (getObjectProperty() != null ? getObjectProperty().getName() : "null") + " " + getObject();
		}

		@Override
		public ObjectPropertyStatement execute(RunTimeEvaluationContext evaluationContext) {
			OWLObjectProperty property = getObjectProperty();
			OWLConcept<?> subject = getPropertySubject(evaluationContext);
			OWLConcept<?> object = getPropertyObject(evaluationContext);
			if (property == null) {
				return null;
			}
			if (subject == null) {
				return null;
			}
			if (object == null) {
				return null;
			}
			return subject.addPropertyStatement(property, object);
		}

	}

	@DefineValidationRule
	public static class AddObjectPropertyStatementActionMustDefineAnObjectProperty
			extends ValidationRule<AddObjectPropertyStatementActionMustDefineAnObjectProperty, AddObjectPropertyStatement> {
		public AddObjectPropertyStatementActionMustDefineAnObjectProperty() {
			super(AddObjectPropertyStatement.class, "add_object_property_statement_action_must_define_an_object_property");
		}

		@Override
		public ValidationIssue<AddObjectPropertyStatementActionMustDefineAnObjectProperty, AddObjectPropertyStatement> applyValidation(
				AddObjectPropertyStatement action) {
			if (action.getObjectProperty() == null && action.getOwner() instanceof AssignationAction) {
				Vector<FixProposal<AddObjectPropertyStatementActionMustDefineAnObjectProperty, AddObjectPropertyStatement>> v = new Vector<>();
				for (ObjectPropertyStatementRole pr : action.getFlexoConcept().getDeclaredProperties(ObjectPropertyStatementRole.class)) {
					v.add(new SetsFlexoRole(pr));
				}
				return new ValidationWarning<>(this, action, "add_object_property_statement_action_does_not_define_an_object_property", v);
			}
			return null;
		}

		protected static class SetsFlexoRole
				extends FixProposal<AddObjectPropertyStatementActionMustDefineAnObjectProperty, AddObjectPropertyStatement> {

			private final ObjectPropertyStatementRole flexoRole;

			public SetsFlexoRole(ObjectPropertyStatementRole flexoRole) {
				super("assign_action_to_flexo_role_($flexoRole.flexoRoleName)");
				this.flexoRole = flexoRole;
			}

			public ObjectPropertyStatementRole getFlexoRole() {
				return flexoRole;
			}

			@Override
			protected void fixAction() {
				AddObjectPropertyStatement action = getValidable();
				((AssignationAction<?>) action.getOwner()).setAssignation(new DataBinding<>(flexoRole.getRoleName()));
			}

		}
	}

	@DefineValidationRule
	public static class ObjectIsRequiredAndMustBeValid extends BindingIsRequiredAndMustBeValid<AddObjectPropertyStatement> {
		public ObjectIsRequiredAndMustBeValid() {
			super("'object'_binding_is_required_and_must_be_valid", AddObjectPropertyStatement.class);
		}

		@Override
		public DataBinding<?> getBinding(AddObjectPropertyStatement object) {
			return object.getObject();
		}

	}

}
