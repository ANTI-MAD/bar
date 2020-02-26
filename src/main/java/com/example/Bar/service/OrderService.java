package com.example.Bar.service;

import com.example.Bar.converter.OrderConverter;
import com.example.Bar.dto.TextResponse;
import com.example.Bar.dto.order.*;
import com.example.Bar.entity.OrderChoiceEntity;
import com.example.Bar.entity.OrderEntity;
import com.example.Bar.entity.UserDiscountCardEntity;
import com.example.Bar.exception.BarNoSuchElementException;
import com.example.Bar.repository.OrderRepository;
import com.example.Bar.repository.UserDiscountCardRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.math3.util.Precision;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserDiscountCardRepository userDiscountCardRepository;
    private final OrderConverter orderConverter;

    public List<OrderDTO> getOrders(){
        return orderRepository.findAll().stream().map(orderConverter::convertToDTO).collect(Collectors.toList());
    }

    public void makeOrder(final MakeNewOrderRequestDTO makeNewOrderRequestDTO) throws BarNoSuchElementException {
        orderRepository.save(orderConverter.convertToEntity(makeNewOrderRequestDTO));
    }

    public TextResponse getRevenueByTime(final RevenueRequest revenueRequest){
        final double price = orderRepository.findAllByTimeCloseAfter(revenueRequest.getDate()).stream()
                .map(orderEntity -> calculateRevenue(orderEntity.getOrderChoiceEntities()))
                .reduce(0D, Double::sum);

        return new TextResponse(price + "р.");
    }

    public TextResponse closeOrder(final CloseOrderRequestDTO closeOrderRequestDTO) throws BarNoSuchElementException {
        final OrderEntity orderEntity = findOrderToClose(closeOrderRequestDTO);
        double orderPrice = calculatePrice(orderEntity);

        if (closeOrderRequestDTO.getClientId() != null){
            orderPrice = applyUserDiscountCard(closeOrderRequestDTO.getClientId(), orderPrice);
        }

        orderEntity.setTimeClose(LocalDateTime.now());
        orderRepository.save(orderEntity);

        return new TextResponse(orderPrice + "р");
    }

    private double calculateRevenue(final List<OrderChoiceEntity> orderChoiceEntities){
        return orderChoiceEntities.stream()
                .map(orderChoiceEntity -> orderChoiceEntity.getAmount() * orderChoiceEntity.getMenuItemEntity().getPrice())
                .reduce(0D, Double::sum);
    }

    private OrderEntity findOrderToClose(final CloseOrderRequestDTO closeOrderRequestDTO) throws BarNoSuchElementException {
        return orderRepository.findByTableNumberAndTimeCloseIsNull(closeOrderRequestDTO.getTableNumber())
                .orElseThrow(() -> new BarNoSuchElementException("Such orderEntity doesn't exist"));
    }

    private double calculatePrice(final OrderEntity orderEntity){
        return orderEntity.getOrderChoiceEntities().stream()
                .map(orderChoiceEntity -> orderChoiceEntity.getAmount() * orderChoiceEntity.getMenuItemEntity().getPrice())
                .reduce(0D, Double::sum);
    }

    private double applyUserDiscountCard(final Integer clientId, double price) throws BarNoSuchElementException {
        final UserDiscountCardEntity userDiscountCardEntity = userDiscountCardRepository.findById(clientId)
                .orElseThrow(() -> new BarNoSuchElementException("Such discountCard doesn't exist"));

        price -= Precision.round(price * userDiscountCardEntity.getClientDiscount(), 2);

        userDiscountCardEntity.setAllSpentMoney(userDiscountCardEntity.getAllSpentMoney() + price);
        calculateDiscount(userDiscountCardEntity);
        userDiscountCardRepository.save(userDiscountCardEntity);

        return price;
    }

    private void calculateDiscount(final UserDiscountCardEntity userDiscountCardEntity){
        final double money = userDiscountCardEntity.getAllSpentMoney();

        if (money > 2000)
            userDiscountCardEntity.setClientDiscount(0.20D);
        else
            userDiscountCardEntity.setClientDiscount(500D % money * 0.05D);
    }

}
