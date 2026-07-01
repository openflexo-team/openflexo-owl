package org.openflexo.technologyadapter.owl.fml.editionaction;

import org.openflexo.foundation.fml.editionaction.AbstractFetchRequest;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.technologyadapter.owl.OWLModelSlot;
import org.openflexo.technologyadapter.owl.model.OWLAnnotation;
import org.openflexo.technologyadapter.owl.model.OWLDataProperty;
import org.openflexo.technologyadapter.owl.model.OWLOntology;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@ModelEntity(isAbstract = true)
@ImplementationClass(AbstractSelectOWLAnnotation.SelectOWLAnnotationImpl.class)
public interface AbstractSelectOWLAnnotation<AT> extends AbstractFetchRequest<OWLModelSlot, OWLOntology, OWLAnnotation, AT> {
    public static abstract class SelectOWLAnnotationImpl<AT> extends
            AbstractFetchRequest.AbstractFetchRequestImpl<OWLModelSlot, OWLOntology, OWLAnnotation, AT> implements AbstractSelectOWLAnnotation<AT> {
        private static final Logger logger = Logger.getLogger(AbstractSelectOWLAnnotation.class.getPackage().getName());

        @Override
        public Type getFetchedType() {
            return OWLAnnotation.class;
        }
        @Override
        public List<OWLAnnotation> performExecute(RunTimeEvaluationContext evaluationContext) {

            // TODO: improve perfs !

            OWLOntology ontology = getReceiver(evaluationContext);

            List<OWLAnnotation> selectedProperties = new ArrayList<>();
            for (OWLAnnotation i : ontology.getAccessibleAnnotationProperties()) {
                boolean takeIt = true;
                if (takeIt && !selectedProperties.contains(i)) {
                    selectedProperties.add(i);
                }
            }

            return filterWithConditions(selectedProperties, evaluationContext);

        }



    }
}
