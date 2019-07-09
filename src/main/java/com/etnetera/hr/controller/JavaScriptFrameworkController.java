package com.etnetera.hr.controller;

import com.etnetera.hr.data.FrameworkVersion;
import com.etnetera.hr.repository.FrameworVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;


import com.etnetera.hr.data.JavaScriptFramework;
import com.etnetera.hr.repository.JavaScriptFrameworkRepository;


import javax.validation.Valid;
import java.util.Optional;

/**
 * Simple REST controller for accessing application logic.
 * 
 * @author Etnetera
 *
 */
@RestController
public class JavaScriptFrameworkController extends EtnRestController {

	private final JavaScriptFrameworkRepository repository;
	private final FrameworVersionRepository versionRepository;

	@Autowired
	public JavaScriptFrameworkController(JavaScriptFrameworkRepository repository, FrameworVersionRepository versionRepository) {
		this.repository = repository;
		this.versionRepository = versionRepository;
	}

	@GetMapping("/frameworks")
	public Iterable<JavaScriptFramework> frameworks() {
		return repository.findAll();
	}


	@GetMapping("/frameworks/search/{name}")
	public Iterable<JavaScriptFramework> searchFramework(@PathVariable String name){
		return repository.findAllByNameContains(name);
	}

	@PostMapping(value = "/frameworks/add")
	public ResponseEntity<String> addFramework(@Valid @RequestBody JavaScriptFramework javaScriptFramework,
											   BindingResult bindingResult) throws MethodArgumentNotValidException {
		if (bindingResult.hasErrors()){
			throw new MethodArgumentNotValidException(null, bindingResult);
		}
		repository.save(javaScriptFramework);
		return ResponseEntity.ok("ok");

	}

	@DeleteMapping("/frameworks/delete/{id}")
	public ResponseEntity deleteFramework(@PathVariable Long id){

		try {
			repository.deleteById(id);
		}
		catch (EmptyResultDataAccessException e){
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body("Framework with id: " + id + " is not presented in database !");
		}

		return new ResponseEntity(HttpStatus.OK);
	}


	@PutMapping("frameworks/update/{id}")
	public void updateFramework(@RequestBody JavaScriptFramework javaScriptFramework, @PathVariable Long id){
		javaScriptFramework.setId(id);
		repository.save(javaScriptFramework);
	}

	@PutMapping("frameworks/setversion/{id}")
	public ResponseEntity setVersion(@RequestBody @Valid FrameworkVersion frameworkVersion, @PathVariable Long id, BindingResult bindingResult) throws MethodArgumentNotValidException {
		Optional<JavaScriptFramework> javaScriptFramework = repository.findById(id);

		if (!javaScriptFramework.isPresent()){
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body("Framework with id: " + id + " is not presented in database !");
		}

		if (bindingResult.hasErrors()){
			throw new MethodArgumentNotValidException(null, bindingResult);
		}

		frameworkVersion.setJavaScriptFramework(javaScriptFramework.get());
		versionRepository.save(frameworkVersion);
		javaScriptFramework.get().setVersion(frameworkVersion);
		repository.save(javaScriptFramework.get());

		return new ResponseEntity(HttpStatus.OK);
	}
}
