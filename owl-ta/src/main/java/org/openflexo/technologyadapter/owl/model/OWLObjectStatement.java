package org.openflexo.technologyadapter.owl.model;

import org.apache.jena.rdf.model.Statement;
import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;

/**
 * Bridge statement class.
 *
 * It remains an OWLStatement for compatibility with existing code, while also
 * allowing a broader OWLObject subject for special cases such as annotations on
 * statements.
 */
public abstract class OWLObjectStatement extends OWLStatement {

    private final OWLObject objectSubject;

    /**
     * Standard constructor for normal OWLConcept subjects.
     */
    public OWLObjectStatement(OWLConcept<?> subject, Statement s, OWLTechnologyAdapter adapter) {
        super(subject, s, adapter);
        this.objectSubject = subject;
    }

    /**
     * Extended constructor for generic OWLObject subjects.
     *
     * If the supplied subject is also an OWLConcept, it is passed to the
     * OWLStatement constructor. Otherwise null is passed to preserve the old
     * OWLStatement contract without changing that class.
     */
    public OWLObjectStatement(OWLObject subject, Statement s, OWLTechnologyAdapter adapter) {
        super(resolveConceptSubject(subject), s, adapter);
        this.objectSubject = subject;
    }

    private static OWLConcept<?> resolveConceptSubject(OWLObject subject) {
        if (subject instanceof OWLConcept<?>) {
            return (OWLConcept<?>) subject;
        }
        if (subject instanceof OWLStatement) {
            return ((OWLStatement) subject).getSubject();
        }
        return null;
    }

    /**
     * Returns the real subject carried by this bridge class.
     */
    public OWLObject getObjectSubject() {
        return objectSubject;
    }

    /**
     * Keeps backward compatibility with code expecting an OWLConcept subject.
     * Returns null when the effective subject is not an OWLConcept.
     */
    @Override
    public OWLConcept<?> getSubject() {
        if (objectSubject instanceof OWLConcept<?>) {
            return (OWLConcept<?>) objectSubject;
        }
        return null;
    }

    @Override
    public OWLOntology getFlexoOntology() {
        if (objectSubject != null) {
            return objectSubject.getFlexoOntology();
        }
        return super.getFlexoOntology();
    }

    @Override
    public OWLOntology getOntology() {
        return getFlexoOntology();
    }

    @Override
    public boolean delete(Object... context) {
        if (getStatement() != null && getFlexoOntology() != null) {
            getFlexoOntology().getOntModel().remove(getStatement());

            // Refresh only when the subject is still a normal ontology concept
            if (objectSubject instanceof OWLConcept<?>) {
                ((OWLConcept<?>) objectSubject).updateOntologyStatements();
            }
        }
        super.delete(context);
        return true;
    }
}