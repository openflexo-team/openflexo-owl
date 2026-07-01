package org.openflexo.technologyadapter.owl.fml.editionaction;

import org.openflexo.foundation.fml.editionaction.AbstractFetchRequest;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.technologyadapter.owl.OWLModelSlot;
import org.openflexo.technologyadapter.owl.model.OWLDataProperty;
import org.openflexo.technologyadapter.owl.model.OWLObjectProperty;
import org.openflexo.technologyadapter.owl.model.OWLOntology;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@ModelEntity(isAbstract = true)
@ImplementationClass(AbstractSelectOWLDataProperty.SelectOWLDataPropertyImpl.class)
public interface AbstractSelectOWLDataProperty<AT> extends AbstractFetchRequest<OWLModelSlot, OWLOntology, OWLDataProperty, AT> {
    public static abstract class SelectOWLDataPropertyImpl<AT> extends
            AbstractFetchRequestImpl<OWLModelSlot, OWLOntology, OWLDataProperty, AT> implements AbstractSelectOWLDataProperty<AT> {

        private static final Logger logger = Logger.getLogger(AbstractSelectOWLDataProperty.class.getPackage().getName());

        @Override
        public Type getFetchedType() {
            return OWLDataProperty.class;
        }
        @Override
        public List<OWLDataProperty> performExecute(RunTimeEvaluationContext evaluationContext) {

            // TODO: improve perfs !

            OWLOntology ontology = getReceiver(evaluationContext);

            List<OWLDataProperty> selectedProperties = new ArrayList<>();
            for (OWLDataProperty i : ontology.getAccessibleDataProperties()) {
                boolean takeIt = true;
                if (takeIt && !selectedProperties.contains(i)) {
                    selectedProperties.add(i);
                }
            }

            return filterWithConditions(selectedProperties, evaluationContext);

        }


    }
}
