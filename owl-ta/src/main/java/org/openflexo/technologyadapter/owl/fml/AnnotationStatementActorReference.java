package org.openflexo.technologyadapter.owl.fml;

import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.rt.ActorReference;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.pamela.annotations.*;
import org.openflexo.technologyadapter.owl.model.*;

import java.util.logging.Logger;

@ModelEntity
@ImplementationClass(AnnotationStatementActorReference.AnnotationStatementActorReferenceImpl.class)
@XMLElement
@FML("AnnotationStatementActorReference")
public interface AnnotationStatementActorReference extends ActorReference<AnnotationStatement> {
    @PropertyIdentifier(type = String.class)
    public static final String SUBJECT_URI_KEY = "subjectURI";
    @PropertyIdentifier(type = String.class)
    public static final String ANNOTATION_URI_KEY = "annotationPropertyURI";
    @PropertyIdentifier(type = String.class)
    public static final String VALUE_KEY = "value";

    @Getter(value = SUBJECT_URI_KEY)
    @XMLAttribute
    public String getSubjectURI();

    @Setter(SUBJECT_URI_KEY)
    public void setSubjectURI(String objectURI);

    @Getter(value = ANNOTATION_URI_KEY)
    @XMLAttribute
    public String getAnnotationPropertyURI();

    @Setter(ANNOTATION_URI_KEY)
    public void setAnnotationPropertyURI(String annotationPropertyURI);

    @Getter(value = VALUE_KEY)
    @XMLAttribute
    public String getValue();

    @Setter(VALUE_KEY)
    public void setValue(String value);

    public static abstract class AnnotationStatementActorReferenceImpl extends ActorReferenceImpl<AnnotationStatement>
            implements AnnotationStatementActorReference {

        static final Logger logger = FlexoLogger.getLogger(AnnotationStatementActorReferenceImpl.class.getPackage().toString());

        private AnnotationStatement statement;
        private String subjectURI;
        private String annotationPropertyURI;
        private String value;

        /**
         * Default constructor
         */
        public AnnotationStatementActorReferenceImpl() {
            super();
        }

		/*public DataPropertyStatementActorReference(DataPropertyStatement o, DataPropertyStatementRole aPatternRole,
				FlexoConceptInstance epi) {
			super(epi.getProject());
			setFlexoConceptInstance(epi);
			setPatternRole(aPatternRole);
			statement = o;
			subjectURI = o.getSubject().getURI();
			value = o.getLiteral().toString();
			dataPropertyURI = o.getProperty().getURI();
		}

		// Constructor used during deserialization
		public DataPropertyStatementActorReference(FlexoProject project) {
			super(project);
		}*/

        @Override
        public void setModellingElement(AnnotationStatement statement) {
            this.statement = statement;
            if (statement != null && getModelSlotInstance() != null) {
                subjectURI = statement.getSubject().getURI();
                value = statement.getLiteral().toString();
                annotationPropertyURI = statement.getProperty().getURI();
            }
        }

        @Override
        public AnnotationStatement getModellingElement(boolean forceLoading) {
            if (statement == null) {
                OWLOntology ontology = (OWLOntology) getModelSlotInstance().getAccessedResourceData();
                if (ontology != null) {
                    OWLConcept<?> subject = ontology.getOntologyObject(subjectURI);
                    OWLAnnotation property = null;//ontology.getAnnotation(annotationPropertyURI);
                    if (subject != null && property != null) {
                        // TODO: also handle value here
                        statement = subject.getAnnotationStatement(property);
                    }
                }
                else {
                    logger.warning("Could not access to ontology referenced by " + getModelSlotInstance());
                }
            }
            if (statement == null) {
                logger.warning("Could not retrieve statement" + subjectURI + " " + annotationPropertyURI + " " + value);
            }
            return statement;
        }

        @Override
        public String getSubjectURI() {
            return subjectURI;
        }

        @Override
        public void setSubjectURI(String subjectURI) {
            this.subjectURI = subjectURI;
        }

        @Override
        public String getAnnotationPropertyURI() {
            return annotationPropertyURI;
        }

        @Override
        public void setAnnotationPropertyURI(String annotationPropertyURI) {
            this.annotationPropertyURI = annotationPropertyURI;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public void setValue(String value) {
            this.value = value;
        }
    }

}
