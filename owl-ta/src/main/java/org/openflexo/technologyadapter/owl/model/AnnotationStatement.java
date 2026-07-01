package org.openflexo.technologyadapter.owl.model;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Statement;
import org.openflexo.foundation.ontology.IFlexoOntologyAnnotationValue;
import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;

/**
 * Concrete literal-valued annotation assertion.
 */
public class AnnotationStatement extends AnnotationPropertyStatement
        implements IFlexoOntologyAnnotationValue<OWLTechnologyAdapter>,Comparable<IFlexoOntologyAnnotationValue<OWLTechnologyAdapter>>{

    private static final Logger logger = Logger.getLogger(AnnotationStatement.class.getPackage().getName());

    private final OWLAnnotation property;

    public AnnotationStatement(OWLObject subject, Statement s, OWLTechnologyAdapter adapter) {
        super(subject, s, adapter);

        OWLAnnotation resolved = null;
        if (getOntology() != null && s != null && s.getPredicate() != null) {
            resolved = getOntology().getAnnotation(s.getPredicate().getURI());
        }
        property = resolved;

        if (!(s.getObject() instanceof Literal)) {
            logger.warning("AnnotationStatement: object is not a Literal !");
        }
    }
    public AnnotationStatement(OWLObject subject, OWLAnnotation property, Statement s, OWLTechnologyAdapter adapter) {
        super(subject, s, adapter);
        this.property = property;

        if (!(s.getObject() instanceof Literal)) {
            logger.warning("AnnotationStatement: object is not a Literal !");
        }
    }

    @Override
    public OWLAnnotation getProperty() {
        return property;
    }

    @Override
    public OWLAnnotation getAnnotation() {
        return getProperty();
    }

    @Override
    public OWLAnnotation getPredicate() {
        return getProperty();
    }

    public OWLDataType getDataType() {
        if (getProperty() != null) {
            return getProperty().getDataType();
        }
        return null;
    }

    @Override
    public Literal getLiteral() {
        if (getStatement() != null && getStatement().getObject() instanceof Literal) {
            return (Literal) getStatement().getObject();
        }
        return null;
    }

    public Object getValue() {
        Literal literal = getLiteral();

        if (literal == null) {
            logger.warning("[AnnotationStatement.getValue] literal is null for statement=" + getStatement());
            return null;
        }

        if (getDataType() != null) {
            Object value = getDataType().valueFromLiteral(literal);
            logger.warning("[AnnotationStatement.getValue] typed value=" + value);
            return value;
        }

        Object value = literal.getValue();
        logger.warning("[AnnotationStatement.getValue] raw literal value=" + value);
        return value;
    }

    public final void setValue(Object aValue) {
        replaceLiteral(aValue, getLanguage());
    }

    @Override
    public List<Object> getValues() {
        return Collections.singletonList(getValue());
    }

    @Override
    public String toString() {
        String subjectName = getObjectSubject() != null ? getObjectSubject().getName() : "null";
        String propertyName = getProperty() != null ? getProperty().getName() : "null";
        return "(A) " + subjectName + " " + propertyName + "=\"" + getLiteral() + "\" language=" + getLanguage();
    }

    @Override
    public int compareTo(IFlexoOntologyAnnotationValue<OWLTechnologyAdapter> o) {

        if (o == null) {
            return 1;
        }

        if (o == this) {
            return 0;
        }

        String thisSubjectURI = "";
        String thisPropertyURI = "";
        String thisValue = "";
        String thisLanguage = "";

        if (getObjectSubject() != null && getObjectSubject().getURI() != null) {
            thisSubjectURI = getObjectSubject().getURI();
        }

        if (getProperty() != null && getProperty().getURI() != null) {
            thisPropertyURI = getProperty().getURI();
        }

        if (getStringValue() != null) {
            thisValue = getStringValue();
        }

        if (getLanguageTag() != null) {
            thisLanguage = getLanguageTag();
        }

        String otherSubjectURI = "";
        String otherPropertyURI = "";
        String otherValue = "";
        String otherLanguage = "";

        if (o instanceof AnnotationStatement) {
            AnnotationStatement other = (AnnotationStatement) o;

            if (other.getObjectSubject() != null && other.getObjectSubject().getURI() != null) {
                otherSubjectURI = other.getObjectSubject().getURI();
            }

            if (other.getProperty() != null && other.getProperty().getURI() != null) {
                otherPropertyURI = other.getProperty().getURI();
            }

            if (other.getStringValue() != null) {
                otherValue = other.getStringValue();
            }

            if (other.getLanguageTag() != null) {
                otherLanguage = other.getLanguageTag();
            }
        }
        else {
            otherValue = String.valueOf(o);
        }

        int comparison = thisSubjectURI.compareTo(otherSubjectURI);
        if (comparison != 0) {
            return comparison;
        }

        comparison = thisPropertyURI.compareTo(otherPropertyURI);
        if (comparison != 0) {
            return comparison;
        }

        comparison = thisValue.compareTo(otherValue);
        if (comparison != 0) {
            return comparison;
        }

        return thisLanguage.compareTo(otherLanguage);
    }
}