package com.securetask.Service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.securetask.DTO.requests.RegisterRequest;
import com.securetask.DTO.responses.UserResponse;

@Service
public class UserServiceImpl {
    

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserValidator userValidator;


    @Override
    public UserResponse register(RegisterRequest userRequest) 
    {
        userValidator.validateRegisterRequest(userRequest);
        return userMapper.toUserResponse(userDAO.save(userMapper.toEntity(userRequest))));
    }

    /** 
     * Exemple of code : 
     * 
@Override
    public List<ItemDTO> getAll() {
        return dao.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDTO getById(Long id) {
        var item = dao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found: " + id));
        item.setFinalPrice(item.getPrice() * (1 - item.getDiscount() / 100.0));
        return mapper.toDTO(item);    }

    @Override
    public ItemDTO update(Long id, ItemDTO dto) {
        var item = dao.findById(id).orElseThrow(()->new ResourceNotFoundException("Item not found: " + id));
        var updatedItem = item.builder()
                .name(dto.getName())
                .price(dto.getPrice())
                .discount(dto.getDiscount())
                .finalPrice(dto.getPrice()*(1-dto.getDiscount() / 100.0))
                .build();
        return mapper.toDTO(dao.save(updatedItem));
    }

    @Override
    public void delete(Long id) {
        if (!dao.existsById(id)) {
            throw new ResourceNotFoundException("Item not found: " + id);
        }
        dao.deleteById(id);
    }

     * 
     * 
     */


}
