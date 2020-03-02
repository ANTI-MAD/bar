package com.example.Bar.controller;

import com.example.Bar.entity.InventoryEntity;
import com.example.Bar.security.Roles;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InventoryControllerTest extends AbstractControllerTest{

    @Test
    public void testGetInventoryIsOk() throws Exception{
        //given
        final String token = signIn(Roles.ADMIN);
        final List<InventoryEntity> testInventoryEntities = getInventoryEntities();
        given(inventoryRepository.findAllByOrderByCategory()).willReturn(testInventoryEntities);

        //when
        mockMvc.perform(get("/inventories").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().json("[\n" +
                        "{\n" +
                        "  \"id\" : 1,\n" +
                        "  \"name\" : \"Рюмка 50 мл\",\n" +
                        "  \"category\" : \"glass\",\n" +
                        "  \"count\" : 23\n" +
                        "},\n" +
                        "{\n" +
                        "  \"id\" : 2,\n" +
                        "  \"name\" : \"Стол\",\n" +
                        "  \"category\" : \"table\",\n" +
                        "  \"count\" : 3\n" +
                        "}\n" +
                        "]"));

        verify(inventoryRepository, times(1)).findAllByOrderByCategory();
    }

    @Test
    public void testGetInventoryAccessDeniedForWaiter() throws Exception{
        //given
        final String token = signIn(Roles.WAITER);

        //when
        mockMvc.perform(get("/inventories").header("Authorization", token))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetInventoryAccessDeniedForClient() throws Exception{
        //given
        final String token = signIn(Roles.CLIENT);

        //when
        mockMvc.perform(get("/inventories").header("Authorization", token))
                .andExpect(status().isForbidden());
    }


    @Test
    public void testChangeInventoryCountIsOk() throws Exception{
        //given
        final String token = signIn(Roles.ADMIN);
        final List<InventoryEntity> testInventoryEntities = getInventoryEntities();
        given(inventoryRepository.findById(1)).willReturn(Optional.of(testInventoryEntities.get(0)));

        //when
        mockMvc.perform(patch("/inventories").header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"id\" : 1,\n" +
                        "  \"count\" : 5\n" +
                        "}"))
                .andExpect(status().isOk());

        verify(inventoryRepository, times(1)).findById(1);
        verify(inventoryRepository, times(1)).save(any(InventoryEntity.class));
    }

    @Test
    public void testChangeInventoryAccessDeniedForWaiter() throws Exception{
        //given
        final String token = signIn(Roles.WAITER);

        //when
        mockMvc.perform(patch("/inventories").header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"id\" : 2,\n" +
                        "  \"count\" : 5\n" +
                        "}"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testChangeInventoryAccessDeniedForClient() throws Exception{
        //given
        final String token = signIn(Roles.CLIENT);

        //when
        mockMvc.perform(patch("/inventories").header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"id\" : 1,\n" +
                        "  \"count\" : 50\n" +
                        "}"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testChangeInventoryThrowsBarNoSuchElementException() throws Exception{
        //given
        final String token = signIn(Roles.ADMIN);
        given(inventoryRepository.findById(1)).willReturn(Optional.empty());

        //when
        mockMvc.perform(patch("/inventories").header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"id\" : 1,\n" +
                        "  \"count\" : 5\n" +
                        "}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\n" +
                        "  \"errorMessage\" : \"Such inventoryEntity doesn't exist\"\n" +
                        "}"));

        verify(inventoryRepository, times(1)).findById(1);
        verify(inventoryRepository, times(0)).save(any(InventoryEntity.class));
    }


    @Test
    public void testAddNewInventoryIsCreated() throws Exception{
        //given
        final String token = signIn(Roles.ADMIN);

        //when
        mockMvc.perform(post("/inventories").header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"name\" : \"Бокал 500 мл\",\n" +
                        "  \"category\" : \"wineglass\",\n" +
                        "  \"count\" : 30\n" +
                        "}"))
                .andExpect(status().isCreated());

        verify(inventoryRepository, times(1)).save(any(InventoryEntity.class));
    }

    @Test
    public void testAddNewInventoryAccessDeniedForWaiter() throws Exception {
        //given
        final String token = signIn(Roles.WAITER);

        //when
        mockMvc.perform(post("/inventories").header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"name\" : \"Бокал 500 мл\",\n" +
                        "  \"category\" : \"wineglass\",\n" +
                        "  \"count\" : 30\n" +
                        "}"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testAddNewInventoryAccessDeniedForClient() throws Exception {
        //given
        final String token = signIn(Roles.CLIENT);

        //when
        mockMvc.perform(post("/inventories").header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"name\" : \"Бокал 500 мл\",\n" +
                        "  \"category\" : \"wineglass\",\n" +
                        "  \"count\" : 30\n" +
                        "}"))
                .andExpect(status().isForbidden());
    }


    @Test
    public void testDeleteInventoryIsOk() throws Exception{
        //given
        final String token = signIn(Roles.ADMIN);
        final List<InventoryEntity> testInventoryEntities = getInventoryEntities();
        given(inventoryRepository.findById(1)).willReturn(Optional.of(testInventoryEntities.get(0)));

        //when
        mockMvc.perform(delete("/inventories/1").header("Authorization", token))
                .andExpect(status().isOk());

        verify(inventoryRepository, times(1)).findById(1);
        verify(inventoryRepository, times(1)).delete(any(InventoryEntity.class));
    }

    @Test
    public void testDeleteInventoryAccessDeniedForWaiter() throws Exception{
        //given
        final String token = signIn(Roles.WAITER);

        //when
        mockMvc.perform(delete("/inventories/1").header("Authorization", token))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testDeleteInventoryAccessDeniedForClient() throws Exception{
        //given
        final String token = signIn(Roles.CLIENT);

        //when
        mockMvc.perform(delete("/inventories/1").header("Authorization", token))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testDeleteInventoryThrowsBarNoSuchElementException() throws Exception{
        //given
        final String token = signIn(Roles.ADMIN);
        given(inventoryRepository.findById(1)).willReturn(Optional.empty());

        //when
        mockMvc.perform(delete("/inventories/1").header("Authorization", token))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\n" +
                        "  \"errorMessage\" : \"Such inventoryEntity doesn't exist\"\n" +
                        "}"));

        verify(inventoryRepository, times(1)).findById(1);
        verify(inventoryRepository, times(0)).delete(any(InventoryEntity.class));
    }

    private List<InventoryEntity> getInventoryEntities(){
        final InventoryEntity glass = new InventoryEntity();
        glass.setId(1);
        glass.setName("Рюмка 50 мл");
        glass.setCategory("glass");
        glass.setAmount(23);

        final InventoryEntity table = new InventoryEntity();
        table.setId(2);
        table.setName("Стол");
        table.setCategory("table");
        table.setAmount(3);

        return Arrays.asList(glass, table);
    }

    private List<InventoryEntity> getInventoryEntitiesByCategory() {

        final InventoryEntity wineglass1 = new InventoryEntity();
        wineglass1.setId(3);
        wineglass1.setName("Бокал 500 мл");
        wineglass1.setCategory("wineglass");
        wineglass1.setAmount(30);

        final InventoryEntity wineglass2 = new InventoryEntity();
        wineglass2.setId(4);
        wineglass2.setName("Бокал 250 мл");
        wineglass2.setCategory("wineglass");
        wineglass2.setAmount(13);

        return Arrays.asList(wineglass1, wineglass2);
    }

    @Test
    public void testGetInventoryByCategoryIsOk() throws Exception{
        //given
        final String token = signIn(Roles.ADMIN);
        final List<InventoryEntity> testInventoryEntities = getInventoryEntitiesByCategory();
        given(inventoryRepository.findAllByCategoryAndExistIsTrue("wineglass")).willReturn(testInventoryEntities);

        //when
        mockMvc.perform(get("/inventories/wineglass").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().json("[\n" +
                        "{\n" +
                        "  \"id\" : 3,\n" +
                        "  \"name\" : \"Бокал 500 мл\",\n" +
                        "  \"category\" : \"wineglass\",\n" +
                        "  \"count\" : 30\n" +
                        "},\n" +
                        "{\n" +
                        "  \"id\" : 4,\n" +
                        "  \"name\" : \"Бокал 250 мл\",\n" +
                        "  \"category\" : \"wineglass\",\n" +
                        "  \"count\" : 13\n" +
                        "}\n" +
                        "]"));

        verify(inventoryRepository, times(1)).findAllByCategoryAndExistIsTrue("wineglass");
    }

}