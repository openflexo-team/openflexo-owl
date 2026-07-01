package org.openflexo.technologyadapter.owl.model;

import org.apache.jena.ontology.OntProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyAnnotation;
import org.openflexo.foundation.ontology.IFlexoOntologyDataProperty;
import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class OWLAnnotation extends OWLProperty
        implements IFlexoOntologyAnnotation<OWLTechnologyAdapter>, Comparable<IFlexoOntologyAnnotation<OWLTechnologyAdapter>>{
    static final Logger logger = Logger.getLogger(IFlexoOntologyAnnotation.class.getPackage().getName());

    protected OWLAnnotation(OntProperty aAnnotation, OWLOntology ontology, OWLTechnologyAdapter adapter) {
        super(aAnnotation, ontology, adapter);
    }


    public boolean delete(Object... context) {
        getFlexoOntology().removeAnnotationProperty(this);
        getOntResource().remove();
        getFlexoOntology().updateConceptsAndProperties();
        super.delete(context);
        deleteObservers();
        return true;
    }

    @Override
    public int compareTo(IFlexoOntologyAnnotation<OWLTechnologyAdapter> o) {
        return COMPARATOR.compare(this, o);
    }

    public OWLDataType getDataType() {
        return getRange();
    }

    @Override
    public Type getType() {
        if (getDataType() != null) {
            return getDataType().getAccessedType();
        }
        return Object.class;
    }

    @Override
    public OWLDataType getRange() {
        if (getRangeStatement() != null) {
            return getRangeStatement().getDataType();
        }
        return null;
    }

    @Override
    public String getDisplayableDescription() {
        return "<html>Annotation property <b>" + getName() + "</b><br>" + "<i>" + getURI() + "</i><br>" + "Domain: "
                + (getDomain() != null ? getDomain().getURI() : "?") + "<br>" + "Range: "
                + (getRange() != null ? getRange().toString() : "?") + "<br>" + "</html>";
    }

    @Override
    public String getHTMLDescription() {
        StringBuffer sb = new StringBuffer();
        sb.append("<html>");
        sb.append("Annotation <b>" + getName() + "</b><br>");
        sb.append("<i>" + getURI() + "</i><br>");
        sb.append("<b>Asserted in:</b> " + getOntology().getURI() + "<br>");
        sb.append("<b>Domain:</b> " + (getDomain() != null ? getDomain().getURI() : "?") + "<br>");
        sb.append("<b>Datatype:</b> " + (getDataType() != null ? getDataType().toString() : "?") + "<br>");
        if (redefinesOriginalDefinition()) {
            sb.append("<b>Redefines:</b> " + getOriginalDefinition() + "<br>");
        }
        sb.append("</html>");
        return sb.toString();
    }

    @Override
    protected void recursivelySearchRangeAndDomains() {
        super.recursivelySearchRangeAndDomains();
        for (OWLProperty aProperty : getSuperProperties()) {
            propertiesTakingMySelfAsRange.addAll(aProperty.getPropertiesTakingMySelfAsRange());
            propertiesTakingMySelfAsDomain.addAll(aProperty.getPropertiesTakingMySelfAsDomain());
        }
        OWLClass ANNOTATION_CONCEPT = getOntology().getClass(OWL_ANNOTATION_URI);
        // DATA_PROPERTY_CONCEPT is generally non null but can be null when reading RDFS for example
        if (ANNOTATION_CONCEPT != null) {
            propertiesTakingMySelfAsRange.addAll(ANNOTATION_CONCEPT.getPropertiesTakingMySelfAsRange());
            propertiesTakingMySelfAsDomain.addAll(ANNOTATION_CONCEPT.getPropertiesTakingMySelfAsDomain());
        }
    }
    @Override
   public Map<String, String> getDetails(){
        Map<String, String> details = new HashMap<String, String>();

        return details;
    }
}
