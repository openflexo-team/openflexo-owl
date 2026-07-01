package org.openflexo.technologyadapter.owl.fml.editionaction;

import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.foundation.ontology.BuiltInDataType;
import org.openflexo.foundation.ontology.DuplicateURIException;
import org.openflexo.foundation.ontology.fml.editionaction.AddAnnotation;
import org.openflexo.foundation.ontology.fml.editionaction.AddDataProperty;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.owl.OWLModelSlot;
import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;
import org.openflexo.technologyadapter.owl.model.*;

import java.lang.reflect.Type;
import java.util.logging.Logger;

@ModelEntity
@ImplementationClass(AddOWLAnnotation.AddOWLAnnotationImpl.class)
@XMLElement
@FML("AddOWLAnnotation")
public interface AddOWLAnnotation  extends AddAnnotation<OWLModelSlot, OWLOntology, OWLAnnotation>, OWLAction<OWLAnnotation> {

    public static abstract class AddOWLAnnotationImpl extends AddAnnotationImpl<OWLModelSlot, OWLOntology, OWLAnnotation>
            implements AddOWLAnnotation {
        private static final Logger logger = Logger.getLogger(AddOWLAnnotation.class.getPackage().getName());

        @Override
        public Class<OWLAnnotation> getOntologyAnnotationClass() {
            return OWLAnnotation.class;
        }
        @Override
        public OWLAnnotation execute(RunTimeEvaluationContext evaluationContext) {
            /*
            OWLClass domain = null;

            if (getDynamicDomain().isValid()) {
                try {
                    domain = (OWLClass) getDynamicDomain().getBindingValue(evaluationContext);
                } catch (TypeMismatchException e) {
                    e.printStackTrace();
                } catch (NullReferenceException e) {
                    e.printStackTrace();
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            }
*/
            String propertyName = null;
            try {
                propertyName = getPropertyName().getBindingValue(evaluationContext);
            } catch (TypeMismatchException e1) {
                e1.printStackTrace();
            } catch (NullReferenceException e1) {
                e1.printStackTrace();
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
            // System.out.println("individualName="+individualName);

            OWLOntology receiver = getReceiver(evaluationContext);

            /*if (domain == null) {
                domain = receiver.getRootClass();
            }*/

            OWLAnnotation newAnnotation = null;
            try {
                if (receiver != null) {
                    BuiltInDataType dt = getDataType();
                    OWLDataType owlDT = null;
                    if (dt != null) {
                        OWLTechnologyAdapter ta = (OWLTechnologyAdapter) getModelSlotTechnologyAdapter();
                        owlDT = ta.getTechnologyContextManager().getDataType(dt.getURI());
                    }

                    logger.info("Adding OWLAnnotation name=" + propertyName + " dataType=" + getDataType());
                    logger.info("Adding individual individualName=" + propertyName );
                    newAnnotation = receiver.createAnnotation(propertyName, null, null, owlDT);
                    logger.info("********* Added OWLAnnotation" + newAnnotation.getName() + " domain " + newAnnotation.getDomain());
                }
                else {
                    logger.warning("No receiver defined " + getReceiver());
                }
                logger.info("Return " + newAnnotation);
                return newAnnotation;
            } catch (DuplicateURIException e) {
                e.printStackTrace();
                return null;
            }

        }
        @Override
        public Type getAssignableType() {
            return OWLAnnotation.class;
        }
    }
}
