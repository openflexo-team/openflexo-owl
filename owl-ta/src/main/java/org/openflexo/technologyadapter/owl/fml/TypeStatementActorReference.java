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

package org.openflexo.technologyadapter.owl.fml;

import java.util.logging.Logger;

import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.rt.ActorReference;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.owl.model.OWLClass;
import org.openflexo.technologyadapter.owl.model.OWLConcept;
import org.openflexo.technologyadapter.owl.model.OWLIndividual;
import org.openflexo.technologyadapter.owl.model.OWLOntology;
import org.openflexo.technologyadapter.owl.model.TypeStatement;

@ModelEntity
@ImplementationClass(TypeStatementActorReference.TypeStatementActorReferenceImpl.class)
@XMLElement
@FML("TypeStatementActorReference")
public interface TypeStatementActorReference extends ActorReference<TypeStatement> {

	@PropertyIdentifier(type = String.class)
	public static final String SUBJECT_URI_KEY = "subjectURI";
	@PropertyIdentifier(type = String.class)
	public static final String PARENT_URI_KEY = "parentURI";

	@Getter(value = SUBJECT_URI_KEY)
	@XMLAttribute
	public String getSubjectURI();

	@Setter(SUBJECT_URI_KEY)
	public void setSubjectURI(String objectURI);

	@Getter(value = PARENT_URI_KEY)
	@XMLAttribute
	public String getParentURI();

	@Setter(PARENT_URI_KEY)
	public void setParentURI(String parentURI);

	public abstract static class TypeStatementActorReferenceImpl extends ActorReferenceImpl<TypeStatement>
			implements TypeStatementActorReference {

		static final Logger logger = FlexoLogger.getLogger(TypeStatementActorReferenceImpl.class.getPackage().toString());

		private TypeStatement statement;
		private String subjectURI;
		private String parentURI;

		@Override
		public void setModellingElement(TypeStatement statement) {
			this.statement = statement;
			if (statement != null && getModelSlotInstance() != null) {
				subjectURI = statement.getSubject().getURI();
				parentURI = statement.getType().getURI();
			}
		}

		@Override
		public TypeStatement getModellingElement(boolean forceLoading) {
			if (statement == null) {
				OWLOntology ontology = (OWLOntology) getModelSlotInstance().getAccessedResourceData();
				if (ontology != null) {
					OWLConcept<?> subject = ontology.getOntologyObject(subjectURI);
					OWLConcept<?> parent = ontology.getOntologyObject(parentURI);
					if (subject instanceof OWLIndividual && parent instanceof OWLClass) {
						// TODO: also handle value here
						statement = ((OWLIndividual) subject).getTypeStatement((OWLClass) parent);
					}
				}
				else {
					logger.warning("Could not access to ontology referenced by " + getModelSlotInstance());
				}
			}
			if (statement == null) {
				logger.warning("Could not retrieve sub-class statement" + subjectURI + " " + parentURI);
			}
			return statement;
		}

		@Override
		public String getSubjectURI() {
			return subjectURI;
		}

		@Override
		public void setSubjectURI(String subjectURI) {
			this.subjectURI = subjectURI;
		}

		@Override
		public String getParentURI() {
			return parentURI;
		}

		@Override
		public void setParentURI(String parentURI) {
			this.parentURI = parentURI;
		}
	}
}
