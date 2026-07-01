package org.openflexo.technologyadapter.owl.fml.editionaction;

import java.util.List;

import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.editionaction.FetchRequest;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.owl.OWLModelSlot;
import org.openflexo.technologyadapter.owl.model.AnnotationStatement;
import org.openflexo.technologyadapter.owl.model.OWLOntology;

@ModelEntity
@ImplementationClass(SelectOWLAnnotationStatement.SelectOWLAnnotationStatementImpl.class)
@XMLElement
@FML("SelectOWLAnnotationStatement")
public interface SelectOWLAnnotationStatement
        extends AbstractSelectOWLAnnotationStatement<List<AnnotationStatement>>,
        FetchRequest<OWLModelSlot, OWLOntology, AnnotationStatement> {
}