package org.openflexo.technologyadapter.owl.fml;

import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.ontology.fml.AnnotationRole;
import org.openflexo.foundation.ontology.fml.DataPropertyRole;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;
import org.openflexo.technologyadapter.owl.model.OWLAnnotation;
import org.openflexo.technologyadapter.owl.model.OWLDataProperty;
@ModelEntity
@ImplementationClass(OWLAnnotationRole.OWLAnnotationRoleImpl.class)
@XMLElement
@FML("OWLAnnotationRole")
public interface OWLAnnotationRole  extends AnnotationRole<OWLAnnotation> {

    public static abstract class OWLAnnotationRoleImpl extends AnnotationRoleImpl<OWLAnnotation>implements OWLAnnotationRole {

        public OWLAnnotationRoleImpl() {
            super();
        }

        @Override
        public Class<? extends TechnologyAdapter> getRoleTechnologyAdapterClass() {
            return OWLTechnologyAdapter.class;
        }

    }

}
