/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Flexodiagram, a component of the software infrastructure 
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

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.VirtualModelLibrary;
import org.openflexo.foundation.fml.rt.FMLRTVirtualModelInstance;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.test.OpenflexoProjectAtRunTimeTestCase;
import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;
import org.openflexo.technologyadapter.owl.model.OWLOntology;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;
import org.openflexo.test.UITest;

/**
 * Test the instantiation of a VirtualModel whose instances have {@link FMLControlledDiagramVirtualModelNature}
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class TestAnimals extends OpenflexoProjectAtRunTimeTestCase {

	private static FlexoEditor editor;
	private static FlexoProject<File> project;

	private static VirtualModel viewPoint;
	// private static VirtualModel virtualModel;
	// private static FlexoConcept flexoConcept;

	private static FMLRTVirtualModelInstance newView;
	// private static FMLRTVirtualModelInstance newVirtualModelInstance;
	private static OWLOntology animalOntology;

	/**
	 * Retrieve the ViewPoint
	 * 
	 * @throws FlexoException
	 * @throws ResourceLoadingCancelledException
	 * @throws FileNotFoundException
	 */
	@Test
	@TestOrder(1)
	@Category(UITest.class)
	public void testLoadViewPoint() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {
		instanciateTestServiceManager(OWLTechnologyAdapter.class);

		OWLTechnologyAdapter owlTA = serviceManager.getTechnologyAdapterService().getTechnologyAdapter(OWLTechnologyAdapter.class);
		serviceManager.activateTechnologyAdapter(owlTA, true);

		FlexoResourceCenter<?> resourceCenter = serviceManager.getResourceCenterService()
				.getFlexoResourceCenter("http://openflexo.org/owl-test");

		System.out.println("owlTA=" + owlTA);
		System.out.println("resourceCenter=" + resourceCenter);

		VirtualModelLibrary vpLib = serviceManager.getVirtualModelLibrary();
		assertNotNull(vpLib);
		viewPoint = vpLib.getVirtualModel("http://www.openflexo.org/test/owl/Animals.fml");
		assertNotNull(viewPoint);

		System.out.println(viewPoint.getCompilationUnit().getFMLPrettyPrint());

		assertCompilationUnitIsValid(viewPoint.getCompilationUnit());

		/*virtualModel = viewPoint.getVirtualModelNamed("TestVirtualModel");
		assertNotNull(virtualModel);
		assertTrue(virtualModel.hasNature(FMLControlledDiagramVirtualModelNature.INSTANCE));
		
		flexoConcept = virtualModel.getFlexoConcepts().get(0);
		assertNotNull(flexoConcept);
		
		dropScheme = (DropScheme) flexoConcept.getFlexoBehaviours().get(0);
		assertNotNull(dropScheme);*/

		/*DiagramSpecificationRepository<?> repository = diagramTA.getDiagramSpecificationRepository(resourceCenter);
		DiagramSpecificationResource diagramSpecificationResource = repository
				.getResource("http://openflexo.org/test/TestDiagramSpecification");
		assertNotNull(diagramSpecificationResource);
		assertNotNull(diagramSpecification = diagramSpecificationResource.getDiagramSpecification());
		assertEquals(1, diagramSpecificationResource.getDiagramSpecification().getPalettes().size());
		assertNotNull(palette = diagramSpecificationResource.getDiagramSpecification().getPalettes().get(0));
		assertEquals(2, palette.getElements().size());
		assertNotNull(paletteElement = palette.getElements().get(0));*/

	}

	/*@Test
	@TestOrder(2)
	@Category(UITest.class)
	public void testCreateProject() {
		editor = createStandaloneProject("TestProject");
		project = (FlexoProject<File>) editor.getProject();
		System.out.println("Created project " + project.getProjectDirectory());
		assertTrue(project.getProjectDirectory().exists());
	}*/

	/**
	 * Instantiate in project a View conform to the ViewPoint
	 */
	/*	@Test
		@TestOrder(3)
		@Category(UITest.class)
		public void testCreateView() {
	
			CreateBasicVirtualModelInstance action = CreateBasicVirtualModelInstance.actionType
					.makeNewAction(project.getVirtualModelInstanceRepository().getRootFolder(), null, editor);
			action.setNewVirtualModelInstanceName("MyView");
			action.setNewVirtualModelInstanceTitle("Test creation of a new view");
			action.setVirtualModel(viewPoint);
			action.doAction();
			assertTrue(action.hasActionExecutionSucceeded());
			newView = action.getNewVirtualModelInstance();
			assertNotNull(newView);
			assertNotNull(newView.getResource());
			assertTrue(ResourceLocator.retrieveResourceAsFile(((FMLRTVirtualModelInstanceResource) newView.getResource()).getDirectory())
					.exists());
			assertTrue(((FMLRTVirtualModelInstanceResource) newView.getResource()).getIODelegate().exists());
	
		}*/

	/**
	 * Instantiate in project a FMLRTVirtualModelInstance conform to the VirtualModel
	 */
	/*	@Test
		@TestOrder(4)
		@Category(UITest.class)
		public void testCreateVirtualModelInstance() {
	
			log("testCreateVirtualModelInstance()");
	
			assertEquals(1, virtualModel.getModelSlots().size());
	
			assertEquals(1, virtualModel.getModelSlots(TypedDiagramModelSlot.class).size());
			TypedDiagramModelSlot ms = virtualModel.getModelSlots(TypedDiagramModelSlot.class).get(0);
			assertNotNull(ms);
	
			assertTrue(virtualModel.getModelSlots().contains(ms));
	
			CreateBasicVirtualModelInstance action = CreateBasicVirtualModelInstance.actionType.makeNewAction(newView, null, editor);
			action.setNewVirtualModelInstanceName("MyVirtualModelInstance");
			action.setNewVirtualModelInstanceTitle("Test creation of a new FMLRTVirtualModelInstance");
			action.setVirtualModel(virtualModel);
			action.setCreationScheme(virtualModel.getCreationSchemes().get(0));
	
			action.doAction();
	
			if (!action.hasActionExecutionSucceeded()) {
				fail(action.getThrownException().getMessage());
			}
	
			assertTrue(action.hasActionExecutionSucceeded());
			newVirtualModelInstance = action.getNewVirtualModelInstance();
			assertNotNull(newVirtualModelInstance);
			assertNotNull(newVirtualModelInstance.getResource());
			assertTrue(ResourceLocator.retrieveResourceAsFile(((FMLRTVirtualModelInstanceResource) newView.getResource()).getDirectory())
					.exists());
			assertTrue(((FMLRTVirtualModelInstanceResource) newView.getResource()).getIODelegate().exists());
			assertEquals(1, newVirtualModelInstance.getModelSlotInstances().size());
	
			TypeAwareModelSlotInstance<Diagram, DiagramSpecification, TypedDiagramModelSlot> diagramMSInstance = (TypeAwareModelSlotInstance<Diagram, DiagramSpecification, TypedDiagramModelSlot>) newVirtualModelInstance
					.getModelSlotInstances().get(0);
			assertNotNull(diagramMSInstance);
			assertNotNull(diagram = diagramMSInstance.getAccessedResourceData());
			assertNotNull(diagramMSInstance.getResource());
			assertTrue(((DiagramResource) diagramMSInstance.getResource()).getIODelegate().exists());
	
			assertTrue(newVirtualModelInstance.hasNature(FMLControlledDiagramVirtualModelInstanceNature.INSTANCE));
	
			assertNotNull(FMLControlledDiagramVirtualModelInstanceNature.getModelSlotInstance(newVirtualModelInstance));
			assertNotNull(FMLControlledDiagramVirtualModelInstanceNature.getModelSlotInstance(newVirtualModelInstance).getModelSlot());
	
			assertFalse(diagram.isModified());
			assertFalse(newVirtualModelInstance.isModified());
	
		}*/

	/**
	 * Try to populate FMLRTVirtualModelInstance
	 * 
	 * @throws SaveResourceException
	 */
	/*	@Test
		@TestOrder(5)
		@Category(UITest.class)
		public void testPopulateVirtualModelInstance() throws SaveResourceException {
	
			log("testPopulateVirtualModelInstance()");
	
			FMLRTVirtualModelInstanceResource vmiRes = (FMLRTVirtualModelInstanceResource) newVirtualModelInstance.getResource();
	
			assertFalse(diagram.isModified());
			assertFalse(newVirtualModelInstance.isModified());
	
			System.out.println(vmiRes.getFactory().stringRepresentation(vmiRes.getLoadedResourceData()));
	
			System.out.println("Avant le drop");
			for (FlexoResource<?> unsaved : serviceManager.getResourceManager().getUnsavedResources()) {
				System.out.println(" unsaved : " + unsaved);
			}
	
			DropSchemeAction action = new DropSchemeAction(dropScheme, newVirtualModelInstance, null, editor);
			action.setDropLocation(new DianaPoint(100, 100));
	
			action.doAction();
			assertTrue(action.hasActionExecutionSucceeded());
	
			System.out.println("Apres le drop");
			for (FlexoResource<?> unsaved : serviceManager.getResourceManager().getUnsavedResources()) {
				System.out.println(" unsaved : " + unsaved);
			}
	
			System.out.println(vmiRes.getFactory().stringRepresentation(vmiRes.getLoadedResourceData()));
	
			assertTrue(diagram.isModified());
			assertTrue(newVirtualModelInstance.isModified());
	
			assertTrue(diagram.isModified());
			assertTrue(newVirtualModelInstance.isModified());
	
			System.out.println("-----------> Modified resources");
			for (FlexoResource<?> r : serviceManager.getResourceManager().getUnsavedResources()) {
				System.out.println(" > " + r);
			}
	
			// TODO: check this
			// If we uncomment this, virtual model also flagged as saved on Jenkins environement
			// Because of jars ???
			// assertEquals(2, serviceManager.getResourceManager().getUnsavedResources().size());
			// assertTrue(serviceManager.getResourceManager().getUnsavedResources().contains(virtualModel.getResource()));
	
			assertTrue(serviceManager.getResourceManager().getUnsavedResources().contains(newVirtualModelInstance.getResource()));
			assertTrue(serviceManager.getResourceManager().getUnsavedResources().contains(diagram.getResource()));
	
			newVirtualModelInstance.getResource().save();
			assertTrue(((FMLRTVirtualModelInstanceResource) newVirtualModelInstance.getResource()).getIODelegate().exists());
			assertFalse(newVirtualModelInstance.isModified());
	
			diagram.getResource().save();
			assertTrue(((DiagramResource) diagram.getResource()).getIODelegate().exists());
			assertFalse(diagram.isModified());
	
			// assertEquals(0, serviceManager.getResourceManager().getUnsavedResources().size());
	
			// Cf Above
			// virtualModel.getResource().save(null);
			// assertEquals(0,
			// serviceManager.getResourceManager().getUnsavedResources().size());
		}*/

	/**
	 * Instantiate in project a FMLRTVirtualModelInstance conform to the VirtualModel
	 * 
	 * @throws FlexoException
	 * @throws ResourceLoadingCancelledException
	 * @throws FileNotFoundException
	 */
	/*@Test
	@TestOrder(6)
	@Category(UITest.class)
	public void testReloadProject() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {
	
		log("testReloadProject()");
	
		String oldViewURI = newView.getURI();
	
		// instanciateTestServiceManager(DiagramTechnologyAdapter.class);
		editor = reloadProject(project);
		project = (FlexoProject<File>) editor.getProject();
		assertNotNull(editor);
		assertNotNull(project);
	
		System.out.println("All resources=" + project.getAllResources());
		assertEquals(4, project.getAllResources().size());
		assertNotNull(project.getResource(oldViewURI));
	
		FMLRTVirtualModelInstanceResource newViewResource = project.getVirtualModelInstanceRepository().getVirtualModelInstance(oldViewURI);
		assertNotNull(newViewResource);
		newViewResource.loadResourceData();
		assertNotNull(newView = newViewResource.getVirtualModelInstance());
	
		assertEquals(1, newViewResource.getVirtualModelInstanceResources().size());
		FMLRTVirtualModelInstanceResource vmiResource = newViewResource.getVirtualModelInstanceResources().get(0);
		assertNotNull(vmiResource);
		assertNull(vmiResource.getLoadedResourceData());
		vmiResource.loadResourceData();
		assertNotNull(newVirtualModelInstance = vmiResource.getVirtualModelInstance());
	
		assertEquals(1, newVirtualModelInstance.getFlexoConceptInstances().size());
		FlexoConceptInstance fci = newVirtualModelInstance.getFlexoConceptInstances().get(0);
		assertNotNull(fci);
	
		assertTrue(newVirtualModelInstance.hasNature(FMLControlledDiagramVirtualModelInstanceNature.INSTANCE));
		assertNotNull(FMLControlledDiagramVirtualModelInstanceNature.getModelSlotInstance(newVirtualModelInstance));
		assertNotNull(FMLControlledDiagramVirtualModelInstanceNature.getModelSlotInstance(newVirtualModelInstance).getModelSlot());
	
		assertEquals(1, fci.getActors().size());
	
		ModelObjectActorReference<DiagramShape> actorReference = (ModelObjectActorReference<DiagramShape>) fci.getActors().get(0);
		assertNotNull(actorReference);
		assertNotNull(actorReference.getModellingElement());
	
	}*/
}
