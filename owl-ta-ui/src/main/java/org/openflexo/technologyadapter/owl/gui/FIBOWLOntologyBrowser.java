/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2012-2012, AgileBirds
 * 
 * This file is part of Openflexo-technology-adapters-ui, a component of the software infrastructure 
 * developed at Openflexo.
 * 
 * 
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either 
 * version 1.1 of the License, or any later version ), which is available at 
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any 
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * 
 * You can redistribute it and/or modify under the terms of either of these licenses
 * 
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

package org.openflexo.technologyadapter.owl.gui;

import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.ontology.components.widget.FIBOntologyBrowser;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.technologyadapter.owl.model.OWLOntology;

/**
 * Widget allowing to browse an {@link OWLOntology}.<br>
 * 
 * @see FIBOntologyBrowser
 * 
 * @author sguerin
 * 
 */
public class FIBOWLOntologyBrowser extends FIBOntologyBrowser {
	@SuppressWarnings("hiding")
	static final Logger logger = Logger.getLogger(FIBOWLOntologyBrowser.class.getPackage().getName());

	public static final Resource FIB_FILE = ResourceLocator.locateResource("Fib/FIBOWLOntologyBrowser.fib");

	public FIBOWLOntologyBrowser(OWLOntology ontology) {
		super(FIB_FILE, ontology);
	}

	@Override
	public OWLOntology getOntology() {
		return (OWLOntology) super.getOntology();
	}

	public boolean getShowOWLAndRDFConcepts() {
		return showTechnologySpecificConcepts();
	}

	@CustomComponentParameter(name = "showOWLAndRDFConcepts", type = CustomComponentParameter.Type.OPTIONAL)
	public void setShowOWLAndRDFConcepts(boolean showOWLAndRDFConcepts) {
		setShowTechnologySpecificConcepts(showOWLAndRDFConcepts);
	}

	@Override
	public boolean showTechnologySpecificConcepts() {
		return getModel().getShowOWLAndRDFConcepts();
	}

	@Override
	public void setShowTechnologySpecificConcepts(boolean flag) {
		boolean oldValue = showTechnologySpecificConcepts();
		if (oldValue != flag) {
			getModel().setShowOWLAndRDFConcepts(flag);
			update();
			getPropertyChangeSupport().firePropertyChange("showTechnologySpecificConcepts", oldValue, flag);
			getPropertyChangeSupport().firePropertyChange("showOWLAndRDFConcepts", oldValue, flag);
		}
	}

	@Override
	public OWLOntologyBrowserModel getModel() {
		return (OWLOntologyBrowserModel) super.getModel();
	}

	@Override
	protected OWLOntologyBrowserModel performBuildOntologyBrowserModel(IFlexoOntology ontology) {
		return new OWLOntologyBrowserModel((OWLOntology) ontology);
	}

	@Override
	public ImageIcon getOntologyClassIcon() {
		return OWLIconLibrary.ONTOLOGY_CLASS_ICON;
	}

	@Override
	public ImageIcon getOntologyIndividualIcon() {
		return OWLIconLibrary.ONTOLOGY_INDIVIDUAL_ICON;
	}

	@Override
	public ImageIcon getOntologyDataPropertyIcon() {
		return OWLIconLibrary.ONTOLOGY_DATA_PROPERTY_ICON;
	}

	@Override
	public ImageIcon getOntologyObjectPropertyIcon() {
		return OWLIconLibrary.ONTOLOGY_OBJECT_PROPERTY_ICON;
	}

	@Override
	public ImageIcon getOntologyAnnotationPropertyIcon() {
		return OWLIconLibrary.ONTOLOGY_ANNOTATION_PROPERTY_ICON;
	}

	@Override
	public boolean supportTechnologySpecificHiddenConcepts() {
		return true;
	}

	@Override
	public String technologySpecificHiddenConceptsLabel() {
		return "show_OWL_RDF_concepts";
	}

}
