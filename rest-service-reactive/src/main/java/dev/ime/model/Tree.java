package dev.ime.model;

import java.util.Objects;

public class Tree {

	private Long treeId;
	private String kingdom;
	private String family;
	private String species;	
	
	public Tree() {
		super();
	}

	public Tree(Long treeId, String kingdom, String family, String species) {
		super();
		this.treeId = treeId;
		this.kingdom = kingdom;
		this.family = family;
		this.species = species;
	}

	public Long getTreeId() {
		return treeId;
	}

	public void setTreeId(Long treeId) {
		this.treeId = treeId;
	}

	public String getKingdom() {
		return kingdom;
	}

	public void setKingdom(String kingdom) {
		this.kingdom = kingdom;
	}

	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}

	public String getSpecies() {
		return species;
	}

	public void setSpecies(String species) {
		this.species = species;
	}

	@Override
	public int hashCode() {
		return Objects.hash(family, kingdom, species, treeId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tree other = (Tree) obj;
		return Objects.equals(family, other.family) && Objects.equals(kingdom, other.kingdom)
				&& Objects.equals(species, other.species) && Objects.equals(treeId, other.treeId);
	}

	@Override
	public String toString() {
		return "Tree [treeId=" + treeId + ", kingdom=" + kingdom + ", family=" + family + ", species=" + species + "]";
	}
	
}
