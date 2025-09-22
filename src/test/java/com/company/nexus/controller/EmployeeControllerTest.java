package com.company.nexus.controller;

import com.company.nexus.dto.EmployeeRequestDTO;
import com.company.nexus.model.Employee;
import com.company.nexus.repository.EmployeeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    @DisplayName("Deve criar o funcionário Doug Heffernan com sucesso e retornar status 201")
    void createEmployee_withValidData_shouldReturnCreated() throws Exception {
        var requestDTO = new EmployeeRequestDTO(
                "Doug",
                "Heffernan",
                "IPS Driver",
                "doug@ips.com",
                LocalDate.of(1998, 9, 21)
        );

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value("Doug"))
                .andExpect(jsonPath("$.email").value("doug@ips.com"));
    }

    @Test
    @DisplayName("Deve retornar o funcionário Arthur Spooner pelo ID quando o ID existir")
    void getEmployeeById_whenIdExists_shouldReturnEmployee() throws Exception {
        // Arrange
        Employee arthur = employeeRepository.save(new Employee(
                null,
                "Arthur",
                "Spooner",
                "Basement Resident",
                "arthur@spooner.com",
                LocalDate.of(1925, 1, 1)
        ));

        mockMvc.perform(get("/api/v1/employees/{id}", arthur.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(arthur.getId()))
                .andExpect(jsonPath("$.firstName").value("Arthur"));
    }

    @Test
    @DisplayName("Deve retornar status 404 ao buscar um ID de funcionário que não existe")
    void getEmployeeById_whenIdDoesNotExist_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/employees/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar a funcionária Carrie Heffernan com sucesso e retornar status 200")
    void updateEmployee_withValidData_shouldReturnOk() throws Exception {
        Employee carrie = employeeRepository.save(new Employee(
                null,
                "Carrie",
                "Heffernan",
                "Legal Secretary",
                "carrie@lawfirm.com",
                LocalDate.of(1996, 3, 15))
        );
        var requestDTO = new EmployeeRequestDTO(
                "Carrie",
                "Heffernan",
                "Executive Assistant", // Promoção!
                "carrie.h@pricer-less.com",
                carrie.getHireDate()
        );

        mockMvc.perform(put("/api/v1/employees/{id}", carrie.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("Executive Assistant"))
                .andExpect(jsonPath("$.email").value("carrie.h@pricer-less.com"));
    }

    @Test
    @DisplayName("Deve excluir o funcionário Arthur Spooner com sucesso e retornar status 204")
    void deleteEmployee_whenIdExists_shouldReturnNoContent() throws Exception {
        // Arrange
        Employee arthur = employeeRepository.save(new Employee(
                null,
                "Arthur",
                "Spooner",
                "Retired",
                "arthur@retiree.com",
                LocalDate.of(1998, 1, 1))
        );

        mockMvc.perform(delete("/api/v1/employees/{id}", arthur.getId()))
                .andExpect(status().isNoContent());
    }
}