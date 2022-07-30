package com.d108.sduty.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.d108.sduty.dto.Alarm;
import com.d108.sduty.dto.Study;
import com.d108.sduty.dto.Task;
import com.d108.sduty.dto.User;
import com.d108.sduty.service.StudyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/study")
public class StudyController {
	
	@Autowired
	private StudyService studyService;
	
	@ApiOperation(value = "스터디 등록")
	@PostMapping("")
	public ResponseEntity<?> registStudy(@RequestBody ObjectNode reqObject){
		ObjectMapper mapper = new ObjectMapper();
		Study study = null;
		Alarm alarm = null;
		try {
			study = mapper.treeToValue(reqObject.get("study"), Study.class);
			JsonNode node = reqObject.get("alarm");
			if((study.isCamstudy() && node==null)||(!study.isCamstudy()&& node!=null)) {
				//캠스터디인데 알람이 없는경우 || 캠스터디 아닌데 알람이 있는 경우
				return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
			}
			if(node != null) {
				alarm = mapper.treeToValue(node, Alarm.class);
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		//1. form 제출 후, 이름 중복 검사
		boolean result = studyService.checkStudyName(study.getName());
		if(result==true) {//중복
			return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
		}
		//2. 등록
		studyService.registStudy(study, alarm);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
	@ApiOperation(value = "스터디명 중복검사")
	@GetMapping("/check/{study_name}")
	public ResponseEntity<?> checkStudyName(@PathVariable String study_name){
		boolean result = studyService.checkStudyName(study_name);
		if(result==true) {//중복
			return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
	@ApiOperation(value="스터디 전체 조회")
	@GetMapping("")
	public ResponseEntity<?> getAllStudy(){
		return new ResponseEntity<List<Study>>(studyService.getAllStudy(), HttpStatus.OK);
	}
	
	@ApiOperation(value="스터디 상세 조회")
	@GetMapping("/detail/{study_seq}")
	public ResponseEntity<?> getStudyDetail(@PathVariable int study_seq){
		return new ResponseEntity<Study>(studyService.getStudyDetail(study_seq), HttpStatus.OK);
	}
	
	@ApiOperation(value="스터디 참여")
	@PostMapping("/participation/{study_seq}/{user_seq}")
	public ResponseEntity<?> joinStudy(@PathVariable int study_seq, @PathVariable int user_seq){
		if(!studyService.joinStudy(study_seq, user_seq)) {
			return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
	@ApiOperation(value="스터디 탈퇴")
	@DeleteMapping("/participation/{study_seq}/{user_seq}")
	public ResponseEntity<?> disjoinStudy(@PathVariable int study_seq, @PathVariable int user_seq){
		if(!studyService.disjoinStudy(study_seq, user_seq)) {
			return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@ApiOperation(value = "내 스터디 목록")
	@GetMapping("/{user_seq}")
	public ResponseEntity<?> myStudyList(@PathVariable int user_seq){
		System.out.println("내 스터디 목록"+ user_seq);
		List<Study> myStudyList = new ArrayList<>();
//		myStudyList.add(new Study(5, 71, "하루 10시간ㄱ", "하루 10시간하실 분만 오세요!", "대학생", 12, 5, "a123", false, "2022-07-27 08:24:31", "공지사항!!", null));
//		myStudyList.add(new Study(1, 23, "서울가자", "열심히 해봐요", "중학생", 10, 2, null, true, "2022-07-26 22:00:12", null, null));
//		myStudyList.add(new Study(1, 23, "서울가자", "열심히 해봐요", "중학생", 10, 2, null, true, "2022-07-26 22:00:12", null, null));
//		myStudyList.add(new Study(1, 23, "서울가자", "열심히 해봐요", "중학생", 10, 2, null, true, "2022-07-26 22:00:12", null, null));
//		myStudyList.add(new Study(1, 23, "서울가자", "열심히 해봐요", "중학생", 10, 2, null, true, "2022-07-26 22:00:12", null, null));
//		myStudyList.add(new Study(1, 23, "서울가자", "열심히 해봐요", "중학생", 10, 2, null, true, "2022-07-26 22:00:12", null, null));
//		myStudyList.add(new Study(1, 23, "서울가자", "열심히 해봐요", "중학생", 10, 2, null, true, "2022-07-26 22:00:12", null, null));
//		myStudyList.add(new Study(1, 23, "서울가자", "열심히 해봐요", "중학생", 10, 2, null, true, "2022-07-26 22:00:12", null, null));
//		myStudyList.add(new Study(1, 23, "서울가자", "열심히 해봐요", "중학생", 10, 2, null, true, "2022-07-26 22:00:12", null, null));
//		myStudyList.add(new Study(1, 23, "서울가자", "열심히 해봐요", "중학생", 10, 2, null, true, "2022-07-26 22:00:12", null, null));
//		myStudyList.add(new Study(1, 23, "서울가자", "열심히 해봐요", "중학생", 10, 2, null, true, "2022-07-26 22:00:12", null, null));
		Map<String, Object> resultMap = new HashMap();
		resultMap.put("my_study_list", myStudyList);
		return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
	}
	
	@ApiOperation(value = "스터디 삭제")
	@DeleteMapping("/{user_seq}/{study_seq}")
	public ResponseEntity<?> updateStudy(@PathVariable int user_seq, int study_seq){
		Map<String, Object> map = new HashMap<String, Object>();
		if(studyService.deleteStudy(user_seq, study_seq)) {
			map.put("result", "삭제되었습니다.");
		}
		else {
			map.put("result", "삭제할 수 없습니다.");
		}
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}
	
	@ApiOperation(value = "스터디 검색")
	@GetMapping("/{category}/{emptyfilter}/{camfilter}/{publicfilter}/{keyword}")
	public ResponseEntity<?> searchStudy(String category, @PathVariable boolean emptyfilter, @PathVariable boolean camfilter, @PathVariable boolean publicfilter, String keyword){
		studyService.searchStudy(category, emptyfilter, camfilter, publicfilter, keyword);
		
		return new ResponseEntity<List<Study>>(HttpStatus.OK);
	}
}
