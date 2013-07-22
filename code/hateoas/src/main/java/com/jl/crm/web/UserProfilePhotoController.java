package com.jl.crm.web;

import com.jl.crm.services.*;
import org.springframework.hateoas.*;
import org.springframework.hateoas.mvc.HeaderLinksResponseEntity;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.net.URI;
import java.util.List;

@Controller
@RequestMapping (value = ApiUrls.URL_USERS_USER_PHOTO)
class UserProfilePhotoController {

	private CrmService crmService;
	private UserResourceAssembler userResourceAssembler;

	@Inject
	void setCrmService(CrmService crmService) {
		this.crmService = crmService;
	}

	@Inject
	void setUserResourceAssembler(UserResourceAssembler userResourceAssembler) {
		this.userResourceAssembler = userResourceAssembler;
	}

	/** Responds with <A href="http://tools.ietf.org/html/rfc5988">rfc5988</A>-compatible {@code Link:} header. */
	@RequestMapping (method = RequestMethod.POST)
	ResponseEntity<Resource<byte[]>> writeUserProfilePhoto(@PathVariable Long user, @RequestParam MultipartFile file) throws Throwable {
		byte bytesForProfilePhoto[] = FileCopyUtils.copyToByteArray(file.getInputStream());
		this.crmService.writeUserProfilePhoto(user, MediaType.parseMediaType(file.getContentType()), bytesForProfilePhoto);
		Resource<User> userResource = this.userResourceAssembler.toResource(crmService.findById(user));
		List<Link> linkCollection = userResource.getLinks();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(URI.create(userResource.getLink("photo").getHref())); // "Location: /users/{userId}/photo"
		Resource<byte[]> bytesResource = new Resource<byte[]>(bytesForProfilePhoto, linkCollection);
		ResponseEntity<Resource<byte[]>> responseEntity = new ResponseEntity<Resource<byte[]>>(bytesResource, httpHeaders, HttpStatus.ACCEPTED);
		return HeaderLinksResponseEntity.wrap(responseEntity);
	}

	/** this responds with <A href="http://tools.ietf.org/html/rfc5988">rfc5988</A>-compatible {@code Link:} header. */
	/*

	@RequestMapping (method = RequestMethod.POST)
	HttpEntity<Void> writeUserProfilePhoto(@PathVariable Long user, @RequestParam MultipartFile file) throws Throwable {
		byte bytesForProfilePhoto[] = FileCopyUtils.copyToByteArray(file.getInputStream());
		this.crmService.writeUserProfilePhoto(user, MediaType.parseMediaType(file.getContentType()), bytesForProfilePhoto);

		Resource<User> userResource = this.userResourceAssembler.toResource(crmService.findById(user));
		List<Link> linkCollection = userResource.getLinks();
		Links wrapperOfLinks = new Links(linkCollection);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Link", wrapperOfLinks.toString());  // we can't encode the links in the body of the response, so we put them in the "Links:" header.
		httpHeaders.setLocation(URI.create(userResource.getLink("photo").getHref())); // "Location: /users/{userId}/photo"

		return new ResponseEntity<Void>(httpHeaders, HttpStatus.ACCEPTED);
	}
*/
	@RequestMapping (method = RequestMethod.GET)
	HttpEntity<byte[]> loadUserProfilePhoto(@PathVariable Long user) throws Exception {
		CrmService.ProfilePhoto profilePhoto = this.crmService.readUserProfilePhoto(user);
		if (null != profilePhoto){
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(profilePhoto.getMediaType());
			return new ResponseEntity<byte[]>(profilePhoto.getPhoto(), httpHeaders, HttpStatus.OK);
		}
		throw new UserProfilePhotoReadException(user);

	}


}
