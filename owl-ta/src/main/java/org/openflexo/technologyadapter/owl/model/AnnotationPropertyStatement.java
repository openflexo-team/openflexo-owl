package org.openflexo.technologyadapter.owl.model;

import java.util.Vector;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.ontology.IFlexoOntologyPropertyValue;
import org.openflexo.localization.Language;
import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;
import org.openflexo.toolbox.StringUtils;

/**
 * Base class for annotation assertions.
 *
 * This class stays in the OWLStatement hierarchy through OWLObjectStatement,
 * while being able to target either:
 * - an OWLConcept
 * - an OWLStatement (through OWL2 owl:Axiom encoding)
 */
public abstract class AnnotationPropertyStatement extends OWLObjectStatement
        implements IFlexoOntologyPropertyValue<OWLTechnologyAdapter> {

    public static final String AS_STRING = "asString";
    public static final String AS_BOOLEAN = "asBoolean";
    public static final String AS_INTEGER = "asInteger";
    public static final String AS_BYTE = "asByte";
    public static final String AS_SHORT = "asShort";
    public static final String AS_LONG = "asLong";
    public static final String AS_CHARACTER = "asCharacter";
    public static final String AS_FLOAT = "asFloat";
    public static final String AS_DOUBLE = "asDouble";

    private static final String RDF_TYPE_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
    private static final String OWL_AXIOM_URI = "http://www.w3.org/2002/07/owl#Axiom";
    private static final String OWL_ANNOTATED_SOURCE_URI = "http://www.w3.org/2002/07/owl#annotatedSource";
    private static final String OWL_ANNOTATED_PROPERTY_URI = "http://www.w3.org/2002/07/owl#annotatedProperty";
    private static final String OWL_ANNOTATED_TARGET_URI = "http://www.w3.org/2002/07/owl#annotatedTarget";

    private Language language = null;
    private String stringValue = null;
    private boolean booleanValue = false;
    private int intValue = 0;
    private byte byteValue = 0;
    private short shortValue = 0;
    private long longValue = 0;
    private char charValue = 0;
    private float floatValue = 0;
    private double doubleValue = 0;

    protected AnnotationPropertyStatement(OWLObject subject, Statement statement, OWLTechnologyAdapter adapter) {
        super(subject, statement, adapter);
    }

    /**
     * Returns the subject as OWLConcept when possible.
     */
    protected OWLConcept<?> getConceptSubject() {
        return getSubject();
    }

    protected boolean hasConceptSubject() {
        return getConceptSubject() != null;
    }

    /**
     * Returns the subject as OWLStatement when the annotation targets a statement.
     */
    protected OWLStatement getStatementSubject() {
        if (getObjectSubject() instanceof OWLStatement) {
            return (OWLStatement) getObjectSubject();
        }
        return null;
    }

    protected boolean hasStatementSubject() {
        return getStatementSubject() != null;
    }

    protected Model getModel() {
        return getFlexoOntology().getOntModel();
    }

    protected Property rdfTypeProperty() {
        return getModel().getProperty(RDF_TYPE_URI);
    }

    protected Resource owlAxiomClass() {
        return getModel().getResource(OWL_AXIOM_URI);
    }

    protected Property annotatedSourceProperty() {
        return getModel().getProperty(OWL_ANNOTATED_SOURCE_URI);
    }

    protected Property annotatedPropertyProperty() {
        return getModel().getProperty(OWL_ANNOTATED_PROPERTY_URI);
    }

    protected Property annotatedTargetProperty() {
        return getModel().getProperty(OWL_ANNOTATED_TARGET_URI);
    }

    /**
     * Finds an owl:Axiom resource corresponding to the supplied base statement.
     */
    protected Resource findAxiomResource(Statement baseStatement) {
        StmtIterator it = getModel().listStatements(null, rdfTypeProperty(), owlAxiomClass());
        while (it.hasNext()) {
            Statement typeStmt = it.nextStatement();
            Resource axiom = typeStmt.getSubject();

            Statement s1 = axiom.getProperty(annotatedSourceProperty());
            Statement s2 = axiom.getProperty(annotatedPropertyProperty());
            Statement s3 = axiom.getProperty(annotatedTargetProperty());

            if (s1 == null || s2 == null || s3 == null) {
                continue;
            }

            RDFNode src = s1.getObject();
            RDFNode pred = s2.getObject();
            RDFNode tgt = s3.getObject();

            boolean sameSource = src.isResource() && src.asResource().equals(baseStatement.getSubject());
            boolean samePredicate = pred.isURIResource()
                    && baseStatement.getPredicate().getURI().equals(pred.asResource().getURI());
            boolean sameTarget = tgt.equals(baseStatement.getObject());

            if (sameSource && samePredicate && sameTarget) {
                return axiom;
            }
        }
        return null;
    }

    /**
     * Returns the RDF resource that must carry the annotation:
     * - the concept resource for concept annotations
     * - an owl:Axiom resource for statement annotations
     */
    protected Resource getOrCreateCarrier() {
        if (hasConceptSubject()) {
            return getConceptSubject().getOntResource();
        }

        if (hasStatementSubject()) {
            OWLStatement base = getStatementSubject();
            Resource axiom = findAxiomResource(base.getStatement());
            if (axiom != null) {
                return axiom;
            }

            axiom = getModel().createResource();
            axiom.addProperty(rdfTypeProperty(), owlAxiomClass());
            axiom.addProperty(annotatedSourceProperty(), base.getStatement().getSubject());
            axiom.addProperty(annotatedPropertyProperty(), base.getStatement().getPredicate());
            axiom.addProperty(annotatedTargetProperty(), base.getStatement().getObject());
            return axiom;
        }

        throw new UnsupportedOperationException("Unsupported annotation subject: "
                + (getObjectSubject() != null ? getObjectSubject().getClass().getName() : "null"));
    }

    /**
     * Tries to retrieve the newly created annotation statement after update.
     */
    protected Statement findMatchingStatement(Resource carrier, Property property, Object value, Language lang) {
        StmtIterator it = carrier.listProperties(property);
        while (it.hasNext()) {
            Statement s = it.nextStatement();
            if (!(s.getObject() instanceof Literal)) {
                continue;
            }

            Literal l = (Literal) s.getObject();

            if (value instanceof String) {
                String expected = (String) value;
                String expectedLang = lang != null ? lang.getTag() : "";
                String actualLang = l.getLanguage() != null ? l.getLanguage() : "";
                if (expected.equals(l.getString()) && expectedLang.equals(actualLang)) {
                    return s;
                }
            }
            else {
                Object literalValue = l.getValue();
                if (literalValue != null && literalValue.equals(value)) {
                    return s;
                }
            }
        }
        return null;
    }

    /**
     * Replaces the current literal annotation value.
     *
     * For concept subjects, the annotation is written directly on the concept
     * resource.
     *
     * For statement subjects, the annotation is written on an owl:Axiom resource.
     */
    protected void replaceLiteral(Object value, Language lang) {
        Resource carrier = getOrCreateCarrier();
        Property property = getProperty().getOntProperty();

        if (getStatement() != null) {
            getModel().remove(getStatement());
        }

        if (value instanceof String) {
            if (lang != null) {
                carrier.addProperty(property, (String) value, lang.getTag());
            }
            else {
                carrier.addProperty(property, (String) value);
            }
        }
        else if (value instanceof Boolean) {
            carrier.addLiteral(property, ((Boolean) value).booleanValue());
        }
        else if (value instanceof Integer) {
            carrier.addLiteral(property, ((Integer) value).intValue());
        }
        else if (value instanceof Byte) {
            carrier.addLiteral(property, ((Byte) value).byteValue());
        }
        else if (value instanceof Short) {
            carrier.addLiteral(property, ((Short) value).shortValue());
        }
        else if (value instanceof Long) {
            carrier.addLiteral(property, ((Long) value).longValue());
        }
        else if (value instanceof Float) {
            carrier.addLiteral(property, ((Float) value).floatValue());
        }
        else if (value instanceof Double) {
            carrier.addLiteral(property, ((Double) value).doubleValue());
        }
        else if (value instanceof Character) {
            carrier.addLiteral(property, ((Character) value).charValue());
        }
        else {
            carrier.addLiteral(property, value);
        }

        Statement newStatement = findMatchingStatement(carrier, property, value, lang);
        if (newStatement == null) {
            newStatement = carrier.getProperty(property);
        }

        if (newStatement != null) {
            getFlexoOntology().setIsModified();
        }

        if (hasConceptSubject()) {
            getConceptSubject().updateOntologyStatements();
        }
    }

    @Override
    public abstract OWLProperty getProperty();

    public abstract Literal getLiteral();

    public final boolean isAnnotationProperty() {
        return true;
    }

    public String getDisplayableRepresentation() {
        return toString();
    }

    public boolean hasLitteralValue() {
        return getLiteral() != null;
    }

    public final boolean isStringValue() {
        return getLiteral() != null
                && (getLiteral().getDatatype() == null
                || String.class.equals(getLiteral().getDatatype().getJavaClass()));
    }

    public final Language getLanguage() {
        if (getLiteral() == null) {
            return null;
        }
        if (language == null) {
            language = Language.retrieveLanguage(getLiteral().getLanguage());
        }
        return language;
    }

    public final String getLanguageTag() {
        return getLanguage() != null ? getLanguage().getTag() : "";
    }

    public final String getStringValue() {
        if (getLiteral() == null) {
            return null;
        }
        if (stringValue == null) {
            stringValue = getLiteral().getString();
        }
        return stringValue;
    }

    public final void setLanguage(Language aLanguage) {
        if (aLanguage == getLanguage()) {
            return;
        }
        replaceLiteral(getStringValue(), aLanguage);
        language = aLanguage;
    }

    public final void setStringValue(String aValue, String languageTag) {
        if (StringUtils.isSame(aValue, getStringValue())) {
            return;
        }

        Language lang = null;
        if (StringUtils.isNotEmpty(languageTag)) {
            lang = Language.retrieveLanguage(languageTag);
        }

        replaceLiteral(aValue, lang);
        stringValue = aValue;
    }

    public final void setStringValue(String aValue) {
        if (StringUtils.isSame(aValue, getStringValue())) {
            return;
        }

        String oldValue = getStringValue();
        replaceLiteral(aValue, getLanguage());
        stringValue = aValue;
        setChanged();
        notifyObservers(new DataModification(AS_STRING, oldValue, aValue));
    }

    public Vector<Language> allLanguages() {
        return Language.availableValues();
    }

    public final boolean getBooleanValue() {
        if (getLiteral() == null) {
            return false;
        }
        booleanValue = getLiteral().getBoolean();
        return booleanValue;
    }

    public final void setBooleanValue(boolean aValue) {
        if (aValue == getBooleanValue()) {
            return;
        }

        boolean oldValue = getBooleanValue();
        replaceLiteral(Boolean.valueOf(aValue), null);
        booleanValue = aValue;
        setChanged();
        notifyObservers(new DataModification(AS_BOOLEAN, oldValue, aValue));
    }

    public final int getIntegerValue() {
        if (getLiteral() == null) {
            return 0;
        }
        intValue = getLiteral().getInt();
        return intValue;
    }

    public final void setIntegerValue(int aValue) {
        if (aValue == getIntegerValue()) {
            return;
        }

        int oldValue = getIntegerValue();
        replaceLiteral(Integer.valueOf(aValue), null);
        intValue = aValue;
        setChanged();
        notifyObservers(new DataModification(AS_INTEGER, oldValue, aValue));
    }

    public final byte getByteValue() {
        if (getLiteral() == null) {
            return 0;
        }
        byteValue = getLiteral().getByte();
        return byteValue;
    }

    public final void setByteValue(byte aValue) {
        if (aValue == getByteValue()) {
            return;
        }

        byte oldValue = getByteValue();
        replaceLiteral(Byte.valueOf(aValue), null);
        byteValue = aValue;
        setChanged();
        notifyObservers(new DataModification(AS_BYTE, oldValue, aValue));
    }

    public final short getShortValue() {
        if (getLiteral() == null) {
            return 0;
        }
        shortValue = getLiteral().getShort();
        return shortValue;
    }

    public final void setShortValue(short aValue) {
        if (aValue == getShortValue()) {
            return;
        }

        short oldValue = getShortValue();
        replaceLiteral(Short.valueOf(aValue), null);
        shortValue = aValue;
        setChanged();
        notifyObservers(new DataModification(AS_SHORT, oldValue, aValue));
    }

    public final long getLongValue() {
        if (getLiteral() == null) {
            return 0L;
        }
        longValue = getLiteral().getLong();
        return longValue;
    }

    public final void setLongValue(long aValue) {
        if (aValue == getLongValue()) {
            return;
        }

        long oldValue = getLongValue();
        replaceLiteral(Long.valueOf(aValue), null);
        longValue = aValue;
        setChanged();
        notifyObservers(new DataModification(AS_LONG, oldValue, aValue));
    }

    public final float getFloatValue() {
        if (getLiteral() == null) {
            return 0F;
        }
        floatValue = getLiteral().getFloat();
        return floatValue;
    }

    public final void setFloatValue(float aValue) {
        if (aValue == getFloatValue()) {
            return;
        }

        float oldValue = getFloatValue();
        replaceLiteral(Float.valueOf(aValue), null);
        floatValue = aValue;
        setChanged();
        notifyObservers(new DataModification(AS_FLOAT, oldValue, aValue));
    }

    public final double getDoubleValue() {
        if (getLiteral() == null) {
            return 0D;
        }
        doubleValue = getLiteral().getDouble();
        return doubleValue;
    }

    public final void setDoubleValue(double aValue) {
        if (aValue == getDoubleValue()) {
            return;
        }

        double oldValue = getDoubleValue();
        replaceLiteral(Double.valueOf(aValue), null);
        doubleValue = aValue;
        setChanged();
        notifyObservers(new DataModification(AS_DOUBLE, oldValue, aValue));
    }

    public final char getCharacterValue() {
        if (getLiteral() == null) {
            return 0;
        }
        charValue = getLiteral().getChar();
        return charValue;
    }

    public final void setCharacterValue(char aValue) {
        if (aValue == getCharacterValue()) {
            return;
        }

        char oldValue = getCharacterValue();
        replaceLiteral(Character.valueOf(aValue), null);
        charValue = aValue;
        setChanged();
        notifyObservers(new DataModification(AS_CHARACTER, oldValue, aValue));
    }
}