package org.openflexo.technologyadapter.owl.fml.editionaction;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.foundation.fml.FlexoProperty;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.annotations.FMLAttribute;
import org.openflexo.foundation.fml.editionaction.AssignationAction;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.foundation.fml.validation.BindingIsRequiredAndMustBeValid;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IndividualOfClass;
import org.openflexo.foundation.ontology.fml.editionaction.SetAnnotationValueAction;
import org.openflexo.foundation.ontology.fml.editionaction.SetDataPropertyValueAction;
import org.openflexo.pamela.annotations.*;
import org.openflexo.pamela.validation.FixProposal;
import org.openflexo.pamela.validation.ValidationError;
import org.openflexo.pamela.validation.ValidationIssue;
import org.openflexo.pamela.validation.ValidationRule;
import org.openflexo.technologyadapter.owl.OWLModelSlot;
import org.openflexo.technologyadapter.owl.fml.AnnotationStatementRole;
import org.openflexo.technologyadapter.owl.fml.DataPropertyStatementRole;
import org.openflexo.technologyadapter.owl.model.*;
import org.openflexo.technologyadapter.owl.nature.OWLOntologyVirtualModelNature;
import org.openflexo.toolbox.StringUtils;
import org.apache.jena.rdf.model.Statement;

import java.lang.reflect.Type;
import java.util.Vector;
import java.util.logging.Logger;

@ModelEntity
@ImplementationClass(AddAnnotationStatement.AddAnnotationStatementImpl.class)
@XMLElement
@FML("AddAnnotationStatement")
public interface AddAnnotationStatement<T> extends OWLAction<AnnotationStatement> {

    @PropertyIdentifier(type = DataBinding.class)
    public static final String VALUE_KEY = "value";
    @PropertyIdentifier(type = String.class)
    public static final String ANNOTATION_PROPERTY_URI_KEY = "annotationPropertyURI";
    @PropertyIdentifier(type = DataBinding.class)
    public static final String DYNAMIC_PROPERTY_KEY = "dynamicProperty";
    @PropertyIdentifier(type = DataBinding.class)
    public static final String SUBJECT_KEY = "subject";

    @Getter(value = SUBJECT_KEY)
    @XMLAttribute
    @FMLAttribute(value = SUBJECT_KEY, required = true)
    public DataBinding<OWLObject> getSubject();

    @Setter(SUBJECT_KEY)
    public void setSubject(DataBinding<OWLObject> subject);
    //@Override
    @Getter(value = VALUE_KEY)
    @XMLAttribute
    @FMLAttribute(value = VALUE_KEY, required = true, description = "<html>property beeing addressed</html>")
    public DataBinding<T> getValue();

    //@Override
    @Setter(VALUE_KEY)
    public void setValue(DataBinding<T> value);

    @Getter(value = ANNOTATION_PROPERTY_URI_KEY)
    @XMLAttribute
    public String _getAnnotationPropertyURI();

    @Setter(ANNOTATION_PROPERTY_URI_KEY)
    public void _setAnnotationPropertyURI(String annotationPropertyURI);
    public OWLAnnotation getAnnotation();

    public void setAnnotation(OWLAnnotation ontologyProperty);
    //@Override
    public OWLAnnotation getProperty();

    //@Override
    public void setProperty(OWLAnnotation aProperty);

    // TODO: pull up this method
    @Getter(value = DYNAMIC_PROPERTY_KEY)
    @FMLAttribute(value = DYNAMIC_PROPERTY_KEY, required = false, description = "<html>property beeing addressed</html>")
    public DataBinding<OWLAnnotation> getDynamicProperty();

    @Setter(DYNAMIC_PROPERTY_KEY)
    public void setDynamicProperty(DataBinding<OWLAnnotation> dynamicProperty);

    public static abstract class AddAnnotationStatementImpl<T>
            extends TechnologySpecificActionDefiningReceiverImpl<OWLModelSlot, OWLOntology, AnnotationStatement>
            implements AddAnnotationStatement<T> {

        private static final Logger logger = Logger.getLogger(AddAnnotationStatement.class.getPackage().getName());

        private String annotationPropertyURI = null;
        private DataBinding<T> value;

        public AddAnnotationStatementImpl() {
            super();
        }
        /*
        @Override
        public Type getSubjectType() {
            if (getAnnotation() != null && getAnnotation().getDomain() instanceof IFlexoOntologyClass) {
                return IndividualOfClass.getIndividualOfClass((IFlexoOntologyClass) getAnnotation().getDomain());
            }
            return super.getSubjectType();
        }*/

		/*@Override
		public List<DataPropertyStatementRole> getAvailablePatternRoles() {
			return getFlexoConcept().getPatternRoles(DataPropertyStatementRole.class);
		}*/
        private DataBinding<OWLObject> subject;

        public Type getSubjectType() {
            return OWLObject.class;
        }

        //@Override
        public DataBinding<OWLObject> getSubject() {
            if (subject == null) {
                subject = new DataBinding<OWLObject>(this, getSubjectType(), DataBinding.BindingDefinitionType.GET) {
                    @Override
                    public Type getDeclaredType() {
                        return getSubjectType();
                    }
                };
                subject.setBindingName("subject");
            }
            return subject;
        }


        //@Override
        public void setSubject(DataBinding<OWLObject> subject) {
            if (subject != null) {
                subject = new DataBinding<OWLObject>(subject.toString(), this, getSubjectType(), DataBinding.BindingDefinitionType.GET) {
                    @Override
                    public Type getDeclaredType() {
                        return getSubjectType();
                    }
                };
                subject.setBindingName("subject");
            }
            this.subject = subject;
        }
        public OWLObject getAnnotationSubject(RunTimeEvaluationContext evaluationContext) {
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
        public AnnotationStatementRole getAssignedFlexoProperty() {
            FlexoProperty<?> superFlexoRole = super.getAssignedFlexoProperty();
            if (superFlexoRole instanceof AnnotationStatementRole) {
                return (AnnotationStatementRole) superFlexoRole;
            } else if (superFlexoRole != null) {
                // logger.warning("Unexpected pattern property of type " + superPatternRole.getClass().getSimpleName());
                return null;
            }
            return null;
        }

        @Override
        public OWLAnnotation getProperty() {
            return getAnnotation();
        }

        @Override
        public void setProperty(OWLAnnotation aProperty) {
            setAnnotation(aProperty);
        }

        @Override
        public OWLAnnotation getAnnotation() {
            if (StringUtils.isNotEmpty(annotationPropertyURI) && OWLOntologyVirtualModelNature.INSTANCE.hasNature(getOwningVirtualModel())) {
                return OWLOntologyVirtualModelNature.getOWLAnnotation(annotationPropertyURI, getOwningVirtualModel());
            } else {
                if (getAssignedFlexoProperty() != null) {
                    return getAssignedFlexoProperty().getAnnotation();
                }
            }
            return null;
        }

        @Override
        public void setAnnotation(OWLAnnotation ontologyProperty) {
            if (ontologyProperty != null) {
                if (getAssignedFlexoProperty() != null) {
                    if (getAssignedFlexoProperty().getAnnotation().isSuperConceptOf(ontologyProperty)) {
                        annotationPropertyURI = ontologyProperty.getURI();
                    } else {
                        getAssignedFlexoProperty().setAnnotation(ontologyProperty);
                    }
                } else {
                    annotationPropertyURI = ontologyProperty.getURI();
                }
            } else {
                annotationPropertyURI = null;
            }
        }

        @Override
        public String _getAnnotationPropertyURI() {
            if (getAnnotation() != null) {
                if (getAssignedFlexoProperty() != null && getAssignedFlexoProperty().getAnnotation() == getAnnotation()) {
                    // No need to store an overriding type, just use default provided by pattern property
                    return null;
                }
                return getAnnotation().getURI();
            }
            return annotationPropertyURI;
        }

        @Override
        public void _setAnnotationPropertyURI(String annotationPropertyURI) {
            this.annotationPropertyURI = annotationPropertyURI;
        }

        public Object getValue(RunTimeEvaluationContext evaluationContext) {
            try {
                return getValue().getBindingValue(evaluationContext);
            } catch (TypeMismatchException e) {
                e.printStackTrace();
            } catch (NullReferenceException e) {
                e.printStackTrace();
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
            return null;
        }

        public Type getType() {
            if (getAnnotation() != null) {
                return getAnnotation().getRange().getAccessedType();
            }
            return Object.class;
        }

        ;

        private DataBinding<OWLAnnotation> dynamicProperty;

        @Override
        public DataBinding<OWLAnnotation> getDynamicProperty() {
            if (dynamicProperty == null) {
                dynamicProperty = new DataBinding<>(this, OWLAnnotation.class, DataBinding.BindingDefinitionType.GET);
                dynamicProperty.setBindingName(DYNAMIC_PROPERTY_KEY);
            }

            return dynamicProperty;
        }

        @Override
        public void setDynamicProperty(DataBinding<OWLAnnotation> dynamicProperty) {
            if (dynamicProperty != null) {
                dynamicProperty.setOwner(this);
                dynamicProperty.setDeclaredType(OWLAnnotation.class);
                dynamicProperty.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
                dynamicProperty.setBindingName(DYNAMIC_PROPERTY_KEY);
            }
            this.dynamicProperty = dynamicProperty;
        }

        @Override
        public DataBinding<T> getValue() {
            if (value == null) {
                value = new DataBinding<T>(this, getType(), DataBinding.BindingDefinitionType.GET) {
                    @Override
                    public Type getDeclaredType() {
                        return getType();
                    }
                };
                value.setBindingName("value");
            }
            return value;
        }

        @Override
        public void setValue(DataBinding<T> value) {
            if (value != null) {
                value = new DataBinding<T>(value.toString(), this, getType(), DataBinding.BindingDefinitionType.GET) {
                    @Override
                    public Type getDeclaredType() {
                        return getType();
                    }
                };
                value.setBindingName("value");
            }
            this.value = value;
        }

        @Override
        public Type getAssignableType() {
            if (getAnnotation() == null) {
                return AnnotationStatement.class;
            }
            return StatementWithProperty.getStatementWithProperty(getAnnotation());
        }

        @Override
        public String getStringRepresentation() {
            if (getSubject() == null || getAnnotation() == null || getValue() == null) {
                return "Add AnnotationStatement";
            }
            return getSubject() + " " + (getAnnotation() != null ? getAnnotation().getName() : "null") + " " + getValue();
        }

        @Override
        public AnnotationStatement execute(RunTimeEvaluationContext evaluationContext) {

            OWLAnnotation property = null;


            if (getDynamicProperty() != null && getDynamicProperty().isSet() && getDynamicProperty().isValid()) {
                try {
                    property = getDynamicProperty().getBindingValue(evaluationContext);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (property == null) {
                property = getAnnotation();
            }
            //setDataProperty(property);
            OWLObject subject = getAnnotationSubject(evaluationContext);
            Object value = getValue(evaluationContext);

            logger.info("[AddAnnotationStatement] Executing:");
            logger.info("  dynamicProperty binding = " + getDynamicProperty());
            logger.info("  dynamicProperty isSet = " + (getDynamicProperty() != null && getDynamicProperty().isSet()));
            logger.info("  dynamicProperty isValid = " + (getDynamicProperty() != null && getDynamicProperty().isValid()));
            logger.info("  annotationPropertyURI = " + annotationPropertyURI);
            logger.info("  property URI = " + (property != null ? property.getURI() : "null"));
            logger.info("  property name = " + (property != null ? property.getName() : "null"));
            logger.info("  subject class = " + (subject != null ? subject.getClass().getSimpleName() : "null"));
            logger.info("  value = " + value + " (" + (value != null ? value.getClass().getSimpleName() : "null") + ")");

            if (property == null || subject == null || value == null) {
                System.out.println(property);
                return null;
            }

            if (subject instanceof OWLConcept<?>) {
                return ((OWLConcept<?>) subject).addAnnotationStatement(property, value);
            }

            if (subject instanceof OWLStatement) {

                OWLStatement owlStatementSubject = (OWLStatement) subject;
                org.apache.jena.rdf.model.Statement baseStatement =
                        owlStatementSubject.getStatement();

                org.apache.jena.rdf.model.Model model = baseStatement.getModel();

                org.apache.jena.rdf.model.Resource axiom = model.createResource();

                axiom.addProperty(
                        org.apache.jena.vocabulary.RDF.type,
                        org.apache.jena.vocabulary.OWL2.Axiom);

                axiom.addProperty(
                        org.apache.jena.vocabulary.OWL2.annotatedSource,
                        baseStatement.getSubject());

                axiom.addProperty(
                        org.apache.jena.vocabulary.OWL2.annotatedProperty,
                        baseStatement.getPredicate());

                axiom.addProperty(
                        org.apache.jena.vocabulary.OWL2.annotatedTarget,
                        baseStatement.getObject());

                org.apache.jena.rdf.model.Statement annotationRDFStatement =
                        model.createStatement(
                                axiom,
                                property.getOntProperty(),
                                String.valueOf(value));

                model.add(annotationRDFStatement);

                logger.warning("[AddAnnotationStatement] created OWL axiom annotation statement = "
                        + annotationRDFStatement);

                return new AnnotationStatement(
                        (OWLObject) subject,
                        property,
                        annotationRDFStatement,
                        property.getTechnologyAdapter());
            }
            return null;
        }

    }

    public static class AddAnnotationStatementActionMustDefineAnnotationProperty
            extends ValidationRule<AddAnnotationStatement.AddAnnotationStatementActionMustDefineAnnotationProperty, AddAnnotationStatement> {
        public AddAnnotationStatementActionMustDefineAnnotationProperty() {
            super(AddAnnotationStatement.class, "add_annotation_property_statement_action_must_define_annotation_property");
        }

        @Override
        public ValidationIssue<AddAnnotationStatement.AddAnnotationStatementActionMustDefineAnnotationProperty, AddAnnotationStatement> applyValidation(
                AddAnnotationStatement action) {
            if (action.getAnnotation() == null && action.getOwner() instanceof AssignationAction) {
                Vector<FixProposal<AddAnnotationStatement.AddAnnotationStatementActionMustDefineAnnotationProperty, AddAnnotationStatement>> v = new Vector<>();
                for (AnnotationStatementRole pr : action.getFlexoConcept().getDeclaredProperties(AnnotationStatementRole.class)) {
                    v.add(new AddAnnotationStatement.AddAnnotationStatementActionMustDefineAnnotationProperty.SetsFlexoRole(pr));
                }
                return new ValidationError<>(this, action, "add_annotation_property_statement_action_does_not_define_an_annotation_property", v);
            }
            return null;
        }

        protected static class SetsFlexoRole
                extends FixProposal<AddAnnotationStatement.AddAnnotationStatementActionMustDefineAnnotationProperty, AddAnnotationStatement> {

            private final AnnotationStatementRole flexoRole;

            public SetsFlexoRole(AnnotationStatementRole flexoRole) {
                super("assign_action_to_flexo_role_($flexoRole.flexoRoleName)");
                this.flexoRole = flexoRole;
            }

            public AnnotationStatementRole getFlexoRole() {
                return flexoRole;
            }

            @Override
            protected void fixAction() {
                AddAnnotationStatement action = getValidable();
                ((AssignationAction<?>) action.getOwner()).setAssignation(new DataBinding<>(flexoRole.getRoleName()));
            }

        }
    }

    public static class ValueIsRequiredAndMustBeValid extends BindingIsRequiredAndMustBeValid<AddAnnotationStatement> {
        public ValueIsRequiredAndMustBeValid() {
            super("'value'_binding_is_required_and_must_be_valid", AddAnnotationStatement.class);
        }

        @Override
        public DataBinding<?> getBinding(AddAnnotationStatement object) {
            return object.getValue();
        }

    }
}
