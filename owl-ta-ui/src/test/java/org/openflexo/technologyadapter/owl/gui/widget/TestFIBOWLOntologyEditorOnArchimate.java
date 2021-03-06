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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.resource.ResourceRepository;
import org.openflexo.gina.test.OpenflexoTestCaseWithGUI;
import org.openflexo.gina.test.SwingGraphicalContextDelegate;
import org.openflexo.gina.view.widget.browser.impl.FIBBrowserModel;
import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;
import org.openflexo.technologyadapter.owl.gui.FIBOWLOntologyEditor;
import org.openflexo.technologyadapter.owl.model.OWLClass;
import org.openflexo.technologyadapter.owl.model.OWLOntology;
import org.openflexo.technologyadapter.owl.model.OWLOntologyLibrary;
import org.openflexo.technologyadapter.owl.rm.OWLOntologyResource;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;
import org.openflexo.test.UITest;

/**
 * Test the structural and behavioural features of FIBOWLOntologyBrowser copy of the test on SKOSontology to test performance Issues
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class TestFIBOWLOntologyEditorOnArchimate extends OpenflexoTestCaseWithGUI {

	private static SwingGraphicalContextDelegate gcDelegate;

	private static OWLOntologyResource ontologyResource;
	private static FIBOWLOntologyEditor editor;
	private static OWLTechnologyAdapter owlAdapter;
	private static OWLOntologyLibrary ontologyLibrary;
	protected static final Logger logger = Logger.getLogger(TestFIBOWLOntologyEditorOnArchimate.class.getPackage().getName());

	@BeforeClass
	public static void setupClass() {
		// ResourceLocator.locateResource("/org.openflexo.owlconnector/TestResourceCenter");
		instanciateTestServiceManager(OWLTechnologyAdapter.class);

		for (FlexoResourceCenter<?> rc : serviceManager.getResourceCenterService().getResourceCenters()) {
			System.out.println("> rc: " + rc.getDefaultBaseURI() + " " + rc.getBaseArtefact());
			for (FlexoResource<?> r : rc.getAllResources()) {
				System.out.println(" >>> " + r.getURI());
			}
		}

		owlAdapter = serviceManager.getTechnologyAdapterService().getTechnologyAdapter(OWLTechnologyAdapter.class);
		ontologyLibrary = (OWLOntologyLibrary) serviceManager.getTechnologyAdapterService().getTechnologyContextManager(owlAdapter);
		initGUI();

		// Default behaviour is to update browser cells asynchronously in event-dispatch-thread
		// But in this test environment, we need to "force" the update to be done synchronously
		FIBBrowserModel.UPDATE_BROWSER_SYNCHRONOUSLY = true;
	}

	@Test
	@TestOrder(1)
	@Category(UITest.class)
	public void test1RetrieveOntology() {

		OWLTechnologyAdapter owlTA = serviceManager.getTechnologyAdapterService().getTechnologyAdapter(OWLTechnologyAdapter.class);

		assertNotNull(owlTA);

		List<ResourceRepository<?, ?>> owlRepositories = serviceManager.getResourceManager().getAllRepositories(owlTA);

		ResourceRepository<OWLOntologyResource, ?> ontologyRepository = null;
		for (ResourceRepository<?, ?> rep : owlRepositories) {
			if (rep.getSize() > 0) {
				ontologyRepository = (ResourceRepository<OWLOntologyResource, ?>) rep;
				break;
			}
		}

		assertNotNull(ontologyRepository);

		ontologyResource = (OWLOntologyResource) serviceManager.getResourceManager().getResource("http://www.bolton.ac.uk/archimate",
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

		archimateOntology = ontologyResource.getLoadedResourceData();
		assertNotNull(archimateOntology);

		owlOntology = ontologyLibrary.getOWLOntology();
		assertNotNull(owlOntology);
		assertTrue(archimateOntology.getImportedOntologies().contains(owlOntology));

		thing = archimateOntology.getRootConcept();
		assertNotNull(thing);
		OWLClass thingInOWL = owlOntology.getRootConcept();
		assertSame(thing.getOriginalDefinition(), thingInOWL);

	}

	private static OWLOntology archimateOntology, owlOntology;
	private static OWLClass thing;

	@Test
	@TestOrder(2)
	@Category(UITest.class)
	public void test2InstanciateWidget() {
		long previousDate, currentDate;

		logger.info("test2InstanciateWidget");

		previousDate = System.currentTimeMillis();

		editor = new FIBOWLOntologyEditor(ontologyResource.getLoadedResourceData(), null);

		gcDelegate.addTab("FIBOntologyEditor", editor.getFIBController());

		currentDate = System.currentTimeMillis();
		System.out.println(" initial creation of view took : " + (currentDate - previousDate));
		previousDate = currentDate;

	}

	@Test
	@TestOrder(3)
	@Category(UITest.class)
	public void TestPerfOnView() {
		long previousDate, currentDate;

		logger.info("TestPerfOnView");

		previousDate = System.currentTimeMillis();
		int i = 4;

		while (i > 0) {
			i--;
			editor.setShowIndividuals(false);
			currentDate = System.currentTimeMillis();
			System.out.println(" setShowIndividuals (FALSE)  took: " + (currentDate - previousDate));
			previousDate = currentDate;

			editor.setShowClasses(false);
			currentDate = System.currentTimeMillis();
			System.out.println(" setShowClasses (FALSE)  took: " + (currentDate - previousDate));
			previousDate = currentDate;

			editor.setShowDataProperties(false);
			currentDate = System.currentTimeMillis();
			System.out.println(" setShowDataProperties (FALSE)  took: " + (currentDate - previousDate));
			previousDate = currentDate;

			editor.setShowObjectProperties(false);
			currentDate = System.currentTimeMillis();
			System.out.println(" setShowObjectProperties (FALSE)  took: " + (currentDate - previousDate));
			previousDate = currentDate;

			editor.setShowAnnotationProperties(false);
			currentDate = System.currentTimeMillis();
			System.out.println(" setShowAnnotationProperties (FALSE)  took: " + (currentDate - previousDate));
			previousDate = currentDate;

			editor.update();
			currentDate = System.currentTimeMillis();
			System.out.println(" update   took: " + (currentDate - previousDate));
			previousDate = currentDate;

			editor.setShowClasses(true);
			currentDate = System.currentTimeMillis();
			System.out.println(" setShowClasses (TRUE)  took: " + (currentDate - previousDate));
			previousDate = currentDate;

			editor.setShowDataProperties(true);
			currentDate = System.currentTimeMillis();
			System.out.println(" setShowDataProperties (TRUE) took: " + (currentDate - previousDate));
			previousDate = currentDate;

			editor.setShowObjectProperties(true);
			currentDate = System.currentTimeMillis();
			System.out.println(" setShowObjectProperties (TRUE)  took: " + (currentDate - previousDate));
			previousDate = currentDate;

			editor.setShowAnnotationProperties(true);
			currentDate = System.currentTimeMillis();
			System.out.println(" setShowAnnotationProperties (TRUE) took: " + (currentDate - previousDate));
			previousDate = currentDate;

			editor.setShowIndividuals(true);
			currentDate = System.currentTimeMillis();
			System.out.println(" setShowIndividuals (TRUE) took: " + (currentDate - previousDate));
			previousDate = currentDate;

			editor.update();
			currentDate = System.currentTimeMillis();
			System.out.println(" update  took: " + (currentDate - previousDate));
			previousDate = currentDate;

			int mb = 1024 * 1024;

			// Getting the runtime reference from system
			Runtime runtime = Runtime.getRuntime();

			System.out.println("##### Heap utilization statistics [MB] #####");

			// Print used memory
			System.out.println("Used Memory:" + (runtime.totalMemory() - runtime.freeMemory()) / mb);

			// Print free memory
			System.out.println("Free Memory:" + runtime.freeMemory() / mb);

			// Print total available memory
			System.out.println("Total Memory:" + runtime.totalMemory() / mb);

			// Print Maximum available memory
			System.out.println("Max Memory:" + runtime.maxMemory() / mb);

		}
	}

	public static void initGUI() {
		gcDelegate = new SwingGraphicalContextDelegate(TestFIBOWLOntologyEditorOnArchimate.class.getSimpleName());
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
