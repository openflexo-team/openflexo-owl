/**
 * 
 * Copyright (c) 2014-2015, Openflexo
 * 
 * This file is part of Emfconnector, a component of the software infrastructure 
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.fml.editionaction.AbstractFetchRequest;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.fml.editionaction.AbstractSelectClass;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.technologyadapter.owl.OWLModelSlot;
import org.openflexo.technologyadapter.owl.model.OWLClass;
import org.openflexo.technologyadapter.owl.model.OWLOntology;

/**
 * OWL technology - specific {@link AbstractFetchRequest} allowing to retrieve a selection of some {@link OWLClass} subclass of a given
 * {@link OWLClass} matching some conditions<br>
 * 
 * @author sylvain
 */
@ModelEntity(isAbstract = true)
@ImplementationClass(AbstractSelectOWLClass.AbstractSelectOWLClassImpl.class)
public interface AbstractSelectOWLClass<AT> extends AbstractSelectClass<OWLModelSlot, OWLOntology, OWLClass, AT> {

	public static abstract class AbstractSelectOWLClassImpl<AT> extends AbstractSelectClassImpl<OWLModelSlot, OWLOntology, OWLClass, AT>
			implements AbstractSelectOWLClass<AT> {

		private static final Logger logger = Logger.getLogger(AbstractSelectOWLClass.class.getPackage().getName());

		@Override
		public Type getFetchedType() {
			if (getParentClass() != null) {
				return super.getFetchedType();
			}
			return OWLClass.class;
		}

		@Override
		public List<OWLClass> performExecute(RunTimeEvaluationContext evaluationContext) {

			// TODO: improve perfs !

			OWLOntology ontology = getReceiver(evaluationContext);

			System.out.println("On cherche tous les sousclasses de " + getParentClass() + " dans " + ontology);

			List<OWLClass> selectedClasses = new ArrayList<>();
			IFlexoOntologyClass parentClass = getParentClass();
			for (OWLClass c : ontology.getAccessibleClasses()) {
				if (parentClass == null || parentClass.isSuperClassOf(c)) {
					selectedClasses.add(c);
				}
			}

			List<OWLClass> returned = filterWithConditions(selectedClasses, evaluationContext);

			System.out.println("SelectOWLClass, without filtering =" + selectedClasses + " after filtering=" + returned);

			return returned;

		}

	}
}
