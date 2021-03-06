/**
 * 
 * Copyright (c) 2014-2015, Openflexo
 * 
 * This file is part of Owlconnector, a component of the software infrastructure 
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

package org.openflexo.technologyadapter.owl.fml.editionaction;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.foundation.ontology.DuplicateURIException;
import org.openflexo.foundation.ontology.fml.editionaction.AddClass;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.owl.OWLModelSlot;
import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;
import org.openflexo.technologyadapter.owl.model.OWLClass;
import org.openflexo.technologyadapter.owl.model.OWLOntology;

@ModelEntity
@ImplementationClass(AddOWLClass.AddOWLClassImpl.class)
@XMLElement
@FML("AddOWLClass")
public interface AddOWLClass extends AddClass<OWLModelSlot, OWLOntology, OWLClass, OWLTechnologyAdapter>, OWLAction<OWLClass> {

	public static abstract class AddOWLClassImpl extends AddClassImpl<OWLModelSlot, OWLOntology, OWLClass, OWLTechnologyAdapter>
			implements AddOWLClass {

		private static final Logger logger = Logger.getLogger(AddOWLClass.class.getPackage().getName());

		// Unused private final String dataPropertyURI = null;

		public AddOWLClassImpl() {
			super();
		}

		@Override
		public OWLClass getOntologyClass() {
			return super.getOntologyClass();
		}

		@Override
		public Class<OWLClass> getOntologyClassClass() {
			return OWLClass.class;
		}

		@Override
		public OWLClass execute(RunTimeEvaluationContext evaluationContext) {

			OWLClass father = getOntologyClass();
			String newClassName = null;
			try {
				newClassName = getClassName().getBindingValue(evaluationContext);
			} catch (TypeMismatchException e1) {
				e1.printStackTrace();
			} catch (NullReferenceException e1) {
				e1.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			OWLClass newClass = null;
			try {
				OWLOntology ontology = getReceiver(evaluationContext);
				logger.info("Adding class " + newClassName + " as " + father);
				logger.info("ontology=" + ontology);
				if (ontology != null) {
					newClass = ontology.createOntologyClass(newClassName, father);
					logger.info("Added class " + newClass.getName() + " as " + father);
				}
				else {
					logger.warning("Could not access ontology " + getReceiver());
				}
			} catch (DuplicateURIException e) {
				e.printStackTrace();
			}
			return newClass;

		}

	}
}
