package org.openflexo.technologyadapter.owl.fml.editionaction;

import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.editionaction.UniqueFetchRequest;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.owl.OWLModelSlot;
import org.openflexo.technologyadapter.owl.model.OWLAnnotation;
import org.openflexo.technologyadapter.owl.model.OWLDataProperty;
import org.openflexo.technologyadapter.owl.model.OWLOntology;

@ModelEntity
@ImplementationClass(SelectUniqueOWLAnnotation.SelectOWLAnnotationImpl.class)
@XMLElement
@FML("SelectUniqueOWLAnnotation")
public interface SelectUniqueOWLAnnotation extends AbstractSelectOWLAnnotation<OWLAnnotation>, UniqueFetchRequest<OWLModelSlot, OWLOntology, OWLAnnotation> {
}

