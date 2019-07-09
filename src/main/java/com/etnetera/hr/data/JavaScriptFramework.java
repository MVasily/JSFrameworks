package com.etnetera.hr.data;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.util.Set;

/**
 * Simple data entity describing basic properties of every JavaScript framework.
 * 
 * @author Etnetera
 *
 */
@Entity
public class JavaScriptFramework {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false, length = 30)
	@NotEmpty
	@Size(max = 30)
	private String name;

	@Column
	@Min(0)
	@Max(100)
	private int hypeLevel;


	@Column
	private Timestamp deprecationDate;

	@OneToMany(mappedBy = "javaScriptFramework")
	@JsonManagedReference
	private Set<FrameworkVersion> versions;



	public JavaScriptFramework() {
	}

	public JavaScriptFramework(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getHypeLevel() {
		return hypeLevel;
	}

	public void setHypeLevel(int hypeLevel) {
		this.hypeLevel = hypeLevel;
	}

	public Timestamp getDeprecationDate() {
		return deprecationDate;
	}

	public void setDeprecationDate(Timestamp deprecationDate) {
		this.deprecationDate = deprecationDate;
	}

	public Set<FrameworkVersion> getVersions() {
		return versions;
	}

	public void setVersion(FrameworkVersion frameworkVersion){
		this.versions.add(frameworkVersion);
	}

	public void setVersions(Set<FrameworkVersion> versions) {
		this.versions = versions;
	}

	@Override
	public String toString() {
		return "JavaScriptFramework [id=" + id + ", name=" + name + "]";
	}

}
