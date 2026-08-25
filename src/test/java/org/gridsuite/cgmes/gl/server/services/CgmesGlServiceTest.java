/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.cgmes.gl.server.services;

import com.powsybl.cases.datasource.CaseDataSourceClient;
import com.powsybl.cgmes.conformity.CgmesConformity1Catalog;
import com.powsybl.cgmes.model.GridModelReferenceResources;
import com.powsybl.iidm.network.Network;
import org.gridsuite.cgmes.gl.server.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * @author Chamseddine Benhamed <chamseddine.benhamed at rte-france.com>
 */
@RestClientTest(CgmesGlService.class)
class CgmesGlServiceTest {

    @Autowired
    private MockRestServiceServer geoDataServer;

    @Mock
    private CaseDataSourceClient caseServerDataSource; //halt database

    @MockitoSpyBean
    private CgmesGlService cgmesGlService;

    private static final UUID CASE_UUID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        GridModelReferenceResources gridModel =
            CgmesConformity1Catalog.microGridBaseCaseBE();

        when(caseServerDataSource.newInputStream(anyString())).then(delegatesTo(gridModel.dataSource()));
        when(caseServerDataSource.getBaseName()).then(delegatesTo(gridModel.dataSource()));
        when(caseServerDataSource.listNames(anyString())).then(delegatesTo(gridModel.dataSource()));
        when(caseServerDataSource.exists(anyString())).then(delegatesTo(gridModel.dataSource()));

        doReturn(caseServerDataSource).when(cgmesGlService).createCaseServerDataSource(CASE_UUID);
    }

    @AfterEach
    void tearDown() {
        geoDataServer.verify();
    }

    @Test
    void test() {
        Network network = cgmesGlService.getNetwork(CASE_UUID);
        assertNotNull(network);

        TestUtils.checkExtensions(network, new HashSet<>());

        geoDataServer.expect(requestTo(
                "http://localhost:8087/" +
                    CgmesGlConstants.GEO_DATA_API_VERSION +
                    "/substations"
            ))
            .andExpect(method(HttpMethod.POST))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andRespond(withSuccess());

        geoDataServer.expect(requestTo(
                "http://localhost:8087/" +
                    CgmesGlConstants.GEO_DATA_API_VERSION +
                    "/lines"
            ))
            .andExpect(method(HttpMethod.POST))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andRespond(withSuccess());

        cgmesGlService.toGeoDataServer(CASE_UUID, new HashSet<>());
    }
}