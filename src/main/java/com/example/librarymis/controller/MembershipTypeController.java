package com.example.librarymis.controller;

import com.example.librarymis.model.entity.MembershipType;
import com.example.librarymis.service.MembershipTypeService;
import com.example.librarymis.service.impl.MembershipTypeServiceImpl;
import java.util.List;

public class MembershipTypeController {
    private final MembershipTypeService membershipTypeService = new MembershipTypeServiceImpl();

    public MembershipType save(MembershipType membershipType) {
        // Mục đích: xử lý logic của hàm save.
        return membershipTypeService.save(membershipType);
    }

    public List<MembershipType> findAll() {
        // Mục đích: xử lý logic của hàm findAll.
        return membershipTypeService.getAll();
    }

    public List<MembershipType> search(String keyword) {
        // Mục đích: xử lý logic của hàm search.
        return membershipTypeService.search(keyword);
    }

    public void delete(Long id) {
        // Mục đích: xử lý logic của hàm delete.
        membershipTypeService.delete(id);
    }
}
