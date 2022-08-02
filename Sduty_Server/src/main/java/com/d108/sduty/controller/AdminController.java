package com.d108.sduty.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.d108.sduty.dto.Admin;
import com.d108.sduty.dto.DailyQuestion;
import com.d108.sduty.dto.Notice;
import com.d108.sduty.service.AdminService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private AdminService adminService;
	
	@ApiOperation(value = "로그인")
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody ObjectNode objectNode){
		JsonNode idNode = objectNode.get("id");
		JsonNode passwordNode = objectNode.get("password");
		if(idNode==null || passwordNode==null) {
			return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
		}
		String id = idNode.asText();
		String password = passwordNode.asText();
		Optional<Admin> adminOp = adminService.getAdmin(id);
		if(adminOp.isPresent()) {
			Admin adminObject = adminOp.get();
			if(password.equals(adminObject.getPassword())) {
				return new ResponseEntity<Admin>(adminObject, HttpStatus.OK);
			}
		}
		return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
	}
	
	@ApiOperation(value = "공지사항 등록")
	@PostMapping("/notice")
	public ResponseEntity<?> registNotice(@RequestBody Notice newNotice){
		if(newNotice.getWriterSeq()!=null && newNotice.getContent()!=null) {
			newNotice.setSeq(null);
			if(adminService.registNotice(newNotice)) {
				return new ResponseEntity<Void>(HttpStatus.OK);
			}
		}
		return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
	}
	
	@ApiOperation(value = "공지사항 수정")
	@PutMapping("/notice/{notice_seq}")
	public ResponseEntity<?> updateNotice(@RequestParam int notice_seq, @RequestBody Notice notice){
		System.out.println(notice);
		if(notice_seq == notice.getSeq()) {
			Notice result = adminService.updateNotice(notice);
			if(result!=null) {
				return new ResponseEntity<Notice>(result, HttpStatus.OK);
			}
		}
		return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
	}
	
	@ApiOperation(value = "공지사항 삭제")
	@DeleteMapping("/notice/{notice_seq}")
	public ResponseEntity<?> deleteNotice(@RequestParam int notice_seq){
		adminService.deleteNotice(notice_seq);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
	//신고 관리
	
	//데일리 질문
	@ApiOperation(value = "데일리질문 등록")
	@PostMapping("/question")
	public ResponseEntity<?> registDailyQuestion(@RequestBody DailyQuestion dailyq){
		if(adminService.registDailyQuestion(dailyq)) {
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
	}
	
	@ApiOperation(value = "데일리질문 목록")
	@GetMapping("/question")
	public ResponseEntity<?> getDailyQuestions(){
		return new ResponseEntity<List<DailyQuestion>>(adminService.getDailyQuestions(), HttpStatus.OK);
	}
	
	//1:1 문의
}
