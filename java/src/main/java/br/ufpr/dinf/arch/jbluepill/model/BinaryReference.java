package br.ufpr.dinf.arch.jbluepill.model;

import java.io.Serializable;
import java.util.Hashtable;

import br.ufpr.dinf.arch.jbluepill.util.TraceUtils;

/**
 * binary file ELF information.
 * 
 * @author Lauri Laux
 *
 */
public class BinaryReference implements Serializable {

	private static final long serialVersionUID = 2377481245546465370L;

	private String fileName;
	private boolean isElfObject = false;
	private Hashtable<String, BinaryReferenceSection> sectionSizeTable = new Hashtable<String, BinaryReferenceSection>();
	private long fileSize = 0L;
	private long instructionsCalled = 0L;

	public boolean isElfObject() {
		return isElfObject;
	}

	public void setElfObject(boolean isElfObject) {
		this.isElfObject = isElfObject;
	}

	public Hashtable<String, BinaryReferenceSection> getSectionSizeTable() {
		return sectionSizeTable;
	}

	public void setSectionSizeTable(Hashtable<String, BinaryReferenceSection> sectionSizeTable) {
		this.sectionSizeTable = sectionSizeTable;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String objectName) {
		this.fileName = objectName;
	}

	@Override
	public String toString() {
		return "ObjectReference [fileName=" + fileName + 
				", sectionsSize=" + sectionSizeTable.size() + 
				", isElfObject=" + isElfObject + 
				", fileSize=" + TraceUtils.getLongHexString(fileSize) + 
				", instructionsCalled=" + instructionsCalled +
				"]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
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
		BinaryReference other = (BinaryReference) obj;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		return true;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public long getInstructionsCalled() {
		return instructionsCalled;
	}

	public void setInstructionsCalled(long instructionsCalled) {
		this.instructionsCalled = instructionsCalled;
	}

}