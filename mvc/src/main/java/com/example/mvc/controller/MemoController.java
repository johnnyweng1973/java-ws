package com.example.mvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.mvc.model.Memo;
import com.example.mvc.service.MemoService;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/memo")
public class MemoController {
	private static final Logger log = LoggerFactory.getLogger(MathProblemsController.class);


    @Autowired
    private MemoService memoService;

    @GetMapping("/add")
    public String manageMemos(Model model) {
        model.addAttribute("newMemo", new Memo());
        return "add_memo";
    }

    @PostMapping("/add")
    public String addMemo(@ModelAttribute("newMemo") Memo newMemo) {
        memoService.addMemo(newMemo);
        return "redirect:/memo/list";
    }

    @GetMapping
    public String list(Model model) {
        List<Memo> memos;
        try {
        	memos = memoService.getAllMemos();
        } catch (Exception e) {
        	memos = new ArrayList<>();
        }

        // Logging the number of elements in the 'memos' list
        log.info("Number of elements in the 'memos' list: {}", memos.size());
        // Group memos by category
        Map<String, List<Memo>> groupedMemos = new HashMap<>();
        for (Memo memo : memos) {
            groupedMemos
                .computeIfAbsent(memo.getCategory(), k -> new ArrayList<>())
                .add(memo);
        }
        log.info("map is {}", groupedMemos);
        model.addAttribute("categories", groupedMemos.keySet());
        model.addAttribute("groupedMemos", groupedMemos);

        return "memo";
    }

    // following two are for new interface which support batch update and batch delete
    @DeleteMapping("/deleteAll")
    @ResponseBody
    public ResponseEntity<String> handleDelete(@RequestBody List<Long> ids) {
    	try {
    		 memoService.deleteMemos(ids);
             return new ResponseEntity<>("Records deleted successfully", HttpStatus.OK);
		} catch (Exception e) {
			log.info("exception in deleteAll handling");
			
			// Handle any exceptions that occur during JSON decoding
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error processing JSON data");
		}

    }
    
    @PutMapping("/updateAll")
    @ResponseBody
    public ResponseEntity<String> updateAllMemos(@RequestBody List<Memo> updatedMemos) {
        try {
        	memoService.saveAll(updatedMemos);
        	return ResponseEntity.ok().body("Memos updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating memos: " + e.getMessage());
        }
    }

    @GetMapping("/memo-obsolete")
    public String memo(Model model) {
        List<Memo> memos;
        try {
        	memos = memoService.getAllMemos();
        } catch (Exception e) {
        	memos = new ArrayList<>();
        }

        // Logging the number of elements in the 'memos' list
        log.info("Number of elements in the 'memos' list: {}", memos.size());

        model.addAttribute("memos", memos);
        return "memo_obsolete";
    }
    
    @ResponseBody
    @GetMapping("/list-rest")
    public List<Memo> getListFromRestful() {
        List<Memo> memos;
        try {
        	memos = memoService.getAllMemos();
        } catch (Exception e) {
        	memos = new ArrayList<>();
        }

        return memos;
    }
    
    // following two are for obsolete interface 
    @ResponseBody
    @GetMapping("/update-ajax/{memoId}")
    public Memo getMemoById(@PathVariable Long memoId) {
        // Fetch memo details based on memoId and add them to the model
        Memo memo = memoService.findById(memoId).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + memoId));
  
        return memo;
    }
    
    @ResponseBody
    @PostMapping("/update-ajax/{memoId}")
    public String updateMemoById(@PathVariable Long memoId, @RequestBody @ModelAttribute Memo memo) {
        // Update the memo with the provided memoId
        memoService.updateMemo(memo);
        String responseData = "Response data from server";
        return responseData;
    }
}
