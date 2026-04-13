package com.example.librarymis.service;

import com.example.librarymis.model.entity.MembershipType;
import java.util.List;

public interface MembershipTypeService {
    MembershipType save(MembershipType membershipType);
    List<MembershipType> getAll();
    List<MembershipType> search(String keyword);
    void delete(Long id);
}
