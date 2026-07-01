package org.openflexo.technologyadapter.owl.fml.editionaction;

import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.editionaction.FetchRequest;
import org.openflexo.foundation.fml.editionaction.UniqueFetchRequest;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.owl.OWLModelSlot;
import org.openflexo.technologyadapter.owl.model.AnnotationStatement;
import org.openflexo.technologyadapter.owl.model.OWLOntology;

@ModelEntity
@ImplementationClass(SelectUniqueOWLAnnotationStatement.SelectOWLAnnotationStatementImpl.class)
@XMLElement
@FML("SelectUniqueOWLAnnotationStatement")
public interface SelectUniqueOWLAnnotationStatement
        extends AbstractSelectOWLAnnotationStatement<AnnotationStatement>,
        UniqueFetchRequest<OWLModelSlot, OWLOntology, AnnotationStatement> {
}