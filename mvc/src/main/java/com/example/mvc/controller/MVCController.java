package com.example.mvc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.mvc.model.Memo;
import com.example.mvc.service.MVCService;

import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/memo")
public class MVCController {

    @Autowired
    private MVCService memoService;

    @GetMapping("/manage")
    public String manageVideos(Model model) {
        List<Memo> memos;
        try {
        	memos = memoService.getAllMemos();
        } catch (Exception e) {
        	memos = new ArrayList<>();
        }

        model.addAttribute("memos", memos);
        model.addAttribute("newMemo", new Memo());
        return "manage_memo";
    }

    @PostMapping("/add")
    public String addVideo(@ModelAttribute("newMemo") Memo newMemo) {
        memoService.addMemo(newMemo);
        return "redirect:/memo/manage";
    }
}
