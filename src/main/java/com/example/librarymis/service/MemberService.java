package com.example.librarymis.service;

import com.example.librarymis.model.entity.Member;
import java.util.List;

public interface MemberService {
    Member save(Member member);
    List<Member> getAll();
    List<Member> search(String keyword);
    List<Member> getActiveMembers();
    void delete(Long id);
    long count();
}
