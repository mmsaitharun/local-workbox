package oneapp.incture.workbox.inbox.dto;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WorkBoxActionDto {

	private String taskInstanceId;
	private String action;
	private String userId;
	private String comment;
	private String status;


	@Override
	public String toString() {
		return "WorkBoxActionDto [taskInstanceId=" + taskInstanceId + ", action=" + action + ", userId=" + userId
				+ ", comment=" + comment + ", status=" + status + "]";
	}



	public String getTaskInstanceId() {
		return taskInstanceId;
	}



	public void setTaskInstanceId(String taskInstanceId) {
		this.taskInstanceId = taskInstanceId;
	}

	public String getUserId() {
		return userId;
	}



	public void setUserId(String userId) {
		this.userId = userId;
	}



	public String getComment() {
		return comment;
	}



	public void setComment(String comment) {
		this.comment = comment;
	}



	public String getStatus() {
		return status;
	}



	public void setStatus(String status) {
		this.status = status;
	}



	public String getAction() {
		return action;
	}



	public void setAction(String action) {
		this.action = action;
	}

	
	
}
