/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.cgmes.gl.server;

import org.gridsuite.cgmes.gl.server.services.CgmesGlService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Chamseddine Benhamed <chamseddine.benhamed at rte-france.com>
 */
@RunWith(SpringRunner.class)
@WebMvcTest(CgmesGlController.class)
@ContextConfiguration(classes = {CgmesGlApplication.class})
public class CgmesGlControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CgmesGlService cgmesGlService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test() throws Exception {
        mvc.perform(post("/" + CgmesGlController.API_VERSION + "/{caseUuid}/to-geo-data", UUID.randomUUID()))
                .andExpect(status().isOk());
    }
}
