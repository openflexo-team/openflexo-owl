package org.openflexo.technologyadapter.owl.fml.editionaction;

import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.editionaction.FetchRequest;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.owl.OWLModelSlot;
import org.openflexo.technologyadapter.owl.model.OWLDataProperty;
import org.openflexo.technologyadapter.owl.model.OWLObjectProperty;
import org.openflexo.technologyadapter.owl.model.OWLOntology;

import java.util.List;

@ModelEntity
@ImplementationClass(SelectOWLDataProperty.SelectOWLDataPropertyImpl.class)
@XMLElement
@FML("SelectOWLDataProperty")
public interface SelectOWLDataProperty extends AbstractSelectOWLDataProperty<List<OWLDataProperty>>, FetchRequest<OWLModelSlot, OWLOntology, OWLDataProperty> {
}
