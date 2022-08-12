package com.d108.sduty.utils;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.d108.sduty.dto.Profile;
import com.d108.sduty.dto.User;
import com.d108.sduty.repo.ProfileRepo;
import com.d108.sduty.repo.UserRepo;

@Component
public class BirthdayScheduler {
	
	@Autowired
	private ProfileRepo profileRepo;
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private FCMUtil fcmUtil;
	
//	//8시 정각에 알림
	@Scheduled(cron="0 * * * * *")
	public void sendMsg() {
		List<User> listU = userRepo.findAll();
		LocalDate today = LocalDate.now();
		SimpleDateFormat df = new SimpleDateFormat("MM-dd");
		System.out.println(today);
		for(User u : listU) {
			if(u.getFcmToken() != null && u.getUserPublic() != 0 && !u.getFcmToken().equals("") && u.getSeq() == 2) {
				Profile p = profileRepo.findById(u.getSeq()).get();
				String birthday = df.format(p.getBirthday());
			    String title = "생일 축하합니다.";
			    String content =  "축하합니다";
			    if(birthday.equals(today.format(DateTimeFormatter.ofPattern("MM-dd")))){
			    	fcmUtil.send_FCM(u.getFcmToken(), title, content);
			    }
				
			}
		}
	}
}
	
	
