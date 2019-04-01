package com.infy.bpe.models;

public class MetaData {
	private String name;
	private String type;
	public MetaData() {
		super();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MetaData other = (MetaData) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "MetaData [name=" + name + ", type=" + type + "]";
	}
	
	public boolean isApexClass() {
		return this.getType() != null && this.getType().equals("ApexClass");
	}
	
	public boolean isApexPage() {
		return this.getType() != null && this.getType().equals("ApexPage");
	}
	
	public boolean isApexTrigger() {
		return this.getType() != null && this.getType().equals("ApexTrigger");
	}
	
	public boolean isValidationRule() {
		return this.getType() != null && this.getType().equals("ValidationRule");
	}
}
