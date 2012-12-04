package org.maziarz.yiiclipse.datatransfer;

public class DataTransferException extends RuntimeException {

	public DataTransferException(String localizedMessage, Exception e) {
		super(localizedMessage, e);
	}

	private static final long serialVersionUID = 1L;

}
