/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
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

package org.openflexo.technologyadapter.owl.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.jena.base.Sys;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.ResourceUtils;
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.ontology.IFlexoOntologyAnnotation;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IFlexoOntologyConceptVisitor;
import org.openflexo.foundation.ontology.IFlexoOntologyDataProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyFeatureAssociation;
import org.openflexo.foundation.ontology.IFlexoOntologyObjectProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.ontology.dm.OntologyObjectStatementsChanged;
import org.openflexo.foundation.ontology.dm.URIChanged;
import org.openflexo.foundation.ontology.dm.URINameChanged;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.localization.LocalizedString;
import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;
import org.openflexo.toolbox.StringUtils;

public abstract class OWLConcept<R extends OntResource> extends OWLObject implements IFlexoOntologyConcept<OWLTechnologyAdapter> {

	private static final Logger logger = Logger.getLogger(IFlexoOntologyConcept.class.getPackage().getName());

	private final Vector<OWLStatement> _statements;
	private final Vector<PropertyStatement> _annotationStatements;
	private final Vector<ObjectPropertyStatement> _annotationObjectsStatements;
	private final Vector<OWLStatement> _semanticStatements;
	private final Vector<AnnotationStatement> _owlAnnotationStatements;

	private boolean domainsAndRangesAreUpToDate = false;
	private boolean domainsAndRangesAreRecursivelyUpToDate = false;
	private final Set<OWLProperty> declaredPropertiesTakingMySelfAsRange;
	private final Set<OWLProperty> declaredPropertiesTakingMySelfAsDomain;
	protected List<OWLProperty> propertiesTakingMySelfAsRange;
	protected List<OWLProperty> propertiesTakingMySelfAsDomain;

	private String uri;
	private String name;
	private final OWLOntology _ontology;

	private OWLConcept<R> originalDefinition;

	public OWLConcept(OntResource ontResource, OWLOntology ontology, OWLTechnologyAdapter adapter) {
		super(adapter);

		_ontology = ontology;

		uri = computeURI(ontResource);
		name = computeName(ontResource, uri);

		_statements = new Vector<>();
		_semanticStatements = new Vector<>();
		_annotationStatements = new Vector<>();
		_annotationObjectsStatements = new Vector<>();
		_owlAnnotationStatements = new Vector<>();

		propertiesTakingMySelfAsRange = new ArrayList<>();
		propertiesTakingMySelfAsDomain = new ArrayList<>();

		declaredPropertiesTakingMySelfAsRange = new HashSet<>();
		declaredPropertiesTakingMySelfAsDomain = new HashSet<>();
	}
	private String computeURI(OntResource ontResource) {
		if (ontResource == null) {
			return null;
		}

		// Works for rdf:about and rdf:ID after Jena resolves the resource.
		String computedURI = ontResource.getURI();

		if (computedURI != null && !computedURI.trim().isEmpty()) {
			return computedURI;
		}

		// Fallback for resources where Jena exposes namespace/local name
		// but getURI() is unexpectedly null.
		String namespace = ontResource.getNameSpace();
		String localName = ontResource.getLocalName();

		if (namespace != null && !namespace.trim().isEmpty()
				&& localName != null && !localName.trim().isEmpty()) {
			return namespace + localName;
		}

		return null;
	}

	private String computeName(OntResource ontResource, String computedURI) {
		if (ontResource != null) {
			String localName = ontResource.getLocalName();

			if (localName != null && !localName.trim().isEmpty()) {
				return localName;
			}
		}

		return localIdFromURI(computedURI);
	}

	private String localIdFromURI(String computedURI) {
		if (computedURI == null || computedURI.trim().isEmpty()) {
			return null;
		}

		int hashIndex = computedURI.lastIndexOf('#');

		if (hashIndex >= 0 && hashIndex < computedURI.length() - 1) {
			return computedURI.substring(hashIndex + 1);
		}

		int slashIndex = computedURI.lastIndexOf('/');

		if (slashIndex >= 0 && slashIndex < computedURI.length() - 1) {
			return computedURI.substring(slashIndex + 1);
		}

		return computedURI;
	}

	protected abstract void update();

	@Override
	public String getURI() {
		return uri;
	}

	@Override
	public OWLOntology getFlexoOntology() {
		return _ontology;
	}

	@Override
	public OWLOntology getOntology() {
		return getFlexoOntology();
	}

	@Override
	public OWLOntologyLibrary getOntologyLibrary() {
		if (getOntology() != null) {
			return getOntology().getOntologyLibrary();
		}
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public abstract void setName(String aName);

	protected R renameURI(String newName, R resource, Class<R> resourceClass) {
		String oldURI = getURI();
		String oldName = getName();
		String newURI;
		if (getURI().indexOf("#") > -1) {
			newURI = getURI().substring(0, getURI().indexOf("#") + 1) + newName;
		}
		else {
			newURI = newName;
		}
		logger.info("Rename object " + getURI() + " to " + newURI);
		R returned = ResourceUtils.renameResource(resource, newURI).as(resourceClass);
		_setOntResource(returned);
		name = newName;
		uri = newURI;
		getFlexoOntology().renameObject(this, oldURI, newURI);
		update();
		setChanged();
		notifyObservers(new NameChanged(oldName, newName));
		setChanged();
		notifyObservers(new URINameChanged(oldName, newName));
		setChanged();
		notifyObservers(new URIChanged(oldURI, newURI));
		return returned;
	}

	public abstract R getOntResource();

	protected abstract void _setOntResource(R r);

	public Resource getResource() {
		return getOntResource();
	}

	@Override
	public String getDescription() {
		if (getOntResource() != null) {
			return getOntResource().getComment(FlexoLocalization.getCurrentLanguage().getTag());
		}
		return null;
	}

	public void setDescription(String aDescription) {
		if (getOntResource() != null) {
			getOntResource().setComment(aDescription, FlexoLocalization.getCurrentLanguage().getTag());
		}
	}

	public String simpleRepresentation() {
		return getClass().getSimpleName() + ":" + getName();
	}

	public String fullQualifiedRepresentation() {
		return getURI();
	}

	public void updateOntologyStatements() {
		updateOntologyStatements(getOntResource());
	}

	protected void updateOntologyStatements(R anOntResource) {
		// TODO: optimize this (do not always recalculate)

		_statements.clear();
		_semanticStatements.clear();
		_annotationStatements.clear();
		_annotationObjectsStatements.clear();
		_owlAnnotationStatements.clear();

		for (StmtIterator j = anOntResource.listProperties(); j.hasNext();) {
			Statement s = j.nextStatement();

			OWLStatement newStatement = null;

			if (!s.getSubject().equals(anOntResource)) {
				logger.warning("Inconsistant data: subject is not " + this);
			}
			else {
				Property predicate = s.getPredicate();
				if (predicate.getURI().equals(TYPE_URI)) {
					if (s.getObject() instanceof Resource && StringUtils.isNotEmpty(((Resource) s.getObject()).getURI())) {
						if (((Resource) s.getObject()).getURI().equals(OWL_CLASS_URI)) {
							newStatement = new IsClassStatement(this, s, getTechnologyAdapter());
						}
						else if (((Resource) s.getObject()).getURI().equals(OWL_OBJECT_PROPERTY_URI)) {
							newStatement = new IsObjectPropertyStatement(this, s, getTechnologyAdapter());
						}
						else if (((Resource) s.getObject()).getURI().equals(OWL_DATA_PROPERTY_URI)) {
							newStatement = new IsDatatypePropertyStatement(this, s, getTechnologyAdapter());
						}
						else if (((Resource) s.getObject()).getURI().equals(OWL_ANNOTATED_PROPERTY_URI)) {
							newStatement = new IsAnnotationStatement(this, s, getTechnologyAdapter());
						}
						else {
							newStatement = new TypeStatement(this, s, getTechnologyAdapter());
						}
					}
					else {
						newStatement = new TypeStatement(this, s, getTechnologyAdapter());
					}
				}
				else if (predicate.getURI().equals(RDFS_SUB_CLASS_URI)) {
					newStatement = new SubClassStatement(this, s, getTechnologyAdapter());
				}
				else if (predicate.getURI().equals(RDFS_RANGE_URI)) {
					newStatement = new RangeStatement(this, s, getTechnologyAdapter());
				}
				else if (predicate.getURI().equals(RDFS_DOMAIN_URI)) {
					newStatement = new DomainStatement(this, s, getTechnologyAdapter());
				}
				else if (predicate.getURI().equals(OWL_INVERSE_OF_URI)) {
					newStatement = new InverseOfStatement(this, s, getTechnologyAdapter());
				}
				else if (predicate.getURI().equals(RDFS_SUB_PROPERTY_URI)) {
					newStatement = new SubPropertyStatement(this, s, getTechnologyAdapter());
				}
				else if (predicate.getURI().equals(OWL_EQUIVALENT_CLASS_URI)) {
					newStatement = new EquivalentClassStatement(this, s, getTechnologyAdapter());
				}
				else {
					IFlexoOntologyConcept<OWLTechnologyAdapter> predicateProperty =
							getOntology().getOntologyObject(predicate.getURI());

					OWLDataProperty legacyDataProperty =
							getOntology().getDataProperty(predicate.getURI());

					OWLObjectProperty legacyObjectProperty =
							getOntology().getObjectProperty(predicate.getURI());

					if (s.getObject().isLiteral() && legacyDataProperty != null) {
						newStatement = new DataPropertyStatement(this, s, getTechnologyAdapter());
					}
					else if (s.getObject().isResource() && legacyObjectProperty != null) {
						newStatement = new ObjectPropertyStatement(this, s, getTechnologyAdapter());
					}
					else if (predicateProperty instanceof IFlexoOntologyObjectProperty) {
						newStatement = new ObjectPropertyStatement(this, s, getTechnologyAdapter());
					}
					else if (predicateProperty instanceof IFlexoOntologyDataProperty) {
						newStatement = new DataPropertyStatement(this, s, getTechnologyAdapter());
					}
					else if (predicateProperty instanceof OWLAnnotation) {
						if (s.getObject().isLiteral()) {
							newStatement = new AnnotationStatement(
									this,
									(OWLAnnotation) predicateProperty,
									s,
									getTechnologyAdapter());
						}
						else {
							logger.warning("Object-valued OWLAnnotation not handled yet: " + predicate.getURI());
						}
					}
					else {
						logger.warning("Inconsistant data: unkwown property " + predicate);
					}
				}
			}

			if (newStatement != null) {

				_statements.add(newStatement);

				if (newStatement instanceof PropertyStatement
						&& ((PropertyStatement) newStatement).isAnnotationProperty()) {

					PropertyStatement propertyStatement = (PropertyStatement) newStatement;

					if (propertyStatement.hasLitteralValue()) {

						// old API
						_annotationStatements.add(propertyStatement);

						// new AnnotationStatement API
						OWLAnnotation annotation =
								getOntology().getAnnotation(propertyStatement.getProperty().getURI());

						if (annotation != null) {
							AnnotationStatement annotationStatement =
									new AnnotationStatement(
											this,
											annotation,
											propertyStatement.getStatement(),
											getTechnologyAdapter());

							_owlAnnotationStatements.add(annotationStatement);
						}
					}
					else if (newStatement instanceof ObjectPropertyStatement) {
						_annotationObjectsStatements.add((ObjectPropertyStatement) newStatement);
					}
				}
				else {
					_semanticStatements.add(newStatement);
				}
			}

		}

		// for (OWLStatement s : _statements) {
		// System.out.println("> "+s.toString());
		// }

		setChanged();
		notifyObservers(new OntologyObjectStatementsChanged(this));

	}

	public Vector<OWLStatement> getStatements() {
		return _statements;
	}

	public Vector<OWLStatement> getSemanticStatements() {
		return _semanticStatements;
	}

	public Vector<PropertyStatement> getAnnotationStatements() {
		return _annotationStatements;
	}
	public Vector<AnnotationStatement> getOWLAnnotationStatements() {
		return _owlAnnotationStatements;
	}
	public Vector<ObjectPropertyStatement> getAnnotationObjectStatements() {
		return _annotationObjectsStatements;
	}

	/**
	 * Return all statement related to supplied property
	 * 
	 * @param property
	 * @return
	 */
	public Vector<PropertyStatement> getPropertyStatements(IFlexoOntologyStructuralProperty<OWLTechnologyAdapter> property) {
		Vector<PropertyStatement> returned = new Vector<>();
		for (OWLStatement statement : getStatements()) {
			if (statement instanceof PropertyStatement) {
				PropertyStatement s = (PropertyStatement) statement;
				if (s.getProperty().equalsToConcept(property)) {
					returned.add(s);
				}
			}
		}
		return returned;
	}

	/**
	 * Return all annotation statement related to supplied property
	 * 
	 * @param property
	 * @return
	 */
	public Vector<DataPropertyStatement> getAnnotationStatements(IFlexoOntologyDataProperty<OWLTechnologyAdapter> property) {
		Vector<DataPropertyStatement> returned = new Vector<>();

		for (PropertyStatement statement : getAnnotationStatements()) {

			if (statement instanceof DataPropertyStatement
					&& statement.getProperty() != null
					&& statement.getProperty().equalsToConcept(property)) {

				returned.add((DataPropertyStatement) statement);
			}
		}

		return returned;
	}
	/**
	 * Return all annotation statement related to supplied property
	 *
	 * @param property
	 * @return
	 */
	public Vector<AnnotationStatement> getAnnotationStatements(IFlexoOntologyAnnotation<OWLTechnologyAdapter> property) {
		Vector<AnnotationStatement> returned = new Vector<>();

		for (AnnotationStatement statement : getOWLAnnotationStatements()) {
			if (statement.getProperty() != null
					&& statement.getProperty().equalsToConcept(property)) {
				returned.add(statement);
			}
		}

		return returned;
	}

	/**
	 * Return all annotation object statement related to supplied property
	 * 
	 * @param property
	 * @return
	 */
	public Vector<ObjectPropertyStatement> getAnnotationObjectStatements(IFlexoOntologyStructuralProperty<OWLTechnologyAdapter> property) {
		Vector<ObjectPropertyStatement> returned = new Vector<>();
		for (OWLStatement statement : getAnnotationObjectStatements()) {
			if (statement instanceof PropertyStatement) {
				ObjectPropertyStatement s = (ObjectPropertyStatement) statement;
				if (s.getProperty().equalsToConcept(property)) {
					returned.add(s);
				}
			}
		}
		return returned;
	}



	/**
	 * Return all statement related to supplied property
	 * 
	 * @param property
	 * @return
	 */
	public Vector<ObjectPropertyStatement> getObjectPropertyStatements(IFlexoOntologyObjectProperty<OWLTechnologyAdapter> property) {
		Vector<ObjectPropertyStatement> returned = new Vector<>();
		for (OWLStatement statement : getStatements()) {
			if (statement instanceof ObjectPropertyStatement) {
				ObjectPropertyStatement s = (ObjectPropertyStatement) statement;
				if (s.getProperty().equalsToConcept(property)) {
					returned.add(s);
				}
			}
		}
		return returned;
	}

	/**
	 * Return all statement related to supplied property
	 * 
	 * @param property
	 * @return
	 */
	public Vector<DataPropertyStatement> getDataPropertyStatements(IFlexoOntologyDataProperty<OWLTechnologyAdapter> property) {
		Vector<DataPropertyStatement> returned = new Vector<>();
		for (OWLStatement statement : getStatements()) {
			if (statement instanceof DataPropertyStatement) {
				DataPropertyStatement s = (DataPropertyStatement) statement;
				if (s.getProperty() == property) {
					returned.add(s);
				}
			}
		}
		return returned;
	}

	/**
	 * Return all object property statements
	 * 
	 * @param property
	 * @return
	 */
	public Vector<ObjectPropertyStatement> getAllObjectPropertyStatements() {
		Vector<ObjectPropertyStatement> returned = new Vector<>();
		for (OWLStatement statement : getStatements()) {
			if (statement instanceof ObjectPropertyStatement) {
				ObjectPropertyStatement s = (ObjectPropertyStatement) statement;
				returned.add(s);
			}
		}
		return returned;
	}

	/**
	 * Return all data property statements
	 * 
	 * @param property
	 * @return
	 */
	public Vector<DataPropertyStatement> getAllDataPropertyStatements() {
		Vector<DataPropertyStatement> returned = new Vector<>();
		for (OWLStatement statement : getStatements()) {
			if (statement instanceof DataPropertyStatement) {
				DataPropertyStatement s = (DataPropertyStatement) statement;
				returned.add(s);
			}
		}
		return returned;
	}

	/**
	 * Return all data property statements
	 * 
	 * @param property
	 * @return
	 */
	public Vector<PropertyStatement> getAllPropertyStatements() {
		Vector<PropertyStatement> returned = new Vector<>();
		for (OWLStatement statement : getStatements()) {
			if (statement instanceof PropertyStatement) {
				PropertyStatement s = (PropertyStatement) statement;
				returned.add(s);
			}
		}
		return returned;
	}

	/**
	 * Return first found statement related to supplied property
	 * 
	 * @param property
	 * @return
	 */
	public PropertyStatement getPropertyStatement(IFlexoOntologyStructuralProperty<OWLTechnologyAdapter> property) {
		Vector<PropertyStatement> returned = getPropertyStatements(property);
		if (returned.size() > 0) {
			return returned.firstElement();
		}
		return null;
	}

	/**
	 * Return statement related to supplied property and value
	 * 
	 * @param property
	 * @return
	 */
	public DataPropertyStatement getDataPropertyStatement(IFlexoOntologyDataProperty<OWLTechnologyAdapter> property, Object value) {
		Vector<DataPropertyStatement> returned = getDataPropertyStatements(property);
		for (DataPropertyStatement statement : returned) {
			Object v = statement.getValue();
			if (v == null && value == null) return statement;
			if (v != null && v.equals(value)) return statement;
		}
		return null;
	}
	/**
	 * Return statement related to supplied property and value
	 *
	 * @param property
	 * @return
	 */
	public AnnotationStatement getAnnotationStatement(IFlexoOntologyAnnotation<OWLTechnologyAdapter> property, Object value) {
		Vector<AnnotationStatement> returned = getAnnotationStatements(property);
		for (AnnotationStatement statement : returned) {
			Object v = statement.getValue();
			if (v == null && value == null) return statement;
			if (v != null && v.equals(value)) return statement;
		}
		return null;
	}

	/**
	 * Return statement related to supplied property and value
	 * 
	 * @param property
	 * @return
	 */
	public PropertyStatement getPropertyStatement(IFlexoOntologyStructuralProperty<OWLTechnologyAdapter> property, String value) {
		Vector<PropertyStatement> returned = getPropertyStatements(property);
		for (PropertyStatement statement : returned) {
			if (statement.hasLitteralValue() && statement.getStringValue().equals(value)) {
				return statement;
			}
		}
		return null;
	}

	/**
	 * Return statement related to supplied property and value
	 * 
	 * @param property
	 * @return
	 */
	public PropertyStatement getPropertyStatement(IFlexoOntologyStructuralProperty<OWLTechnologyAdapter> property, Object value) {
		Vector<PropertyStatement> returned = getPropertyStatements(property);
		for (PropertyStatement statement : returned) {
			if (statement.hasLitteralValue() && statement.getLiteral().getValue().equals(value)) {
				return statement;
			}
		}
		return null;
	}

	/**
	 * Return statement related to supplied property and value
	 * 
	 * @param property
	 * @return
	 */
	public ObjectPropertyStatement getPropertyStatement(IFlexoOntologyObjectProperty<OWLTechnologyAdapter> property,
			IFlexoOntologyConcept<OWLTechnologyAdapter> object) {
		Vector<ObjectPropertyStatement> returned = getObjectPropertyStatements(property);
		for (ObjectPropertyStatement statement : returned) {
			if (statement.getStatementObject() == object) {
				return statement;
			}
		}
		return null;
	}

	/**
	 * Return statement related to supplied property, value and language
	 * 
	 * @param property
	 * @return
	 */
	public PropertyStatement getPropertyStatement(IFlexoOntologyStructuralProperty<OWLTechnologyAdapter> property, String value,
			Language language) {
		Vector<PropertyStatement> returned = getPropertyStatements(property);
		for (PropertyStatement statement : returned) {
			if (statement.hasLitteralValue() && statement.getStringValue().equals(value)
					&& statement.getLanguage().equals(language.getTag())) {
				return statement;
			}
		}
		return null;
	}

	/**
	 * Return first found statement related to supplied property
	 * 
	 * @param property
	 * @return
	 */
	// TODO: need to handle multiple statements
	public DataPropertyStatement getDataPropertyStatement(IFlexoOntologyDataProperty<OWLTechnologyAdapter> property) {
		for (OWLStatement statement : getStatements()) {
			if (statement instanceof DataPropertyStatement && ((DataPropertyStatement) statement).getProperty() == property) {
				return (DataPropertyStatement) statement;
			}
		}
		return null;
	}
	/**
	 * Return first found statement related to supplied property
	 *
	 * @param property
	 * @return
	 */
	// TODO: need to handle multiple statements
	public AnnotationStatement getAnnotationStatement(IFlexoOntologyAnnotation<OWLTechnologyAdapter> property) {
		for (AnnotationStatement statement : getOWLAnnotationStatements()) {
			if (statement.getProperty() != null
					&& statement.getProperty().equalsToConcept(property)) {
				return statement;
			}
		}
		return null;
	}

	/**
	 * Return first found statement related to supplied property
	 * 
	 * @param property
	 * @return
	 */
	// TODO: need to handle multiple statements
	public ObjectPropertyStatement getObjectPropertyStatement(IFlexoOntologyObjectProperty<OWLTechnologyAdapter> property) {
		for (OWLStatement statement : getStatements()) {
			if (statement instanceof ObjectPropertyStatement && ((ObjectPropertyStatement) statement).getProperty() == property) {
				return (ObjectPropertyStatement) statement;
			}
		}
		return null;
	}

	/**
	 * Return statement related to supplied property and referencing supplied object
	 * 
	 * @param property
	 * @return
	 */
	public ObjectPropertyStatement getObjectPropertyStatement(OWLObjectProperty property, OWLConcept<?> object) {
		for (OWLStatement statement : getStatements()) {
			if (statement instanceof ObjectPropertyStatement && ((ObjectPropertyStatement) statement).getProperty() == property
					&& ((ObjectPropertyStatement) statement).getStatementObject() == object) {
				return (ObjectPropertyStatement) statement;
			}
		}
		return null;
	}

	/**
	 * Return first found statement related to supplied property
	 * 
	 * @param property
	 * @return
	 */
	// TODO: need to handle multiple statements
	public SubClassStatement getSubClassStatement(IFlexoOntologyConcept<OWLTechnologyAdapter> father) {
		for (OWLStatement statement : getStatements()) {
			if (statement instanceof SubClassStatement && ((SubClassStatement) statement).getParent().equals(father)) {
				return (SubClassStatement) statement;
			}
		}
		return null;
	}

	@Override
	public abstract boolean isSuperConceptOf(IFlexoOntologyConcept<OWLTechnologyAdapter> concept);

	@Override
	public boolean isSubConceptOf(IFlexoOntologyConcept<OWLTechnologyAdapter> concept) {
		return concept.isSuperConceptOf(this);
	}

	public PropertyStatement createNewCommentAnnotation() {
		return createNewAnnotation("http://www.w3.org/2000/01/rdf-schema#comment");
	}

	public PropertyStatement createNewLabelAnnotation() {
		return createNewAnnotation("http://www.w3.org/2000/01/rdf-schema#label");
	}

	public PropertyStatement createNewSeeAlsoAnnotation() {
		return createNewAnnotation("http://www.w3.org/2000/01/rdf-schema#seeAlso", this);
	}

	public PropertyStatement createNewIsDefinedByAnnotation() {
		return createNewAnnotation("http://www.w3.org/2000/01/rdf-schema#isDefinedBy", getFlexoOntology());
	}

	public PropertyStatement createNewAnnotation(String propertyURI) {
		OWLProperty property = getOntology().getProperty(propertyURI);
		if (property != null) {
			return addPropertyStatement(property, "label", Language.ENGLISH);
		}
		else {
			logger.warning("Could not find property " + property);
			return null;
		}
	}

	public PropertyStatement createNewAnnotation(String propertyURI, OWLObject object) {
		if (object == null) {
			logger.warning("Cannot create annotation " + propertyURI + " with null object");
			return null;
		}
		OWLProperty property = getOntology().getProperty(propertyURI);
		if (property instanceof OWLObjectProperty) {
			return addPropertyStatement(property, object);
		}
		else {
			logger.warning("Could not find property " + property);
			return null;
		}
	}

	public void deleteAnnotation(PropertyStatement annotation) {
		removePropertyStatement(annotation);
	}

	public boolean isAnnotationAddable() {
		return !getIsReadOnly();
	}

	public boolean isAnnotationDeletable(PropertyStatement annotation) {
		return !getIsReadOnly();
	}

	/**
	 * Return the value defined for supplied property, asserting that current individual defines one and only one assertion for this
	 * property.<br>
	 * <ul>
	 * <li>If many assertions for this properties are defined for this individual, then the first assertion is used<br>
	 * Special case: if supplied property is an annotation property defined on a literal (datatype property) then the returned value will
	 * match the current language as defined in FlexoLocalization.</li>
	 * <li>If no assertion is defined for this property, then the result will be null</li>
	 * </ul>
	 * 
	 * @param property
	 * @return
	 */
	public Object getPropertyValue(OWLProperty property) {
		if (property == null) {
			logger.warning("getPropertyValue() called for null property");
			return null;
		}
		if (property.isAnnotationProperty() && property instanceof OWLDataProperty
				&& getAnnotationStatements((OWLDataProperty) property).size() > 1) {
			return getAnnotationValue((OWLDataProperty) property, FlexoLocalization.getCurrentLanguage());
		}

		// Special case for label annotation
		if (property.equalsToConcept(property.getFlexoOntology().getProperty(RDFS_LABEL_URI)) && getPropertyStatement(property) == null) {
			// If label is requested and no label annotation are set, return uriName
			return getName();
		}

		PropertyStatement s = getPropertyStatement(property);
		if (s != null) {
			if (s.hasLitteralValue()) {
				return s.getLiteral().getValue();
			}
			else if (s instanceof ObjectPropertyStatement) {
				return ((ObjectPropertyStatement) s).getStatementObject();
			}
		}
		return null;
	}

	/**
	 * Sets the value defined for supplied property, asserting that current individual defines one and only one assertion for this property.
	 * <br>
	 * 
	 * @param property
	 * @param newValue
	 */
	public void setPropertyValue(OWLProperty property, Object newValue) {
		PropertyStatement s = getPropertyStatement(property);
		if (s != null) {
			if (s.hasLitteralValue() && newValue instanceof String) {
				s.setStringValue((String) newValue);
				return;
			}
			else if (s instanceof ObjectPropertyStatement && newValue instanceof OWLConcept) {
				((ObjectPropertyStatement) s).setStatementObject((OWLConcept<?>) newValue);
				return;
			}
		}
		else {
			if (newValue instanceof String) {
				getOntResource().addProperty(property.getOntProperty(), (String) newValue);
				updateOntologyStatements();
			}
			else if (newValue instanceof OWLConcept) {
				getOntResource().addProperty(property.getOntProperty(), ((OWLConcept<?>) newValue).getOntResource());
				updateOntologyStatements();
			}
		}
	}

	/**
	 * Return value of specified property, asserting this property is an annotation property matching a literal value
	 * 
	 * @param property
	 * @param language
	 * @return
	 */
	public Object getAnnotationValue(OWLDataProperty property, Language language) {

		if (property == null) {
			return null;
		}

		String expectedLanguageTag = language != null ? language.getTag() : null;

		for (PropertyStatement annotation : getAnnotationStatements()) {

			if (annotation == null
					|| annotation.getProperty() == null
					|| !annotation.getProperty().equalsToConcept(property)
					|| !annotation.hasLitteralValue()) {
				continue;
			}

			Language actualLanguage = annotation.getLanguage();
			String actualLanguageTag = actualLanguage != null ? actualLanguage.getTag() : null;

			if (expectedLanguageTag == null) {
				if (actualLanguageTag == null || actualLanguageTag.length() == 0) {
					return annotation.getLiteral().getValue();
				}
			}
			else if (expectedLanguageTag.equals(actualLanguageTag)) {
				return annotation.getLiteral().getValue();
			}
		}

		return null;
	}

	public void setAnnotationValue(Object value, OWLDataProperty property, Language language) {
		// TODO: implement this
		logger.warning("setAnnotationValue not implemented !");
	}

	/**
	 * Return value of specified property, asserting this property is an annotation property matching an object value
	 * 
	 * @param property
	 * @param language
	 * @return
	 */
	public Object getAnnotationObjectValue(OWLObjectProperty property) {
		List<ObjectPropertyStatement> annotations = getAnnotationObjectStatements(property);
		for (ObjectPropertyStatement annotation : annotations) {
			IFlexoOntologyConcept<OWLTechnologyAdapter> returned = annotation.getStatementObject();
			if (returned != null) {
				return returned;
			}
		}
		return null;
	}

	/**
	 * Sets value of specified property, asserting this property is an annotation property matching an object value
	 * 
	 * @param value
	 * @param property
	 * @param language
	 */
	public void setAnnotationObjectValue(Object value, OWLObjectProperty property, Language language) {
		// TODO: implement this
		logger.warning("setAnnotationObjectValue not implemented !");
	}

	/**
	 * Append object property statement for specified property and object
	 * 
	 * @param property
	 * @param object
	 * @return an object representing the added statement
	 */
	public ObjectPropertyStatement addPropertyStatement(OWLObjectProperty property, OWLConcept<?> object) {
		// System.out.println("Subject: "+this+" resource="+getOntResource());
		// System.out.println("Predicate: "+property+" resource="+property.getOntProperty());
		// System.out.println("Object: "+object+" resource="+object.getOntResource());
		getOntResource().addProperty(((OWLProperty) property).getOntProperty(), object.getResource());
		updateOntologyStatements();
		setChanged();
		return getPropertyStatement(property, object);
	}

	/**
	 * Append property statement for specified property and object
	 * 
	 * @param property
	 * @param value
	 * @return an object representing the added statement
	 */
	public PropertyStatement addPropertyStatement(OWLProperty property, Object value) {
		if (property != null) {
			if (value instanceof String) {
				getOntResource().addProperty(property.getOntProperty(), (String) value);
				updateOntologyStatements();
				setChanged();
				return getPropertyStatement(property, (String) value);
			}
			getOntResource().addLiteral(property.getOntProperty(), value);
			updateOntologyStatements();
			setChanged();
			return getPropertyStatement(property, value);
		}
		logger.warning("Property " + property + " is not a OWLProperty");
		return null;
	}

	/**
	 * Append property statement for specified property, object and language
	 * 
	 * @param property
	 * @param object
	 * @return an object representing the added statement
	 */
	public PropertyStatement addPropertyStatement(OWLProperty property, String value, Language language) {
		// System.out.println("****** Add statement for property "+property.getName()+" value="+value+" language="+language);
		getOntResource().addProperty(property.getOntProperty(), value, language.getTag());
		updateOntologyStatements();
		setChanged();
		return getPropertyStatement(property, value, language);
	}

	/**
	 * Append property statement for specified property and value
	 * 
	 * @param property
	 * @param object
	 * @return an object representing the added statement
	 */
	public DataPropertyStatement addDataPropertyStatement(OWLDataProperty property, Object value) {
		getOntResource().addLiteral(((OWLProperty) property).getOntProperty(), value);
		updateOntologyStatements();
		setChanged();
		return getDataPropertyStatement(property, value);
	}

	/**
	 * Append property statement for specified property and value
	 *
	 * @param property
	 * @param object
	 * @return an object representing the added statement
	 */
	public AnnotationStatement addAnnotationStatement(OWLAnnotation property, Object value) {

		logger.warning("[addAnnotationStatement] subject=" + getURI());
		logger.warning("[addAnnotationStatement] property=" + (property != null ? property.getURI() : "null"));
		logger.warning("[addAnnotationStatement] value=" + value);

		if (property == null) {
			logger.warning("[addAnnotationStatement] NULL property");
			return null;
		}

		if (!(property instanceof OWLProperty)) {
			logger.warning("[addAnnotationStatement] property is not OWLProperty: " + property.getClass());
			return null;
		}

		getOntResource().addLiteral(((OWLProperty) property).getOntProperty(), value);

		logger.warning("[addAnnotationStatement] after addLiteral, raw RDF statements:");

		for (StmtIterator it = getOntResource().listProperties(); it.hasNext();) {
			Statement s = it.nextStatement();
			logger.warning(
					"  RDF "
							+ s.getSubject()
							+ " "
							+ s.getPredicate().getURI()
							+ " "
							+ s.getObject()
							+ " literal=" + s.getObject().isLiteral()
			);
		}

		updateOntologyStatements();

		logger.warning("[addAnnotationStatement] after updateOntologyStatements:");
		logger.warning("  _statements=" + getStatements().size());
		logger.warning("  old annotationStatements=" + getAnnotationStatements().size());
		logger.warning("  new owlAnnotationStatements=" + getOWLAnnotationStatements().size());

		return getAnnotationStatement(property, value);
	}

	public void removePropertyStatement(PropertyStatement statement) {
		getFlexoOntology().getOntModel().remove(statement.getStatement());
		updateOntologyStatements();
		setChanged();
	}

	public PropertyStatement addLiteral(OWLProperty property, Object value) {
		if (property != null) {
			if (value instanceof String) {
				getOntResource().addProperty(property.getOntProperty(), (String) value);
			}
			else if (value instanceof LocalizedString) {
				if (!StringUtils.isEmpty(((LocalizedString) value).string)) {
					getOntResource().addProperty(property.getOntProperty(), ((LocalizedString) value).string,
							((LocalizedString) value).language.getTag());
				}
			}
			else if (value instanceof Double) {
				getOntResource().addLiteral(property.getOntProperty(), ((Double) value).doubleValue());
			}
			else if (value instanceof Float) {
				getOntResource().addLiteral(property.getOntProperty(), ((Float) value).floatValue());
			}
			else if (value instanceof Long) {
				getOntResource().addLiteral(property.getOntProperty(), ((Long) value).longValue());
			}
			else if (value instanceof Integer) {
				getOntResource().addLiteral(property.getOntProperty(), ((Integer) value).longValue());
			}
			else if (value instanceof Short) {
				getOntResource().addLiteral(property.getOntProperty(), ((Short) value).longValue());
			}
			else if (value instanceof Boolean) {
				getOntResource().addLiteral(property.getOntProperty(), ((Boolean) value).booleanValue());
			}
			else if (value != null) {
				logger.warning("Unexpected " + value + " of " + value.getClass());
			}
			else {
				// If value is null, just ignore
			}
			setChanged();
			return getPropertyStatement(property);
		}
		return null;
	}

	public boolean getIsReadOnly() {
		return getFlexoOntology().getIsReadOnly();
	}

	protected void updateDomainsAndRanges() {
		domainsAndRangesAreUpToDate = false;
		domainsAndRangesAreRecursivelyUpToDate = false;
	}

	public Set<OWLProperty> getDeclaredPropertiesTakingMySelfAsRange() {
		if (!domainsAndRangesAreUpToDate) {
			searchRangeAndDomains();
		}
		return declaredPropertiesTakingMySelfAsRange;
	}

	public Set<OWLProperty> getDeclaredPropertiesTakingMySelfAsDomain() {
		if (!domainsAndRangesAreUpToDate) {
			searchRangeAndDomains();
		}
		return declaredPropertiesTakingMySelfAsDomain;
	}

	public List<OWLProperty> getPropertiesTakingMySelfAsRange() {
		getDeclaredPropertiesTakingMySelfAsRange(); // Required in some cases: TODO: investigate this
		if (!domainsAndRangesAreRecursivelyUpToDate) {
			recursivelySearchRangeAndDomains();
		}
		return propertiesTakingMySelfAsRange;
	}

	public List<OWLProperty> getPropertiesTakingMySelfAsDomain() {
		getDeclaredPropertiesTakingMySelfAsDomain(); // Required in some cases: TODO: investigate this
		if (!domainsAndRangesAreRecursivelyUpToDate) {
			recursivelySearchRangeAndDomains();
		}
		return propertiesTakingMySelfAsDomain;
	}

	// TODO implement a nice and documented API here !
	public Vector<OWLDataProperty> getDataPropertiesTakingMySelfAsDomain(Object range) {
		Vector<OWLDataProperty> returned = new Vector<>();
		Vector<OWLProperty> allProperties = getPropertiesTakingMyselfAsDomain(true, false, false, false, null, null, getOntology());
		for (OWLProperty p : allProperties) {
			returned.add((OWLDataProperty) p);
		}
		return returned;
	}

	public Vector<OWLObjectProperty> getObjectPropertiesTakingMySelfAsDomain(OWLConcept<?> range) {
		Vector<OWLObjectProperty> returned = new Vector<>();
		Vector<OWLProperty> allProperties = getPropertiesTakingMyselfAsDomain(false, true, false, false, null, null, getOntology());
		for (OWLProperty p : allProperties) {
			returned.add((OWLObjectProperty) p);
		}
		return returned;
	}

	private Vector<OWLProperty> getPropertiesTakingMyselfAsDomain(boolean includeDataProperties, boolean includeObjectProperties,
			boolean includeAnnotationProperties, boolean includeBaseOntologies, OWLConcept<?> range, OWLDataType dataType,
			OWLOntology... ontologies) {
		Vector<OWLProperty> allProperties = new Vector<>(getPropertiesTakingMySelfAsDomain());
		Vector<OWLProperty> returnedProperties = new Vector<>();
		for (OWLProperty p : allProperties) {
			boolean takeIt = includeDataProperties && p instanceof OWLDataProperty
					|| includeObjectProperties && p instanceof OWLObjectProperty || includeAnnotationProperties && p.isAnnotationProperty();
			if (range != null && p instanceof OWLObjectProperty && !((OWLObjectProperty) p).getRange().isSuperConceptOf(range)) {
				takeIt = false;
			}
			if (dataType != null && p instanceof OWLDataProperty && ((OWLDataProperty) p).getDataType() != dataType) {
				takeIt = false;
			}
			OWLOntology containerOntology = p.getOntology();
			if (containerOntology == p.getOntologyLibrary().getOWLOntology() && !includeBaseOntologies) {
				takeIt = false;
			}
			if (containerOntology == p.getOntologyLibrary().getRDFOntology() && !includeBaseOntologies) {
				takeIt = false;
			}
			if (containerOntology == p.getOntologyLibrary().getRDFSOntology() && !includeBaseOntologies) {
				takeIt = false;
			}
			if (ontologies != null) {
				boolean containedInGivenOntologies = false;
				for (OWLOntology o : ontologies) {
					if (containerOntology == o) {
						containedInGivenOntologies = true;
					}
				}
				if (!containedInGivenOntologies) {
					takeIt = false;
				}
			}
			if (takeIt) {
				returnedProperties.add(p);
			}
		}
		return returnedProperties;
	}

	private void searchRangeAndDomains() {
		declaredPropertiesTakingMySelfAsRange.clear();
		declaredPropertiesTakingMySelfAsDomain.clear();
		if (redefinesOriginalDefinition()) {
			declaredPropertiesTakingMySelfAsRange.addAll(getOriginalDefinition().declaredPropertiesTakingMySelfAsRange);
			declaredPropertiesTakingMySelfAsDomain.addAll(getOriginalDefinition().declaredPropertiesTakingMySelfAsDomain);
		}

		Vector<OWLOntology> alreadyDone = new Vector<>();
		for (OWLOntology ontology : getOntology().getAllImportedOntologies()) {
			searchRangeAndDomains(declaredPropertiesTakingMySelfAsRange, declaredPropertiesTakingMySelfAsDomain, ontology, alreadyDone);
		}
		domainsAndRangesAreUpToDate = true;
	}

	protected void recursivelySearchRangeAndDomains() {
		propertiesTakingMySelfAsRange.clear();
		propertiesTakingMySelfAsDomain.clear();
		propertiesTakingMySelfAsRange.addAll(getDeclaredPropertiesTakingMySelfAsRange());
		propertiesTakingMySelfAsDomain.addAll(getDeclaredPropertiesTakingMySelfAsDomain());
		if (redefinesOriginalDefinition()) {
			propertiesTakingMySelfAsRange.addAll(getOriginalDefinition().getPropertiesTakingMySelfAsRange());
			propertiesTakingMySelfAsDomain.addAll(getOriginalDefinition().getPropertiesTakingMySelfAsDomain());
		}
		domainsAndRangesAreRecursivelyUpToDate = true;
	}

	private void searchRangeAndDomains(Set<OWLProperty> rangeProperties, Set<OWLProperty> domainProperties, OWLOntology ontology,
			Vector<OWLOntology> alreadyDone) {
		if (alreadyDone.contains(ontology)) {
			return;
		}
		if (ontology == null) {
			logger.warning("Null ontology !");
			return;
		}
		alreadyDone.add(ontology);
		for (OWLProperty p : ontology.getObjectProperties()) {
			for (OWLConcept<?> o : p.getRangeList()) {
				if (o == this) {
					rangeProperties.add(p);
				}
			}
			for (OWLConcept<?> o : p.getDomainList()) {
				if (o == this) {
					domainProperties.add(p);
				}
			}
			/* if (p.getRange() != null && p.getRange() == this) {
				rangeProperties.add(p);
			}
			if (p.getDomain() != null && p.getDomain() == this) {
				domainProperties.add(p);
			}*/
		}
		for (OWLProperty p : ontology.getDataProperties()) {
			for (OWLConcept<?> o : p.getRangeList()) {
				if (o == this) {
					rangeProperties.add(p);
				}
			}
			for (OWLConcept<?> o : p.getDomainList()) {
				if (o == this) {
					domainProperties.add(p);
				}
			}
			/*if (p.getRange() != null && p.getRange() == this) {
				rangeProperties.add(p);
			}
			if (p.getDomain() != null && p.getDomain() == this) {
				domainProperties.add(p);
			}*/
		}

		// TODO in 1.5: Manage this with inheritance
		if (this instanceof OWLClass) {
			for (OWLClass superClass : ((OWLClass) this).getSuperClasses()) {
				if (superClass instanceof OWLRestriction) {
					OWLProperty p = ((OWLRestriction) superClass).getProperty();
					domainProperties.add(p);
				}
			}
		}

		for (OWLOntology o : ontology.getImportedOntologies()) {
			searchRangeAndDomains(rangeProperties, domainProperties, o, alreadyDone);
		}
	}

	public String getHTMLDescription() {
		return getDisplayableDescription();
	}

	public OWLConcept<R> getOriginalDefinition() {
		return originalDefinition;
	}

	public void setOriginalDefinition(OWLConcept<R> originalDefinition) {
		this.originalDefinition = originalDefinition;
		logger.info("*** " + getOntology() + " Declare object " + this + " as a redefinition of object property initially asserted in "
				+ originalDefinition.getOntology());
	}

	public boolean redefinesOriginalDefinition() {
		return originalDefinition != null;
	}

	/**
	 * This equals has a particular semantics in the way that it returns true only and only if compared objects are representing same
	 * concept regarding URI. This does not guarantee that both objects will respond the same way to some methods.<br>
	 * This method returns true if and only if objects are same, or if one of both object redefine the other one (with eventual many levels)
	 * 
	 * @param o
	 * @return
	 */
	@Override
	public boolean equalsToConcept(IFlexoOntologyConcept<OWLTechnologyAdapter> o) {
		if (o == null) {
			return false;
		}
		return StringUtils.isNotEmpty(getURI()) && getURI().equals(o.getURI());
	}

	@Override
	public List<? extends IFlexoOntologyAnnotation> getAnnotations() {
		// TODO: return annotation statements here...
		return null;
	}

	@Override
	public OWLOntology getContainer() {
		return getOntology();
	}

	@Override
	public <T> T accept(IFlexoOntologyConceptVisitor<T> visitor) {
		return null;
	}

	@Override
	public final synchronized void setChanged() {
		/*
		 * The final keyword is added here mainly because this part of the code
		 * is highly sensitive. A synchronized modifier could cause many
		 * problems (essentially with the auto-saving thread)
		 */

		synchronized (this) {
			super.setChanged();
			setIsModified();
			if (getFlexoOntology() != null) {
				getFlexoOntology().setIsModified();
			}
		}
	}

	public OWLOntology getResourceData() {
		return getFlexoOntology();
	}

	/**
	 * Follow the link. (No behavioural features in OWL)
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConcept#getBehaviouralFeatureAssociations()
	 */
	@Override
	public List<? extends IFlexoOntologyFeatureAssociation<OWLTechnologyAdapter>> getBehaviouralFeatureAssociations() {
		return Collections.emptyList();
	}

}
