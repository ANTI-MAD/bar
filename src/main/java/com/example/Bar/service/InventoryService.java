package com.example.Bar.service;

import com.example.Bar.converter.InventoryConverter;
import com.example.Bar.dto.inventory.AddNewInventoryRequestDTO;
import com.example.Bar.dto.inventory.ChangeInventoryCountRequestDTO;
import com.example.Bar.dto.inventory.InventoryDTO;
import com.example.Bar.entity.InventoryEntity;
import com.example.Bar.exception.BarNoSuchElementException;
import com.example.Bar.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryConverter inventoryConverter;

    public List<InventoryDTO> getInventories(){
        return inventoryRepository.findAllByOrderByCategory().stream().map(inventoryConverter::convertToDTO)
                .collect(Collectors.toList());
    }

    public void changeInventoryCount(final ChangeInventoryCountRequestDTO inventoryDTO)
            throws BarNoSuchElementException {
        final InventoryEntity inventoryEntity = inventoryRepository.findById(inventoryDTO.getId())
                .orElseThrow(() -> new BarNoSuchElementException("Such inventoryEntity doesn't exist"));

        inventoryEntity.setAmount(inventoryDTO.getCount());
        inventoryRepository.save(inventoryEntity);
    }

    public void addInventory(final AddNewInventoryRequestDTO inventoryDTO){
        inventoryRepository.save(inventoryConverter.convertToEntity(inventoryDTO));
    }

    public void deleteInventory(final Integer inventoryId) throws BarNoSuchElementException {
        final InventoryEntity inventoryEntity = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new BarNoSuchElementException("Such inventoryEntity doesn't exist"));

        inventoryRepository.delete(inventoryEntity);
    }

    public List<InventoryDTO> getInventoriesByCategory(final String category) {
        return inventoryRepository.findAllByCategoryAndExistIsTrue(category).stream().map(inventoryConverter::convertToDTO)
                .collect(Collectors.toList());
    }

}
