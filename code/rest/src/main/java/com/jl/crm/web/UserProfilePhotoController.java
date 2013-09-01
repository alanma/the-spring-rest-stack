package com.jl.crm.web;

import java.net.URI;
import java.util.Collections;

import javax.inject.Inject;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.jl.crm.services.CrmService;
import com.jl.crm.services.User;
import com.jl.crm.services.UserProfilePhotoReadException;
import com.mangofactory.swagger.annotations.ApiIgnore;

@Controller
@RequestMapping (value = ApiUrls.ROOT_URL_USERS_USER_PHOTO)
class UserProfilePhotoController {

	private CrmService crmService;

	@Inject
	void setCrmService(CrmService crmService) {
		this.crmService = crmService;
	}

	@ApiIgnore
	@RequestMapping (method = RequestMethod.POST)
	HttpEntity<Void> writeUserProfilePhoto(@PathVariable Long user,  
			            @RequestParam MultipartFile file) throws Throwable {
		byte bytesForProfilePhoto[] = FileCopyUtils.copyToByteArray(file.getInputStream());
		this.crmService.writeUserProfilePhoto(user, MediaType.parseMediaType(file.getContentType()), bytesForProfilePhoto);
		HttpHeaders httpHeaders = new HttpHeaders() ;
		URI uriOfPhoto = ServletUriComponentsBuilder.fromCurrentContextPath()
	                    .pathSegment(ApiUrls.ROOT_URL_USERS_USER_PHOTO.substring(1))
						.buildAndExpand(Collections.singletonMap("user", user))
	                    .toUri();
		httpHeaders.setLocation( uriOfPhoto );

		return new ResponseEntity<Void>(httpHeaders, HttpStatus.CREATED);
	}

	@ApiIgnore
	@RequestMapping (method = RequestMethod.GET)
	HttpEntity<byte[]> loadUserProfilePhoto(@PathVariable Long user) throws Throwable {
		CrmService.ProfilePhoto profilePhoto = this.crmService.readUserProfilePhoto(user);
		if (null != profilePhoto){
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(profilePhoto.getMediaType());
			return new ResponseEntity<byte[]>(profilePhoto.getPhoto(), httpHeaders, HttpStatus.OK);
		}
		throw new UserProfilePhotoReadException(user);

	}

}
