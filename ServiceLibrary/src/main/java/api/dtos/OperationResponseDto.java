package api.dtos;

public class OperationResponseDto {
    private String message;

    public OperationResponseDto() {}
    
    public OperationResponseDto(String message) {
        this.message = message;
    }

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
 
}