package org.openflexo.technologyadapter.owl.fml;

import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.pamela.annotations.*;
import org.openflexo.pamela.validation.ValidationError;
import org.openflexo.pamela.validation.ValidationIssue;
import org.openflexo.pamela.validation.ValidationRule;
import org.openflexo.technologyadapter.owl.OWLModelSlot;
import org.openflexo.technologyadapter.owl.model.*;
import org.openflexo.technologyadapter.owl.nature.OWLOntologyVirtualModelNature;

import java.lang.reflect.Type;
import java.util.logging.Logger;

@ModelEntity
@ImplementationClass(AnnotationStatementRole.AnnotationStatementRoleImpl.class)
@XMLElement
@FML("AnnotationStatementRole")
public interface AnnotationStatementRole extends StatementRole<AnnotationStatement> {
    @PropertyIdentifier(type = String.class)
    public static final String ANNOTATION_URI_KEY = "annotationPropertyURI";

    @Getter(value = ANNOTATION_URI_KEY)
    @XMLAttribute(xmlTag = "annotationProperty")
    public String _getAnnotationPropertyURI();

    @Setter(ANNOTATION_URI_KEY)
    public void _setAnnotationPropertyURI(String dataPropertyURI);

    public OWLAnnotation getAnnotation();

    public void setAnnotation(OWLAnnotation p);
    public static abstract class AnnotationStatementRoleImpl extends StatementRoleImpl<AnnotationStatement>
            implements AnnotationStatementRole {
        static final Logger logger = FlexoLogger.getLogger(AnnotationStatementRole.class.getPackage().toString());

        public AnnotationStatementRoleImpl() {
            super();
        }

        @Override
        public Type getType() {
            if (getAnnotation() == null) {
                return AnnotationStatement.class;
            }
            return StatementWithProperty.getStatementWithProperty(getAnnotation());
        }

        @Override
        public String getTypeDescription() {
            if (getAnnotation() != null) {
                return getAnnotation().getName();
            }
            return "";
        }

        private String annotationPropertyURI;

        @Override
        public String _getAnnotationPropertyURI() {
            return annotationPropertyURI;
        }

        @Override
        public void _setAnnotationPropertyURI(String annotationPropertyURI) {
            this.annotationPropertyURI = annotationPropertyURI;
        }

        @Override
        public OWLAnnotation getAnnotation() {
            if (OWLOntologyVirtualModelNature.INSTANCE.hasNature(getOwningVirtualModel())) {
                return OWLOntologyVirtualModelNature.getOWLAnnotation(_getAnnotationPropertyURI(), getOwningVirtualModel());
            }
            return null;
        }

        @Override
        public void setAnnotation(OWLAnnotation p) {
            _setAnnotationPropertyURI(p != null ? p.getURI() : null);
        }

        @Override
        public AnnotationStatementActorReference makeActorReference(AnnotationStatement object, FlexoConceptInstance epi) {
            org.openflexo.pamela.factory.PamelaModelFactory factory = OWLModelSlot.OWLModelSlotImpl.getModelFactory();
            AnnotationStatementActorReference returned = factory.newInstance(AnnotationStatementActorReference.class);
            returned.setFlexoRole(this);
            returned.setFlexoConceptInstance(epi);
            returned.setModellingElement(object);
            return returned;
        }

    }

    @DefineValidationRule
    public static class AnnotationStatementPatternRoleMustDefineAValidProperty
            extends ValidationRule<AnnotationStatementRole.AnnotationStatementPatternRoleMustDefineAValidProperty, AnnotationStatementRole> {
        public AnnotationStatementPatternRoleMustDefineAValidProperty() {
            super(AnnotationStatementRole.class, "pattern_role_must_define_a_valid_annotation_property");
        }

        @Override
        public ValidationIssue<AnnotationStatementRole.AnnotationStatementPatternRoleMustDefineAValidProperty, AnnotationStatementRole> applyValidation(
                AnnotationStatementRole patternRole) {
            if (patternRole.getAnnotation() == null) {
                return new ValidationError<>(this,
                        patternRole, "pattern_role_does_not_define_any_valid_annotation_property");
            }
            return null;
        }

    }
}
