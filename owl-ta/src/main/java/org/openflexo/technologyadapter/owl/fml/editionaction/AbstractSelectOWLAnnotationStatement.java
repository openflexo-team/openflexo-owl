package org.openflexo.technologyadapter.owl.fml.editionaction;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.fml.editionaction.AbstractFetchRequest;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.technologyadapter.owl.OWLModelSlot;
import org.openflexo.technologyadapter.owl.model.AnnotationStatement;
import org.openflexo.technologyadapter.owl.model.OWLConcept;
import org.openflexo.technologyadapter.owl.model.OWLOntology;

@ModelEntity(isAbstract = true)
@ImplementationClass(AbstractSelectOWLAnnotationStatement.SelectOWLAnnotationStatementImpl.class)
public interface AbstractSelectOWLAnnotationStatement<AT>
        extends AbstractFetchRequest<OWLModelSlot, OWLOntology, AnnotationStatement, AT> {

    public static abstract class SelectOWLAnnotationStatementImpl<AT>
            extends AbstractFetchRequest.AbstractFetchRequestImpl<OWLModelSlot, OWLOntology, AnnotationStatement, AT>
            implements AbstractSelectOWLAnnotationStatement<AT> {

        private static final Logger logger =
                Logger.getLogger(AbstractSelectOWLAnnotationStatement.class.getPackage().getName());

        @Override
        public Type getFetchedType() {
            return AnnotationStatement.class;
        }

        @Override
        public List<AnnotationStatement> performExecute(RunTimeEvaluationContext evaluationContext) {

            OWLOntology ontology = getReceiver(evaluationContext);

            List<AnnotationStatement> selectedStatements = new ArrayList<>();

            if (ontology == null) {
                return selectedStatements;
            }

            collectAnnotationStatements(ontology.getClasses(), selectedStatements);
            collectAnnotationStatements(ontology.getObjectProperties(), selectedStatements);
            collectAnnotationStatements(ontology.getDataProperties(), selectedStatements);
            collectAnnotationStatements(ontology.getIndividuals(), selectedStatements);

            selectedStatements.addAll(ontology.getAxiomAnnotationStatements());

            System.out.println(selectedStatements);

            return filterWithConditions(selectedStatements, evaluationContext);
        }

        private void collectAnnotationStatements(
                List<? extends OWLConcept<?>> concepts,
                List<AnnotationStatement> selectedStatements) {

            if (concepts == null) {
                return;
            }

            for (OWLConcept<?> concept : concepts) {
                collectAnnotationStatements(concept, selectedStatements);
            }
        }

        @SuppressWarnings("rawtypes")
        private void collectAnnotationStatements(
                OWLConcept<?> concept,
                List<AnnotationStatement> selectedStatements) {

            if (concept == null) {
                return;
            }

            Vector<AnnotationStatement> statements = concept.getOWLAnnotationStatements();

            if (statements == null) {
                return;
            }

            for (AnnotationStatement statement : statements) {
                if (!selectedStatements.contains(statement)) {
                    selectedStatements.add(statement);
                }
            }
        }
    }
}