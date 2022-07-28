package com.d108.sduty.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.d108.sduty.dto.Task;
import com.d108.sduty.service.ReportService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Data;

@RestController
@RequestMapping("/report")
public class ReportController {
	
	@Autowired
	private ReportService reportService;
	
	@PutMapping("/timer/{user_seq}")
	public ResponseEntity<?> startTimer(@PathVariable int user_seq){
		reportService.startTask(user_seq);
		return new ResponseEntity<Map<String, Object>>(HttpStatus.OK);
	}
	
	@PostMapping("/tasks")
	public ResponseEntity<?> regist(@RequestBody ObjectNode reqObject) throws JsonProcessingException, IllegalArgumentException{
		ObjectMapper mapper = new ObjectMapper();
		int userSeq = reqObject.get("user_seq").intValue();
		String date = reqObject.get("date").asText();
		Task task = mapper.treeToValue(reqObject.get("task"), Task.class);
		reportService.registTask(userSeq, date, task);
		return new ResponseEntity<Map<String, Object>>(HttpStatus.OK);
	}
	
	@GetMapping("/{user_seq}/{date}")
	public ResponseEntity<?> report(@PathVariable int user_seq, @PathVariable String date){
		System.out.println(user_seq+", "+date);
		//testcode
		Map<String, Object> report = new HashMap<String, Object>();
		//
		List<Task> tasks = new ArrayList<>();
		Task task1 = new Task(2, 5, "토익", "리딩공부하기", "14:00:00", "16:30:00", "02:30:00");
		Task task2 = new Task(3, 46, "코딩", "리포트구현하기", "22:00:00", "23:59:59", "01:59:59");
		tasks.add(task1);
		tasks.add(task2);
		report.put("report_date", "2022-07-25");
		report.put("total_time", "04:29:59");
		report.put("tasks", tasks);
		return new ResponseEntity<Map<String, Object>>(report, HttpStatus.OK);
	}
}
