package com.d108.sduty.repo;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.d108.sduty.dto.Task;

public interface TaskRepo extends JpaRepository<Task, String>{
	Task findBySeq(int taskSeq);
	@Transactional
	void deleteBySeq(int taskSeq);
}