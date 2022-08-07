package com.d108.sduty.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.d108.sduty.dto.InterestHashtag;
import com.d108.sduty.dto.Profile;
import com.d108.sduty.dto.Reply;
import com.d108.sduty.dto.Story;
import com.d108.sduty.dto.StoryInterest;
import com.d108.sduty.dto.Timeline;
import com.d108.sduty.repo.InterestHashtagRepo;
import com.d108.sduty.repo.LikesRepo;
import com.d108.sduty.repo.ProfileRepo;
import com.d108.sduty.repo.ReplyRepo;
import com.d108.sduty.repo.ScrapRepo;
import com.d108.sduty.repo.StoryInterestHashtagRepo;
import com.d108.sduty.repo.StoryRepo;

@Service
public class TimelineServiceImpl implements TimelineService {

	@Autowired
	private ProfileRepo profileRepo;
	
	@Autowired
	private StoryRepo storyRepo;
	
	@Autowired
	private ReplyRepo replyRepo;
	
	@Autowired
	private LikesRepo likesRepo;
	
	@Autowired
	private ScrapRepo scrapRepo;
	
	@Autowired
	private InterestHashtagRepo interestHashtagRepo;
	
	@Autowired
	private StoryInterestHashtagRepo storyInterestRepo;
	
	@Override
	public List<Timeline> selectAllTimelines(int userSeq){
		List<Story> storyList = storyRepo.findAllByOrderByRegtimeDesc();
		List<Timeline> timelineList = new ArrayList<Timeline>();
		for(Story s : storyList) {
			Timeline t = new Timeline();
			t.setProfile(getProfile(s.getWriterSeq()));
			t.setStory(s);
			t.setCntReply(replyRepo.countAllByStorySeq(s.getSeq()));
			t.setLikes(likesRepo.existsByUserSeqAndStorySeq(userSeq, s.getSeq()));
			t.setScrap(scrapRepo.existsByUserSeqAndStorySeq(userSeq, s.getSeq()));
			setInterestList(t, s);
			timelineList.add(t);
		}
		return timelineList;
	}

	@Override
	public List<Timeline> selectAllByUserSeqsOrderByRegtime(int userSeq, List<Integer> writerSeq) {
		List<Story> selectedOStory =  storyRepo.findAllByWriterSeqInOrderByRegtimeDesc(writerSeq);
		List<Timeline> tList = new ArrayList<>();
		for(Story s : selectedOStory) {
			Timeline t = new Timeline();
			t.setProfile(getProfile(s.getWriterSeq()));
			t.setStory(s);
			t.setCntReply(replyRepo.countAllByStorySeq(s.getSeq()));
			t.setLikes(likesRepo.existsByUserSeqAndStorySeq(userSeq, s.getSeq()));
			t.setScrap(scrapRepo.existsByUserSeqAndStorySeq(userSeq, s.getSeq()));
			setInterestList(t, s);
			tList.add(t);
		}
		return tList;
	}
	

	@Override
	public List<Timeline> selectAllByUserSeqsWithTag(int userSeq, List<Integer> writerSeq, int jobSeq) {
		List<Story> selectedOStory =  storyRepo.findAllByWriterSeqInAndJobHashtagOrderByRegtimeDesc(writerSeq, jobSeq);
		List<Timeline> tList = new ArrayList<>();
		for(Story s : selectedOStory) {
			Timeline t = new Timeline();
			t.setProfile(getProfile(s.getWriterSeq()));
			t.setStory(s);
			t.setCntReply(replyRepo.countAllByStorySeq(s.getSeq()));
			t.setLikes(likesRepo.existsByUserSeqAndStorySeq(userSeq, s.getSeq()));
			t.setScrap(scrapRepo.existsByUserSeqAndStorySeq(userSeq, s.getSeq()));
			setInterestList(t, s);
			tList.add(t);
		}
		return tList;
	}
	
	@Override
	public Timeline selectRecommandTimeline(int userSeq) {
		Story s = storyRepo.findRecommanded(getProfile(userSeq).getJob());
		Timeline t = new Timeline();
		t.setProfile(getProfile(s.getWriterSeq()));
		t.setStory(s);
		t.setCntReply(replyRepo.countAllByStorySeq(s.getSeq()));
		t.setLikes(likesRepo.existsByUserSeqAndStorySeq(userSeq, s.getSeq()));
		t.setScrap(scrapRepo.existsByUserSeqAndStorySeq(userSeq, s.getSeq()));
		setInterestList(t, s);

		return t;
	}
	
	public Timeline selectDetailTimeline(int storySeq) {
		Optional<Story> selectedOStory = storyRepo.findById(storySeq);
		if(selectedOStory.isPresent()) {
			Timeline t =  new Timeline();
			Story s = selectedOStory.get();
			int userSeq = s.getWriterSeq();
			t.setProfile(getProfile(userSeq));
			t.setLikes(likesRepo.existsByUserSeqAndStorySeq(userSeq, storySeq));
			t.setScrap(scrapRepo.existsByUserSeqAndStorySeq(userSeq, storySeq));
			List<Reply> listReply = replyRepo.findAllByStorySeqOrderByRegtimeDesc(storySeq);
			for(Reply r : listReply) {
				r.setProfile(profileRepo.findById((r.getUserSeq())).get());
			}
			t.setReplies(listReply);
			t.setStory(s);
			setInterestList(t, s);
			return t;
		}
		
		
		return null;
	}
	
	public Profile getProfile(int userSeq) {
		Optional<Profile> OProfile = profileRepo.findById(userSeq);
		if(OProfile.isPresent()) {
			return OProfile.get();
		}
		return null;
	}

	public void setInterestList(Timeline t, Story s) {
		List<StoryInterest> listSI = storyInterestRepo.findAllBySeq(s.getSeq());
		List<InterestHashtag> listIH = new ArrayList<InterestHashtag>();
		for(StoryInterest i : listSI) {
			if(interestHashtagRepo.findById(i.getInterestSeq()).isPresent())
				listIH.add(interestHashtagRepo.findById(i.getInterestSeq()).get());
		}
		t.setInterestHashtags(listIH);
	}
	
	public List<Reply> findAllReplyByStorySeqOrderByRegtimeDesc(int storySeq){
		return replyRepo.findAllByStorySeqOrderByRegtimeDesc(storySeq);
	}
	
}
