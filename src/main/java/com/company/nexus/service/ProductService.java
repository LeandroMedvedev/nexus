package com.company.nexus.service;

import com.company.nexus.dto.ProductRequestDTO;
import com.company.nexus.dto.ProductResponseDTO;
import com.company.nexus.model.Product;
import com.company.nexus.model.Supplier;
import com.company.nexus.repository.ProductRepository;
import com.company.nexus.repository.SupplierRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO requestDTO) {
        if (productRepository.findBySku(requestDTO.sku()).isPresent()) {
            throw new IllegalArgumentException("Product with SKU " + requestDTO.sku() + " already exists.");
        }

        Supplier supplier = findSupplierById(requestDTO.supplierId());  // Fornecedor existe?
        Product product = new Product();
        mapDtoToEntity(requestDTO, product, supplier);
        Product savedProduct = productRepository.save(product);

        return new ProductResponseDTO(savedProduct);
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductResponseDTO::new)  // Converte cada Product em ProductResponseDTO
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO getProductById(Long id) {
        Product product = findProductById(id);

        return new ProductResponseDTO(product);
    }

    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO requestDTO) {
        Product existingProduct = findProductById(id);
        Supplier existingSupplier = findSupplierById(requestDTO.supplierId());

        mapDtoToEntity(requestDTO, existingProduct, existingSupplier);
        Product updatedProduct = productRepository.save(existingProduct);

        return new ProductResponseDTO(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product productToDelete = findProductById(id);
        productRepository.delete(productToDelete);
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
    }

    private Supplier findSupplierById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found with id: " + id));
    }

    private void mapDtoToEntity(ProductRequestDTO dto, Product product, Supplier supplier) {
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setSku(dto.sku());
        product.setSupplier(supplier);  // Associação product-supplier
    }
}
