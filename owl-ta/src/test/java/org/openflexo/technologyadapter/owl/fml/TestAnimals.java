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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openflexo.connie.exception.InvalidBindingException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.fml.CreationScheme;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.VirtualModelLibrary;
import org.openflexo.foundation.fml.rt.FMLRTVirtualModelInstance;
import org.openflexo.foundation.fml.rt.action.CreateBasicVirtualModelInstance;
import org.openflexo.foundation.fml.rt.rm.FMLRTVirtualModelInstanceResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.test.OpenflexoProjectAtRunTimeTestCase;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;
import org.openflexo.technologyadapter.owl.model.OWLClass;
import org.openflexo.technologyadapter.owl.rm.OWLOntologyResource;
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

	private static VirtualModel rootVM;

	private static FMLRTVirtualModelInstance vmi;
	private static OWLOntologyResource animalOntologyResource;

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
		rootVM = vpLib.getVirtualModel("http://www.openflexo.org/test/owl/Animals.fml");
		assertNotNull(rootVM);

		System.out.println(rootVM.getCompilationUnit().getFMLPrettyPrint());

		assertCompilationUnitIsValid(rootVM.getCompilationUnit());

		animalOntologyResource = (OWLOntologyResource) serviceManager.getResourceManager().getResource("http://openflexo.org/test/animals");
		assertNotNull(animalOntologyResource);

	}

	@Test
	@TestOrder(2)
	@Category(UITest.class)
	public void testCreateProject() {
		editor = createStandaloneProject("TestProject");
		project = (FlexoProject<File>) editor.getProject();
		System.out.println("Created project " + project.getProjectDirectory());
		assertTrue(project.getProjectDirectory().exists());
	}

	@Test
	@TestOrder(3)
	@Category(UITest.class)
	public void testCreateInstance() {

		System.err.println("testCreateInstance()");
		CreateBasicVirtualModelInstance action = CreateBasicVirtualModelInstance.actionType
				.makeNewAction(project.getVirtualModelInstanceRepository().getRootFolder(), null, editor);
		action.setNewVirtualModelInstanceName("AnimalsInstance");
		action.setNewVirtualModelInstanceTitle("Test animals");
		action.setVirtualModel(rootVM);

		CreationScheme creationScheme = rootVM.getCreationSchemes().get(0);
		action.setCreationScheme(creationScheme);

		action.setParameterValue(creationScheme.getParameters().get(0), animalOntologyResource);
		action.doAction();
		assertTrue(action.hasActionExecutionSucceeded());
		vmi = action.getNewVirtualModelInstance();
		assertNotNull(vmi);
		assertNotNull(vmi.getResource());
		assertTrue(ResourceLocator.retrieveResourceAsFile(((FMLRTVirtualModelInstanceResource) vmi.getResource()).getDirectory()).exists());
		assertTrue(((FMLRTVirtualModelInstanceResource) vmi.getResource()).getIODelegate().exists());

		System.err.println("View: " + vmi + " in " + vmi.getResource().getIODelegate());

	}

	@Test
	@TestOrder(4)
	@Category(UITest.class)
	public void testListAllOWLClasses() throws TypeMismatchException, NullReferenceException, ReflectiveOperationException,
			InvalidBindingException, FileNotFoundException, ResourceLoadingCancelledException, FlexoException {
		System.err.println("testListAllOWLClasses()");
		List<OWLClass> allClasses = vmi.execute("this.listAllOWLClasses()");
		assertEquals(30, allClasses.size());
	}

	@Test
	@TestOrder(5)
	@Category(UITest.class)
	public void listDeclaredOWLClasses() throws TypeMismatchException, NullReferenceException, ReflectiveOperationException,
			InvalidBindingException, FileNotFoundException, ResourceLoadingCancelledException, FlexoException {
		System.err.println("listDeclaredOWLClasses()");
		List<OWLClass> allClasses = vmi.execute("this.listDeclaredOWLClasses()");
		assertEquals(4, allClasses.size());
	}

	@Test
	@TestOrder(6)
	@Category(UITest.class)
	public void listDeclaredOWLClasses2() throws TypeMismatchException, NullReferenceException, ReflectiveOperationException,
			InvalidBindingException, FileNotFoundException, ResourceLoadingCancelledException, FlexoException {
		System.err.println("listDeclaredOWLClasses2()");
		List<OWLClass> allClasses = vmi.execute("this.listDeclaredOWLClasses2()");
		assertEquals(4, allClasses.size());
	}
}
