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

package org.openflexo.technologyadapter.owl.model;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.apache.jena.graph.GraphMaker;
import org.apache.jena.graph.impl.SimpleGraphMaker;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ModelMaker;
import org.apache.jena.rdf.model.ModelReader;
import org.apache.jena.rdf.model.impl.ModelCom;
import org.apache.jena.shared.AlreadyExistsException;
import org.apache.jena.shared.DoesNotExistException;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.ontology.OntologyUtils;
import org.openflexo.foundation.ontology.technologyadapter.FlexoOntologyTechnologyContextManager;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterResource;
import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;
import org.openflexo.technologyadapter.owl.rm.OWLOntologyResource;
import org.openflexo.toolbox.JavaUtils;
import org.openflexo.toolbox.StringUtils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * The {@link OWLOntologyLibrary} works in conjunction with a {@link FlexoResourceCenterService}. It provides the mechanism to keep
 * reference to all {@link OWLOntology} known in the scope provided by the {@link FlexoResourceCenterService}
 * 
 * @author sylvain
 * 
 */
public class OWLOntologyLibrary extends FlexoOntologyTechnologyContextManager<OWLTechnologyAdapter>
		implements ModelMaker, RemovalListener<OWLOntology, Set<OWLOntology>> {

	private static final Logger logger = Logger.getLogger(OWLOntologyLibrary.class.getPackage().getName());

	private final SimpleGraphMaker graphMaker;

	private final Map<String, OWLOntologyResource> ontologies;
	private final Map<String, OWLDataType> dataTypes;

	protected Hashtable<OWLProperty, StatementWithProperty> statementsWithProperty;

	public StatementWithProperty getStatementWithProperty(OWLProperty aProperty) {
		if (statementsWithProperty.get(aProperty) != null) {
			return statementsWithProperty.get(aProperty);
		}
		StatementWithProperty returned = new StatementWithProperty(aProperty);
		statementsWithProperty.put(aProperty, returned);
		return returned;
	}

	public OWLOntologyLibrary(OWLTechnologyAdapter adapter, FlexoResourceCenterService resourceCenterService) {
		super(adapter, resourceCenterService);
		graphMaker = new SimpleGraphMaker();

		ontologies = new HashMap<>();
		dataTypes = new HashMap<>();

		statementsWithProperty = new Hashtable<>();

	}

	private boolean defaultOntologiesLoaded = false;

	public void init() {

		if (defaultOntologiesLoaded) {
			return;
		}

		logger.info("Instantiating OWLOntologyLibrary Done. Trying to load some ontologies...");

		logger.info("ontologies=" + ontologies);
		logger.info("serviceManager=" + getServiceManager());
		logger.info("rcService=" + getServiceManager().getResourceCenterService());
		logger.info("resources centers: " + getServiceManager().getResourceCenterService().getResourceCenters());

		logger.info("getRDFSOntology()=" + getRDFSOntology());
		logger.info("getRDFOntology()=" + getRDFOntology());
		logger.info("getOWLOntology()=" + getOWLOntology());
		// logger.info("getFlexoConceptOntology()=" +
		// getFlexoConceptOntology());

		FlexoResource<OWLOntology> rdfsOntologyResource = ontologies.get(RDFSURIDefinitions.RDFS_ONTOLOGY_URI);
		logger.info("rdfsOntologyResource=" + rdfsOntologyResource);

		if (getRDFSOntology() != null && getRDFOntology() != null && getOWLOntology() != null
		/* && getFlexoConceptOntology() != null */) {
			logger.info("Loading some ontologies...");
			getRDFSOntology().loadWhenUnloaded();
			getRDFOntology().loadWhenUnloaded();
			getOWLOntology().loadWhenUnloaded();
			// Because some ontologies have cross reference, we update again
			// concept and properties to setup cross references
			getRDFSOntology().updateConceptsAndProperties();
			getRDFOntology().updateConceptsAndProperties();
			getOWLOntology().updateConceptsAndProperties();
			// Because we have updated again, we have to clear the modification
			// stamp
			getRDFSOntology().clearIsModified();
			getRDFOntology().clearIsModified();
			getOWLOntology().clearIsModified();
			// getFlexoConceptOntology().loadWhenUnloaded();
			defaultOntologiesLoaded = true;
		}
	}

	public OWLDataType getDataType(String dataTypeURI) {
		OWLDataType returned = dataTypes.get(dataTypeURI);
		if (returned == null) {
			returned = new OWLDataType(dataTypeURI, getTechnologyAdapter());
			dataTypes.put(dataTypeURI, returned);
		}
		return returned;

	}

	public List<OWLDataType> getDataTypes() {
		ArrayList<OWLDataType> returned = new ArrayList<>();
		for (OWLDataType dt : dataTypes.values()) {
			returned.add(dt);
		}
		return returned;
	}

	public void registerOntology(OWLOntologyResource ontologyResource) {
		ontologies.put(ontologyResource.getURI(), ontologyResource);
		clearAllImportedOntologiesCache();
	}

	/**
	 * Return ontology with supplied URI, look in all repositories of all known resource centers
	 * 
	 * @param ontologyUri
	 * @return
	 */
	public OWLOntology getOntology(String ontologyURI) {
		/*
		 * for (FlexoResourceCenter rc :
		 * resourceCenterService.getResourceCenters()) { MetaModelRepository<?
		 * extends OWLOntologyResource, OWLOntology, OWLOntology,
		 * OWLTechnologyAdapter> mmRep = rc .getMetaModelRepository(adapter);
		 * OWLOntologyResource mmResource = mmRep.getResource(ontologyURI); if
		 * (mmResource != null) { return mmResource.getResourceData(); }
		 * ModelRepository<? extends OWLOntologyResource, OWLOntology,
		 * OWLOntology, OWLTechnologyAdapter> mRep = rc
		 * .getModelRepository(adapter); OWLOntologyResource mResource =
		 * mmRep.getResource(ontologyURI); if (mResource != null) { return
		 * mResource.getResourceData(); } }
		 * logger.warning("Not found ontology: " + ontologyURI); return null;
		 */
		FlexoResource<OWLOntology> ontologyResource = ontologies.get(ontologyURI);
		if (ontologyResource != null) {
			try {
				return ontologyResource.getResourceData();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (ResourceLoadingCancelledException e) {
				e.printStackTrace();
			} catch (FlexoException e) {
				e.printStackTrace();
			}
		}
		logger.warning("Not found ontology: " + ontologyURI);
		return null;
	}

	/*
	 * public OWLOntology getFlexoConceptOntology() { return
	 * getOntology(FLEXO_CONCEPT_ONTOLOGY_URI); }
	 */

	public OWLOntology getRDFOntology() {
		return getOntology(RDFURIDefinitions.RDF_ONTOLOGY_URI);
	}

	public OWLOntology getRDFSOntology() {
		return getOntology(RDFSURIDefinitions.RDFS_ONTOLOGY_URI);
	}

	public OWLOntology getOWLOntology() {
		return getOntology(OWL2URIDefinitions.OWL_ONTOLOGY_URI);
	}

	public Collection<OWLOntologyResource> getRegisteredOntologies() {
		return ontologies.values();
	}

	@Override
	public GraphMaker getGraphMaker() {
		return graphMaker;
	}

	@Override
	public void close() {
		getGraphMaker().close();
	}

	public Model openModel() {
		return new ModelCom(getGraphMaker().openGraph());
	}

	@Override
	public OntModel openModelIfPresent(String name) {
		return getGraphMaker().hasGraph(name) ? openModel(name) : null;
	}

	@Override
	public OntModel openModel(String name, boolean strict) {
		getGraphMaker().openGraph(name, strict);
		OWLOntology ont = getOntology(name);
		if (ont != null) {
			ont.loadWhenUnloaded();
			return ont.getOntModel();
		}
		if (!strict) {
			/*
			 * OWLMetaModel newOntology = new OWLMetaModel(name, null, this);
			 * newOntology.setOntModel(createFreshModel()); ontologies.put(name,
			 * newOntology); setChanged(); notifyObservers(new
			 * OntologyImported(newOntology)); return newOntology.getOntModel();
			 */
			logger.warning("Not implemented yet !!!");
			return null;
		}
		throw new DoesNotExistException(name);
	}

	@Override
	public OntModel openModel(String name) {
		return openModel(name, false);
	}

	@Override
	public OntModel createModel(String name, boolean strict) {
		getGraphMaker().createGraph(name, strict);
		OWLOntology ont = getOntology(name);
		if (ont != null) {
			if (strict) {
				throw new AlreadyExistsException(name);
			}
			return createDefaultModel();
		}
		/*
		 * OWLMetaModel newOntology = new OWLMetaModel(name, null, this);
		 * newOntology.setOntModel(createFreshModel()); ontologies.put(name,
		 * newOntology); setChanged(); notifyObservers(new
		 * OntologyImported(newOntology)); return newOntology.getOntModel();
		 */
		logger.warning("Not implemented yet !!!");
		return null;
	}

	@Override
	public OntModel createModel(String name) {
		return createModel(name, false);
	}

	public OntModel createModelOver(String name) {
		return createModel(name);
	}

	@Override
	public OntModel createFreshModel() {
		return ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, this, null);
	}

	@Override
	public OntModel createDefaultModel() {
		return ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, this, null);
	}

	@Override
	public void removeModel(String name) {
		getGraphMaker().removeGraph(name);
	}

	@Override
	public boolean hasModel(String name) {
		if (getOntology(name) != null) {
			return true;
		}
		return getGraphMaker().hasGraph(name);
	}

	@Override
	public ExtendedIterator<String> listModels() {
		return getGraphMaker().listGraphs();
	}

	/**
	 * ModelGetter implementation component.
	 */
	@Override
	public Model getModel(String URL) {
		return hasModel(URL) ? openModel(URL) : null;
	}

	@Override
	public Model getModel(String URL, ModelReader loadIfAbsent) {
		Model already = getModel(URL);
		return already == null ? loadIfAbsent.readModel(createModel(URL), URL) : already;
	}

	/**
	 * Return true if URI is well formed and valid regarding its unicity (no one other object has same URI)
	 * 
	 * @param uri
	 * @return
	 */
	public boolean testValidURI(String ontologyURI, String conceptURI) {
		if (StringUtils.isEmpty(conceptURI)) {
			return false;
		}
		if (StringUtils.isEmpty(conceptURI.trim())) {
			return false;
		}
		return conceptURI.equals(JavaUtils.getJavaName(conceptURI)) && !isDuplicatedURI(ontologyURI, conceptURI);
	}

	/**
	 * Return true if URI is duplicated
	 * 
	 * @param uri
	 * @return
	 */
	public boolean isDuplicatedURI(String ontologyURI, String conceptURI) {
		OWLOntology o = getOntology(ontologyURI);
		if (o != null) {
			return o.getOntologyObject(ontologyURI + "#" + conceptURI) != null;
		}
		return false;
	}

	@Override
	public void registerResource(TechnologyAdapterResource<?, OWLTechnologyAdapter> resource) {
		super.registerResource(resource);
		if (resource instanceof OWLOntologyResource) {
			registerOntology((OWLOntologyResource) resource);
		}
	}

	@Override
	public TechnologyAdapterResource<?, OWLTechnologyAdapter> getResourceWithURI(String uri) {
		return ontologies.get(uri);
	}

	private void clearAllImportedOntologiesCache() {
		// TODO: this might be optimized
		allImportedOntologiesMapCache.invalidateAll();
	}

	protected Set<OWLOntology> getAllImportedOntology(OWLOntology ontology) {
		try {
			return allImportedOntologiesMapCache.get(ontology);
		} catch (ExecutionException e) {
			e.printStackTrace();
			System.err.println("Caused by: ");
			e.getCause().printStackTrace();
			return null;
		}
	}

	/**
	 * This cache stores all imported ontologies of a related ontology (transitiviy)
	 */
	private final LoadingCache<OWLOntology, Set<OWLOntology>> allImportedOntologiesMapCache = CacheBuilder.newBuilder().maximumSize(10000)
			.expireAfterWrite(10, TimeUnit.MINUTES).removalListener(this).build(new CacheLoader<OWLOntology, Set<OWLOntology>>() {
				@Override
				public Set<OWLOntology> load(OWLOntology ontology) {
					return OntologyUtils.getAllImportedOntologies(ontology);
				}
			});

	@Override
	public void onRemoval(RemovalNotification<OWLOntology, Set<OWLOntology>> notification) {
	}

}
