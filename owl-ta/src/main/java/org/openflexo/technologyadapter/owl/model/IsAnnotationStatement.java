package org.openflexo.technologyadapter.owl.model;

import org.apache.jena.rdf.model.Statement;
import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;

import java.util.logging.Logger;

public class IsAnnotationStatement extends IsAStatement {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(IsAnnotationStatement.class.getPackage().getName());

    public IsAnnotationStatement(OWLConcept<?> subject, Statement s, OWLTechnologyAdapter adapter) {
        super(subject, s, adapter);
    }

    @Override
    public String toString() {
        return getSubject().getName() + " is an Annotation";
    }
}
