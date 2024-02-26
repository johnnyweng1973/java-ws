package com.example.mvc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.mvc.model.Memo;
import com.example.mvc.repository.MemoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class MVCService {

    @Autowired
    private MemoRepository memoRepository;

    public List<Memo> getAllMemos() {
        return memoRepository.findAll();
    }

    public Memo addMemo(Memo memo) {
        return memoRepository.save(memo);
    }

	public Optional<Memo> findById(Long memoId) {
		return memoRepository.findById(memoId);
	}

	public void updateMemo(Memo memo) {
		memoRepository.save(memo);
		return;
	}
}
