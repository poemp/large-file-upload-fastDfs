package org.poem.entity;


import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Shared entity (java/javascript) containing information about a file being uploaded.<br>
 * @author antoinem
 * 上传的文件的信息 import
 */
public class FileStateJsonBase implements Serializable {

	/**
	 * generated id
	 */
	private static final long serialVersionUID = 5043865795253104456L;

	/** 原始文件名. */
	private String originalFileName;

	/** 原始文件的大小 */
	private Long originalFileSizeInBytes;

	/** 原始文件的创建时间 */
	private Date creationDate;

	/**
	 * 以千字节为单位的客户率
	 */
	private Long rateInKiloBytes;

	/**
	 * Amount of bytes that were correctly validated.<br>
	 * When resuming an upload, all bytes in the file that have not been validated are revalidated.
     * 上传了的文件中验证了的大小
	 */
	private AtomicLong crcedBytes;

	/**  */
	private String firstChunkCrc;

	private static final Object LockObject = new Object();
	/**
	 * Default constructor.
	 */
	public FileStateJsonBase() {
		super();
	}


	public String getOriginalFileName() {
		return originalFileName;
	}


	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}


	public Long getOriginalFileSizeInBytes() {
		return originalFileSizeInBytes;
	}


	public void setOriginalFileSizeInBytes(Long originalFileSizeInBytes) {
		this.originalFileSizeInBytes = originalFileSizeInBytes;
	}


	public Long getRateInKiloBytes() {
		return rateInKiloBytes;
	}


	public void setRateInKiloBytes(Long rateInKiloBytes) {
		this.rateInKiloBytes = rateInKiloBytes;
	}


	public AtomicLong getCrcedBytes() {
            return crcedBytes;
	}


	public void setCrcedBytes(AtomicLong crcedBytes) {
            this.crcedBytes = crcedBytes;
	}


	public Date getCreationDate() {
		return creationDate;
	}


	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}


	public String getFirstChunkCrc() {
		return firstChunkCrc;
	}


	public void setFirstChunkCrc(String firstChunkCrc) {
		this.firstChunkCrc = firstChunkCrc;
	}
}
