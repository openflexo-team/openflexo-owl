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

import java.util.logging.Logger;

import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.foundation.ontology.DuplicateURIException;
import org.openflexo.foundation.ontology.fml.editionaction.AddDataProperty;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.owl.OWLModelSlot;
import org.openflexo.technologyadapter.owl.model.OWLClass;
import org.openflexo.technologyadapter.owl.model.OWLDataProperty;
import org.openflexo.technologyadapter.owl.model.OWLOntology;

@ModelEntity
@ImplementationClass(AddOWLDataProperty.AddOWLDataPropertyImpl.class)
@XMLElement
@FML("AddOWLDataProperty")
public interface AddOWLDataProperty extends AddDataProperty<OWLModelSlot, OWLOntology, OWLDataProperty>, OWLAction<OWLDataProperty> {

	public static abstract class AddOWLDataPropertyImpl extends AddDataPropertyImpl<OWLModelSlot, OWLOntology, OWLDataProperty>
			implements AddOWLDataProperty {

		private static final Logger logger = Logger.getLogger(AddOWLDataProperty.class.getPackage().getName());

		@Override
		public Class<OWLDataProperty> getOntologyDataPropertyClass() {
			return OWLDataProperty.class;
		}

		@Override
		public OWLDataProperty execute(RunTimeEvaluationContext evaluationContext) {

			OWLClass domain = null;

			if (getDynamicDomain().isValid()) {
				try {
					domain = (OWLClass) getDynamicDomain().getBindingValue(evaluationContext);
				} catch (TypeMismatchException e) {
					e.printStackTrace();
				} catch (NullReferenceException e) {
					e.printStackTrace();
				} catch (ReflectiveOperationException e) {
					e.printStackTrace();
				}
			}

			String propertyName = null;
			try {
				propertyName = getPropertyName().getBindingValue(evaluationContext);
			} catch (TypeMismatchException e1) {
				e1.printStackTrace();
			} catch (NullReferenceException e1) {
				e1.printStackTrace();
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
			}
			// System.out.println("individualName="+individualName);

			OWLOntology receiver = getReceiver(evaluationContext);

			if (domain == null) {
				domain = receiver.getRootClass();
			}

			OWLDataProperty newDataProperty = null;
			try {
				if (receiver != null) {
					logger.info("Adding OWLDataProperty name=" + propertyName + " domain =" + domain + " dataType=" + getDataType());
					logger.info("Adding individual individualName=" + propertyName + " father =" + domain);
					newDataProperty = receiver.createDataProperty(propertyName, null, domain, null/*getDataType()*/);
					logger.info("********* Added OWLDataProperty " + newDataProperty.getName() + " domain " + newDataProperty.getDomain());
				}
				else {
					logger.warning("No receiver defined " + getReceiver());
				}
				logger.info("Return " + newDataProperty);
				return newDataProperty;
			} catch (DuplicateURIException e) {
				e.printStackTrace();
				return null;
			}

		}

	}

}
