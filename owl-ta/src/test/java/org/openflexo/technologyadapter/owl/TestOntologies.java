/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2012-2012, AgileBirds
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

package org.openflexo.technologyadapter.owl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.test.OpenflexoTestCase;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.technologyadapter.owl.model.OWL2URIDefinitions;
import org.openflexo.technologyadapter.owl.model.OWLClass;
import org.openflexo.technologyadapter.owl.model.OWLDataProperty;
import org.openflexo.technologyadapter.owl.model.OWLIndividual;
import org.openflexo.technologyadapter.owl.model.OWLObjectProperty;
import org.openflexo.technologyadapter.owl.model.OWLOntology;
import org.openflexo.technologyadapter.owl.model.OWLOntologyLibrary;
import org.openflexo.technologyadapter.owl.model.OWLProperty;
import org.openflexo.technologyadapter.owl.model.RDFSURIDefinitions;
import org.openflexo.technologyadapter.owl.model.RDFURIDefinitions;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

@RunWith(OrderedRunner.class)
public class TestOntologies extends OpenflexoTestCase {

	protected static final Logger logger = Logger.getLogger(TestOntologies.class.getPackage().getName());

	private static OWLTechnologyAdapter owlAdapter;
	private static OWLOntologyLibrary ontologyLibrary;

	public static final String FLEXO_CONCEPT_ONTOLOGY_URI = "http://www.openflexo.org/openflexo/ontologies/FlexoConceptsOntology.owl";

	/**
	 * Instanciate test ResourceCenter
	 */
	@Test
	@TestOrder(1)
	public void test0LoadTestResourceCenter() {

		log("test0LoadTestResourceCenter()");

		instanciateTestServiceManager(OWLTechnologyAdapter.class);

		// testServiceManager = new
		// TestFlexoServiceManager(ResourceLocator.retrieveResourceAsFile(ResourceLocator
		// .locateResource("TestResourceCenter/Ontologies")));
		owlAdapter = serviceManager.getTechnologyAdapterService().getTechnologyAdapter(OWLTechnologyAdapter.class);
		ontologyLibrary = (OWLOntologyLibrary) serviceManager.getTechnologyAdapterService().getTechnologyContextManager(owlAdapter);

		for (FlexoResourceCenter<?> rc : serviceManager.getResourceCenterService().getResourceCenters()) {
			System.out.println("> rc: " + rc.getDefaultBaseURI() + " " + rc.getBaseArtefact());
			for (FlexoResource<?> r : rc.getAllResources()) {
				System.out.println(" >>> " + r.getURI());
			}
		}
	}

	@Test
	@TestOrder(2)
	public void test1AssertRDFOntologyPresentAndLoaded() {
		log("test1AssertRDFOntologyPresentAndLoaded()");

		OWLOntology rdfOntology = ontologyLibrary.getRDFOntology();
		assertNotNull(rdfOntology);

		// System.out.println("RDFOntology=" +
		// ontologyLibrary.getRDFOntology());
		// System.out.println("hash=" +
		// ontologyLibrary.getRDFOntology().hashCode());
		// System.out.println("loaded=" +
		// ontologyLibrary.getRDFOntology().isLoaded());

		assertTrue(rdfOntology.isLoaded());
		assertTrue(rdfOntology.getImportedOntologies().size() == 1);
		assertTrue(rdfOntology.getImportedOntologies().get(0) == ontologyLibrary.getRDFSOntology());
	}

	@Test
	@TestOrder(3)
	public void test2AssertRDFSOntologyPresentAndLoaded() {
		log("test2AssertRDFSOntologyPresentAndLoaded()");
		OWLOntology rdfsOntology = ontologyLibrary.getRDFSOntology();
		assertNotNull(rdfsOntology);
		assertTrue(rdfsOntology.isLoaded());

		OWLClass LITERAL = rdfsOntology.getClass(RDFSURIDefinitions.RDFS_LITERAL_URI);
		assertNotNull(LITERAL);
		OWLClass RESOURCE = rdfsOntology.getClass(RDFSURIDefinitions.RDFS_RESOURCE_URI);
		assertNotNull(RESOURCE);
		OWLClass CLASS = rdfsOntology.getClass(RDFSURIDefinitions.RDFS_CLASS_URI);
		assertNotNull(CLASS);
		OWLClass DATATYPE = rdfsOntology.getClass(RDFSURIDefinitions.RDFS_DATATYPE_URI);
		assertNotNull(DATATYPE);
		OWLClass CONTAINER = rdfsOntology.getClass(RDFSURIDefinitions.RDFS_CONTAINER_URI);

		assertNotNull(CONTAINER);

		OWLProperty DOMAIN = rdfsOntology.getProperty(RDFSURIDefinitions.RDFS_DOMAIN_URI);
		assertNotNull(DOMAIN);
		assertFalse(DOMAIN.isAnnotationProperty());

		OWLProperty RANGE = rdfsOntology.getProperty(RDFSURIDefinitions.RDFS_RANGE_URI);
		assertNotNull(RANGE);
		assertFalse(RANGE.isAnnotationProperty());

		OWLProperty SUB_CLASS = rdfsOntology.getProperty(RDFSURIDefinitions.RDFS_SUB_CLASS_URI);
		assertNotNull(SUB_CLASS);
		assertFalse(SUB_CLASS.isAnnotationProperty());

		OWLProperty SUB_PROPERTY = rdfsOntology.getProperty(RDFSURIDefinitions.RDFS_SUB_PROPERTY_URI);
		assertNotNull(SUB_PROPERTY);
		assertFalse(SUB_PROPERTY.isAnnotationProperty());

		OWLProperty MEMBER = rdfsOntology.getProperty(RDFSURIDefinitions.RDFS_MEMBER_URI);
		assertNotNull(MEMBER);
		assertFalse(MEMBER.isAnnotationProperty());

		OWLProperty SEE_ALSO = rdfsOntology.getProperty(RDFSURIDefinitions.RDFS_SEE_ALSO_URI);
		assertNotNull(SEE_ALSO);
		assertTrue(SEE_ALSO.isAnnotationProperty());

		OWLProperty IS_DEFINED_BY = rdfsOntology.getProperty(RDFSURIDefinitions.RDFS_IS_DEFINED_BY_URI);
		assertNotNull(IS_DEFINED_BY);
		assertTrue(IS_DEFINED_BY.isAnnotationProperty());

		OWLProperty LABEL = rdfsOntology.getProperty(RDFSURIDefinitions.RDFS_LABEL_URI);
		assertNotNull(LABEL);
		assertTrue(LABEL.isAnnotationProperty());

		OWLProperty COMMENT = rdfsOntology.getProperty(RDFSURIDefinitions.RDFS_COMMENT_URI);
		assertNotNull(COMMENT);
		assertTrue(COMMENT.isAnnotationProperty());

	}

	@Test
	@TestOrder(4)
	public void test3AssertRDFAndRDFSOntologyCorrectImports() {
		log("test3AssertRDFAndRDFSOntologyCorrectImports()");
		OWLOntology rdfOntology = ontologyLibrary.getRDFOntology();
		OWLOntology rdfsOntology = ontologyLibrary.getRDFSOntology();
		assertTrue(rdfOntology.getImportedOntologies().size() == 1);
		assertTrue(rdfOntology.getImportedOntologies().get(0) == rdfsOntology);
		assertTrue(rdfsOntology.getImportedOntologies().size() == 1);
		assertTrue(rdfsOntology.getImportedOntologies().get(0) == rdfOntology);
	}

	@Test
	@TestOrder(5)
	public void test4AssertOWLOntologyPresentAndLoaded() {
		log("test4AssertOWLOntologyPresentAndLoaded()");
		OWLOntology owlOntology = ontologyLibrary.getOWLOntology();
		assertNotNull(owlOntology);
		assertTrue(owlOntology.isLoaded());
		assertTrue(owlOntology.getImportedOntologies().size() == 2);
		assertTrue(owlOntology.getImportedOntologies().contains(ontologyLibrary.getRDFOntology()));
		assertTrue(owlOntology.getImportedOntologies().contains(ontologyLibrary.getRDFSOntology()));
		assertNotNull(owlOntology.getRootConcept());
	}

	@Test
	@TestOrder(6)
	public void test5AssertFlexoConceptsOntologyIsCorrect() {
		log("test5AssertFlexoConceptsOntologyIsCorrect()");
		OWLOntology flexoConceptsOntology = ontologyLibrary.getOntology(FLEXO_CONCEPT_ONTOLOGY_URI);
		assertNotNull(flexoConceptsOntology);
		assertFalse(flexoConceptsOntology.isLoaded());
		flexoConceptsOntology.loadWhenUnloaded();

		assertTrue(flexoConceptsOntology.isLoaded());
		assertEquals(10, flexoConceptsOntology.getClasses().size());

		assertNotNull(flexoConceptsOntology.getRootConcept());

		OWLClass flexoConcept = flexoConceptsOntology.getClass(FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoConcept");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OWLClass flexoModelObject = flexoConceptsOntology.getClass(FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoModelObject");
		assertNotNull(flexoModelObject);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OWLClass flexoProcessFolder = flexoConceptsOntology.getClass(FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoProcessFolder");
		assertNotNull(flexoProcessFolder);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OWLClass flexoProcess = flexoConceptsOntology.getClass(FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoProcess");
		assertNotNull(flexoProcess);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OWLClass flexoRole = flexoConceptsOntology.getClass(FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoRole");
		assertNotNull(flexoRole);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OWLClass flexoProcessElement = flexoConceptsOntology.getClass(FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoProcessElement");
		assertNotNull(flexoProcessElement);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OWLClass flexoActivity = flexoConceptsOntology.getClass(FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoActivity");
		assertNotNull(flexoActivity);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OWLClass flexoOperation = flexoConceptsOntology.getClass(FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoOperation");
		assertNotNull(flexoOperation);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OWLClass flexoEvent = flexoConceptsOntology.getClass(FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoEvent");
		assertNotNull(flexoEvent);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));

		OWLClass classConcept = flexoConceptsOntology.getClass(OWL2URIDefinitions.OWL_CLASS_URI);
		assertNotNull(classConcept);

		assertFalse(flexoConcept.redefinesOriginalDefinition());
		assertFalse(flexoModelObject.redefinesOriginalDefinition());

		assertTrue(flexoConcept.getSuperClasses().size() == 1);
		assertSameList(flexoConcept.getSuperClasses(), flexoConceptsOntology.getRootConcept());

		assertTrue(flexoModelObject.getSuperClasses().size() == 1);
		assertSameList(flexoModelObject.getSuperClasses(), flexoConceptsOntology.getRootConcept());

		assertSameList(flexoProcess.getSuperClasses(), flexoConceptsOntology.getRootConcept(), flexoModelObject);

		assertSameList(flexoProcessFolder.getSuperClasses(), flexoConceptsOntology.getRootConcept(), flexoModelObject);

		assertSameList(flexoRole.getSuperClasses(), flexoConceptsOntology.getRootConcept(), flexoModelObject);

		assertSameList(flexoProcessElement.getSuperClasses(), flexoConceptsOntology.getRootConcept(), flexoModelObject);

		assertSameList(flexoActivity.getSuperClasses(), flexoConceptsOntology.getRootConcept(), flexoProcessElement);

		assertSameList(flexoEvent.getSuperClasses(), flexoConceptsOntology.getRootConcept(), flexoProcessElement);

		assertSameList(flexoOperation.getSuperClasses(), flexoConceptsOntology.getRootConcept(), flexoProcessElement);

		OWLObjectProperty inRelationWithProperty = flexoConceptsOntology
				.getObjectProperty(FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "inRelationWith");
		assertNotNull(inRelationWithProperty);
		OWLObjectProperty linkedToModelProperty = flexoConceptsOntology
				.getObjectProperty(FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "linkedToModel");
		assertNotNull(linkedToModelProperty);
		OWLObjectProperty linkedToConceptProperty = flexoConceptsOntology
				.getObjectProperty(FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "linkedToConcept");
		assertNotNull(linkedToConceptProperty);
		OWLDataProperty resourceNameProperty = flexoConceptsOntology.getDataProperty(FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "resourceName");
		assertNotNull(resourceNameProperty);
		OWLDataProperty classNameProperty = flexoConceptsOntology.getDataProperty(FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "className");
		assertNotNull(classNameProperty);
		OWLDataProperty flexoIDProperty = flexoConceptsOntology.getDataProperty(FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "flexoID");
		assertNotNull(flexoIDProperty);

		assertFalse(inRelationWithProperty.redefinesOriginalDefinition());
		assertFalse(linkedToModelProperty.redefinesOriginalDefinition());
		assertFalse(linkedToConceptProperty.redefinesOriginalDefinition());
		assertFalse(resourceNameProperty.redefinesOriginalDefinition());
		assertFalse(classNameProperty.redefinesOriginalDefinition());
		assertFalse(flexoIDProperty.redefinesOriginalDefinition());

		assertEquals(1, flexoModelObject.getAnnotationStatements().size());

	}

	@Test
	@TestOrder(7)
	public void test6TestMultiReferencesO3() {
		log("test6TestMultiReferencesO3()");
		OWLOntology o1 = ontologyLibrary.getOntology("http://www.openflexo.org/test/O1.owl");
		assertNotNull(o1);
		OWLOntology o2 = ontologyLibrary.getOntology("http://www.openflexo.org/test/O2.owl");
		assertNotNull(o2);
		OWLOntology o3 = ontologyLibrary.getOntology("http://www.openflexo.org/test/O3.owl");
		assertNotNull(o3);

		o3.loadWhenUnloaded();
		assertTrue(o3.isLoaded());
		assertTrue(o1.isLoaded());
		assertTrue(o2.isLoaded());

		assertEquals(1, o3.getClasses().size());
		assertSameList(o3.getClasses(), o3.getRootConcept());
		assertEquals(o3.getOntologyLibrary().getOWLOntology().getRootConcept(), o3.getRootConcept().getOriginalDefinition());

		OWLClass a2 = o2.getClass(o2.getURI() + "#A2");
		assertNotNull(a2);
		OWLClass b2 = o2.getClass(o2.getURI() + "#B2");
		assertNotNull(b2);
		OWLClass c2 = o2.getClass(o2.getURI() + "#C2");
		assertNotNull(c2);
		assertEquals(4, o2.getClasses().size());
		assertSameList(o2.getClasses(), o2.getRootConcept(), a2, b2, c2);

		OWLClass a1 = o1.getClass(o1.getURI() + "#A1");
		assertNotNull(a1);
		OWLClass b1 = o1.getClass(o1.getURI() + "#B1");
		assertNotNull(b1);
		OWLClass c1 = o1.getClass(o1.getURI() + "#C1");
		assertNotNull(c1);
		assertEquals(4, o1.getClasses().size());
		assertSameList(o1.getClasses(), o1.getRootConcept(), a1, b1, c1);
		assertTrue(b1.isSuperConceptOf(c1));

	}

	@Test
	@TestOrder(8)
	public void test7TestMultiReferencesO4() {
		log("test7TestMultiReferencesO4()");
		OWLOntology o1 = ontologyLibrary.getOntology("http://www.openflexo.org/test/O1.owl");
		assertNotNull(o1);
		OWLOntology o2 = ontologyLibrary.getOntology("http://www.openflexo.org/test/O2.owl");
		assertNotNull(o2);
		OWLOntology o3 = ontologyLibrary.getOntology("http://www.openflexo.org/test/O3.owl");
		assertNotNull(o3);
		OWLOntology o5 = ontologyLibrary.getOntology("http://www.openflexo.org/test/O4.owl");
		assertNotNull(o5);

		o3.loadWhenUnloaded();
		assertTrue(o3.isLoaded());
		assertTrue(o1.isLoaded());
		assertTrue(o2.isLoaded());

		assertEquals(1, o3.getClasses().size());
		assertSameList(o3.getClasses(), o3.getRootConcept());
		assertEquals(o3.getOntologyLibrary().getOWLOntology().getRootConcept(), o3.getRootConcept().getOriginalDefinition());

		OWLClass a2 = o2.getClass(o2.getURI() + "#A2");
		assertNotNull(a2);
		OWLClass b2 = o2.getClass(o2.getURI() + "#B2");
		assertNotNull(b2);
		OWLClass c2 = o2.getClass(o2.getURI() + "#C2");
		assertNotNull(c2);
		assertEquals(4, o2.getClasses().size());
		assertSameList(o2.getClasses(), o2.getRootConcept(), a2, b2, c2);

		OWLClass a1 = o1.getClass(o1.getURI() + "#A1");
		assertNotNull(a1);
		OWLClass b1 = o1.getClass(o1.getURI() + "#B1");
		assertNotNull(b1);
		OWLClass c1 = o1.getClass(o1.getURI() + "#C1");
		assertNotNull(c1);
		assertEquals(4, o1.getClasses().size());
		assertSameList(o1.getClasses(), o1.getRootConcept(), a1, b1, c1);
		assertTrue(b1.isSuperConceptOf(c1));

		o5.loadWhenUnloaded();
		assertTrue(o5.isLoaded());

		OWLClass a1fromO4 = o5.getClass(o1.getURI() + "#A1");
		assertNotNull(a1fromO4);
		assertEquals(a1, a1fromO4.getOriginalDefinition());

		OWLClass a2fromO4 = o5.getClass(o2.getURI() + "#A2");
		assertNotNull(a2fromO4);
		assertEquals(a2, a2fromO4.getOriginalDefinition());

		assertEquals(3, o5.getClasses().size());
		assertSameList(o5.getClasses(), o5.getRootConcept(), a1fromO4, a2fromO4);

		assertTrue(a1fromO4.isSuperConceptOf(a2fromO4));

	}

	@Test
	@TestOrder(9)
	public void test8TestMultiReferencesO5() {
		log("test8TestMultiReferencesO5()");
		OWLOntology o1 = ontologyLibrary.getOntology("http://www.openflexo.org/test/O1.owl");
		assertNotNull(o1);
		OWLOntology o2 = ontologyLibrary.getOntology("http://www.openflexo.org/test/O2.owl");
		assertNotNull(o2);
		OWLOntology o3 = ontologyLibrary.getOntology("http://www.openflexo.org/test/O3.owl");
		assertNotNull(o3);
		OWLOntology o5 = ontologyLibrary.getOntology("http://www.openflexo.org/test/O5.owl");
		assertNotNull(o5);

		o3.loadWhenUnloaded();
		assertTrue(o3.isLoaded());
		assertTrue(o1.isLoaded());
		assertTrue(o2.isLoaded());

		assertEquals(1, o3.getClasses().size());
		assertSameList(o3.getClasses(), o3.getRootConcept());
		assertEquals(o3.getOntologyLibrary().getOWLOntology().getRootConcept(), o3.getRootConcept().getOriginalDefinition());

		OWLClass a2 = o2.getClass(o2.getURI() + "#A2");
		assertNotNull(a2);
		OWLClass b2 = o2.getClass(o2.getURI() + "#B2");
		assertNotNull(b2);
		OWLClass c2 = o2.getClass(o2.getURI() + "#C2");
		assertNotNull(c2);
		assertEquals(4, o2.getClasses().size());
		assertSameList(o2.getClasses(), o2.getRootConcept(), a2, b2, c2);

		OWLClass a1 = o1.getClass(o1.getURI() + "#A1");
		assertNotNull(a1);
		OWLClass b1 = o1.getClass(o1.getURI() + "#B1");
		assertNotNull(b1);
		OWLClass c1 = o1.getClass(o1.getURI() + "#C1");
		assertNotNull(c1);
		assertEquals(4, o1.getClasses().size());
		assertSameList(o1.getClasses(), o1.getRootConcept(), a1, b1, c1);
		assertTrue(b1.isSuperConceptOf(c1));

		o5.loadWhenUnloaded();
		assertTrue(o5.isLoaded());

		OWLClass a1fromO5 = o5.getClass(o1.getURI() + "#A1");
		assertNotNull(a1fromO5);
		assertEquals(a1, a1fromO5.getOriginalDefinition());

		OWLClass a2fromO5 = o5.getClass(o2.getURI() + "#A2");
		assertNotNull(a2fromO5);
		assertEquals(a2, a2fromO5.getOriginalDefinition());

		assertEquals(3, o5.getClasses().size());
		assertSameList(o5.getClasses(), o5.getRootConcept(), a1fromO5, a2fromO5);

		assertTrue(a2fromO5.isSuperConceptOf(a1fromO5));

	}

	@Test
	@TestOrder(10)
	public void test9TestInstances() {
		log("test9TestInstances()");
		OWLOntology ontology = ontologyLibrary.getOntology("http://www.openflexo.org/test/TestInstances.owl");
		assertNotNull(ontology);

		ontology.loadWhenUnloaded();
		assertTrue(ontology.isLoaded());

		assertEquals(4, ontology.getIndividuals().size());

		OWLIndividual activity1 = ontology.getIndividual(ontology.getURI() + "#Activity1");
		assertNotNull(activity1);
		OWLIndividual activity2 = ontology.getIndividual(ontology.getURI() + "#Activity2");
		assertNotNull(activity2);
		OWLIndividual activity3 = ontology.getIndividual(ontology.getURI() + "#Activity3");
		assertNotNull(activity3);
		OWLIndividual untypedIndividual = ontology.getIndividual(ontology.getURI() + "#UntypedIndividual");
		assertNotNull(untypedIndividual);

		assertSameList(ontology.getIndividuals(), activity1, activity2, activity3, untypedIndividual);

		OWLOntology flexoConceptsOntology = ontologyLibrary.getOntology(FLEXO_CONCEPT_ONTOLOGY_URI);

		OWLClass redefinedFlexoActivity = ontology.getClass(FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoActivity");
		assertNotNull(redefinedFlexoActivity);
		OWLClass flexoActivity = flexoConceptsOntology.getClass(FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoActivity");
		assertNotNull(flexoActivity);
		assertTrue(redefinedFlexoActivity.redefinesOriginalDefinition());
		assertEquals(flexoActivity, redefinedFlexoActivity.getOriginalDefinition());

		assertTrue(redefinedFlexoActivity.isSuperConceptOf(activity1));
		assertTrue(redefinedFlexoActivity.isSuperConceptOf(activity2));
		assertTrue(redefinedFlexoActivity.isSuperConceptOf(activity3));

		assertTrue(ontology.getRootConcept().isSuperConceptOf(untypedIndividual));
		assertTrue(ontology.getRootConcept().getOriginalDefinition().isSuperConceptOf(untypedIndividual));

		OWLDataProperty resourceNameProperty = flexoConceptsOntology.getDataProperty(FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "resourceName");
		assertNotNull(resourceNameProperty);

		assertEquals("Process1", activity1.getPropertyValue(resourceNameProperty));

		assertEquals(35, ontology.getAccessibleClasses().size());

	}

	@Test
	@TestOrder(11)
	public void test10TestAnnotations() {
		log("test10TestAnnotations()");
		OWLOntology ontology = ontologyLibrary.getOntology("http://www.openflexo.org/test/TestInstances.owl");
		assertNotNull(ontology);

		ontology.loadWhenUnloaded();
		assertTrue(ontology.isLoaded());

		assertEquals(4, ontology.getIndividuals().size());

		OWLIndividual activity1 = ontology.getIndividual(ontology.getURI() + "#Activity1");
		assertNotNull(activity1);
		OWLIndividual activity2 = ontology.getIndividual(ontology.getURI() + "#Activity2");
		assertNotNull(activity2);
		OWLIndividual activity3 = ontology.getIndividual(ontology.getURI() + "#Activity3");
		assertNotNull(activity3);

		assertEquals(4, activity1.getAnnotationStatements().size());

		OWLDataProperty COMMENT = ontology.getDataProperty(RDFSURIDefinitions.RDFS_COMMENT_URI);
		assertNotNull(COMMENT);
		assertTrue(COMMENT.isAnnotationProperty());

		assertEquals(activity1.getAnnotationValue(COMMENT, Language.ENGLISH), "A comment in english");
		assertEquals(activity1.getAnnotationValue(COMMENT, Language.FRENCH), "Un commentaire en francais");

		FlexoLocalization.setCurrentLanguage(Language.ENGLISH);
		// System.out.println("english value = " +
		// activity1.getPropertyValue(COMMENT));
		assertEquals(activity1.getPropertyValue(COMMENT), "A comment in english");

		FlexoLocalization.setCurrentLanguage(Language.FRENCH);
		// System.out.println("french value = " +
		// activity1.getPropertyValue(COMMENT));
		assertEquals(activity1.getPropertyValue(COMMENT), "Un commentaire en francais");

		// System.out.println("les annotations pour activity2=" +
		// activity2.getAnnotationStatements());
		assertEquals(activity2.getPropertyValue(COMMENT), "Activity2 is only commented in english");

		// System.out.println("les annotations pour activity3=" +
		// activity3.getAnnotationStatements());
		assertEquals(activity3.getPropertyValue(COMMENT), "Cette activite est commentee en francais uniquement");

		assertEquals(1, activity1.getAnnotationObjectStatements().size());

		OWLObjectProperty SEE_ALSO = ontology.getObjectProperty(RDFSURIDefinitions.RDFS_SEE_ALSO_URI);
		assertNotNull(SEE_ALSO);
		assertTrue(SEE_ALSO.isAnnotationProperty());

		assertEquals(activity3, activity1.getAnnotationObjectValue(SEE_ALSO));

	}

	@Test
	@TestOrder(12)
	public void test11TestLoadArchimateOntology() {
		log("test11TestLoadArchimateOntology()");
		OWLOntology ontology = ontologyLibrary.getOntology("http://www.bolton.ac.uk/archimate");
		assertNotNull(ontology);

		ontology.loadWhenUnloaded();
		assertTrue(ontology.isLoaded());
	}

	@Test
	@TestOrder(13)
	public void test12TestLoadCPMFInstanceOntology() {
		log("test12TestLoadCPMFInstanceOntology()");
		OWLOntology ontology = ontologyLibrary.getOntology("http://www.cpmf.org/ontologies/cpmfInstance/1.0");
		assertNotNull(ontology);

		ontology.loadWhenUnloaded();
		assertTrue(ontology.isLoaded());
	}

	@Test
	@TestOrder(14)
	public void test13TestLoadBPMNOntology() {
		log("test13TestLoadBPMNOntology()");
		OWLOntology ontology = ontologyLibrary.getOntology("http://dkm.fbk.eu/index.php/BPMN_Ontology");
		assertNotNull(ontology);

		ontology.loadWhenUnloaded();
		assertTrue(ontology.isLoaded());
	}

	@Test
	@TestOrder(15)
	public void test14TestBasicOntologEditor() {
		log("test14TestBasicOntologEditor()");
		OWLOntology ontology = ontologyLibrary.getOntology("http://www.openflexo.org/openflexo/ViewPoints/BasicOntology.owl");
		assertNotNull(ontology);

		ontology.loadWhenUnloaded();
		assertTrue(ontology.isLoaded());
	}

	@Test
	@TestOrder(16)
	public void test15TestLoadSKOS() {
		log("test15TestLoadSKOS()");
		String SKOS_URI = "http://www.w3.org/2004/02/skos/core";
		OWLOntology skosOntology = ontologyLibrary.getOntology(SKOS_URI);
		assertNotNull(skosOntology);

		skosOntology.loadWhenUnloaded();
		assertTrue(skosOntology.isLoaded());

		OWLObjectProperty hasTopConceptProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "hasTopConcept");
		assertNotNull(hasTopConceptProperty);
		OWLObjectProperty inSchemeProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "inScheme");
		assertNotNull(inSchemeProperty);
		OWLObjectProperty topConceptOfProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "topConceptOf");
		assertNotNull(topConceptOfProperty);
		OWLObjectProperty skosMemberProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "member");
		assertNotNull(skosMemberProperty);
		OWLObjectProperty memberListProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "memberList");
		assertNotNull(memberListProperty);
		OWLObjectProperty semanticRelationProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "semanticRelation");
		assertNotNull(semanticRelationProperty);
		OWLObjectProperty relatedProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "related");
		assertNotNull(relatedProperty);
		OWLObjectProperty relatedMatchProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "relatedMatch");
		assertNotNull(relatedMatchProperty);
		OWLObjectProperty mappingRelationProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "mappingRelation");
		assertNotNull(mappingRelationProperty);
		OWLObjectProperty closeMatchProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "closeMatch");
		assertNotNull(closeMatchProperty);
		OWLObjectProperty exactMatchProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "exactMatch");
		assertNotNull(exactMatchProperty);
		OWLObjectProperty narrowMatchProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "narrowMatch");
		assertNotNull(narrowMatchProperty);
		OWLObjectProperty broadMatchProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "broadMatch");
		assertNotNull(broadMatchProperty);
		OWLObjectProperty broaderTransitiveProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "broaderTransitive");
		assertNotNull(broaderTransitiveProperty);
		OWLObjectProperty broaderProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "broader");
		assertNotNull(broaderProperty);
		OWLObjectProperty narrowerTransitiveProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "narrowerTransitive");
		assertNotNull(narrowerTransitiveProperty);
		OWLObjectProperty narrowerProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "narrower");
		assertNotNull(narrowerProperty);

	}

	@Test
	@TestOrder(17)
	public void test16TestAccessibleProperties() {
		log("test16TestAccessibleProperties()");

		OWLOntology rdfOntology = ontologyLibrary.getRDFOntology();
		assertNotNull(rdfOntology);
		assertTrue(rdfOntology.isLoaded());
		OWLOntology rdfsOntology = ontologyLibrary.getRDFSOntology();
		assertNotNull(rdfsOntology);
		assertTrue(rdfsOntology.isLoaded());
		OWLOntology owlOntology = ontologyLibrary.getOWLOntology();
		assertNotNull(owlOntology);
		assertTrue(owlOntology.isLoaded());

		OWLObjectProperty valueProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "value");
		assertNotNull(valueProperty);
		OWLObjectProperty typeProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "type");
		assertNotNull(typeProperty);

		OWLDataProperty labelProperty = rdfOntology.getDataProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "label");
		assertNotNull(labelProperty);
		OWLDataProperty commentProperty = rdfOntology.getDataProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "comment");
		assertNotNull(commentProperty);
		OWLObjectProperty isDefinedByProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "isDefinedBy");
		assertNotNull(isDefinedByProperty);
		OWLObjectProperty seeAlsoProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "seeAlso");
		assertNotNull(seeAlsoProperty);
		OWLObjectProperty memberProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "member");
		assertNotNull(memberProperty);
		OWLObjectProperty domainProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "domain");
		assertNotNull(domainProperty);
		OWLObjectProperty rangeProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "range");
		assertNotNull(rangeProperty);
		OWLObjectProperty subClassOfProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "subClassOf");
		assertNotNull(subClassOfProperty);
		OWLObjectProperty subPropertyOfProperty = rdfOntology
				.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "subPropertyOf");
		assertNotNull(subPropertyOfProperty);

		OWLObjectProperty topObjectProperty = owlOntology
				.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "topObjectProperty");
		assertNotNull(topObjectProperty);
		OWLObjectProperty bottomObjectProperty = owlOntology
				.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "bottomObjectProperty");
		assertNotNull(bottomObjectProperty);
		OWLDataProperty bottomDataProperty = owlOntology.getDataProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "bottomDataProperty");
		assertNotNull(bottomDataProperty);
		OWLDataProperty topDataProperty = owlOntology.getDataProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "topDataProperty");
		assertNotNull(topDataProperty);

		OWLObjectProperty annotatedPropertyProperty = owlOntology
				.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "annotatedProperty");
		assertNotNull(annotatedPropertyProperty);
		OWLObjectProperty annotatedSourceProperty = owlOntology
				.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "annotatedSource");
		assertNotNull(annotatedSourceProperty);
		OWLObjectProperty annotatedTargetProperty = owlOntology
				.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "annotatedTarget");
		assertNotNull(annotatedTargetProperty);
		OWLObjectProperty datatypeComplementOfProperty = owlOntology
				.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "datatypeComplementOf");
		assertNotNull(datatypeComplementOfProperty);
		OWLObjectProperty deprecatedProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "deprecated");
		assertNotNull(deprecatedProperty);
		OWLObjectProperty equivalentClassProperty = owlOntology
				.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "equivalentClass");
		assertNotNull(equivalentClassProperty);
		OWLObjectProperty equivalentPropertyProperty = owlOntology
				.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "equivalentProperty");
		assertNotNull(equivalentPropertyProperty);
		OWLObjectProperty intersectionOfProperty = owlOntology
				.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "intersectionOf");
		assertNotNull(intersectionOfProperty);
		OWLObjectProperty membersProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "members");
		assertNotNull(membersProperty);
		OWLObjectProperty onDatatypeProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "onDatatype");
		assertNotNull(onDatatypeProperty);
		OWLObjectProperty oneOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "oneOf");
		assertNotNull(oneOfProperty);
		OWLObjectProperty propertyDisjointWithProperty = owlOntology
				.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "propertyDisjointWith");
		assertNotNull(propertyDisjointWithProperty);
		OWLObjectProperty unionOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "unionOf");
		assertNotNull(unionOfProperty);
		OWLObjectProperty versionInfoProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "versionInfo");
		assertNotNull(versionInfoProperty);
		OWLObjectProperty withRestrictionsProperty = owlOntology
				.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "withRestrictions");
		assertNotNull(withRestrictionsProperty);
		OWLObjectProperty differentFromProperty = owlOntology
				.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "differentFrom");
		assertNotNull(differentFromProperty);
		OWLObjectProperty sameAsProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "sameAs");
		assertNotNull(sameAsProperty);
		OWLObjectProperty distinctMembersProperty = owlOntology
				.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "distinctMembers");
		assertNotNull(distinctMembersProperty);
		OWLObjectProperty hasKeyProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "hasKey");
		assertNotNull(hasKeyProperty);
		OWLObjectProperty disjointUnionOfProperty = owlOntology
				.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "disjointUnionOf");
		assertNotNull(disjointUnionOfProperty);
		OWLObjectProperty complementOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "complementOf");
		assertNotNull(complementOfProperty);
		OWLObjectProperty disjointWithProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "disjointWith");
		assertNotNull(disjointWithProperty);
		OWLObjectProperty sourceIndividualProperty = owlOntology
				.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "sourceIndividual");
		assertNotNull(sourceIndividualProperty);
		OWLObjectProperty assertionPropertyProperty = owlOntology
				.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "assertionProperty");
		assertNotNull(assertionPropertyProperty);
		OWLObjectProperty targetValueProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "targetValue");
		assertNotNull(targetValueProperty);
		OWLObjectProperty targetIndividualProperty = owlOntology
				.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "targetIndividual");
		assertNotNull(targetIndividualProperty);
		OWLObjectProperty propertyChainAxiomProperty = owlOntology
				.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "propertyChainAxiom");
		assertNotNull(propertyChainAxiomProperty);
		OWLObjectProperty inverseOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "inverseOf");
		assertNotNull(inverseOfProperty);

		String SKOS_URI = "http://www.w3.org/2004/02/skos/core";
		OWLOntology skosOntology = ontologyLibrary.getOntology(SKOS_URI);
		assertNotNull(skosOntology);

		skosOntology.loadWhenUnloaded();
		assertTrue(skosOntology.isLoaded());

		OWLObjectProperty hasTopConceptProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "hasTopConcept");
		assertNotNull(hasTopConceptProperty);
		OWLObjectProperty inSchemeProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "inScheme");
		assertNotNull(inSchemeProperty);
		OWLObjectProperty topConceptOfProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "topConceptOf");
		assertNotNull(topConceptOfProperty);
		OWLObjectProperty skosMemberProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "member");
		assertNotNull(skosMemberProperty);
		OWLObjectProperty memberListProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "memberList");
		assertNotNull(memberListProperty);
		OWLObjectProperty semanticRelationProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "semanticRelation");
		assertNotNull(semanticRelationProperty);
		OWLObjectProperty relatedProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "related");
		assertNotNull(relatedProperty);
		OWLObjectProperty relatedMatchProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "relatedMatch");
		assertNotNull(relatedMatchProperty);
		OWLObjectProperty mappingRelationProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "mappingRelation");
		assertNotNull(mappingRelationProperty);
		OWLObjectProperty closeMatchProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "closeMatch");
		assertNotNull(closeMatchProperty);
		OWLObjectProperty exactMatchProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "exactMatch");
		assertNotNull(exactMatchProperty);
		OWLObjectProperty narrowMatchProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "narrowMatch");
		assertNotNull(narrowMatchProperty);
		OWLObjectProperty broadMatchProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "broadMatch");
		assertNotNull(broadMatchProperty);
		OWLObjectProperty broaderTransitiveProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "broaderTransitive");
		assertNotNull(broaderTransitiveProperty);
		OWLObjectProperty broaderProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "broader");
		assertNotNull(broaderProperty);
		OWLObjectProperty narrowerTransitiveProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "narrowerTransitive");
		assertNotNull(narrowerTransitiveProperty);
		OWLObjectProperty narrowerProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "narrower");
		assertNotNull(narrowerProperty);

		OWLClass concept = skosOntology.getClass("http://www.w3.org/2004/02/skos/core#Concept");
		System.out.println("concept super classes = " + concept.getSuperClasses());
		System.out.println("concept super classes = " + concept.getSuperClasses().get(0).getPropertiesTakingMySelfAsDomain());
		System.out.println("accessible properties = " + concept.getPropertiesTakingMySelfAsDomain());

		assertSameList(concept.getPropertiesTakingMySelfAsDomain(), broaderProperty, broaderTransitiveProperty, broadMatchProperty,
				closeMatchProperty, exactMatchProperty, mappingRelationProperty, narrowerProperty, narrowerTransitiveProperty,
				narrowMatchProperty, relatedProperty, relatedMatchProperty, semanticRelationProperty, topConceptOfProperty,
				bottomObjectProperty, bottomDataProperty, differentFromProperty, sameAsProperty, topDataProperty, topObjectProperty,
				hasKeyProperty, complementOfProperty, seeAlsoProperty, memberProperty, disjointUnionOfProperty, disjointWithProperty,
				subClassOfProperty, valueProperty, typeProperty, commentProperty, isDefinedByProperty, labelProperty);

		OWLClass thingFromOWL = owlOntology.getRootConcept();
		System.out.println("thingFromOWL = " + thingFromOWL.getPropertiesTakingMySelfAsDomain());

		OWLClass thingFromSKOS = skosOntology.getRootConcept();
		System.out.println("thingFromSKOS = " + thingFromSKOS.getPropertiesTakingMySelfAsDomain());

	}

	/*
	 * public static void main(String[] args) {
	 * logger.info("Try to load ontology " +
	 * RDFSURIDefinitions.RDFS_ONTOLOGY_URI);
	 * 
	 * OntModel ontModel =
	 * ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null, null);
	 * 
	 * // FIXES add strict to FALSE (XtoF) // FIXES OPENFLEXO-39, OPENFLEXO-40,
	 * OPENFLEXO-41, OPENFLEXO-42, OPENFLEXO-43, OPENFLEXO-44 //
	 * ontModel.setStrictMode(false);
	 * 
	 * // we have a local copy of flexo concept ontology if
	 * (alternativeLocalFile != null) { logger.fine("Alternative local file: " +
	 * alternativeLocalFile.getAbsolutePath()); try {
	 * ontModel.getDocumentManager().addAltEntry(ontologyURI,
	 * alternativeLocalFile.toURL().toString()); } catch (MalformedURLException
	 * e) { e.printStackTrace(); } }
	 * 
	 * // read the source document try { logger.info("BEGIN Read " +
	 * ontologyURI); ontModel.read(ontologyURI); logger.info("END read " +
	 * ontologyURI); } catch (Exception e) {
	 * logger.warning("Unexpected exception while reading ontology " +
	 * ontologyURI); logger.warning("Exception " + e.getMessage() +
	 * ". See logs for details"); e.printStackTrace(); }
	 * 
	 * isLoaded = true;
	 * 
	 * for (Object o : ontModel.listImportedOntologyURIs()) { OWLOntology
	 * importedOnt = _library.getOntology((String) o);
	 * logger.info("importedOnt= " + importedOnt); if (importedOnt != null) {
	 * importedOnt.loadWhenUnloaded(); importedOntologies.add(importedOnt); } }
	 * 
	 * logger.info("For " + ontologyURI + " Imported ontologies = " +
	 * getImportedOntologies());
	 * 
	 * logger.info("Loaded ontology " + ontologyURI +
	 * " search for concepts and properties");
	 * 
	 * for (OWLOntology o : getImportedOntologies()) {
	 * logger.info("Imported ontology: " + o); }
	 * 
	 * createConceptsAndProperties();
	 * 
	 * logger.info("Finished loading ontology " + ontologyURI);
	 * 
	 * }
	 * 
	 * }
	 */

	/*
	 * public static void main(String[] args) {
	 * 
	 * ResourceLocator.init(); File rdfsOWLOntologyFile = new
	 * FileResource("Ontologies/www.w3.org/2000/01/rdf-schema.owl");
	 * 
	 * testResourceCenter = LocalResourceCenterImplementation.
	 * instanciateTestLocalResourceCenterImplementation(new FileResource(
	 * "TestResourceCenter"));
	 * 
	 * OntModel ontModel =
	 * ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, ontologyLibrary,
	 * null); for (OWLOntology o : ontologyLibrary.getAllOntologies()) { try {
	 * System.out.println("Onto: " + o.getURI() + " file " +
	 * o.getAlternativeLocalFile().toURL().toString());
	 * ontModel.getDocumentManager().addAltEntry(o.getURI(),
	 * o.getAlternativeLocalFile().toURL().toString()); } catch
	 * e.printStackTrace(); } }
	 * 
	 * if (rdfsOWLOntologyFile != null) { logger.fine("RDFS local file: " +
	 * rdfsOWLOntologyFile.getAbsolutePath()); try {
	 * ontModel.getDocumentManager().addAltEntry(RDFSURIDefinitions.
	 * RDFS_ONTOLOGY_URI, rdfsOWLOntologyFile.toURL().toString()); } catch
	 * (MalformedURLException e) { e.printStackTrace(); } }
	 * 
	 * // read the source document try { logger.info("BEGIN Read " +
	 * RDFSURIDefinitions.RDFS_ONTOLOGY_URI);
	 * ontModel.read(RDFSURIDefinitions.RDFS_ONTOLOGY_URI);
	 * logger.info("END read " + RDFSURIDefinitions.RDFS_ONTOLOGY_URI); } catch
	 * (Exception e) {
	 * logger.warning("Unexpected exception while reading ontology " +
	 * RDFSURIDefinitions.RDFS_ONTOLOGY_URI); logger.warning("Exception " +
	 * e.getMessage() + ". See logs for details"); e.printStackTrace(); }
	 * 
	 * for (NodeIterator i = ontModel.listObjects(); i.hasNext();) { RDFNode
	 * node = i.nextNode(); System.out.println("Node " + node + " as " +
	 * node.getClass().getName()); }
	 * 
	 * for (NodeIterator i = ontModel.listObjects(); i.hasNext();) { RDFNode
	 * node = i.nextNode(); if (node instanceof Resource && ((Resource)
	 * node).canAs(OntClass.class)) { OntClass ontClass = ((Resource)
	 * node).as(OntClass.class); System.out.println("> Class: " + ontClass); } }
	 * 
	 * }
	 */
}
