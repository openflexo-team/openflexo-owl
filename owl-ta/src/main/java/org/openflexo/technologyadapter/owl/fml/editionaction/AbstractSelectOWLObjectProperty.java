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
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.technologyadapter.owl.OWLModelSlot;
import org.openflexo.technologyadapter.owl.model.OWLObjectProperty;
import org.openflexo.technologyadapter.owl.model.OWLOntology;

/**
 * OWL technology - specific {@link AbstractFetchRequest} allowing to retrieve a selection of some {@link OWLObjectProperty}<br>
 * 
 * @author sylvain
 */
@ModelEntity(isAbstract = true)
@ImplementationClass(AbstractSelectOWLObjectProperty.AbstractSelectOWLObjectPropertyImpl.class)
public interface AbstractSelectOWLObjectProperty<AT> extends AbstractFetchRequest<OWLModelSlot, OWLOntology, OWLObjectProperty, AT> {

	public static abstract class AbstractSelectOWLObjectPropertyImpl<AT> extends
			AbstractFetchRequestImpl<OWLModelSlot, OWLOntology, OWLObjectProperty, AT> implements AbstractSelectOWLObjectProperty<AT> {

		private static final Logger logger = Logger.getLogger(AbstractSelectOWLObjectProperty.class.getPackage().getName());

		@Override
		public Type getFetchedType() {
			return OWLObjectProperty.class;
		}

		@Override
		public List<OWLObjectProperty> performExecute(RunTimeEvaluationContext evaluationContext) {

			// TODO: improve perfs !

			OWLOntology ontology = getReceiver(evaluationContext);

			List<OWLObjectProperty> selectedProperties = new ArrayList<>();
			for (OWLObjectProperty i : ontology.getAccessibleObjectProperties()) {
				boolean takeIt = true;
				if (takeIt && !selectedProperties.contains(i)) {
					selectedProperties.add(i);
				}
			}

			return filterWithConditions(selectedProperties, evaluationContext);

		}

	}
}
