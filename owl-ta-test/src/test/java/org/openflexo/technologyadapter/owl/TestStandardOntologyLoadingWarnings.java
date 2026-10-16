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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.test.OpenflexoTestCase;
import org.openflexo.technologyadapter.owl.model.OWLClass;
import org.openflexo.technologyadapter.owl.model.OWLDataType;
import org.openflexo.technologyadapter.owl.model.OWLOntology;
import org.openflexo.technologyadapter.owl.model.OWLOntologyLibrary;
import org.openflexo.technologyadapter.owl.model.ObjectPropertyStatement;
import org.openflexo.technologyadapter.owl.model.RDFSURIDefinitions;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Warnings logged while the W3C standard ontologies (rdf, rdfs, owl) are loaded (OWL-D-1): an ontology referring to itself (or to another
 * known ontology) through <code>rdfs:isDefinedBy</code> is not a dangling reference, whereas a reference to an unknown resource still is.
 */
@RunWith(OrderedRunner.class)
public class TestStandardOntologyLoadingWarnings extends OpenflexoTestCase {

	private static OWLTechnologyAdapter owlAdapter;
	private static OWLOntologyLibrary ontologyLibrary;

	/** Captures the warnings logged by the model package */
	private static class Recorder extends Handler {
		final List<String> messages = new ArrayList<>();

		@Override
		public void publish(LogRecord record) {
			if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
				messages.add(record.getMessage());
			}
		}

		@Override
		public void flush() {
		}

		@Override
		public void close() {
		}
	}

	@Test
	@TestOrder(1)
	public void testInitialize() {
		Logger modelLogger = Logger.getLogger("org.openflexo.technologyadapter.owl.model");
		// OWLConcept uses the logger of the package of IFlexoOntologyConcept
		Logger conceptLogger = Logger.getLogger("org.openflexo.foundation.ontology");
		Recorder recorder = new Recorder();
		modelLogger.addHandler(recorder);
		conceptLogger.addHandler(recorder);
		try {
			instanciateTestServiceManager(OWLTechnologyAdapter.class);
			// Force the library to load the standard ontologies
			serviceManager.getTechnologyAdapterService().getTechnologyAdapter(OWLTechnologyAdapter.class).getOntologyLibrary();
		} finally {
			modelLogger.removeHandler(recorder);
			conceptLogger.removeHandler(recorder);
		}
		owlAdapter = serviceManager.getTechnologyAdapterService().getTechnologyAdapter(OWLTechnologyAdapter.class);
		ontologyLibrary = (OWLOntologyLibrary) serviceManager.getTechnologyAdapterService().getTechnologyContextManager(owlAdapter);
		List<String> unknownProperties = new ArrayList<>();
		for (String m : recorder.messages) {
			if (m.contains("unkwown property")) {
				unknownProperties.add(m);
			}
		}
		assertEquals("Standard ontologies bootstrap: " + unknownProperties, 0, unknownProperties.size());
		List<String> unmappedDataTypes = new ArrayList<>();
		for (String m : recorder.messages) {
			if (m.contains("Could not map a data type")) {
				unmappedDataTypes.add(m);
			}
		}
		assertEquals("Standard ontologies bootstrap: " + unmappedDataTypes, 0, unmappedDataTypes.size());
		assertNotNull(ontologyLibrary.getRDFSOntology());
	}

	private List<String> buildIsDefinedByStatement(OWLOntology ontology, String objectURI) {
		Logger modelLogger = Logger.getLogger("org.openflexo.technologyadapter.owl.model");
		Recorder recorder = new Recorder();
		modelLogger.addHandler(recorder);
		try {
			OWLClass subject = ontology.getClass(RDFSURIDefinitions.RDFS_CLASS_URI);
			if (subject == null) {
				subject = ontology.getClasses().get(0);
			}
			OntModel model = ontology.getOntModel();
			Property isDefinedBy = model.createProperty(RDFSURIDefinitions.RDFS_IS_DEFINED_BY_URI);
			Resource object = model.createResource(objectURI);
			Statement s = model.createStatement(model.createResource(subject.getURI()), isDefinedBy, object);
			new ObjectPropertyStatement(subject, s, owlAdapter);
		} finally {
			modelLogger.removeHandler(recorder);
		}
		return recorder.messages;
	}

	@Test
	@TestOrder(2)
	public void testOntologyReferringToItselfIsNotDangling() {
		for (OWLOntology o : new OWLOntology[] { ontologyLibrary.getRDFSOntology(), ontologyLibrary.getRDFOntology(),
				ontologyLibrary.getOWLOntology() }) {
			assertEquals("with '#' on " + o, 0, buildIsDefinedByStatement(o, o.getOntologyURI() + "#").size());
			assertEquals("without '#' on " + o, 0, buildIsDefinedByStatement(o, o.getOntologyURI()).size());
		}
	}

	@Test
	@TestOrder(3)
	public void testReferenceToAnotherKnownOntologyIsNotDangling() {
		assertEquals(0, buildIsDefinedByStatement(ontologyLibrary.getRDFSOntology(), ontologyLibrary.getOWLOntology().getOntologyURI() + "#")
				.size());
	}

	@Test
	@TestOrder(4)
	public void testDanglingReferenceStillWarns() {
		OWLOntology rdfs = ontologyLibrary.getRDFSOntology();
		assertEquals(1, buildIsDefinedByStatement(rdfs, "http://example.org/unknown#Foo").size());
		assertEquals(1, buildIsDefinedByStatement(rdfs, "http://example.org/unknown#").size());
	}

	@Test
	@TestOrder(5)
	public void testStatementsOfLoadedStandardOntologiesAreResolved() {
		Logger modelLogger = Logger.getLogger("org.openflexo.foundation.ontology");
		Recorder recorder = new Recorder();
		modelLogger.addHandler(recorder);
		try {
			for (OWLOntology o : new OWLOntology[] { ontologyLibrary.getRDFSOntology(), ontologyLibrary.getRDFOntology(),
					ontologyLibrary.getOWLOntology() }) {
				for (OWLClass c : o.getClasses()) {
					c.updateOntologyStatements();
				}
			}
		} finally {
			modelLogger.removeHandler(recorder);
		}
		assertEquals(recorder.messages.toString(), 0, recorder.messages.size());
	}

	@Test
	@TestOrder(6)
	public void testRdfsClassesAreNotDataTypes() {
		for (OWLDataType dt : ontologyLibrary.getDataTypes()) {
			assertFalse(dt.getURI() + " is not a data type", dt.getURI().startsWith("http://www.w3.org/2000/01/rdf-schema#"));
		}
	}

	@Test
	@TestOrder(7)
	public void testXMLSchemaDataTypesAreMapped() {
		Logger modelLogger = Logger.getLogger("org.openflexo.technologyadapter.owl.model");
		Recorder recorder = new Recorder();
		modelLogger.addHandler(recorder);
		try {
			String xsd = "http://www.w3.org/2001/XMLSchema#";
			String[] names = { "nonNegativeInteger", "positiveInteger", "anyURI", "date", "dateTime" };
			for (String name : names) {
				OWLDataType dt = ontologyLibrary.getDataType(xsd + name);
				assertNotNull(name, dt.getBuiltInDataType());
				assertEquals(xsd + name, dt.getBuiltInDataType().getURI());
			}
		} finally {
			modelLogger.removeHandler(recorder);
		}
		assertEquals(recorder.messages.toString(), 0, recorder.messages.size());
	}
}
