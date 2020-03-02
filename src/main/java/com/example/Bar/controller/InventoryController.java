package com.example.Bar.controller;

import com.example.Bar.dto.TextResponse;
import com.example.Bar.dto.inventory.AddNewInventoryRequestDTO;
import com.example.Bar.dto.inventory.ChangeInventoryCountRequestDTO;
import com.example.Bar.dto.inventory.InventoryDTO;
import com.example.Bar.exception.BarNoSuchElementException;
import com.example.Bar.service.InventoryService;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/inventories")
@Api(value = "Inventory system")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "View inventories", notes = "Use this method, if you want to view inventories")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully get inventories"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public List<InventoryDTO> getInventories(){
        return inventoryService.getInventories();
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Change inventory count", notes = "Use this method, if you want to change inventory count")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully change inventory count"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public void changeInventoryCount(@ApiParam(value = "Change inventory count data", required = true)
            @Valid @RequestBody final ChangeInventoryCountRequestDTO changeInventoryCountRequestDTO) throws BarNoSuchElementException {
        inventoryService.changeInventoryCount(changeInventoryCountRequestDTO);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Add new inventory", notes = "Use this method, if you want to add new inventory")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully add inventory"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public void addInventory(@ApiParam(value = "Add new inventory data", required = true)
            @Valid @RequestBody final AddNewInventoryRequestDTO addNewInventoryRequestDTO){
        inventoryService.addInventory(addNewInventoryRequestDTO);
    }

    @DeleteMapping("/{inventoryId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Delete inventory", notes = "Use this method, if you want to delete inventory")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully delete inventory"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public void deleteInventory(@ApiParam(value = "Inventory id", required = true)
            @PathVariable("inventoryId") final Integer inventoryId) throws BarNoSuchElementException {
        inventoryService.deleteInventory(inventoryId);
    }

    @GetMapping("/{category}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "View inventories by category", notes = "Use this method, if you want to view inventories")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully get inventories by category"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public List<InventoryDTO> getInventoriesByCategory(@ApiParam(value = "Category", required = true)
                                                       @PathVariable final String category){
        return inventoryService.getInventoriesByCategory(category);
    }
}
