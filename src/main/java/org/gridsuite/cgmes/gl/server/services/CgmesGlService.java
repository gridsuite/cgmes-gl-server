/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.cgmes.gl.server.services;

import com.powsybl.cases.datasource.CaseDataSourceClient;
import com.powsybl.cgmes.conversion.CgmesImport;
import org.gridsuite.cgmes.gl.server.dto.LineGeoData;
import org.gridsuite.cgmes.gl.server.dto.SubstationGeoData;
import com.powsybl.iidm.network.*;
import com.powsybl.iidm.network.extensions.LinePosition;
import com.powsybl.iidm.network.extensions.SubstationPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Chamseddine Benhamed <chamseddine.benhamed at rte-france.com>
 */
@Service
public class CgmesGlService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CgmesGlService.class);

    private RestTemplate geoDataServerRest;
    private String geoDataServerBaseUri;

    private String caseServerBaseUri;

    public CgmesGlService(@Value("${geo-data-server.base.url:http://geo-data-server/}") String geoDataServerBaseUri,
                          @Value("${case-server.base.url:http://case-server/}") String caseServerBaseUri,
                          RestTemplateBuilder restTemplateBuilder) {
        this.geoDataServerBaseUri = Objects.requireNonNull(geoDataServerBaseUri);
        this.geoDataServerRest = restTemplateBuilder.uriTemplateHandler(new DefaultUriBuilderFactory(geoDataServerBaseUri)).build();

        this.caseServerBaseUri = Objects.requireNonNull(caseServerBaseUri);
    }

    public void toGeoDataServer(UUID caseUuid, Set<Country> countries) {
        Network network = getNetwork(caseUuid);

        List<SubstationPosition> substationPositions = network.getSubstationStream()
                .map(s -> (SubstationPosition) s.getExtension(SubstationPosition.class))
                .filter(Objects::nonNull)
                .filter(s -> countries.isEmpty() || countries.contains(s.getExtendable().getCountry().orElse(null)))
                .collect(Collectors.toList());

        List<LinePosition<Line>> linePositions = new ArrayList<>();
        Country country1;
        Country country2;
        for (Line line : network.getLines()) {
            LinePosition<Line> linePosition = line.getExtension(LinePosition.class);
            country1 = line.getTerminal1().getVoltageLevel().getSubstation().flatMap(Substation::getCountry).orElse(null);
            country2 = line.getTerminal2().getVoltageLevel().getSubstation().flatMap(Substation::getCountry).orElse(null);
            if (linePosition != null && (countries.isEmpty() || countries.contains(country1) || countries.contains(country2))) {
                linePositions.add(linePosition);
            }
        }

        List<LineGeoData> lines = linePositions.stream().map(LineGeoData::fromLinePosition).collect(Collectors.toList());
        List<SubstationGeoData> substations = substationPositions.stream().map(SubstationGeoData::fromSubstationPosition).collect(Collectors.toList());
        saveData(substations, lines);
        LOGGER.info("{} lines and {} substations are sent to the geodata server", lines.size(), substations.size());
    }

    Network getNetwork(UUID caseUuid) {
        CaseDataSourceClient caseServerDataSource = createCaseServerDataSource(caseUuid);
        CgmesImport importer = new CgmesImport();
        Properties properties = new Properties();
        properties.put("iidm.import.cgmes.post-processors", "cgmesGLImport");
        return importer.importData(caseServerDataSource, NetworkFactory.findDefault(), properties);
    }

    CaseDataSourceClient createCaseServerDataSource(UUID caseUuid) {
        return new CaseDataSourceClient(caseServerBaseUri, caseUuid);
    }

    private void saveData(List<SubstationGeoData> substationsGeoData, List<LineGeoData> linesGeoData) {
        // send geographic data to geo data server
        pushSubstations(substationsGeoData);
        pushLines(linesGeoData);
    }

    private void pushSubstations(List<SubstationGeoData> substationsGeoData) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(geoDataServerBaseUri + "/" + CgmesGlConstants.GEO_DATA_API_VERSION + "/substations");

        HttpEntity<List<SubstationGeoData>> requestEntity = new HttpEntity<>(substationsGeoData, requestHeaders);

        geoDataServerRest.exchange(uriBuilder.toUriString(),
                HttpMethod.POST,
                requestEntity,
                Void.class);
    }

    private void pushLines(List<LineGeoData> linesGeoData) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(geoDataServerBaseUri + "/" + CgmesGlConstants.GEO_DATA_API_VERSION + "/lines");

        HttpEntity<List<LineGeoData>> requestEntity = new HttpEntity<>(linesGeoData, requestHeaders);

        geoDataServerRest.exchange(uriBuilder.toUriString(),
                HttpMethod.POST,
                requestEntity,
                Void.class);
    }
}
