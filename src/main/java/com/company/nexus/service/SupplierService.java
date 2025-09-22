package com.company.nexus.service;

import com.company.nexus.dto.SupplierRequestDTO;
import com.company.nexus.dto.SupplierResponseDTO;
import com.company.nexus.model.Supplier;
import com.company.nexus.repository.SupplierRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    @Transactional
    public SupplierResponseDTO createSupplier(SupplierRequestDTO requestDTO) {
        if (supplierRepository.findByEmail(requestDTO.email()).isPresent()) {
            throw new IllegalArgumentException("Supplier with email " + requestDTO.email() + " already exists.");
        }

        Supplier supplier = new Supplier();
        mapDtoToEntity(requestDTO, supplier);
        Supplier savedSupplier = supplierRepository.save(supplier);

        return new SupplierResponseDTO(savedSupplier);
    }

    @Transactional(readOnly = true)
    public List<SupplierResponseDTO> getAllSuppliers() {
        return supplierRepository.findAll()
                .stream()
                .map(SupplierResponseDTO::new)  // Converte cada Supplier em SupplierResponseDTO
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SupplierResponseDTO getSupplierById(Long id) {
        Supplier supplier = findSupplierById(id);

        return new SupplierResponseDTO(supplier);
    }

    @Transactional
    public SupplierResponseDTO updateSupplier(Long id, SupplierRequestDTO requestDTO) {
        Supplier existingSupplier = findSupplierById(id);
        mapDtoToEntity(requestDTO, existingSupplier);
        Supplier updatedSupplier = supplierRepository.save(existingSupplier);

        return new SupplierResponseDTO(updatedSupplier);
    }

    @Transactional
    public void deleteSupplier(Long id) {
        Supplier supplierToDelete = findSupplierById(id);
        supplierRepository.delete(supplierToDelete);
    }

    public Supplier findSupplierById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found with id: " + id));
    }

    private void mapDtoToEntity(SupplierRequestDTO dto, Supplier supplier) {
        supplier.setName(dto.name());
        supplier.setContactPerson(dto.contactPerson());
        supplier.setEmail(dto.email());
        supplier.setPhone(dto.phone());
    }
}
