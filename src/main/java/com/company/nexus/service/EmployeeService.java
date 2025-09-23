package com.company.nexus.service;

import com.company.nexus.dto.EmployeeRequestDTO;
import com.company.nexus.dto.EmployeeResponseDTO;
import com.company.nexus.model.Employee;
import com.company.nexus.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Transactional
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO requestDTO) {
        if (employeeRepository.findByEmail(requestDTO.email()).isPresent()) {
            throw new IllegalArgumentException("Employee with email " + requestDTO.email() + " already exists.");
        }
        Employee employee = new Employee();
        mapDtoToEntity(requestDTO, employee);
        Employee savedEmployee = employeeRepository.save(employee);

        return new EmployeeResponseDTO(savedEmployee);
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponseDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(EmployeeResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmployeeResponseDTO getEmployeeById(Long id) {
        return new EmployeeResponseDTO(findEmployeeById(id));
    }

    @Transactional
    public EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO requestDTO) {
        Employee existingEmployee = findEmployeeById(id);
        mapDtoToEntity(requestDTO, existingEmployee);
        Employee updatedEmployee = employeeRepository.save(existingEmployee);

        return new EmployeeResponseDTO(updatedEmployee);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        Employee employeeToDelete = findEmployeeById(id);
        employeeRepository.delete(employeeToDelete);
    }

    public Employee findEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + id));
    }

    private void mapDtoToEntity(EmployeeRequestDTO dto, Employee employee) {
        employee.setFirstName(dto.firstName());
        employee.setLastName(dto.lastName());
        employee.setRole(dto.role());
        employee.setEmail(dto.email());
        employee.setHireDate(dto.hireDate());
    }
}
