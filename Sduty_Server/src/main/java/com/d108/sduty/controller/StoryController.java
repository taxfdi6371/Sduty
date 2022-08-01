package com.d108.sduty.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.apache.commons.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.d108.sduty.dto.Profile;
import com.d108.sduty.dto.Story;
import com.d108.sduty.service.FollowService;
import com.d108.sduty.service.ImageService;
import com.d108.sduty.service.StoryService;
import com.google.gson.Gson;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;

@Api(value = "Story")
@RestController
@RequestMapping("/story")
public class StoryController {
	@Autowired
	private StoryService storyService;
	
	@Autowired
	private ImageService imageService;
	
	@ApiOperation(value = "스토리 저장 > Story > Story 리턴", response = Story.class)
	@PostMapping("")
	public ResponseEntity<?> insertProfile(@RequestParam("uploaded_file") MultipartFile imageFile,  @RequestParam("story") String json) throws Exception {
		Gson gson = new Gson();		
		Story story = gson.fromJson(json, Story.class);
		
		//Story Image Uploaded
		story.setImageSource(imageFile.getOriginalFilename());
		imageService.fileUpload(imageFile);
		
		MultipartFile mpfImage = makeThumbnail(imageFile);
		story.setThumbnail(mpfImage.getOriginalFilename());
		imageService.fileUpload(mpfImage);
		
		Story result = storyService.insertStory(story);
		if(result != null) {
			return new ResponseEntity<Story>(story, HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
	}
	
	@ApiOperation(value = "스토리 작성자 시퀀스로로 조회 > UserSeq > List<Story> 리턴", response = Story.class)
	@GetMapping("/writer/{userSeq}")
	public ResponseEntity<?> selectByWriterSeq(@PathVariable int userSeq) throws Exception {
		List<Optional<Story>> selectedOStory = storyService.findBywriterSeq(userSeq);
		List<Story> listStory = new ArrayList<Story>();
		for(Optional<Story> s : selectedOStory) {
			if(s.isPresent())
				listStory.add(s.get());
			else	
				return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
		}
		
		if(listStory.size() > 0) {
			return new ResponseEntity<List<Story>>(listStory, HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
	}
	
	public MultipartFile makeThumbnail(MultipartFile mpImage) throws Exception {
		//Make Thumbnail
		//Convert Multipartfile to file
		File fileImage = new File(mpImage.getOriginalFilename());
		mpImage.transferTo(fileImage);
		
		//Thumbnail generation
		BufferedImage bi = ImageIO.read(fileImage);
		File thumbnailImage = Thumbnails.of(bi)
        .size(360, 480)
        .asFiles(Rename.PREFIX_HYPHEN_THUMBNAIL)
        .get(0);
		
		FileItem fileItem = (FileItem) new DiskFileItem("mainFile", Files.probeContentType(thumbnailImage.toPath()), false, thumbnailImage.getName(), (int) thumbnailImage.length(), thumbnailImage.getParentFile());

		try {
		     IOUtils.copy(new FileInputStream(thumbnailImage), fileItem.getOutputStream());
		} catch (IOException ex) {
			return null;
		}

		return new CommonsMultipartFile(fileItem);
	}
}