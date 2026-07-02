package org.openflexo.technologyadapter.owl.fml.editionaction;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.connie.DataBinding;
import org.openflexo.foundation.fml.annotations.FMLAttribute;
import org.openflexo.foundation.fml.editionaction.AbstractFetchRequest;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.pamela.annotations.*;
import org.openflexo.technologyadapter.owl.OWLModelSlot;
import org.openflexo.technologyadapter.owl.model.*;

@ModelEntity(isAbstract = true)
@ImplementationClass(AbstractSelectOWLAnnotationStatement.SelectOWLAnnotationStatementImpl.class)
public interface AbstractSelectOWLAnnotationStatement<AT>
        extends AbstractFetchRequest<OWLModelSlot, OWLOntology, AnnotationStatement, AT> {
    @PropertyIdentifier(type = DataBinding.class)
    public static final String SUBJECT_KEY = "subject";

    @Getter(SUBJECT_KEY)
    @FMLAttribute(value = SUBJECT_KEY, required = true)
    public DataBinding<OWLObject> getSubject();

    @Setter(SUBJECT_KEY)
    public void setSubject(DataBinding<OWLObject> subject);

    public static abstract class SelectOWLAnnotationStatementImpl<AT>
            extends AbstractFetchRequest.AbstractFetchRequestImpl<OWLModelSlot, OWLOntology, AnnotationStatement, AT>
            implements AbstractSelectOWLAnnotationStatement<AT> {

        private static final Logger logger =
                Logger.getLogger(AbstractSelectOWLAnnotationStatement.class.getPackage().getName());

        private DataBinding<OWLObject> subject;

        @Override
        public DataBinding<OWLObject> getSubject() {
            if (subject == null) {
                subject = new DataBinding<>(this, OWLObject.class, DataBinding.BindingDefinitionType.GET);
                subject.setBindingName("subject");
            }
            return subject;
        }

        @Override
        public void setSubject(DataBinding<OWLObject> subject) {
            if (subject != null) {
                subject.setOwner(this);
                subject.setDeclaredType(OWLObject.class);
                subject.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
                subject.setBindingName("subject");
            }
            this.subject = subject;
        }
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

            OWLObject requestedSubject =
                    evaluateSubject(evaluationContext);

            if (requestedSubject != null) {
                collectAnnotationStatementsForSubject(
                        ontology,
                        requestedSubject,
                        selectedStatements);

                return filterWithConditions(
                        selectedStatements,
                        evaluationContext);
            }

            collectAnnotationStatements(
                    ontology.getClasses(),
                    selectedStatements);

            collectAnnotationStatements(
                    ontology.getObjectProperties(),
                    selectedStatements);

            collectAnnotationStatements(
                    ontology.getDataProperties(),
                    selectedStatements);

            collectAnnotationStatements(
                    ontology.getIndividuals(),
                    selectedStatements);

            return filterWithConditions(
                    selectedStatements,
                    evaluationContext);
        }

        private OWLObject evaluateSubject(
                RunTimeEvaluationContext evaluationContext) {

            if (getSubject() == null
                    || !getSubject().isSet()
                    || !getSubject().isValid()) {
                return null;
            }

            try {
                return getSubject().getBindingValue(evaluationContext);
            } catch (Exception e) {
                logger.warning(
                        "Could not evaluate subject binding for SelectOWLAnnotationStatement: "
                                + e.getMessage());
                return null;
            }
        }

        private void collectAnnotationStatementsForSubject(
                OWLOntology ontology,
                OWLObject subject,
                List<AnnotationStatement> selectedStatements) {

            if (subject instanceof OWLConcept<?>) {
                collectAnnotationStatements(
                        (OWLConcept<?>) subject,
                        selectedStatements);
                return;
            }

            if (subject instanceof OWLStatement) {
                addAnnotationStatements(
                        ontology.getAxiomAnnotationStatements((OWLStatement) subject),
                        selectedStatements);
            }
        }

        private void collectAnnotationStatements(
                List<? extends OWLConcept<?>> concepts,
                List<AnnotationStatement> selectedStatements) {

            if (concepts == null) {
                return;
            }

            for (OWLConcept<?> concept : concepts) {
                collectAnnotationStatements(
                        concept,
                        selectedStatements);
            }
        }

        private void collectAnnotationStatements(
                OWLConcept<?> concept,
                List<AnnotationStatement> selectedStatements) {

            if (concept == null) {
                return;
            }

            addAnnotationStatements(
                    concept.getOWLAnnotationStatements(),
                    selectedStatements);

            if (concept.getOntology() != null) {
                addAnnotationStatements(
                        concept.getOntology().getAxiomAnnotationStatements(concept),
                        selectedStatements);
            }
        }

        private void addAnnotationStatements(
                Vector<AnnotationStatement> statements,
                List<AnnotationStatement> selectedStatements) {

            if (statements == null || selectedStatements == null) {
                return;
            }

            for (AnnotationStatement statement : statements) {
                if (statement != null && !selectedStatements.contains(statement)) {
                    selectedStatements.add(statement);
                }
            }
        }
    }
}