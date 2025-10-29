/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.cgmes.gl.server;

import org.gridsuite.cgmes.gl.server.services.CgmesGlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Chamseddine Benhamed <chamseddine.benhamed at rte-france.com>
 */
@WebMvcTest(CgmesGlController.class)
@ContextConfiguration(classes = {CgmesGlApplication.class})
class CgmesGlControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CgmesGlService cgmesGlService;

    @Test
    void test() throws Exception {
        final UUID caseUuid = UUID.randomUUID();
        mvc.perform(post("/" + CgmesGlController.API_VERSION + "/{caseUuid}/to-geo-data", caseUuid))
                .andExpect(status().isOk());
        verify(cgmesGlService).toGeoDataServer(caseUuid, Collections.emptySet());
    }
}
