package com.example.mvc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.mvc.model.Memo;
import com.example.mvc.service.MVCService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/memo")
public class MVCController {

    @Autowired
    private MVCService memoService;

    @GetMapping("/manage")
    public String manageMemos(Model model) {
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

    @GetMapping("/list")
    public String listMemos(Model model) {
        List<Memo> memos;
        try {
        	memos = memoService.getAllMemos();
        } catch (Exception e) {
        	memos = new ArrayList<>();
        }

        model.addAttribute("memos", memos);
        return "list_memo";
    }

    @PostMapping("/add")
    public String addMemo(@ModelAttribute("newMemo") Memo newMemo) {
        memoService.addMemo(newMemo);
        return "redirect:/memo/manage";
    }
    
    @GetMapping("/update/{memoId}")
    public String showEditForm(@PathVariable Long memoId, Model model) {
        // Fetch memo details based on memoId and add them to the model
        Memo memo = memoService.findById(memoId).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + memoId));
        model.addAttribute("memo", memo);
        return "update_Memo"; // Return the name of the edit form template
    }

    @PostMapping("/update/{memoId}")
    public String editMemo(@PathVariable Long memoId, @ModelAttribute Memo memo) {
        // Update the memo with the provided memoId
        memoService.updateMemo(memo);
        return "redirect:/memo/list"; // Redirect to the memo list page after editing
    }
}
