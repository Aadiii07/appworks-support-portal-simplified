package com.appworks.portal;

import com.appworks.portal.dto.AlertConfigurationRequest;
import com.appworks.portal.dto.AlertRecipientRequest;
import com.appworks.portal.dto.CustomerRequest;
import com.appworks.portal.entity.AlertFrequency;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AlertConfigurationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long createCustomer(String code) throws Exception {
        CustomerRequest request = new CustomerRequest();
        request.setName("Test Customer " + code);
        request.setCode(code);
        request.setContactEmail("ops@" + code.toLowerCase() + ".com");

        String response = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }

    @Test
    void getConfiguration_beforeAnyIsSet_returns404() throws Exception {
        Long customerId = createCustomer("ALCFG1");

        mockMvc.perform(get("/api/v1/customers/" + customerId + "/alert-configuration"))
                .andExpect(status().isNotFound());
    }

    @Test
    void upsertConfiguration_createsThenUpdatesInPlace() throws Exception {
        Long customerId = createCustomer("ALCFG2");

        AlertConfigurationRequest create = new AlertConfigurationRequest();
        create.setFrequency(AlertFrequency.EVERY_FAILURE);

        mockMvc.perform(put("/api/v1/customers/" + customerId + "/alert-configuration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.frequency").value("EVERY_FAILURE"))
                .andExpect(jsonPath("$.enabled").value(true));

        AlertConfigurationRequest update = new AlertConfigurationRequest();
        update.setFrequency(AlertFrequency.DAILY);
        update.setEnabled(false);

        // Same customer, second PUT — must update the existing row, not create a second one.
        mockMvc.perform(put("/api/v1/customers/" + customerId + "/alert-configuration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.frequency").value("DAILY"))
                .andExpect(jsonPath("$.enabled").value(false));

        mockMvc.perform(get("/api/v1/customers/" + customerId + "/alert-configuration"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.frequency").value("DAILY"))
                .andExpect(jsonPath("$.enabled").value(false));
    }

    @Test
    void addRecipient_thenListIt_succeeds() throws Exception {
        Long customerId = createCustomer("ALCFG3");

        AlertConfigurationRequest config = new AlertConfigurationRequest();
        config.setFrequency(AlertFrequency.EVERY_FAILURE);
        mockMvc.perform(put("/api/v1/customers/" + customerId + "/alert-configuration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(config)))
                .andExpect(status().isOk());

        AlertRecipientRequest recipient = new AlertRecipientRequest();
        recipient.setEmail("oncall@alcfg3.com");

        mockMvc.perform(post("/api/v1/customers/" + customerId + "/alert-configuration/recipients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipient)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("oncall@alcfg3.com"));

        mockMvc.perform(get("/api/v1/customers/" + customerId + "/alert-configuration/recipients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void addSameRecipientTwice_returns409() throws Exception {
        Long customerId = createCustomer("ALCFG4");

        AlertConfigurationRequest config = new AlertConfigurationRequest();
        config.setFrequency(AlertFrequency.EVERY_FAILURE);
        mockMvc.perform(put("/api/v1/customers/" + customerId + "/alert-configuration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(config)))
                .andExpect(status().isOk());

        AlertRecipientRequest recipient = new AlertRecipientRequest();
        recipient.setEmail("dup@alcfg4.com");

        mockMvc.perform(post("/api/v1/customers/" + customerId + "/alert-configuration/recipients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipient)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/customers/" + customerId + "/alert-configuration/recipients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipient)))
                .andExpect(status().isConflict());
    }

    @Test
    void addRecipient_beforeConfigurationExists_returns404() throws Exception {
        Long customerId = createCustomer("ALCFG5");

        AlertRecipientRequest recipient = new AlertRecipientRequest();
        recipient.setEmail("nobody@alcfg5.com");

        mockMvc.perform(post("/api/v1/customers/" + customerId + "/alert-configuration/recipients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipient)))
                .andExpect(status().isNotFound());
    }

    @Test
    void removeRecipient_thenListIsEmpty() throws Exception {
        Long customerId = createCustomer("ALCFG6");

        AlertConfigurationRequest config = new AlertConfigurationRequest();
        config.setFrequency(AlertFrequency.EVERY_FAILURE);
        mockMvc.perform(put("/api/v1/customers/" + customerId + "/alert-configuration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(config)))
                .andExpect(status().isOk());

        AlertRecipientRequest recipient = new AlertRecipientRequest();
        recipient.setEmail("temp@alcfg6.com");

        String response = mockMvc.perform(post("/api/v1/customers/" + customerId + "/alert-configuration/recipients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipient)))
                .andReturn().getResponse().getContentAsString();

        Long recipientId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/v1/customers/" + customerId + "/alert-configuration/recipients/" + recipientId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/customers/" + customerId + "/alert-configuration/recipients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
