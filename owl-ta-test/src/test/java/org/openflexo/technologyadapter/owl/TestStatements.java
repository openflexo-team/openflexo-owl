package org.openflexo.technologyadapter.owl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.apache.jena.base.Sys;
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
import org.openflexo.foundation.fml.rm.CompilationUnitResource;
import org.openflexo.foundation.fml.rt.FMLRTVirtualModelInstance;
import org.openflexo.foundation.fml.rt.action.CreateBasicVirtualModelInstance;
import org.openflexo.foundation.fml.rt.rm.FMLRTVirtualModelInstanceResource;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.test.OpenflexoProjectAtRunTimeTestCase;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.technologyadapter.owl.model.AnnotationStatement;
import org.openflexo.technologyadapter.owl.model.DataPropertyStatement;
import org.openflexo.technologyadapter.owl.model.ObjectPropertyStatement;
import org.openflexo.technologyadapter.owl.rm.OWLOntologyResource;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;
import org.openflexo.test.UITest;

@RunWith(OrderedRunner.class)
public class TestStatements extends OpenflexoProjectAtRunTimeTestCase {

    private static FlexoEditor editor;
    private static FlexoProject<File> project;

    private static VirtualModel rootVM;
    private static FMLRTVirtualModelInstance vmi;
    private static OWLOntologyResource ontologyResource;

    @Test
    @TestOrder(1)
    @Category(UITest.class)
    public void testLoadViewPoint() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {
        instanciateTestServiceManager(OWLTechnologyAdapter.class);

        OWLTechnologyAdapter owlTA = serviceManager.getTechnologyAdapterService().getTechnologyAdapter(OWLTechnologyAdapter.class);
        serviceManager.activateTechnologyAdapter(owlTA, true);

        FlexoResourceCenter<?> resourceCenter = serviceManager.getResourceCenterService()
                .getFlexoResourceCenter("http://openflexo.org/owl-test");
        assertNotNull(resourceCenter);

        for (FlexoResource<?> flexoResource : serviceManager.getResourceManager().getRegisteredResources()) {
            System.out.println("> " + flexoResource + " loaded: " + flexoResource.isLoaded() + " uri=" + flexoResource.getURI());
        }

        CompilationUnitResource cur = (CompilationUnitResource) serviceManager.getResourceManager()
                .getResource("http://www.openflexo.org/test/owl/Statements.fml");
        assertNotNull(cur);

        VirtualModelLibrary vpLib = serviceManager.getVirtualModelLibrary();
        assertNotNull(vpLib);

        rootVM = vpLib.getVirtualModel("http://www.openflexo.org/test/owl/Statements.fml");
        assertNotNull(rootVM);

        assertCompilationUnitIsValid(rootVM.getCompilationUnit());

        ontologyResource = (OWLOntologyResource) serviceManager.getResourceManager()
                .getResource("http://openflexo.org/test/statements");
        assertNotNull(ontologyResource);
    }

    @Test
    @TestOrder(2)
    @Category(UITest.class)
    @SuppressWarnings("unchecked")
    public void testCreateProject() {
        editor = createStandaloneProject("TestStatementsProject");
        project = (FlexoProject<File>) editor.getProject();
        assertTrue(project.getProjectDirectory().exists());
    }

    @Test
    @TestOrder(3)
    @Category(UITest.class)
    public void testCreateInstance() {
        CreateBasicVirtualModelInstance action = CreateBasicVirtualModelInstance.actionType
                .makeNewAction(project.getVirtualModelInstanceRepository().getRootFolder(), null, editor);
        action.setNewVirtualModelInstanceName("StatementsInstance");
        action.setNewVirtualModelInstanceTitle("Test statements");
        action.setVirtualModel(rootVM);

        CreationScheme creationScheme = rootVM.getCreationSchemes().get(0);
        action.setCreationScheme(creationScheme);
        action.setParameterValue(creationScheme.getParameters().get(0), ontologyResource);

        action.doAction();
        assertTrue(action.hasActionExecutionSucceeded());

        vmi = action.getNewVirtualModelInstance();
        assertNotNull(vmi);
        assertNotNull(vmi.getResource());
        assertTrue(ResourceLocator.retrieveResourceAsFile(((FMLRTVirtualModelInstanceResource) vmi.getResource()).getDirectory()).exists());
        assertTrue(((FMLRTVirtualModelInstanceResource) vmi.getResource()).getIODelegate().exists());
    }

    @Test
    @TestOrder(4)
    @Category(UITest.class)
    public void testAddDataPropertyStatement() throws TypeMismatchException, NullReferenceException,
            ReflectiveOperationException, InvalidBindingException, FileNotFoundException,
            ResourceLoadingCancelledException, FlexoException {
        DataPropertyStatement statement = vmi.execute("this.addDataPropertyStatement()");
        assertNotNull(statement);
        assertNotNull(statement.getProperty());
        assertNotNull(statement.getSubject());
        assertEquals("0.9", statement.getStringValue());
    }
    /*
    @Test
    @TestOrder(5)
    @Category(UITest.class)
    public void testSelectDataPropertyStatements() throws TypeMismatchException, NullReferenceException,
            ReflectiveOperationException, InvalidBindingException, FileNotFoundException,
            ResourceLoadingCancelledException, FlexoException {
        List<DataPropertyStatement> statements = vmi.execute("this.selectDataPropertyStatements()");
        assertNotNull(statements);
        assertTrue(statements.size() >= 1);
        assertEquals("0.9", statements.get(0).getStringValue());
    }
*/

    @Test
    @TestOrder(6)
    @Category(UITest.class)
    public void testAddObjectPropertyStatement() throws TypeMismatchException, NullReferenceException,
            ReflectiveOperationException, InvalidBindingException, FileNotFoundException,
            ResourceLoadingCancelledException, FlexoException {
        ObjectPropertyStatement statement = vmi.execute("this.addObjectPropertyStatement()");
        assertNotNull(statement);
        assertNotNull(statement.getProperty());
        assertNotNull(statement.getSubject());
        assertNotNull(statement.getStatementObject());
        assertEquals("i2", statement.getStatementObject().getName());
    }
/*
    @Test
    @TestOrder(7)
    @Category(UITest.class)
    public void testSelectObjectPropertyStatements() throws TypeMismatchException, NullReferenceException,
            ReflectiveOperationException, InvalidBindingException, FileNotFoundException,
            ResourceLoadingCancelledException, FlexoException {
        List<ObjectPropertyStatement> statements = vmi.execute("this.selectObjectPropertyStatements()");
        assertNotNull(statements);
        assertTrue(statements.size() >= 1);
        assertNotNull(statements.get(0).getStatementObject());
    }
*/
    @Test
    @TestOrder(8)
    @Category(UITest.class)
    public void testAddAnnotationOnConcept() throws TypeMismatchException, NullReferenceException,
            ReflectiveOperationException, InvalidBindingException, FileNotFoundException,
            ResourceLoadingCancelledException, FlexoException {
        AnnotationStatement statement = vmi.execute("this.addAnnotationOnConcept()");
        assertNotNull(statement);
        assertNotNull(statement.getProperty());
        assertNotNull(statement.getObjectSubject());
        assertEquals("SomeValue", statement.getValue());
    }

    @Test
    @TestOrder(9)
    @Category(UITest.class)
    public void testSelectAnnotationStatements() throws TypeMismatchException, NullReferenceException,
            ReflectiveOperationException, InvalidBindingException, FileNotFoundException,
            ResourceLoadingCancelledException, FlexoException {
        List<AnnotationStatement> statements = vmi.execute("this.selectAnnotationStatements()");
        assertNotNull(statements);
        assertTrue(statements.size() >= 1);
        assertEquals("SomeValue", statements.get(0).getValue());
    }

    @Test
    @TestOrder(10)
    @Category(UITest.class)
    public void testAddAnnotationOnStatement() throws TypeMismatchException, NullReferenceException,
            ReflectiveOperationException, InvalidBindingException, FileNotFoundException,
            ResourceLoadingCancelledException, FlexoException {
        AnnotationStatement statement = vmi.execute("this.addAnnotationOnStatement()");
        assertNotNull(statement);
        System.out.println(statement);
        assertNotNull(statement.getProperty());
        System.out.println(statement.getProperty());
        assertNotNull(statement.getObjectSubject());
        System.out.println(statement.getObjectSubject());
        assertTrue(statement.getObjectSubject() instanceof ObjectPropertyStatement
                || statement.getObjectSubject() instanceof DataPropertyStatement);
        assertEquals("0.9", statement.getValue());
    }

    @Test
    @TestOrder(11)
    @Category(UITest.class)
    public void testSelectAnnotationOnStatement() throws TypeMismatchException, NullReferenceException,
            ReflectiveOperationException, InvalidBindingException, FileNotFoundException,
            ResourceLoadingCancelledException, FlexoException {
        List<AnnotationStatement> statements = vmi.execute("this.selectAnnotationOnStatement()");
        assertNotNull(statements);
        assertTrue(!statements.isEmpty());
        System.out.println(statements);
        assertTrue(statements.get(0).getObjectSubject() instanceof ObjectPropertyStatement
                || statements.get(0).getObjectSubject() instanceof DataPropertyStatement);
    }
}