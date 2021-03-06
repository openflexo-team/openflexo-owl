/**
 * 
 * Copyright (c) 2014, Openflexo
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

package org.openflexo.technologyadapter.owl.gui.widget;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.gina.test.OpenflexoTestCaseWithGUI;
import org.openflexo.gina.test.SwingGraphicalContextDelegate;
import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;
import org.openflexo.technologyadapter.owl.gui.FIBOWLIndividualSelector;
import org.openflexo.technologyadapter.owl.gui.OWLOntologyBrowserModel;
import org.openflexo.technologyadapter.owl.model.OWLClass;
import org.openflexo.technologyadapter.owl.model.OWLIndividual;
import org.openflexo.technologyadapter.owl.model.OWLOntology;
import org.openflexo.technologyadapter.owl.model.OWLOntologyLibrary;
import org.openflexo.technologyadapter.owl.model.RDFURIDefinitions;
import org.openflexo.technologyadapter.owl.rm.OWLOntologyResource;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;
import org.openflexo.test.UITest;

/**
 * Test the structural and behavioural features of FIBOWLPropertySelector
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class TestFIBOWLIndividualSelector extends OpenflexoTestCaseWithGUI {

	private static SwingGraphicalContextDelegate gcDelegate;

	private static OWLOntologyResource ontologyResource;

	private static FIBOWLIndividualSelector selector;

	private static OWLOntology skosOntology, owlOntology;
	private static OWLClass thing, collection, orderedCollection, concept, conceptScheme, list;
	private static OWLIndividual coreIndividual;

	private static OWLTechnologyAdapter owlAdapter;
	private static OWLOntologyLibrary ontologyLibrary;

	@BeforeClass
	public static void setupClass() {
		// ResourceLocator.locateResource("/org.openflexo.owlconnector/TestResourceCenter");
		instanciateTestServiceManager(OWLTechnologyAdapter.class);
		owlAdapter = serviceManager.getTechnologyAdapterService().getTechnologyAdapter(OWLTechnologyAdapter.class);
		ontologyLibrary = (OWLOntologyLibrary) serviceManager.getTechnologyAdapterService().getTechnologyContextManager(owlAdapter);
		initGUI();
	}

	@Test
	@TestOrder(1)
	@Category(UITest.class)
	public void test1RetrieveOntology() {

		OWLTechnologyAdapter owlTA = serviceManager.getTechnologyAdapterService().getTechnologyAdapter(OWLTechnologyAdapter.class);

		assertNotNull(owlTA);

		ontologyResource = (OWLOntologyResource) serviceManager.getResourceManager().getResource("http://www.w3.org/2004/02/skos/core",
				OWLOntology.class);

		assertNotNull(ontologyResource);

		System.out.println("Found ontology resource " + ontologyResource);

		System.out.println("Try to load ontology resource " + ontologyResource);

		try {
			ontologyResource.loadResourceData();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (ResourceLoadingCancelledException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (FlexoException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		skosOntology = ontologyResource.getLoadedResourceData();
		assertNotNull(skosOntology);

		owlOntology = ontologyLibrary.getOWLOntology();
		assertNotNull(owlOntology);
		assertTrue(skosOntology.getImportedOntologies().contains(owlOntology));

		thing = skosOntology.getRootConcept();
		assertNotNull(thing);
		OWLClass thingInOWL = owlOntology.getRootConcept();
		assertSame(thing.getOriginalDefinition(), thingInOWL);

		assertNotNull(collection = skosOntology.getClass(skosOntology.getURI() + "#" + "Collection"));
		assertNotNull(orderedCollection = skosOntology.getClass(skosOntology.getURI() + "#" + "OrderedCollection"));
		assertNotNull(concept = skosOntology.getClass(skosOntology.getURI() + "#" + "Concept"));
		assertNotNull(conceptScheme = skosOntology.getClass(skosOntology.getURI() + "#" + "ConceptScheme"));
		assertNotNull(list = skosOntology.getClass(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "List"));

		assertNotNull(coreIndividual = skosOntology.getIndividual(skosOntology.getURI()));
	}

	@Test
	@TestOrder(2)
	@Category(UITest.class)
	public void test2InstanciateWidget() {

		selector = new FIBOWLIndividualSelector(null);
		selector.setContext(ontologyResource.getLoadedResourceData());
		selector.setStrictMode(false);
		selector.getCustomPanel();

		assertNotNull(selector.getSelectorPanel().getController());

		gcDelegate.addTab("FIBOWLClassSelector", selector.getSelectorPanel().getController());
	}

	@Test
	@TestOrder(3)
	@Category(UITest.class)
	public void checkInitialStructure() {

		OWLOntologyBrowserModel obm = selector.getModel();

		assertEquals(1, obm.getRoots().size());
		assertEquals(obm.getRoots().get(0), thing);

		assertEquals(1, obm.getChildren(thing).size());
		assertSameList(obm.getChildren(thing), coreIndividual);

	}

	@Test
	@TestOrder(4)
	@Category(UITest.class)
	public void setType() {

		selector.setType(concept);

		OWLOntologyBrowserModel obm = selector.getModel();

		assertEquals(0, obm.getRoots().size());
	}

	public static void initGUI() {
		gcDelegate = new SwingGraphicalContextDelegate(TestFIBOWLIndividualSelector.class.getSimpleName());
	}

	@AfterClass
	public static void waitGUI() {
		gcDelegate.waitGUI();
	}

	@Before
	public void setUp() {
		gcDelegate.setUp();
	}

	@Override
	@After
	public void tearDown() throws Exception {
		gcDelegate.tearDown();
	}

}
