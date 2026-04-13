package com.example.librarymis.controller;

import com.example.librarymis.model.entity.Member;
import com.example.librarymis.service.MemberService;
import com.example.librarymis.service.impl.MemberServiceImpl;
import java.util.List;

public class MemberController {
    private final MemberService memberService = new MemberServiceImpl();

    public Member save(Member member) {
        // Mục đích: xử lý logic của hàm save.
        return memberService.save(member);
    }

    public List<Member> findAll() {
        // Mục đích: xử lý logic của hàm findAll.
        return memberService.getAll();
    }

    public List<Member> search(String keyword) {
        // Mục đích: xử lý logic của hàm search.
        return memberService.search(keyword);
    }

    public List<Member> activeMembers() {
        // Mục đích: xử lý logic của hàm activeMembers.
        return memberService.getActiveMembers();
    }

    public void delete(Long id) {
        // Mục đích: xử lý logic của hàm delete.
        memberService.delete(id);
    }

    public long count() {
        // Mục đích: xử lý logic của hàm count.
        return memberService.count();
    }
}
