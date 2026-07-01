package org.openflexo.technologyadapter.owl.fml.editionaction;

import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.foundation.ontology.DuplicateURIException;
import org.openflexo.foundation.ontology.fml.editionaction.AddDataProperty;
import org.openflexo.foundation.ontology.fml.editionaction.AddObjectProperty;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.owl.OWLModelSlot;
import org.openflexo.technologyadapter.owl.model.OWLClass;
import org.openflexo.technologyadapter.owl.model.OWLDataProperty;
import org.openflexo.technologyadapter.owl.model.OWLObjectProperty;
import org.openflexo.technologyadapter.owl.model.OWLOntology;

import java.lang.reflect.Type;
import java.util.logging.Logger;

@ModelEntity
@ImplementationClass(AddOWLObjectProperty.AddOWLObjectPropertyImpl.class)
@XMLElement
@FML("AddOWLObjectProperty")
public interface AddOWLObjectProperty extends AddObjectProperty<OWLModelSlot, OWLOntology, OWLObjectProperty>, OWLAction<OWLObjectProperty>{
    public static abstract class AddOWLObjectPropertyImpl extends AddObjectProperty.AddObjectPropertyImpl<OWLModelSlot, OWLOntology, OWLObjectProperty>
            implements AddOWLObjectProperty {
        private static final Logger logger = Logger.getLogger(AddOWLObjectProperty.class.getPackage().getName());

        @Override
        public Class<OWLObjectProperty> getOntologyObjectPropertyClass() {
            return OWLObjectProperty.class;
        }

        @Override
        public OWLObjectProperty execute(RunTimeEvaluationContext evaluationContext) {

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
            logger.warning(domain.getURI());
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
            OWLClass range = null;

            if (getRange().isValid()) {
                try {
                    range = (OWLClass) getRange().getBindingValue(evaluationContext);
                } catch (TypeMismatchException e) {
                    e.printStackTrace();
                } catch (NullReferenceException e) {
                    e.printStackTrace();
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            }

            OWLOntology receiver = getReceiver(evaluationContext);

            if (domain == null) {
                domain = receiver.getRootClass();
            }
            if (range == null) {
                range = receiver.getRootClass();
            }

            OWLObjectProperty newObjectProperty = null;
            try {
                if (receiver != null) {
                    logger.info("Adding OWLObjectProperty name=" + propertyName + " domain =" + domain + " range=" + range);
                    logger.info("Adding individual individualName=" + propertyName + " father =" + domain);
                    newObjectProperty = receiver.createObjectProperty(propertyName, null, domain, range);
                    logger.info("********* Added OWLObjectProperty " + newObjectProperty.getName() + " domain " + newObjectProperty.getDomain());
                }
                else {
                    logger.warning("No receiver defined " + getReceiver());
                }
                logger.info("Return " + newObjectProperty);
                return newObjectProperty;
            } catch (DuplicateURIException e) {
                e.printStackTrace();
                return null;
            }

        }
        @Override
        public Type getAssignableType() {
            return OWLObjectProperty.class;
        }


    }
}
