/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Cartoeditor, a component of the software infrastructure 
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

package org.openflexo.technologyadapter.owl.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.fml.ActionScheme;
import org.openflexo.foundation.fml.ElementImportDeclaration;
import org.openflexo.foundation.fml.FMLCompilationUnit;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.JavaImportDeclaration;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.action.DeclareNewVariableAction;
import org.openflexo.foundation.fml.parser.ParseException;
import org.openflexo.foundation.fml.parser.fmlnodes.ActionSchemeNode;
import org.openflexo.foundation.fml.parser.fmlnodes.ElementImportNode;
import org.openflexo.foundation.fml.parser.fmlnodes.FMLCompilationUnitNode;
import org.openflexo.foundation.fml.parser.fmlnodes.JavaImportNode;
import org.openflexo.foundation.fml.parser.fmlnodes.ModelSlotPropertyNode;
import org.openflexo.foundation.fml.parser.fmlnodes.VirtualModelNode;
import org.openflexo.foundation.fml.parser.fmlnodes.controlgraph.FMLEditionActionNode;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.test.parser.FMLParserTestCase;
import org.openflexo.p2pp.RawSource;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.technologyadapter.owl.OWLModelSlot;
import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;
import org.openflexo.technologyadapter.owl.fml.editionaction.AddOWLIndividual;
import org.openflexo.technologyadapter.owl.model.OWLOntology;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Parse a FML file, perform some edits and checks that pretty-print is correct
 * 
 * @author sylvain
 *
 */
@RunWith(OrderedRunner.class)
public class TestFMLPrettyPrint1 extends FMLParserTestCase {

	private static FMLCompilationUnit compilationUnit;
	private static VirtualModel virtualModel;
	private static FlexoConcept conceptA;

	static FlexoEditor editor;

	@Test
	@TestOrder(1)
	public void initServiceManager() throws /*ParseException,*/ ModelDefinitionException, IOException {
		instanciateTestServiceManager();

		editor = new DefaultFlexoEditor(null, serviceManager);
		assertNotNull(editor);

	}

	private static FMLCompilationUnitNode rootNode;
	private static VirtualModelNode vmNode;

	private static JavaImportDeclaration owlOntologyImport;

	private static ElementImportDeclaration animalsImport;
	private static ElementImportDeclaration mouseImport;
	private static OWLModelSlot owlModelSlot;
	private static ActionScheme actionScheme;
	private static AddOWLIndividual addIndividual;

	private static JavaImportNode owlOntologyImportNode;
	private static ElementImportNode animalsImportNode;
	private static ElementImportNode mouseImportNode;
	private static ModelSlotPropertyNode<?, ?> modelSlotNode;
	private static ActionSchemeNode actionSchemeNode;
	private static FMLEditionActionNode addIndividualNode;

	private static OWLOntology owlOntology;

	@Test
	@TestOrder(2)
	public void loadInitialVersion()
			throws ParseException, ModelDefinitionException, IOException, ResourceLoadingCancelledException, FlexoException {
		instanciateTestServiceManager(OWLTechnologyAdapter.class);

		// We force the loading of the resource because we bypass here resource management
		FlexoResource<?> ontologyResource = serviceManager.getResourceManager().getResource("http://openflexo.org/test/animals");
		System.out.println("ontologyResource:" + ontologyResource);
		ontologyResource.loadResourceData();

		log("Initial version");

		final Resource fmlFile = ResourceLocator.locateResource("TestFMLPrettyPrint1/InitialVersion.fml");
		compilationUnit = parseFile(fmlFile);
		assertNotNull(virtualModel = compilationUnit.getVirtualModel());
		assertEquals("MyModel", virtualModel.getName());

		assertNotNull(rootNode = (FMLCompilationUnitNode) compilationUnit.getPrettyPrintDelegate());
		assertNotNull(vmNode = (VirtualModelNode) rootNode.getObjectNode(virtualModel));

		assertNotNull(owlOntologyImport = compilationUnit.getJavaImports().get(0));

		assertNotNull(animalsImport = compilationUnit.getElementImports().get(0));
		assertNotNull(owlOntology = (OWLOntology) animalsImport.getReferencedObject());

		assertNotNull(mouseImport = compilationUnit.getElementImports().get(1));

		assertNotNull(owlModelSlot = (OWLModelSlot) virtualModel.getAccessibleProperty("ontology"));

		assertNotNull(actionScheme = (ActionScheme) virtualModel.getDeclaredFlexoBehaviours().get(0));
		assertNotNull(addIndividual = (AddOWLIndividual) actionScheme.getControlGraph());

		System.out.println("FML=\n" + compilationUnit.getFMLPrettyPrint());

		testNormalizedFMLRepresentationEquals(compilationUnit, "TestFMLPrettyPrint1/Step1Normalized.fml");
		testFMLPrettyPrintEquals(compilationUnit, "TestFMLPrettyPrint1/Step1PrettyPrint.fml");

		RawSource rawSource = rootNode.getRawSource();
		System.out.println(rawSource.debug());
		debug(rootNode, 0);

		assertNotNull(owlOntologyImportNode = (JavaImportNode) rootNode.getObjectNode(owlOntologyImport));
		assertNotNull(animalsImportNode = (ElementImportNode) rootNode.getObjectNode(animalsImport));
		assertNotNull(mouseImportNode = (ElementImportNode) rootNode.getObjectNode(mouseImport));
		assertNotNull(modelSlotNode = (ModelSlotPropertyNode<?, ?>) vmNode.getObjectNode(owlModelSlot));
		assertNotNull(actionSchemeNode = (ActionSchemeNode) vmNode.getObjectNode(actionScheme));
		assertNotNull(addIndividualNode = (FMLEditionActionNode) vmNode.getObjectNode(addIndividual));

		assertEquals("(3:0)-(3:61)", owlOntologyImportNode.getLastParsedFragment().toString());
		assertEquals(null, owlOntologyImportNode.getPrelude());
		assertEquals("(3:61)-(5:0)", owlOntologyImportNode.getPostlude().toString());

		assertEquals("(5:0)-(5:56)", animalsImportNode.getLastParsedFragment().toString());
		assertEquals(null, animalsImportNode.getPrelude());
		assertEquals("(5:56)-(6:0)", animalsImportNode.getPostlude().toString());

		assertEquals("(6:0)-(6:34)", mouseImportNode.getLastParsedFragment().toString());
		assertEquals(null, mouseImportNode.getPrelude());
		assertEquals("(6:34)-(8:0)", mouseImportNode.getPostlude().toString());

		assertEquals("(10:1)-(10:66)", modelSlotNode.getLastParsedFragment().toString());
		assertEquals("(10:0)-(10:1)", modelSlotNode.getPrelude().toString());
		assertEquals("(10:66)-(11:0)", modelSlotNode.getPostlude().toString());

		assertEquals("(12:1)-(14:2)", actionSchemeNode.getLastParsedFragment().toString());
		assertEquals("(11:0)-(12:0)", actionSchemeNode.getPrelude().toString());
		assertEquals("(14:2)-(15:0)", actionSchemeNode.getPostlude().toString());

		assertEquals("(13:2)-(13:87)", addIndividualNode.getLastParsedFragment().toString());
		assertEquals(null, addIndividualNode.getPrelude());
		assertEquals(null, addIndividualNode.getPostlude());

	}

	@Test
	@TestOrder(3)
	public void declareNewVariableAction() {

		log("DeclareNewVariableAction");

		DeclareNewVariableAction action = DeclareNewVariableAction.actionType.makeNewAction(addIndividual, null, editor);
		action.setNewVariableName("foo");
		action.doAction();

		ActionSchemeNode actionSchemeNode2 = (ActionSchemeNode) vmNode.getObjectNode(actionScheme);
		FMLEditionActionNode addIndividualNode2 = (FMLEditionActionNode) vmNode.getObjectNode(addIndividual);
		assertSame(actionSchemeNode2, actionSchemeNode);
		assertSame(addIndividualNode2, addIndividualNode);

		// actionSchemeNode.getChildren().remove(addIndividualNode);

		System.out.println("FML: " + compilationUnit.getFMLPrettyPrint());
		System.out.println("Normalized: " + compilationUnit.getNormalizedFML());

		System.out.println("Et sinon: " + compilationUnit.getPrettyPrintDelegate()
				.getRepresentation(compilationUnit.getPrettyPrintDelegate().makePrettyPrintContext()));

		// testNormalizedFMLRepresentationEquals(compilationUnit, "TestFMLPrettyPrint1/Step2Normalized.fml");
		testFMLPrettyPrintEquals(compilationUnit, "TestFMLPrettyPrint1/Step2PrettyPrint.fml");

		// RawSource rawSource = rootNode.getRawSource();
		// System.out.println(rawSource.debug());
		debug(rootNode, 0);

		/*JavaImportDeclaration listDeclaration = compilationUnit.getJavaImports().get(1);
		listDeclaration.setFullQualifiedClassName("java.util.ArrayList");
		System.out.println("FML=\n" + compilationUnit.getFMLPrettyPrint());
		testNormalizedFMLRepresentationEquals(compilationUnit, "TestFMLPrettyPrint1/Step2Normalized.fml");
		testFMLPrettyPrintEquals(compilationUnit, "TestFMLPrettyPrint1/Step2PrettyPrint.fml");
		
		assertEquals("(5:0)-(5:24)", stringImportNode.getLastParsedFragment().toString());
		assertEquals(null, stringImportNode.getPrelude());
		assertEquals("(5:24)-(6:0)", stringImportNode.getPostlude().toString());
		
		assertEquals("(6:0)-(6:22)", listImportNode.getLastParsedFragment().toString());
		assertEquals(null, listImportNode.getPrelude());
		assertEquals("(6:22)-(8:0)", listImportNode.getPostlude().toString());
		
		assertEquals("(11:1)-(11:7)", iPropertyNode.getLastParsedFragment().toString());
		assertEquals("(11:0)-(11:1)", iPropertyNode.getPrelude().toString());
		assertEquals("(11:32)-(12:0)", iPropertyNode.getPostlude().toString());
		
		assertEquals("(12:1)-(12:20)", fooPropertyNode.getLastParsedFragment().toString());
		assertEquals("(12:0)-(12:1)", fooPropertyNode.getPrelude().toString());
		assertEquals("(12:20)-(13:0)", fooPropertyNode.getPostlude().toString());*/

	}

	/*@Test
	@TestOrder(4)
	public void addStringProperty() {
	
		log("Add String property");
	
		CreatePrimitiveRole createStringProperty = CreatePrimitiveRole.actionType.makeNewAction(virtualModel, null, editor);
		createStringProperty.setRoleName("newString");
		createStringProperty.setPrimitiveType(PrimitiveType.String);
		createStringProperty.doAction();
		System.out.println("FML=\n" + compilationUnit.getFMLPrettyPrint());
		testNormalizedFMLRepresentationEquals(compilationUnit, "TestFMLPrettyPrint1/Step3Normalized.fml");
		testFMLPrettyPrintEquals(compilationUnit, "TestFMLPrettyPrint1/Step3PrettyPrint.fml");
	
		assertNotNull(stringProperty = virtualModel.getAccessibleProperty("newString"));
		assertNotNull(stringPropertyNode = (FlexoPropertyNode<?, ?>) vmNode.getObjectNode(stringProperty));
	
		debug(rootNode, 0);
	
		assertEquals("(11:1)-(11:7)", iPropertyNode.getLastParsedFragment().toString());
		assertEquals("(11:0)-(11:1)", iPropertyNode.getPrelude().toString());
		assertEquals("(11:32)-(12:0)", iPropertyNode.getPostlude().toString());
	
		assertEquals("(12:1)-(12:20)", fooPropertyNode.getLastParsedFragment().toString());
		assertEquals("(12:0)-(12:1)", fooPropertyNode.getPrelude().toString());
		assertEquals("(12:20)-(13:0)", fooPropertyNode.getPostlude().toString());
	}
	
	@Test
	@TestOrder(5)
	public void addDateProperty() {
	
		log("Add Date property");
	
		debug(rootNode, 0);
	
		CreatePrimitiveRole createDateProperty = CreatePrimitiveRole.actionType.makeNewAction(virtualModel, null, editor);
		createDateProperty.setRoleName("newDate");
		createDateProperty.setPrimitiveType(PrimitiveType.Date);
		createDateProperty.doAction();
		System.out.println("FML=\n" + compilationUnit.getFMLPrettyPrint());
		testNormalizedFMLRepresentationEquals(compilationUnit, "TestFMLPrettyPrint1/Step4Normalized.fml");
		testFMLPrettyPrintEquals(compilationUnit, "TestFMLPrettyPrint1/Step4PrettyPrint.fml");
	}
	
	@Test
	@TestOrder(6)
	public void removeFooProperty() {
	
		log("Remove Foo property");
	
		System.out.println("FML=\n" + compilationUnit.getFMLPrettyPrint());
	
		FlexoProperty<?> fooProperty = virtualModel.getAccessibleProperty("foo");
		virtualModel.removeFromFlexoProperties(fooProperty);
		fooProperty.delete();
	
		System.out.println("FML=\n" + compilationUnit.getFMLPrettyPrint());
	
		RawSource rawSource = rootNode.getRawSource();
		System.out.println(rawSource.debug());
		debug(rootNode, 0);
	
		testNormalizedFMLRepresentationEquals(compilationUnit, "TestFMLPrettyPrint1/Step5Normalized.fml");
		testFMLPrettyPrintEquals(compilationUnit, "TestFMLPrettyPrint1/Step5PrettyPrint.fml");
	
	}
	
	@Test
	@TestOrder(7)
	public void removeDateProperty() {
	
		log("Remove Date property");
	
		FlexoProperty<?> dateProperty = virtualModel.getAccessibleProperty("newDate");
		virtualModel.removeFromFlexoProperties(dateProperty);
		dateProperty.delete();
	
		System.out.println("FML=\n" + compilationUnit.getFMLPrettyPrint());
		testNormalizedFMLRepresentationEquals(compilationUnit, "TestFMLPrettyPrint1/Step6Normalized.fml");
		testFMLPrettyPrintEquals(compilationUnit, "TestFMLPrettyPrint1/Step6PrettyPrint.fml");
	}
	*/
}
