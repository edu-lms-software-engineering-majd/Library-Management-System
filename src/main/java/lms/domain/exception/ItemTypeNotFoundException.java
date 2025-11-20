package lms.domain.exception;

public class ItemTypeNotFoundException extends Exception {

	 public ItemTypeNotFoundException() {
	        super("Item type not found");
	    }

	    public ItemTypeNotFoundException(String message) {
	        super(message);
	    }
	
	
}
